import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRestaurateur } from '../restaurateur.model';
import { RestaurateurService } from '../service/restaurateur.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './restaurateur-delete-dialog.component.html',
})
export class RestaurateurDeleteDialogComponent {
  restaurateur?: IRestaurateur;

  constructor(protected restaurateurService: RestaurateurService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.restaurateurService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
