import { IPanier, NewPanier } from './panier.model';

export const sampleWithRequiredData: IPanier = {
  id: 13332,
  price: 65813,
};

export const sampleWithPartialData: IPanier = {
  id: 70759,
  price: 76442,
};

export const sampleWithFullData: IPanier = {
  id: 33071,
  description: 'intranet',
  price: 86498,
};

export const sampleWithNewData: NewPanier = {
  price: 25528,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
