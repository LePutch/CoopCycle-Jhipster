import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RestaurateurComponent } from './list/restaurateur.component';
import { RestaurateurDetailComponent } from './detail/restaurateur-detail.component';
import { RestaurateurUpdateComponent } from './update/restaurateur-update.component';
import { RestaurateurDeleteDialogComponent } from './delete/restaurateur-delete-dialog.component';
import { RestaurateurRoutingModule } from './route/restaurateur-routing.module';

@NgModule({
  imports: [SharedModule, RestaurateurRoutingModule],
  declarations: [RestaurateurComponent, RestaurateurDetailComponent, RestaurateurUpdateComponent, RestaurateurDeleteDialogComponent],
})
export class RestaurateurModule {}
