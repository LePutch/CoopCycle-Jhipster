import { PaymentType } from 'app/entities/enumerations/payment-type.model';

import { IPaiement, NewPaiement } from './paiement.model';

export const sampleWithRequiredData: IPaiement = {
  id: 69996,
  amount: 52151,
  paymentType: PaymentType['GOOGLE_PAY'],
};

export const sampleWithPartialData: IPaiement = {
  id: 34485,
  amount: 80929,
  paymentType: PaymentType['CHEQUE_REPAS'],
};

export const sampleWithFullData: IPaiement = {
  id: 97812,
  amount: 4235,
  paymentType: PaymentType['IZLY'],
};

export const sampleWithNewData: NewPaiement = {
  amount: 15271,
  paymentType: PaymentType['CHEQUE_REPAS'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
