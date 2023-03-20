import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CommandeFormService, CommandeFormGroup } from './commande-form.service';
import { ICommande } from '../commande.model';
import { CommandeService } from '../service/commande.service';
import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';
import { CommandeStatus } from 'app/entities/enumerations/commande-status.model';

@Component({
  selector: 'jhi-commande-update',
  templateUrl: './commande-update.component.html',
})
export class CommandeUpdateComponent implements OnInit {
  isSaving = false;
  commande: ICommande | null = null;
  commandeStatusValues = Object.keys(CommandeStatus);

  paniersSharedCollection: IPanier[] = [];
  paiementsSharedCollection: IPaiement[] = [];

  editForm: CommandeFormGroup = this.commandeFormService.createCommandeFormGroup();

  constructor(
    protected commandeService: CommandeService,
    protected commandeFormService: CommandeFormService,
    protected panierService: PanierService,
    protected paiementService: PaiementService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePanier = (o1: IPanier | null, o2: IPanier | null): boolean => this.panierService.comparePanier(o1, o2);

  comparePaiement = (o1: IPaiement | null, o2: IPaiement | null): boolean => this.paiementService.comparePaiement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commande }) => {
      this.commande = commande;
      if (commande) {
        this.updateForm(commande);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commande = this.commandeFormService.getCommande(this.editForm);
    if (commande.id !== null) {
      this.subscribeToSaveResponse(this.commandeService.update(commande));
    } else {
      this.subscribeToSaveResponse(this.commandeService.create(commande));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommande>>): void {
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

  protected updateForm(commande: ICommande): void {
    this.commande = commande;
    this.commandeFormService.resetForm(this.editForm, commande);

    this.paniersSharedCollection = this.panierService.addPanierToCollectionIfMissing<IPanier>(
      this.paniersSharedCollection,
      commande.panier
    );
    this.paiementsSharedCollection = this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(
      this.paiementsSharedCollection,
      commande.paiement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.panierService
      .query()
      .pipe(map((res: HttpResponse<IPanier[]>) => res.body ?? []))
      .pipe(map((paniers: IPanier[]) => this.panierService.addPanierToCollectionIfMissing<IPanier>(paniers, this.commande?.panier)))
      .subscribe((paniers: IPanier[]) => (this.paniersSharedCollection = paniers));

    this.paiementService
      .query()
      .pipe(map((res: HttpResponse<IPaiement[]>) => res.body ?? []))
      .pipe(
        map((paiements: IPaiement[]) =>
          this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(paiements, this.commande?.paiement)
        )
      )
      .subscribe((paiements: IPaiement[]) => (this.paiementsSharedCollection = paiements));
  }
}
