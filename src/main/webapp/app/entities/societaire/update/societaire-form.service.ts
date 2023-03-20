import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISocietaire, NewSocietaire } from '../societaire.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISocietaire for edit and NewSocietaireFormGroupInput for create.
 */
type SocietaireFormGroupInput = ISocietaire | PartialWithRequiredKeyOf<NewSocietaire>;

type SocietaireFormDefaults = Pick<NewSocietaire, 'id'>;

type SocietaireFormGroupContent = {
  id: FormControl<ISocietaire['id'] | NewSocietaire['id']>;
  firstName: FormControl<ISocietaire['firstName']>;
  lastName: FormControl<ISocietaire['lastName']>;
  type: FormControl<ISocietaire['type']>;
};

export type SocietaireFormGroup = FormGroup<SocietaireFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SocietaireFormService {
  createSocietaireFormGroup(societaire: SocietaireFormGroupInput = { id: null }): SocietaireFormGroup {
    const societaireRawValue = {
      ...this.getFormDefaults(),
      ...societaire,
    };
    return new FormGroup<SocietaireFormGroupContent>({
      id: new FormControl(
        { value: societaireRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      firstName: new FormControl(societaireRawValue.firstName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
      }),
      lastName: new FormControl(societaireRawValue.lastName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
      }),
      type: new FormControl(societaireRawValue.type, {
        validators: [Validators.required],
      }),
    });
  }

  getSocietaire(form: SocietaireFormGroup): ISocietaire | NewSocietaire {
    return form.getRawValue() as ISocietaire | NewSocietaire;
  }

  resetForm(form: SocietaireFormGroup, societaire: SocietaireFormGroupInput): void {
    const societaireRawValue = { ...this.getFormDefaults(), ...societaire };
    form.reset(
      {
        ...societaireRawValue,
        id: { value: societaireRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SocietaireFormDefaults {
    return {
      id: null,
    };
  }
}
