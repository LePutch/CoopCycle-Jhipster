import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SocietaireDetailComponent } from './societaire-detail.component';

describe('Societaire Management Detail Component', () => {
  let comp: SocietaireDetailComponent;
  let fixture: ComponentFixture<SocietaireDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SocietaireDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ societaire: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SocietaireDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SocietaireDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load societaire on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.societaire).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
