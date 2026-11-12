import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  BOOKING_STATUS_CLASS,
  BOOKING_STATUS_LABEL,
  Booking,
  ServiceOfferService,
  apiErrorMessage,
} from '../../../core/services/service-offer.service';
import { ServicesTabsComponent } from '../services-tabs';

/** Côté client : les réservations que j'ai faites, avec annulation. */
@Component({
  selector: 'app-my-bookings',
  imports: [DatePipe, RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-5xl mx-auto p-6">
      <h1 class="text-3xl font-bold mb-4">Mes réservations</h1>
      <app-services-tabs />

      @if (error()) { <p class="text-red-600 mb-4">{{ error() }}</p> }

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (bookings().length === 0) {
        <div class="bg-white rounded-xl shadow p-6 text-slate-600">
          Vous n'avez encore rien réservé. <a routerLink="/services" class="text-blue-600 underline">Parcourir le catalogue</a>
        </div>
      } @else {
        <div class="space-y-3">
          @for (b of bookings(); track b.id) {
            <div class="bg-white rounded-xl shadow p-4 flex flex-wrap gap-4 items-start">
              @if (b.serviceImageUrl) {
                <img [src]="b.serviceImageUrl" [alt]="b.serviceName" class="h-16 w-16 rounded object-cover bg-slate-100" />
              } @else {
                <div class="h-16 w-16 rounded bg-slate-100 flex items-center justify-center text-2xl">🛠️</div>
              }
              <div class="flex-1 min-w-[200px]">
                <div class="flex flex-wrap items-center gap-2">
                  <a [routerLink]="['/services', b.serviceId]" class="font-semibold hover:underline">{{ b.serviceName }}</a>
                  <span class="text-xs rounded-full px-2 py-0.5" [class]="statusClass[b.status]">{{ statusLabel[b.status] }}</span>
                </div>
                <div class="text-sm text-slate-600 mt-1">
                  📅 {{ b.scheduledAt | date: 'EEEE d MMMM y, HH:mm' : undefined : 'fr' }}
                  · {{ b.providerName || 'Prestataire' }}
                  @if (b.servicePrice != null) { · {{ b.servicePrice }} TND }
                </div>
                @if (b.note) { <p class="text-sm text-slate-500 mt-1">Votre message : {{ b.note }}</p> }
                @if (b.providerNote) {
                  <p class="text-sm mt-1 bg-slate-50 border-l-4 border-slate-300 px-2 py-1">Prestataire : {{ b.providerNote }}</p>
                }
              </div>
              @if (b.status === 'PENDING' || b.status === 'ACCEPTED') {
                <button (click)="cancel(b)" [disabled]="busyId() === b.id"
                        class="text-sm text-red-600 border border-red-200 rounded px-3 py-1.5 hover:bg-red-50 disabled:opacity-50">
                  Annuler
                </button>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class MyBookingsComponent implements OnInit {
  private api = inject(ServiceOfferService);

  readonly statusLabel = BOOKING_STATUS_LABEL;
  readonly statusClass = BOOKING_STATUS_CLASS;

  bookings = signal<Booking[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  busyId = signal<number | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.myBookings().subscribe({
      next: (b) => {
        this.bookings.set(b ?? []);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Impossible de charger vos réservations'));
        this.loading.set(false);
      },
    });
  }

  cancel(b: Booking): void {
    if (!confirm(`Annuler votre réservation « ${b.serviceName} » ?`)) return;
    this.busyId.set(b.id);
    this.error.set(null);
    this.api.changeBookingStatus(b.id, 'CANCELLED').subscribe({
      next: (updated) => {
        this.bookings.update((list) => list.map((x) => (x.id === updated.id ? updated : x)));
        this.busyId.set(null);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, "L'annulation a échoué"));
        this.busyId.set(null);
      },
    });
  }
}
