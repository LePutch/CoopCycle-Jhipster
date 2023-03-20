import { PaymentType } from 'app/entities/enumerations/payment-type.model';

export interface IPaiement {
  id: number;
  amount?: number | null;
  paymentType?: PaymentType | null;
}

export type NewPaiement = Omit<IPaiement, 'id'> & { id: null };
