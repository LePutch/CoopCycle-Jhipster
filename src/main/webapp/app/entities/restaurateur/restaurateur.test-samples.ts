import { IRestaurateur, NewRestaurateur } from './restaurateur.model';

export const sampleWithRequiredData: IRestaurateur = {
  id: 33381,
  firstName: 'Joey',
  lastName: 'Cartwright',
};

export const sampleWithPartialData: IRestaurateur = {
  id: 5061,
  firstName: 'Moises',
  lastName: 'Hudson',
};

export const sampleWithFullData: IRestaurateur = {
  id: 80981,
  firstName: 'Devyn',
  lastName: 'Dickens',
};

export const sampleWithNewData: NewRestaurateur = {
  firstName: 'Antwon',
  lastName: 'Olson',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
