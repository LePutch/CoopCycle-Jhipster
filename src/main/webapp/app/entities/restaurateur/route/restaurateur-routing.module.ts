import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RestaurateurComponent } from '../list/restaurateur.component';
import { RestaurateurDetailComponent } from '../detail/restaurateur-detail.component';
import { RestaurateurUpdateComponent } from '../update/restaurateur-update.component';
import { RestaurateurRoutingResolveService } from './restaurateur-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const restaurateurRoute: Routes = [
  {
    path: '',
    component: RestaurateurComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RestaurateurDetailComponent,
    resolve: {
      restaurateur: RestaurateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RestaurateurUpdateComponent,
    resolve: {
      restaurateur: RestaurateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RestaurateurUpdateComponent,
    resolve: {
      restaurateur: RestaurateurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(restaurateurRoute)],
  exports: [RouterModule],
})
export class RestaurateurRoutingModule {}
