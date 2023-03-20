import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SocietaireFormService } from './societaire-form.service';
import { SocietaireService } from '../service/societaire.service';
import { ISocietaire } from '../societaire.model';

import { SocietaireUpdateComponent } from './societaire-update.component';

describe('Societaire Management Update Component', () => {
  let comp: SocietaireUpdateComponent;
  let fixture: ComponentFixture<SocietaireUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let societaireFormService: SocietaireFormService;
  let societaireService: SocietaireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SocietaireUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SocietaireUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SocietaireUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    societaireFormService = TestBed.inject(SocietaireFormService);
    societaireService = TestBed.inject(SocietaireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const societaire: ISocietaire = { id: 456 };

      activatedRoute.data = of({ societaire });
      comp.ngOnInit();

      expect(comp.societaire).toEqual(societaire);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISocietaire>>();
      const societaire = { id: 123 };
      jest.spyOn(societaireFormService, 'getSocietaire').mockReturnValue(societaire);
      jest.spyOn(societaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ societaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: societaire }));
      saveSubject.complete();

      // THEN
      expect(societaireFormService.getSocietaire).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(societaireService.update).toHaveBeenCalledWith(expect.objectContaining(societaire));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISocietaire>>();
      const societaire = { id: 123 };
      jest.spyOn(societaireFormService, 'getSocietaire').mockReturnValue({ id: null });
      jest.spyOn(societaireService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ societaire: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: societaire }));
      saveSubject.complete();

      // THEN
      expect(societaireFormService.getSocietaire).toHaveBeenCalled();
      expect(societaireService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISocietaire>>();
      const societaire = { id: 123 };
      jest.spyOn(societaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ societaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(societaireService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
