import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SocietaireComponent } from '../list/societaire.component';
import { SocietaireDetailComponent } from '../detail/societaire-detail.component';
import { SocietaireUpdateComponent } from '../update/societaire-update.component';
import { SocietaireRoutingResolveService } from './societaire-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const societaireRoute: Routes = [
  {
    path: '',
    component: SocietaireComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SocietaireDetailComponent,
    resolve: {
      societaire: SocietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SocietaireUpdateComponent,
    resolve: {
      societaire: SocietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SocietaireUpdateComponent,
    resolve: {
      societaire: SocietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(societaireRoute)],
  exports: [RouterModule],
})
export class SocietaireRoutingModule {}
