import { IRestaurateur } from 'app/entities/restaurateur/restaurateur.model';

export interface IRestaurant {
  id: number;
  name?: string | null;
  address?: string | null;
  menu?: string | null;
  restaurateur?: Pick<IRestaurateur, 'id'> | null;
}

export type NewRestaurant = Omit<IRestaurant, 'id'> & { id: null };
