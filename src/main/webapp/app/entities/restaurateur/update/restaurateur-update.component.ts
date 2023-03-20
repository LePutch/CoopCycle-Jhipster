import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RestaurateurFormService, RestaurateurFormGroup } from './restaurateur-form.service';
import { IRestaurateur } from '../restaurateur.model';
import { RestaurateurService } from '../service/restaurateur.service';
import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { ISocietaire } from 'app/entities/societaire/societaire.model';
import { SocietaireService } from 'app/entities/societaire/service/societaire.service';

@Component({
  selector: 'jhi-restaurateur-update',
  templateUrl: './restaurateur-update.component.html',
})
export class RestaurateurUpdateComponent implements OnInit {
  isSaving = false;
  restaurateur: IRestaurateur | null = null;

  commandesSharedCollection: ICommande[] = [];
  societairesSharedCollection: ISocietaire[] = [];

  editForm: RestaurateurFormGroup = this.restaurateurFormService.createRestaurateurFormGroup();

  constructor(
    protected restaurateurService: RestaurateurService,
    protected restaurateurFormService: RestaurateurFormService,
    protected commandeService: CommandeService,
    protected societaireService: SocietaireService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCommande = (o1: ICommande | null, o2: ICommande | null): boolean => this.commandeService.compareCommande(o1, o2);

  compareSocietaire = (o1: ISocietaire | null, o2: ISocietaire | null): boolean => this.societaireService.compareSocietaire(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurateur }) => {
      this.restaurateur = restaurateur;
      if (restaurateur) {
        this.updateForm(restaurateur);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurateur = this.restaurateurFormService.getRestaurateur(this.editForm);
    if (restaurateur.id !== null) {
      this.subscribeToSaveResponse(this.restaurateurService.update(restaurateur));
    } else {
      this.subscribeToSaveResponse(this.restaurateurService.create(restaurateur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurateur>>): void {
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

  protected updateForm(restaurateur: IRestaurateur): void {
    this.restaurateur = restaurateur;
    this.restaurateurFormService.resetForm(this.editForm, restaurateur);

    this.commandesSharedCollection = this.commandeService.addCommandeToCollectionIfMissing<ICommande>(
      this.commandesSharedCollection,
      restaurateur.commande
    );
    this.societairesSharedCollection = this.societaireService.addSocietaireToCollectionIfMissing<ISocietaire>(
      this.societairesSharedCollection,
      restaurateur.societaire
    );
  }

  protected loadRelationshipsOptions(): void {
    this.commandeService
      .query()
      .pipe(map((res: HttpResponse<ICommande[]>) => res.body ?? []))
      .pipe(
        map((commandes: ICommande[]) =>
          this.commandeService.addCommandeToCollectionIfMissing<ICommande>(commandes, this.restaurateur?.commande)
        )
      )
      .subscribe((commandes: ICommande[]) => (this.commandesSharedCollection = commandes));

    this.societaireService
      .query()
      .pipe(map((res: HttpResponse<ISocietaire[]>) => res.body ?? []))
      .pipe(
        map((societaires: ISocietaire[]) =>
          this.societaireService.addSocietaireToCollectionIfMissing<ISocietaire>(societaires, this.restaurateur?.societaire)
        )
      )
      .subscribe((societaires: ISocietaire[]) => (this.societairesSharedCollection = societaires));
  }
}
