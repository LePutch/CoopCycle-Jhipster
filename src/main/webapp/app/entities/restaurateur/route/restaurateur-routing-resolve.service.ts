import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurateur } from '../restaurateur.model';
import { RestaurateurService } from '../service/restaurateur.service';

@Injectable({ providedIn: 'root' })
export class RestaurateurRoutingResolveService implements Resolve<IRestaurateur | null> {
  constructor(protected service: RestaurateurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurateur | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((restaurateur: HttpResponse<IRestaurateur>) => {
          if (restaurateur.body) {
            return of(restaurateur.body);
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
