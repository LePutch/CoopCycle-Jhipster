import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPanier } from '../panier.model';
import { PanierService } from '../service/panier.service';

@Injectable({ providedIn: 'root' })
export class PanierRoutingResolveService implements Resolve<IPanier | null> {
  constructor(protected service: PanierService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPanier | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((panier: HttpResponse<IPanier>) => {
          if (panier.body) {
            return of(panier.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
