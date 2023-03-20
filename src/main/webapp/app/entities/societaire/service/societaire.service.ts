import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISocietaire, NewSocietaire } from '../societaire.model';

export type PartialUpdateSocietaire = Partial<ISocietaire> & Pick<ISocietaire, 'id'>;

export type EntityResponseType = HttpResponse<ISocietaire>;
export type EntityArrayResponseType = HttpResponse<ISocietaire[]>;

@Injectable({ providedIn: 'root' })
export class SocietaireService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/societaires');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(societaire: NewSocietaire): Observable<EntityResponseType> {
    return this.http.post<ISocietaire>(this.resourceUrl, societaire, { observe: 'response' });
  }

  update(societaire: ISocietaire): Observable<EntityResponseType> {
    return this.http.put<ISocietaire>(`${this.resourceUrl}/${this.getSocietaireIdentifier(societaire)}`, societaire, {
      observe: 'response',
    });
  }

  partialUpdate(societaire: PartialUpdateSocietaire): Observable<EntityResponseType> {
    return this.http.patch<ISocietaire>(`${this.resourceUrl}/${this.getSocietaireIdentifier(societaire)}`, societaire, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISocietaire>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISocietaire[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSocietaireIdentifier(societaire: Pick<ISocietaire, 'id'>): number {
    return societaire.id;
  }

  compareSocietaire(o1: Pick<ISocietaire, 'id'> | null, o2: Pick<ISocietaire, 'id'> | null): boolean {
    return o1 && o2 ? this.getSocietaireIdentifier(o1) === this.getSocietaireIdentifier(o2) : o1 === o2;
  }

  addSocietaireToCollectionIfMissing<Type extends Pick<ISocietaire, 'id'>>(
    societaireCollection: Type[],
    ...societairesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const societaires: Type[] = societairesToCheck.filter(isPresent);
    if (societaires.length > 0) {
      const societaireCollectionIdentifiers = societaireCollection.map(societaireItem => this.getSocietaireIdentifier(societaireItem)!);
      const societairesToAdd = societaires.filter(societaireItem => {
        const societaireIdentifier = this.getSocietaireIdentifier(societaireItem);
        if (societaireCollectionIdentifiers.includes(societaireIdentifier)) {
          return false;
        }
        societaireCollectionIdentifiers.push(societaireIdentifier);
        return true;
      });
      return [...societairesToAdd, ...societaireCollection];
    }
    return societaireCollection;
  }
}
