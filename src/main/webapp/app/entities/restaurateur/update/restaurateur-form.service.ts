import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRestaurateur, NewRestaurateur } from '../restaurateur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRestaurateur for edit and NewRestaurateurFormGroupInput for create.
 */
type RestaurateurFormGroupInput = IRestaurateur | PartialWithRequiredKeyOf<NewRestaurateur>;

type RestaurateurFormDefaults = Pick<NewRestaurateur, 'id'>;

type RestaurateurFormGroupContent = {
  id: FormControl<IRestaurateur['id'] | NewRestaurateur['id']>;
  firstName: FormControl<IRestaurateur['firstName']>;
  lastName: FormControl<IRestaurateur['lastName']>;
  commande: FormControl<IRestaurateur['commande']>;
  societaire: FormControl<IRestaurateur['societaire']>;
};

export type RestaurateurFormGroup = FormGroup<RestaurateurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RestaurateurFormService {
  createRestaurateurFormGroup(restaurateur: RestaurateurFormGroupInput = { id: null }): RestaurateurFormGroup {
    const restaurateurRawValue = {
      ...this.getFormDefaults(),
      ...restaurateur,
    };
    return new FormGroup<RestaurateurFormGroupContent>({
      id: new FormControl(
        { value: restaurateurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      firstName: new FormControl(restaurateurRawValue.firstName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
      }),
      lastName: new FormControl(restaurateurRawValue.lastName, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
      }),
      commande: new FormControl(restaurateurRawValue.commande),
      societaire: new FormControl(restaurateurRawValue.societaire),
    });
  }

  getRestaurateur(form: RestaurateurFormGroup): IRestaurateur | NewRestaurateur {
    return form.getRawValue() as IRestaurateur | NewRestaurateur;
  }

  resetForm(form: RestaurateurFormGroup, restaurateur: RestaurateurFormGroupInput): void {
    const restaurateurRawValue = { ...this.getFormDefaults(), ...restaurateur };
    form.reset(
      {
        ...restaurateurRawValue,
        id: { value: restaurateurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RestaurateurFormDefaults {
    return {
      id: null,
    };
  }
}
