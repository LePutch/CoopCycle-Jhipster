import { SocietaireType } from 'app/entities/enumerations/societaire-type.model';

import { ISocietaire, NewSocietaire } from './societaire.model';

export const sampleWithRequiredData: ISocietaire = {
  id: 74740,
  firstName: 'Rudolph',
  lastName: 'Leffler',
  type: SocietaireType['CLIENT'],
};

export const sampleWithPartialData: ISocietaire = {
  id: 71440,
  firstName: 'Linnea',
  lastName: 'Littel',
  type: SocietaireType['LIVREUR'],
};

export const sampleWithFullData: ISocietaire = {
  id: 312,
  firstName: 'Letha',
  lastName: 'Schumm',
  type: SocietaireType['RESTAURATEUR'],
};

export const sampleWithNewData: NewSocietaire = {
  firstName: 'Dexter',
  lastName: 'Bashirian',
  type: SocietaireType['RESTAURATEUR'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
