import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SocietaireFormService, SocietaireFormGroup } from './societaire-form.service';
import { ISocietaire } from '../societaire.model';
import { SocietaireService } from '../service/societaire.service';
import { SocietaireType } from 'app/entities/enumerations/societaire-type.model';

@Component({
  selector: 'jhi-societaire-update',
  templateUrl: './societaire-update.component.html',
})
export class SocietaireUpdateComponent implements OnInit {
  isSaving = false;
  societaire: ISocietaire | null = null;
  societaireTypeValues = Object.keys(SocietaireType);

  editForm: SocietaireFormGroup = this.societaireFormService.createSocietaireFormGroup();

  constructor(
    protected societaireService: SocietaireService,
    protected societaireFormService: SocietaireFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ societaire }) => {
      this.societaire = societaire;
      if (societaire) {
        this.updateForm(societaire);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const societaire = this.societaireFormService.getSocietaire(this.editForm);
    if (societaire.id !== null) {
      this.subscribeToSaveResponse(this.societaireService.update(societaire));
    } else {
      this.subscribeToSaveResponse(this.societaireService.create(societaire));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISocietaire>>): void {
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

  protected updateForm(societaire: ISocietaire): void {
    this.societaire = societaire;
    this.societaireFormService.resetForm(this.editForm, societaire);
  }
}
