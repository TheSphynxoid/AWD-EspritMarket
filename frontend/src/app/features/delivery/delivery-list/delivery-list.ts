import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

/** Page d'accueil livraison : seulement l'accès à la création (la liste est dans admin-delivery). */
@Component({
  selector: 'app-delivery-list',
  imports: [RouterLink],
  template: `
    <div class="max-w-6xl mx-auto p-6 flex justify-center pt-24">
      <a routerLink="/delivery/new" class="bg-blue-600 text-white text-lg px-6 py-3 rounded-lg shadow hover:bg-blue-500">
        + Nouvelle livraison
      </a>
    </div>
  `,
})
export class DeliveryListComponent {}
