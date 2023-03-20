import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RestaurantFormService, RestaurantFormGroup } from './restaurant-form.service';
import { IRestaurant } from '../restaurant.model';
import { RestaurantService } from '../service/restaurant.service';
import { IRestaurateur } from 'app/entities/restaurateur/restaurateur.model';
import { RestaurateurService } from 'app/entities/restaurateur/service/restaurateur.service';

@Component({
  selector: 'jhi-restaurant-update',
  templateUrl: './restaurant-update.component.html',
})
export class RestaurantUpdateComponent implements OnInit {
  isSaving = false;
  restaurant: IRestaurant | null = null;

  restaurateursSharedCollection: IRestaurateur[] = [];

  editForm: RestaurantFormGroup = this.restaurantFormService.createRestaurantFormGroup();

  constructor(
    protected restaurantService: RestaurantService,
    protected restaurantFormService: RestaurantFormService,
    protected restaurateurService: RestaurateurService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareRestaurateur = (o1: IRestaurateur | null, o2: IRestaurateur | null): boolean =>
    this.restaurateurService.compareRestaurateur(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurant }) => {
      this.restaurant = restaurant;
      if (restaurant) {
        this.updateForm(restaurant);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurant = this.restaurantFormService.getRestaurant(this.editForm);
    if (restaurant.id !== null) {
      this.subscribeToSaveResponse(this.restaurantService.update(restaurant));
    } else {
      this.subscribeToSaveResponse(this.restaurantService.create(restaurant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurant>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(restaurant: IRestaurant): void {
    this.restaurant = restaurant;
    this.restaurantFormService.resetForm(this.editForm, restaurant);

    this.restaurateursSharedCollection = this.restaurateurService.addRestaurateurToCollectionIfMissing<IRestaurateur>(
      this.restaurateursSharedCollection,
      restaurant.restaurateur
    );
  }

  protected loadRelationshipsOptions(): void {
    this.restaurateurService
      .query()
      .pipe(map((res: HttpResponse<IRestaurateur[]>) => res.body ?? []))
      .pipe(
        map((restaurateurs: IRestaurateur[]) =>
          this.restaurateurService.addRestaurateurToCollectionIfMissing<IRestaurateur>(restaurateurs, this.restaurant?.restaurateur)
        )
      )
      .subscribe((restaurateurs: IRestaurateur[]) => (this.restaurateursSharedCollection = restaurateurs));
  }
}
