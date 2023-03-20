import { SocietaireType } from 'app/entities/enumerations/societaire-type.model';

export interface ISocietaire {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  type?: SocietaireType | null;
}

export type NewSocietaire = Omit<ISocietaire, 'id'> & { id: null };
