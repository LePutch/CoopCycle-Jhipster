import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../societaire.test-samples';

import { SocietaireFormService } from './societaire-form.service';

describe('Societaire Form Service', () => {
  let service: SocietaireFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SocietaireFormService);
  });

  describe('Service methods', () => {
    describe('createSocietaireFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSocietaireFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            type: expect.any(Object),
          })
        );
      });

      it('passing ISocietaire should create a new form with FormGroup', () => {
        const formGroup = service.createSocietaireFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            type: expect.any(Object),
          })
        );
      });
    });

    describe('getSocietaire', () => {
      it('should return NewSocietaire for default Societaire initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSocietaireFormGroup(sampleWithNewData);

        const societaire = service.getSocietaire(formGroup) as any;

        expect(societaire).toMatchObject(sampleWithNewData);
      });

      it('should return NewSocietaire for empty Societaire initial value', () => {
        const formGroup = service.createSocietaireFormGroup();

        const societaire = service.getSocietaire(formGroup) as any;

        expect(societaire).toMatchObject({});
      });

      it('should return ISocietaire', () => {
        const formGroup = service.createSocietaireFormGroup(sampleWithRequiredData);

        const societaire = service.getSocietaire(formGroup) as any;

        expect(societaire).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISocietaire should not enable id FormControl', () => {
        const formGroup = service.createSocietaireFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSocietaire should disable id FormControl', () => {
        const formGroup = service.createSocietaireFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
