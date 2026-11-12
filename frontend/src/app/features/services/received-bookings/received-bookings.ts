import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  BOOKING_STATUS_CLASS,
  BOOKING_STATUS_LABEL,
  Booking,
  BookingStatus,
  ServiceOfferService,
  apiErrorMessage,
} from '../../../core/services/service-offer.service';
import { ServicesTabsComponent } from '../services-tabs';

/** Côté prestataire : réservations reçues sur mes services (accepter, refuser, terminer). */
@Component({
  selector: 'app-received-bookings',
  imports: [DatePipe, FormsModule, RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-5xl mx-auto p-6">
      <h1 class="text-3xl font-bold mb-4">Réservations reçues</h1>
      <app-services-tabs />

      <!-- Filtre par statut -->
      <div class="flex flex-wrap gap-2 mb-5 text-sm">
        @for (f of filters; track f.label) {
          <button (click)="filter.set(f.value)"
                  class="px-3 py-1 rounded border"
                  [class]="filter() === f.value ? 'bg-blue-600 text-white border-blue-600' : 'bg-white hover:bg-slate-50'">
            {{ f.label }} ({{ count(f.value) }})
          </button>
        }
      </div>

      @if (error()) { <p class="text-red-600 mb-4">{{ error() }}</p> }

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (all().length === 0) {
        <div class="bg-white rounded-xl shadow p-6 text-slate-600">
          Aucune réservation reçue pour l'instant.
          <a routerLink="/services/mine" class="text-blue-600 underline">Voir mes services</a>
        </div>
      } @else if (shown().length === 0) {
        <p class="text-slate-500">Aucune réservation dans cette catégorie.</p>
      } @else {
        <div class="space-y-3">
          @for (b of shown(); track b.id) {
            <div class="bg-white rounded-xl shadow p-4">
              <div class="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div class="flex flex-wrap items-center gap-2">
                    <a [routerLink]="['/services', b.serviceId]" class="font-semibold hover:underline">{{ b.serviceName }}</a>
                    <span class="text-xs rounded-full px-2 py-0.5" [class]="statusClass[b.status]">{{ statusLabel[b.status] }}</span>
                  </div>
                  <div class="text-sm text-slate-600 mt-1">
                    👤 {{ b.bookerName || 'Client' }} · 📅 {{ b.scheduledAt | date: 'EEEE d MMMM y, HH:mm' : undefined : 'fr' }}
                  </div>
                  <div class="text-xs text-slate-400 mt-0.5">Demandée le {{ b.createdAt | date: 'd/MM/y HH:mm' }}</div>
                  @if (b.note) { <p class="text-sm mt-2 bg-slate-50 border-l-4 border-blue-300 px-2 py-1">« {{ b.note }} »</p> }
                  @if (b.providerNote) { <p class="text-sm text-slate-500 mt-1">Votre réponse : {{ b.providerNote }}</p> }
                </div>

                @if (b.status === 'PENDING' || b.status === 'ACCEPTED') {
                  <div class="flex flex-col gap-2 w-full sm:w-72">
                    <input [(ngModel)]="notes[b.id]" [name]="'note-' + b.id" maxlength="1000"
                           placeholder="Message au client (optionnel)" class="border rounded px-2 py-1.5 text-sm" />
                    <div class="flex gap-2">
                      @if (b.status === 'PENDING') {
                        <button (click)="setStatus(b, 'ACCEPTED')" [disabled]="busyId() === b.id"
                                class="flex-1 bg-green-600 text-white text-sm rounded px-3 py-1.5 hover:bg-green-500 disabled:opacity-50">Accepter</button>
                        <button (click)="setStatus(b, 'DECLINED')" [disabled]="busyId() === b.id"
                                class="flex-1 bg-red-600 text-white text-sm rounded px-3 py-1.5 hover:bg-red-500 disabled:opacity-50">Refuser</button>
                      } @else {
                        <button (click)="setStatus(b, 'COMPLETED')" [disabled]="busyId() === b.id"
                                class="flex-1 bg-blue-600 text-white text-sm rounded px-3 py-1.5 hover:bg-blue-500 disabled:opacity-50">Marquer terminée</button>
                      }
                    </div>
                  </div>
                }
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class ReceivedBookingsComponent implements OnInit {
  private api = inject(ServiceOfferService);

  readonly statusLabel = BOOKING_STATUS_LABEL;
  readonly statusClass = BOOKING_STATUS_CLASS;
  readonly filters: { label: string; value: BookingStatus | null }[] = [
    { label: 'Toutes', value: null },
    { label: 'En attente', value: 'PENDING' },
    { label: 'Acceptées', value: 'ACCEPTED' },
    { label: 'Terminées', value: 'COMPLETED' },
    { label: 'Refusées', value: 'DECLINED' },
    { label: 'Annulées', value: 'CANCELLED' },
  ];

  all = signal<Booking[]>([]);
  filter = signal<BookingStatus | null>('PENDING');
  shown = computed(() => {
    const f = this.filter();
    return f ? this.all().filter((b) => b.status === f) : this.all();
  });
  loading = signal(true);
  error = signal<string | null>(null);
  busyId = signal<number | null>(null);
  notes: Record<number, string> = {};

  ngOnInit(): void {
    this.api.receivedBookings().subscribe({
      next: (b) => {
        this.all.set(b ?? []);
        // Rien en attente : on affiche tout
        if (!this.all().some((x) => x.status === 'PENDING')) this.filter.set(null);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Impossible de charger les réservations reçues'));
        this.loading.set(false);
      },
    });
  }

  count(status: BookingStatus | null): number {
    return status ? this.all().filter((b) => b.status === status).length : this.all().length;
  }

  setStatus(b: Booking, status: BookingStatus): void {
    if (status === 'DECLINED' && !confirm(`Refuser la réservation de ${b.bookerName || 'ce client'} ?`)) return;
    this.busyId.set(b.id);
    this.error.set(null);
    this.api.changeBookingStatus(b.id, status, this.notes[b.id] || undefined).subscribe({
      next: (updated) => {
        this.all.update((list) => list.map((x) => (x.id === updated.id ? updated : x)));
        delete this.notes[b.id];
        this.busyId.set(null);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Mise à jour impossible'));
        this.busyId.set(null);
      },
    });
  }
}
