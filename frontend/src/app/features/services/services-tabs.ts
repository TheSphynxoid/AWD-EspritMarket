import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/** Onglets de l'espace Services (catalogue / côté prestataire / côté client). */
@Component({
  selector: 'app-services-tabs',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="flex flex-wrap gap-2 mb-6 text-sm">
      <a routerLink="/services" [routerLinkActiveOptions]="{ exact: true }" routerLinkActive="!bg-slate-900 !text-white"
         class="px-3 py-1.5 rounded-full bg-slate-100 text-slate-700 hover:bg-slate-200">Catalogue</a>
      @if (auth.isAuthenticated()) {
        <a routerLink="/services/bookings" routerLinkActive="!bg-slate-900 !text-white"
           class="px-3 py-1.5 rounded-full bg-slate-100 text-slate-700 hover:bg-slate-200">Mes réservations</a>
        <a routerLink="/services/mine" routerLinkActive="!bg-slate-900 !text-white"
           class="px-3 py-1.5 rounded-full bg-slate-100 text-slate-700 hover:bg-slate-200">Mes services</a>
        <a routerLink="/services/received" routerLinkActive="!bg-slate-900 !text-white"
           class="px-3 py-1.5 rounded-full bg-slate-100 text-slate-700 hover:bg-slate-200">Réservations reçues</a>
      }
    </nav>
  `,
})
export class ServicesTabsComponent {
  auth = inject(AuthService);
}
