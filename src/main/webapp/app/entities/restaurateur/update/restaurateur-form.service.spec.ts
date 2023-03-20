import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../restaurateur.test-samples';

import { RestaurateurFormService } from './restaurateur-form.service';

describe('Restaurateur Form Service', () => {
  let service: RestaurateurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestaurateurFormService);
  });

  describe('Service methods', () => {
    describe('createRestaurateurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRestaurateurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            commande: expect.any(Object),
            societaire: expect.any(Object),
          })
        );
      });

      it('passing IRestaurateur should create a new form with FormGroup', () => {
        const formGroup = service.createRestaurateurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            commande: expect.any(Object),
            societaire: expect.any(Object),
          })
        );
      });
    });

    describe('getRestaurateur', () => {
      it('should return NewRestaurateur for default Restaurateur initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRestaurateurFormGroup(sampleWithNewData);

        const restaurateur = service.getRestaurateur(formGroup) as any;

        expect(restaurateur).toMatchObject(sampleWithNewData);
      });

      it('should return NewRestaurateur for empty Restaurateur initial value', () => {
        const formGroup = service.createRestaurateurFormGroup();

        const restaurateur = service.getRestaurateur(formGroup) as any;

        expect(restaurateur).toMatchObject({});
      });

      it('should return IRestaurateur', () => {
        const formGroup = service.createRestaurateurFormGroup(sampleWithRequiredData);

        const restaurateur = service.getRestaurateur(formGroup) as any;

        expect(restaurateur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRestaurateur should not enable id FormControl', () => {
        const formGroup = service.createRestaurateurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRestaurateur should disable id FormControl', () => {
        const formGroup = service.createRestaurateurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
