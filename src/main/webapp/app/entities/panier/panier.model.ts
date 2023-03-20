import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface IPanier {
  id: number;
  description?: string | null;
  price?: number | null;
  restaurant?: Pick<IRestaurant, 'id'> | null;
}

export type NewPanier = Omit<IPanier, 'id'> & { id: null };
