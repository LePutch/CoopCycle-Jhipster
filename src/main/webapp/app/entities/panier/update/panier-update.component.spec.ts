import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PanierFormService } from './panier-form.service';
import { PanierService } from '../service/panier.service';
import { IPanier } from '../panier.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { PanierUpdateComponent } from './panier-update.component';

describe('Panier Management Update Component', () => {
  let comp: PanierUpdateComponent;
  let fixture: ComponentFixture<PanierUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let panierFormService: PanierFormService;
  let panierService: PanierService;
  let restaurantService: RestaurantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PanierUpdateComponent],
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
      .overrideTemplate(PanierUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PanierUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    panierFormService = TestBed.inject(PanierFormService);
    panierService = TestBed.inject(PanierService);
    restaurantService = TestBed.inject(RestaurantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Restaurant query and add missing value', () => {
      const panier: IPanier = { id: 456 };
      const restaurant: IRestaurant = { id: 51856 };
      panier.restaurant = restaurant;

      const restaurantCollection: IRestaurant[] = [{ id: 93534 }];
      jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
      const additionalRestaurants = [restaurant];
      const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
      jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      expect(restaurantService.query).toHaveBeenCalled();
      expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(
        restaurantCollection,
        ...additionalRestaurants.map(expect.objectContaining)
      );
      expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const panier: IPanier = { id: 456 };
      const restaurant: IRestaurant = { id: 72313 };
      panier.restaurant = restaurant;

      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      expect(comp.restaurantsSharedCollection).toContain(restaurant);
      expect(comp.panier).toEqual(panier);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPanier>>();
      const panier = { id: 123 };
      jest.spyOn(panierFormService, 'getPanier').mockReturnValue(panier);
      jest.spyOn(panierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: panier }));
      saveSubject.complete();

      // THEN
      expect(panierFormService.getPanier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(panierService.update).toHaveBeenCalledWith(expect.objectContaining(panier));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPanier>>();
      const panier = { id: 123 };
      jest.spyOn(panierFormService, 'getPanier').mockReturnValue({ id: null });
      jest.spyOn(panierService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: panier }));
      saveSubject.complete();

      // THEN
      expect(panierFormService.getPanier).toHaveBeenCalled();
      expect(panierService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPanier>>();
      const panier = { id: 123 };
      jest.spyOn(panierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(panierService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRestaurant', () => {
      it('Should forward to restaurantService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(restaurantService, 'compareRestaurant');
        comp.compareRestaurant(entity, entity2);
        expect(restaurantService.compareRestaurant).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
