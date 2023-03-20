import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISocietaire } from '../societaire.model';

@Component({
  selector: 'jhi-societaire-detail',
  templateUrl: './societaire-detail.component.html',
})
export class SocietaireDetailComponent implements OnInit {
  societaire: ISocietaire | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ societaire }) => {
      this.societaire = societaire;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
