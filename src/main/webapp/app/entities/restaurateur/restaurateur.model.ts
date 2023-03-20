import { ICommande } from 'app/entities/commande/commande.model';
import { ISocietaire } from 'app/entities/societaire/societaire.model';

export interface IRestaurateur {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  commande?: Pick<ICommande, 'id'> | null;
  societaire?: Pick<ISocietaire, 'id'> | null;
}

export type NewRestaurateur = Omit<IRestaurateur, 'id'> & { id: null };
