import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, debounceTime } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import {
  SERVICE_MODE_LABEL,
  ServiceOffer,
  ServiceOfferService,
  apiErrorMessage,
  formatDuration,
} from '../../../core/services/service-offer.service';
import { ServicesTabsComponent } from '../services-tabs';

@Component({
  selector: 'app-service-list',
  imports: [FormsModule, RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-6xl mx-auto p-6">
      <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
        <h1 class="text-3xl font-bold">Services</h1>
        <a routerLink="/services/new" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Proposer un service</a>
      </div>
      <app-services-tabs />

      <!-- Filtres -->
      <div class="bg-white rounded-xl shadow p-4 mb-6 grid gap-3 md:grid-cols-[2fr_1fr_1fr_1fr_auto] items-end">
        <label class="text-sm">
          <span class="block text-slate-600 mb-1">Recherche</span>
          <input [(ngModel)]="q" (ngModelChange)="search$.next()" placeholder="Nom, description, prestataire..."
                 class="w-full border rounded px-3 py-2" />
        </label>
        <label class="text-sm">
          <span class="block text-slate-600 mb-1">Catégorie</span>
          <select [(ngModel)]="category" (ngModelChange)="load()" class="w-full border rounded px-3 py-2 bg-white">
            <option value="">Toutes</option>
            @for (c of categories(); track c) { <option [value]="c">{{ c }}</option> }
          </select>
        </label>
        <label class="text-sm">
          <span class="block text-slate-600 mb-1">Prix min (TND)</span>
          <input type="number" min="0" [(ngModel)]="minPrice" (ngModelChange)="search$.next()" class="w-full border rounded px-3 py-2" />
        </label>
        <label class="text-sm">
          <span class="block text-slate-600 mb-1">Prix max (TND)</span>
          <input type="number" min="0" [(ngModel)]="maxPrice" (ngModelChange)="search$.next()" class="w-full border rounded px-3 py-2" />
        </label>
        <button (click)="reset()" class="text-sm text-slate-600 hover:text-slate-900 px-3 py-2">Réinitialiser</button>
      </div>

      @if (loading()) {
        <p class="text-slate-500">Chargement des services...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
      } @else if (services().length === 0) {
        <p class="text-slate-500">{{ filtered() ? 'Aucun service ne correspond à ces critères.' : 'Aucun service pour le moment.' }}</p>
      } @else {
        <p class="text-sm text-slate-500 mb-3">{{ services().length }} service(s)</p>
        <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          @for (s of services(); track s.id) {
            <a [routerLink]="['/services', s.id]" class="block bg-white rounded-xl shadow hover:shadow-lg transition overflow-hidden">
              @if (s.imageUrl) {
                <img [src]="s.imageUrl" [alt]="s.name" class="h-40 w-full object-cover bg-slate-100" loading="lazy" />
              } @else {
                <div class="h-40 w-full bg-gradient-to-br from-slate-200 to-slate-100 flex items-center justify-center text-4xl">🛠️</div>
              }
              <div class="p-5">
                <div class="flex items-start justify-between gap-2">
                  <h2 class="text-lg font-semibold">{{ s.name }}</h2>
                  @if (s.providerId === myId()) {
                    <span class="text-xs bg-slate-900 text-white rounded-full px-2 py-0.5 shrink-0">Mon service</span>
                  }
                </div>
                <p class="text-slate-600 text-sm mt-1 line-clamp-2">{{ s.description }}</p>
                <div class="mt-3 flex flex-wrap gap-2 text-xs text-slate-600">
                  <span class="bg-blue-50 text-blue-700 rounded-full px-2 py-0.5">{{ s.category || 'Général' }}</span>
                  @if (s.durationMinutes) { <span class="bg-slate-100 rounded-full px-2 py-0.5">⏱ {{ duration(s.durationMinutes) }}</span> }
                  @if (s.mode) { <span class="bg-slate-100 rounded-full px-2 py-0.5">{{ modeLabel[s.mode] }}</span> }
                  @if (s.location) { <span class="bg-slate-100 rounded-full px-2 py-0.5">📍 {{ s.location }}</span> }
                </div>
                <div class="mt-4 flex items-center justify-between text-sm">
                  <span class="text-slate-500">{{ s.providerName || 'Prestataire' }} · {{ s.bookingCount }} réservation(s)</span>
                  <span class="font-bold">{{ s.price != null ? s.price + ' TND' : '—' }}</span>
                </div>
              </div>
            </a>
          }
        </div>
      }
    </div>
  `,
})
export class ServiceListComponent implements OnInit {
  private api = inject(ServiceOfferService);
  private auth = inject(AuthService);

  readonly modeLabel = SERVICE_MODE_LABEL;
  readonly duration = formatDuration;
  readonly search$ = new Subject<void>();

  services = signal<ServiceOffer[]>([]);
  categories = signal<string[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  myId = signal<string | null>(null);

  q = '';
  category = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;

  ngOnInit(): void {
    this.myId.set(this.auth.userId);
    this.search$.pipe(debounceTime(300)).subscribe(() => this.load());
    this.api.categories().subscribe({ next: (c) => this.categories.set(c ?? []), error: () => {} });
    this.load();
  }

  filtered(): boolean {
    return !!(this.q.trim() || this.category || this.minPrice != null || this.maxPrice != null);
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api
      .list({ q: this.q, category: this.category, minPrice: this.minPrice, maxPrice: this.maxPrice })
      .subscribe({
        next: (data) => {
          this.services.set(data ?? []);
          this.loading.set(false);
        },
        error: (e) => {
          this.error.set(apiErrorMessage(e, 'Impossible de charger les services'));
          this.loading.set(false);
        },
      });
  }

  reset(): void {
    this.q = '';
    this.category = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.load();
  }
}
