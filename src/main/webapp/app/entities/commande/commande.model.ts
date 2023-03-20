import dayjs from 'dayjs/esm';
import { IPanier } from 'app/entities/panier/panier.model';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { CommandeStatus } from 'app/entities/enumerations/commande-status.model';

export interface ICommande {
  id: number;
  dateTime?: dayjs.Dayjs | null;
  status?: CommandeStatus | null;
  panier?: Pick<IPanier, 'id'> | null;
  paiement?: Pick<IPaiement, 'id'> | null;
}

export type NewCommande = Omit<ICommande, 'id'> & { id: null };
