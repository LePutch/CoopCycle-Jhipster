import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISocietaire } from '../societaire.model';
import { SocietaireService } from '../service/societaire.service';

@Injectable({ providedIn: 'root' })
export class SocietaireRoutingResolveService implements Resolve<ISocietaire | null> {
  constructor(protected service: SocietaireService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISocietaire | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((societaire: HttpResponse<ISocietaire>) => {
          if (societaire.body) {
            return of(societaire.body);
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
