import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RestaurateurFormService } from './restaurateur-form.service';
import { RestaurateurService } from '../service/restaurateur.service';
import { IRestaurateur } from '../restaurateur.model';
import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { ISocietaire } from 'app/entities/societaire/societaire.model';
import { SocietaireService } from 'app/entities/societaire/service/societaire.service';

import { RestaurateurUpdateComponent } from './restaurateur-update.component';

describe('Restaurateur Management Update Component', () => {
  let comp: RestaurateurUpdateComponent;
  let fixture: ComponentFixture<RestaurateurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let restaurateurFormService: RestaurateurFormService;
  let restaurateurService: RestaurateurService;
  let commandeService: CommandeService;
  let societaireService: SocietaireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RestaurateurUpdateComponent],
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
      .overrideTemplate(RestaurateurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurateurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    restaurateurFormService = TestBed.inject(RestaurateurFormService);
    restaurateurService = TestBed.inject(RestaurateurService);
    commandeService = TestBed.inject(CommandeService);
    societaireService = TestBed.inject(SocietaireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Commande query and add missing value', () => {
      const restaurateur: IRestaurateur = { id: 456 };
      const commande: ICommande = { id: 18734 };
      restaurateur.commande = commande;

      const commandeCollection: ICommande[] = [{ id: 86572 }];
      jest.spyOn(commandeService, 'query').mockReturnValue(of(new HttpResponse({ body: commandeCollection })));
      const additionalCommandes = [commande];
      const expectedCollection: ICommande[] = [...additionalCommandes, ...commandeCollection];
      jest.spyOn(commandeService, 'addCommandeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurateur });
      comp.ngOnInit();

      expect(commandeService.query).toHaveBeenCalled();
      expect(commandeService.addCommandeToCollectionIfMissing).toHaveBeenCalledWith(
        commandeCollection,
        ...additionalCommandes.map(expect.objectContaining)
      );
      expect(comp.commandesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Societaire query and add missing value', () => {
      const restaurateur: IRestaurateur = { id: 456 };
      const societaire: ISocietaire = { id: 98726 };
      restaurateur.societaire = societaire;

      const societaireCollection: ISocietaire[] = [{ id: 24943 }];
      jest.spyOn(societaireService, 'query').mockReturnValue(of(new HttpResponse({ body: societaireCollection })));
      const additionalSocietaires = [societaire];
      const expectedCollection: ISocietaire[] = [...additionalSocietaires, ...societaireCollection];
      jest.spyOn(societaireService, 'addSocietaireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurateur });
      comp.ngOnInit();

      expect(societaireService.query).toHaveBeenCalled();
      expect(societaireService.addSocietaireToCollectionIfMissing).toHaveBeenCalledWith(
        societaireCollection,
        ...additionalSocietaires.map(expect.objectContaining)
      );
      expect(comp.societairesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const restaurateur: IRestaurateur = { id: 456 };
      const commande: ICommande = { id: 74038 };
      restaurateur.commande = commande;
      const societaire: ISocietaire = { id: 95654 };
      restaurateur.societaire = societaire;

      activatedRoute.data = of({ restaurateur });
      comp.ngOnInit();

      expect(comp.commandesSharedCollection).toContain(commande);
      expect(comp.societairesSharedCollection).toContain(societaire);
      expect(comp.restaurateur).toEqual(restaurateur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurateur>>();
      const restaurateur = { id: 123 };
      jest.spyOn(restaurateurFormService, 'getRestaurateur').mockReturnValue(restaurateur);
      jest.spyOn(restaurateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurateur }));
      saveSubject.complete();

      // THEN
      expect(restaurateurFormService.getRestaurateur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(restaurateurService.update).toHaveBeenCalledWith(expect.objectContaining(restaurateur));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurateur>>();
      const restaurateur = { id: 123 };
      jest.spyOn(restaurateurFormService, 'getRestaurateur').mockReturnValue({ id: null });
      jest.spyOn(restaurateurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurateur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurateur }));
      saveSubject.complete();

      // THEN
      expect(restaurateurFormService.getRestaurateur).toHaveBeenCalled();
      expect(restaurateurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRestaurateur>>();
      const restaurateur = { id: 123 };
      jest.spyOn(restaurateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(restaurateurService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCommande', () => {
      it('Should forward to commandeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(commandeService, 'compareCommande');
        comp.compareCommande(entity, entity2);
        expect(commandeService.compareCommande).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSocietaire', () => {
      it('Should forward to societaireService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(societaireService, 'compareSocietaire');
        comp.compareSocietaire(entity, entity2);
        expect(societaireService.compareSocietaire).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
