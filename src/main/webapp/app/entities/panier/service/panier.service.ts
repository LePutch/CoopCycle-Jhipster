import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPanier, NewPanier } from '../panier.model';

export type PartialUpdatePanier = Partial<IPanier> & Pick<IPanier, 'id'>;

export type EntityResponseType = HttpResponse<IPanier>;
export type EntityArrayResponseType = HttpResponse<IPanier[]>;

@Injectable({ providedIn: 'root' })
export class PanierService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/paniers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(panier: NewPanier): Observable<EntityResponseType> {
    return this.http.post<IPanier>(this.resourceUrl, panier, { observe: 'response' });
  }

  update(panier: IPanier): Observable<EntityResponseType> {
    return this.http.put<IPanier>(`${this.resourceUrl}/${this.getPanierIdentifier(panier)}`, panier, { observe: 'response' });
  }

  partialUpdate(panier: PartialUpdatePanier): Observable<EntityResponseType> {
    return this.http.patch<IPanier>(`${this.resourceUrl}/${this.getPanierIdentifier(panier)}`, panier, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPanier>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPanier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPanierIdentifier(panier: Pick<IPanier, 'id'>): number {
    return panier.id;
  }

  comparePanier(o1: Pick<IPanier, 'id'> | null, o2: Pick<IPanier, 'id'> | null): boolean {
    return o1 && o2 ? this.getPanierIdentifier(o1) === this.getPanierIdentifier(o2) : o1 === o2;
  }

  addPanierToCollectionIfMissing<Type extends Pick<IPanier, 'id'>>(
    panierCollection: Type[],
    ...paniersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paniers: Type[] = paniersToCheck.filter(isPresent);
    if (paniers.length > 0) {
      const panierCollectionIdentifiers = panierCollection.map(panierItem => this.getPanierIdentifier(panierItem)!);
      const paniersToAdd = paniers.filter(panierItem => {
        const panierIdentifier = this.getPanierIdentifier(panierItem);
        if (panierCollectionIdentifiers.includes(panierIdentifier)) {
          return false;
        }
        panierCollectionIdentifiers.push(panierIdentifier);
        return true;
      });
      return [...paniersToAdd, ...panierCollection];
    }
    return panierCollection;
  }
}
