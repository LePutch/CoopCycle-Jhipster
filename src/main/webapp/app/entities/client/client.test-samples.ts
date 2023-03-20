import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 71655,
  firstName: 'Esta',
  lastName: 'Stamm',
  email: "J@m'MUur.6",
  phone: '+205   (12)  598    7207',
  address: 'index Facilitator',
};

export const sampleWithPartialData: IClient = {
  id: 47029,
  firstName: 'Fae',
  lastName: 'Morar',
  email: 'H@(.k!vK0',
  phone: ' 732 290     79  17',
  address: 'Croatia Upgradable SSL',
};

export const sampleWithFullData: IClient = {
  id: 95629,
  firstName: 'Weston',
  lastName: 'Schulist',
  email: '<d.-+6@P.R52J',
  phone: '+9(6)   590   2127',
  address: 'Account schemas Configuration',
};

export const sampleWithNewData: NewClient = {
  firstName: 'Gennaro',
  lastName: 'Mills',
  email: '`gM%@MQQ^I.gzBu',
  phone: '+282  (6)  681 76    12',
  address: 'invoice',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
