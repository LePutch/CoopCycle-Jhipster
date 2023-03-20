import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SocietaireComponent } from './list/societaire.component';
import { SocietaireDetailComponent } from './detail/societaire-detail.component';
import { SocietaireUpdateComponent } from './update/societaire-update.component';
import { SocietaireDeleteDialogComponent } from './delete/societaire-delete-dialog.component';
import { SocietaireRoutingModule } from './route/societaire-routing.module';

@NgModule({
  imports: [SharedModule, SocietaireRoutingModule],
  declarations: [SocietaireComponent, SocietaireDetailComponent, SocietaireUpdateComponent, SocietaireDeleteDialogComponent],
})
export class SocietaireModule {}
