import { ICommande } from 'app/entities/commande/commande.model';

export interface IClient {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  commande?: Pick<ICommande, 'id'> | null;
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
