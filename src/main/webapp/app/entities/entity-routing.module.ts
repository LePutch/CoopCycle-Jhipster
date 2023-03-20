import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'societaire',
        data: { pageTitle: 'coopcycleApp.societaire.home.title' },
        loadChildren: () => import('./societaire/societaire.module').then(m => m.SocietaireModule),
      },
      {
        path: 'client',
        data: { pageTitle: 'coopcycleApp.client.home.title' },
        loadChildren: () => import('./client/client.module').then(m => m.ClientModule),
      },
      {
        path: 'restaurateur',
        data: { pageTitle: 'coopcycleApp.restaurateur.home.title' },
        loadChildren: () => import('./restaurateur/restaurateur.module').then(m => m.RestaurateurModule),
      },
      {
        path: 'restaurant',
        data: { pageTitle: 'coopcycleApp.restaurant.home.title' },
        loadChildren: () => import('./restaurant/restaurant.module').then(m => m.RestaurantModule),
      },
      {
        path: 'commande',
        data: { pageTitle: 'coopcycleApp.commande.home.title' },
        loadChildren: () => import('./commande/commande.module').then(m => m.CommandeModule),
      },
      {
        path: 'panier',
        data: { pageTitle: 'coopcycleApp.panier.home.title' },
        loadChildren: () => import('./panier/panier.module').then(m => m.PanierModule),
      },
      {
        path: 'paiement',
        data: { pageTitle: 'coopcycleApp.paiement.home.title' },
        loadChildren: () => import('./paiement/paiement.module').then(m => m.PaiementModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
