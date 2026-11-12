import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  ServiceOffer,
  ServiceOfferService,
  apiErrorMessage,
  formatDuration,
} from '../../../core/services/service-offer.service';
import { ServicesTabsComponent } from '../services-tabs';

/** Côté prestataire : mes services (y compris en pause). */
@Component({
  selector: 'app-my-services',
  imports: [RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-5xl mx-auto p-6">
      <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
        <h1 class="text-3xl font-bold">Mes services</h1>
        <a routerLink="/services/new" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Proposer un service</a>
      </div>
      <app-services-tabs />

      @if (error()) { <p class="text-red-600 mb-4">{{ error() }}</p> }

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (services().length === 0) {
        <div class="bg-white rounded-xl shadow p-6 text-slate-600">
          Vous ne proposez encore aucun service.
          <a routerLink="/services/new" class="text-blue-600 underline">Créer le premier</a>
        </div>
      } @else {
        <div class="bg-white rounded-xl shadow divide-y">
          @for (s of services(); track s.id) {
            <div class="p-4 flex flex-wrap items-center gap-4">
              <div class="flex-1 min-w-[200px]">
                <div class="flex items-center gap-2">
                  <a [routerLink]="['/services', s.id]" class="font-semibold hover:underline">{{ s.name }}</a>
                  @if (s.status === 'PAUSED') {
                    <span class="text-xs bg-amber-100 text-amber-800 rounded-full px-2 py-0.5">En pause</span>
                  } @else {
                    <span class="text-xs bg-green-100 text-green-800 rounded-full px-2 py-0.5">Actif</span>
                  }
                </div>
                <div class="text-sm text-slate-500 mt-0.5">
                  {{ s.category || 'Général' }} · {{ s.price != null ? s.price + ' TND' : '—' }} · {{ duration(s.durationMinutes) }}
                  · {{ s.bookingCount }} réservation(s)
                </div>
              </div>
              <a [routerLink]="['/services', s.id, 'edit']" class="text-sm bg-slate-100 rounded px-3 py-1.5 hover:bg-slate-200">Modifier</a>
            </div>
          }
        </div>
        <a routerLink="/services/received" class="inline-block mt-4 text-blue-600 hover:underline">Gérer les réservations reçues →</a>
      }
    </div>
  `,
})
export class MyServicesComponent implements OnInit {
  private api = inject(ServiceOfferService);
  readonly duration = formatDuration;

  services = signal<ServiceOffer[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.mine().subscribe({
      next: (s) => {
        this.services.set(s ?? []);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Impossible de charger vos services'));
        this.loading.set(false);
      },
    });
  }
}
