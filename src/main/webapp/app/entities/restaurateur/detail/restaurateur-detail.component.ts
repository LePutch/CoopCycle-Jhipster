import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRestaurateur } from '../restaurateur.model';

@Component({
  selector: 'jhi-restaurateur-detail',
  templateUrl: './restaurateur-detail.component.html',
})
export class RestaurateurDetailComponent implements OnInit {
  restaurateur: IRestaurateur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurateur }) => {
      this.restaurateur = restaurateur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
