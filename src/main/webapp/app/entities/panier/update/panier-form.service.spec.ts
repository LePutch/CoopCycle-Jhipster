import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../panier.test-samples';

import { PanierFormService } from './panier-form.service';

describe('Panier Form Service', () => {
  let service: PanierFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PanierFormService);
  });

  describe('Service methods', () => {
    describe('createPanierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPanierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            restaurant: expect.any(Object),
          })
        );
      });

      it('passing IPanier should create a new form with FormGroup', () => {
        const formGroup = service.createPanierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            restaurant: expect.any(Object),
          })
        );
      });
    });

    describe('getPanier', () => {
      it('should return NewPanier for default Panier initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPanierFormGroup(sampleWithNewData);

        const panier = service.getPanier(formGroup) as any;

        expect(panier).toMatchObject(sampleWithNewData);
      });

      it('should return NewPanier for empty Panier initial value', () => {
        const formGroup = service.createPanierFormGroup();

        const panier = service.getPanier(formGroup) as any;

        expect(panier).toMatchObject({});
      });

      it('should return IPanier', () => {
        const formGroup = service.createPanierFormGroup(sampleWithRequiredData);

        const panier = service.getPanier(formGroup) as any;

        expect(panier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPanier should not enable id FormControl', () => {
        const formGroup = service.createPanierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPanier should disable id FormControl', () => {
        const formGroup = service.createPanierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
