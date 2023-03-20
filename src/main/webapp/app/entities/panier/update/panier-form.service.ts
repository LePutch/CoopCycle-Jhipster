import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPanier, NewPanier } from '../panier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPanier for edit and NewPanierFormGroupInput for create.
 */
type PanierFormGroupInput = IPanier | PartialWithRequiredKeyOf<NewPanier>;

type PanierFormDefaults = Pick<NewPanier, 'id'>;

type PanierFormGroupContent = {
  id: FormControl<IPanier['id'] | NewPanier['id']>;
  description: FormControl<IPanier['description']>;
  price: FormControl<IPanier['price']>;
  restaurant: FormControl<IPanier['restaurant']>;
};

export type PanierFormGroup = FormGroup<PanierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PanierFormService {
  createPanierFormGroup(panier: PanierFormGroupInput = { id: null }): PanierFormGroup {
    const panierRawValue = {
      ...this.getFormDefaults(),
      ...panier,
    };
    return new FormGroup<PanierFormGroupContent>({
      id: new FormControl(
        { value: panierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      description: new FormControl(panierRawValue.description, {
        validators: [Validators.minLength(5), Validators.maxLength(500)],
      }),
      price: new FormControl(panierRawValue.price, {
        validators: [Validators.required, Validators.min(0)],
      }),
      restaurant: new FormControl(panierRawValue.restaurant),
    });
  }

  getPanier(form: PanierFormGroup): IPanier | NewPanier {
    return form.getRawValue() as IPanier | NewPanier;
  }

  resetForm(form: PanierFormGroup, panier: PanierFormGroupInput): void {
    const panierRawValue = { ...this.getFormDefaults(), ...panier };
    form.reset(
      {
        ...panierRawValue,
        id: { value: panierRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PanierFormDefaults {
    return {
      id: null,
    };
  }
}
