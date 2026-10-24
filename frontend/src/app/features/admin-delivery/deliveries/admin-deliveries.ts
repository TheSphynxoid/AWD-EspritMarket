import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AdminDeliveryService } from '../admin-delivery.service';
import { DeliveryService } from '../../delivery/delivery.service';
import {
  Deliverer,
  Delivery,
  DeliveryStatus,
  STATUS_CLASSES,
  STATUS_LABELS,
  TYPE_LABELS,
  apiError,
} from '../../delivery/delivery.models';

/** Toutes les livraisons, avec recherche, filtre par statut et affectation des livreurs. */
@Component({
  selector: 'app-admin-deliveries',
  imports: [FormsModule, RouterLink, DatePipe],
  template: `
    <div class="p-6">
      <h1 class="text-3xl font-bold mb-6">Livraisons</h1>

      <div class="flex flex-wrap gap-3 items-center mb-4">
        <input
          [(ngModel)]="search"
          (ngModelChange)="query.set($event)"
          placeholder="Rechercher un destinataire ou une ville..."
          class="border rounded px-3 py-2 flex-1 min-w-60 bg-white"
        />
        <div class="flex flex-wrap gap-2">
          <button (click)="filter.set(null)" [class]="chip(filter() === null)">Toutes</button>
          @for (st of statuses; track st) {
            <button (click)="filter.set(st)" [class]="chip(filter() === st)">{{ statusLabels[st] }}</button>
          }
        </div>
      </div>

      @if (actionError()) {
        <p class="text-red-700 bg-red-50 rounded p-3 mb-4">{{ actionError() }}</p>
      }

      @if (loading()) {
        <p class="text-slate-500">Chargement des livraisons...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
      } @else if (filtered().length === 0) {
        <div class="bg-white rounded-xl shadow p-10 text-center text-slate-500">Aucune livraison.</div>
      } @else {
        <div class="bg-white rounded-xl shadow overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="bg-slate-50 text-left text-slate-500">
              <tr>
                <th class="px-4 py-3">#</th>
                <th class="px-4 py-3">Destinataire</th>
                <th class="px-4 py-3">Ville</th>
                <th class="px-4 py-3">Produit</th>
                <th class="px-4 py-3">Type</th>
                <th class="px-4 py-3">Frais</th>
                <th class="px-4 py-3">Statut</th>
                <th class="px-4 py-3">Créée le</th>
                <th class="px-4 py-3">Livreur</th>
              </tr>
            </thead>
            <tbody>
              @for (d of filtered(); track d.id) {
                <tr class="border-t hover:bg-slate-50">
                  <td class="px-4 py-3"><a [routerLink]="['/delivery', d.id]" class="text-blue-600 font-medium">#{{ d.id }}</a></td>
                  <td class="px-4 py-3 font-medium">{{ d.recipientName }}</td>
                  <td class="px-4 py-3">{{ d.city }}</td>
                  <td class="px-4 py-3">{{ productName(d.productId) }}</td>
                  <td class="px-4 py-3">{{ typeLabels[d.deliveryType] }}</td>
                  <td class="px-4 py-3 whitespace-nowrap">{{ d.deliveryFee }} TND</td>
                  <td class="px-4 py-3">
                    @if (d.status) {
                      <span class="px-2 py-1 rounded-full text-xs font-medium whitespace-nowrap" [class]="statusClasses[d.status]">
                        {{ statusLabels[d.status] }}
                      </span>
                    }
                  </td>
                  <td class="px-4 py-3 text-slate-500 whitespace-nowrap">{{ d.createdAt | date: 'dd/MM/yyyy HH:mm' }}</td>
                  <td class="px-4 py-3">
                    @if (isActive(d)) {
                      <div class="flex gap-2 items-center">
                        <select [(ngModel)]="choice[d.id!]" class="border rounded px-2 py-1 bg-white min-w-36">
                          <option [ngValue]="undefined" disabled>{{ delivererName(d.delivererId) ?? 'Choisir...' }}</option>
                          @for (dl of availableDeliverers(); track dl.id) {
                            <option [ngValue]="dl.id">{{ dl.fullName }}</option>
                          }
                        </select>
                        <button (click)="assign(d)" [disabled]="!choice[d.id!]"
                                class="bg-slate-900 text-white px-3 py-1 rounded disabled:opacity-40">
                          {{ d.delivererId ? 'Changer' : 'Affecter' }}
                        </button>
                      </div>
                    } @else {
                      <span class="text-slate-600">{{ delivererName(d.delivererId) ?? '—' }}</span>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
        @if (availableDeliverers().length === 0) {
          <p class="text-sm text-slate-500 mt-3">
            Aucun livreur disponible. <a routerLink="/admin/delivery/deliverers" class="text-blue-600">Ajouter un livreur</a>
          </p>
        }
      }
    </div>
  `,
})
export class AdminDeliveriesComponent implements OnInit {
  private api = inject(AdminDeliveryService);
  private deliveryApi = inject(DeliveryService);

  readonly statuses: DeliveryStatus[] = ['PENDING', 'IN_PROGRESS', 'DELIVERED', 'CANCELLED'];
  readonly statusLabels = STATUS_LABELS;
  readonly statusClasses = STATUS_CLASSES;
  readonly typeLabels = TYPE_LABELS;

  deliveries = signal<Delivery[]>([]);
  deliverers = signal<Deliverer[]>([]);
  products = signal<Map<number, string>>(new Map());
  loading = signal(true);
  error = signal<string | null>(null);
  actionError = signal<string | null>(null);
  filter = signal<DeliveryStatus | null>(null);
  query = signal('');
  search = '';
  choice: Record<number, number | undefined> = {};

  filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    return this.deliveries().filter(
      (d) =>
        (!this.filter() || d.status === this.filter()) &&
        (!q || d.recipientName.toLowerCase().includes(q) || d.city.toLowerCase().includes(q)),
    );
  });

  availableDeliverers = computed(() => this.deliverers().filter((d) => d.available));

  ngOnInit(): void {
    this.load();
    // Noms des produits (marketplace) : si le service est arrêté, on affiche juste l'id.
    this.deliveryApi.listProducts().subscribe({
      next: (list) => this.products.set(new Map(list.map((p) => [p.id, p.name]))),
      error: () => {},
    });
  }

  isActive(d: Delivery): boolean {
    return d.status === 'PENDING' || d.status === 'IN_PROGRESS';
  }

  productName(id: number | null | undefined): string {
    if (id == null) return '—';
    return this.products().get(id) ?? `Produit #${id}`;
  }

  delivererName(id: number | null | undefined): string | null {
    if (id == null) return null;
    return this.deliverers().find((d) => d.id === id)?.fullName ?? `Livreur #${id}`;
  }

  chip(active: boolean): string {
    return (
      'px-3 py-1 rounded-full text-sm border ' +
      (active ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-600 hover:bg-slate-100')
    );
  }

  assign(d: Delivery): void {
    const delivererId = this.choice[d.id!];
    if (!delivererId) return;
    this.actionError.set(null);
    this.api.assignDeliverer(d.id!, delivererId).subscribe({
      next: () => {
        delete this.choice[d.id!];
        this.load();
      },
      error: (err) => this.actionError.set(apiError(err, 'Affectation impossible')),
    });
  }

  private load(): void {
    forkJoin({ deliveries: this.api.listDeliveries(), deliverers: this.api.listDeliverers() }).subscribe({
      next: ({ deliveries, deliverers }) => {
        this.deliveries.set((deliveries ?? []).sort((a, b) => (b.id ?? 0) - (a.id ?? 0)));
        this.deliverers.set(deliverers ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Impossible de charger les livraisons'));
        this.loading.set(false);
      },
    });
  }
}
