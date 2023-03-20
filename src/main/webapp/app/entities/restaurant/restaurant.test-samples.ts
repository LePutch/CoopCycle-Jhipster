import { IRestaurant, NewRestaurant } from './restaurant.model';

export const sampleWithRequiredData: IRestaurant = {
  id: 59197,
  name: 'zero Human',
  address: 'Future-proofed Rustic Director',
};

export const sampleWithPartialData: IRestaurant = {
  id: 78272,
  name: 'Incredible',
  address: 'Borders',
  menu: 'withdrawal auxiliary Personal',
};

export const sampleWithFullData: IRestaurant = {
  id: 3016,
  name: 'Jersey',
  address: 'Islands',
  menu: 'Fantastic compressing hard',
};

export const sampleWithNewData: NewRestaurant = {
  name: 'Solutions',
  address: 'scalable hardware back',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
