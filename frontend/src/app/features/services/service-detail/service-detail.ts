import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
  selector: 'app-service-detail',
  imports: [FormsModule, RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-4xl mx-auto p-6">
      <app-services-tabs />
      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (error() && !service()) {
        <p class="text-red-600">{{ error() }}</p>
        <a routerLink="/services" class="text-blue-600">← Retour au catalogue</a>
      } @else if (service(); as s) {
        <div class="bg-white rounded-xl shadow overflow-hidden">
          @if (s.imageUrl) {
            <img [src]="s.imageUrl" [alt]="s.name" class="h-64 w-full object-cover bg-slate-100" />
          }
          <div class="p-6 space-y-5">
            <div class="flex flex-wrap items-start justify-between gap-3">
              <div>
                <h1 class="text-3xl font-bold">{{ s.name }}</h1>
                <p class="text-slate-500 mt-1">par {{ s.providerName || 'Prestataire' }}</p>
              </div>
              <div class="text-right">
                <div class="text-2xl font-bold">{{ s.price != null ? s.price + ' TND' : '—' }}</div>
                @if (s.status === 'PAUSED') {
                  <span class="inline-block mt-1 text-xs bg-amber-100 text-amber-800 rounded-full px-2 py-0.5">En pause</span>
                }
              </div>
            </div>

            @if (s.description) { <p class="text-slate-700 whitespace-pre-line">{{ s.description }}</p> }

            <dl class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
              <div><dt class="text-slate-500">Catégorie</dt><dd class="font-medium">{{ s.category || 'Général' }}</dd></div>
              <div><dt class="text-slate-500">Durée</dt><dd class="font-medium">{{ duration(s.durationMinutes) }}</dd></div>
              <div><dt class="text-slate-500">Modalité</dt><dd class="font-medium">{{ s.mode ? modeLabel[s.mode] : '—' }}</dd></div>
              <div><dt class="text-slate-500">Lieu</dt><dd class="font-medium">{{ s.location || '—' }}</dd></div>
              <div><dt class="text-slate-500">Réservations</dt><dd class="font-medium">{{ s.bookingCount }}</dd></div>
            </dl>

            @if (error()) { <p class="text-red-600 text-sm">{{ error() }}</p> }

            @if (isOwner()) {
              <!-- Côté prestataire -->
              <div class="border-t pt-5 flex flex-wrap gap-3 items-center">
                <a [routerLink]="['/services', s.id, 'edit']" class="bg-slate-900 text-white px-4 py-2 rounded hover:bg-slate-700">Modifier</a>
                <button (click)="togglePause()" [disabled]="busy()" class="bg-slate-100 px-4 py-2 rounded hover:bg-slate-200 disabled:opacity-50">
                  {{ s.status === 'PAUSED' ? 'Réactiver' : 'Mettre en pause' }}
                </button>
                <a routerLink="/services/received" class="text-blue-600 hover:underline">Voir les réservations reçues</a>
                <button (click)="remove()" [disabled]="busy()" class="ml-auto text-red-600 hover:underline disabled:opacity-50">Supprimer</button>
              </div>
            } @else if (!auth.isAuthenticated()) {
              <div class="border-t pt-5">
                <a routerLink="/login" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Se connecter pour réserver</a>
              </div>
            } @else if (s.status === 'ACTIVE') {
              <!-- Côté client : formulaire de réservation -->
              <form (ngSubmit)="book()" class="border-t pt-5 space-y-3">
                <h2 class="text-lg font-semibold">Réserver ce service</h2>
                <div class="grid gap-3 md:grid-cols-[1fr_2fr]">
                  <label class="text-sm">
                    <span class="block text-slate-600 mb-1">Date et heure souhaitées *</span>
                    <input type="datetime-local" [(ngModel)]="scheduledAt" name="scheduledAt" [min]="minDate" required
                           class="w-full border rounded px-3 py-2" />
                  </label>
                  <label class="text-sm">
                    <span class="block text-slate-600 mb-1">Message au prestataire</span>
                    <input [(ngModel)]="note" name="note" maxlength="1000" placeholder="Précisions, adresse, besoin..."
                           class="w-full border rounded px-3 py-2" />
                  </label>
                </div>
                <div class="flex items-center gap-3">
                  <button type="submit" [disabled]="busy()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 disabled:opacity-50">
                    {{ busy() ? 'Envoi...' : 'Envoyer la demande' }}
                  </button>
                  @if (booked()) {
                    <span class="text-green-700 text-sm">Demande envoyée ! Suivez-la dans
                      <a routerLink="/services/bookings" class="underline">Mes réservations</a>.</span>
                  }
                </div>
              </form>
            }
          </div>
        </div>
        <a routerLink="/services" class="inline-block mt-4 text-blue-600">← Retour au catalogue</a>
      }
    </div>
  `,
})
export class ServiceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private api = inject(ServiceOfferService);
  auth = inject(AuthService);

  readonly modeLabel = SERVICE_MODE_LABEL;
  readonly duration = formatDuration;
  /** Pas de date passée dans le sélecteur (format datetime-local). */
  readonly minDate = new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16);

  service = signal<ServiceOffer | null>(null);
  loading = signal(true);
  busy = signal(false);
  error = signal<string | null>(null);
  booked = signal(false);
  isOwner = computed(() => {
    const s = this.service();
    return !!s && !!this.auth.userId && s.providerId === this.auth.userId;
  });

  scheduledAt = '';
  note = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error.set('Identifiant de service invalide');
      this.loading.set(false);
      return;
    }
    this.api.get(id).subscribe({
      next: (s) => {
        this.service.set(s);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Impossible de charger ce service'));
        this.loading.set(false);
      },
    });
  }

  book(): void {
    const s = this.service();
    if (!s) return;
    if (!this.scheduledAt) {
      this.error.set('Choisissez une date et une heure');
      return;
    }
    this.busy.set(true);
    this.error.set(null);
    this.api.book(s.id, this.scheduledAt, this.note || undefined).subscribe({
      next: () => {
        this.busy.set(false);
        this.booked.set(true);
        this.scheduledAt = '';
        this.note = '';
        this.service.set({ ...s, bookingCount: s.bookingCount + 1 });
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(apiErrorMessage(e, 'La réservation a échoué'));
      },
    });
  }

  togglePause(): void {
    const s = this.service();
    if (!s) return;
    this.busy.set(true);
    this.error.set(null);
    this.api
      .update(s.id, {
        name: s.name,
        description: s.description,
        category: s.category,
        price: s.price ?? 0,
        durationMinutes: s.durationMinutes,
        mode: s.mode,
        location: s.location,
        imageUrl: s.imageUrl,
        status: s.status === 'PAUSED' ? 'ACTIVE' : 'PAUSED',
      })
      .subscribe({
        next: (updated) => {
          this.service.set(updated);
          this.busy.set(false);
        },
        error: (e) => {
          this.error.set(apiErrorMessage(e, 'Mise à jour impossible'));
          this.busy.set(false);
        },
      });
  }

  remove(): void {
    const s = this.service();
    if (!s || !confirm(`Supprimer « ${s.name} » ? Cette action est définitive.`)) return;
    this.busy.set(true);
    this.api.delete(s.id).subscribe({
      next: () => this.router.navigate(['/services/mine']),
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Suppression impossible'));
        this.busy.set(false);
      },
    });
  }
}
