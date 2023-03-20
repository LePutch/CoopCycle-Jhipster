import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RestaurateurDetailComponent } from './restaurateur-detail.component';

describe('Restaurateur Management Detail Component', () => {
  let comp: RestaurateurDetailComponent;
  let fixture: ComponentFixture<RestaurateurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RestaurateurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ restaurateur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RestaurateurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RestaurateurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load restaurateur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.restaurateur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
