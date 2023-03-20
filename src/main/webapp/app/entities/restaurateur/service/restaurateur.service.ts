import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRestaurateur, NewRestaurateur } from '../restaurateur.model';

export type PartialUpdateRestaurateur = Partial<IRestaurateur> & Pick<IRestaurateur, 'id'>;

export type EntityResponseType = HttpResponse<IRestaurateur>;
export type EntityArrayResponseType = HttpResponse<IRestaurateur[]>;

@Injectable({ providedIn: 'root' })
export class RestaurateurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurateurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(restaurateur: NewRestaurateur): Observable<EntityResponseType> {
    return this.http.post<IRestaurateur>(this.resourceUrl, restaurateur, { observe: 'response' });
  }

  update(restaurateur: IRestaurateur): Observable<EntityResponseType> {
    return this.http.put<IRestaurateur>(`${this.resourceUrl}/${this.getRestaurateurIdentifier(restaurateur)}`, restaurateur, {
      observe: 'response',
    });
  }

  partialUpdate(restaurateur: PartialUpdateRestaurateur): Observable<EntityResponseType> {
    return this.http.patch<IRestaurateur>(`${this.resourceUrl}/${this.getRestaurateurIdentifier(restaurateur)}`, restaurateur, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRestaurateur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurateur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRestaurateurIdentifier(restaurateur: Pick<IRestaurateur, 'id'>): number {
    return restaurateur.id;
  }

  compareRestaurateur(o1: Pick<IRestaurateur, 'id'> | null, o2: Pick<IRestaurateur, 'id'> | null): boolean {
    return o1 && o2 ? this.getRestaurateurIdentifier(o1) === this.getRestaurateurIdentifier(o2) : o1 === o2;
  }

  addRestaurateurToCollectionIfMissing<Type extends Pick<IRestaurateur, 'id'>>(
    restaurateurCollection: Type[],
    ...restaurateursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const restaurateurs: Type[] = restaurateursToCheck.filter(isPresent);
    if (restaurateurs.length > 0) {
      const restaurateurCollectionIdentifiers = restaurateurCollection.map(
        restaurateurItem => this.getRestaurateurIdentifier(restaurateurItem)!
      );
      const restaurateursToAdd = restaurateurs.filter(restaurateurItem => {
        const restaurateurIdentifier = this.getRestaurateurIdentifier(restaurateurItem);
        if (restaurateurCollectionIdentifiers.includes(restaurateurIdentifier)) {
          return false;
        }
        restaurateurCollectionIdentifiers.push(restaurateurIdentifier);
        return true;
      });
      return [...restaurateursToAdd, ...restaurateurCollection];
    }
    return restaurateurCollection;
  }
}
