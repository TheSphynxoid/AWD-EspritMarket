import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AdminDeliveryService } from '../admin-delivery.service';
import {
  Deliverer,
  Delivery,
  DeliveryStatus,
  DeliveryType,
  STATUS_LABELS,
  TYPE_LABELS,
  VEHICLE_LABELS,
  Vehicle,
  VehicleType,
  apiError,
} from '../../delivery/delivery.models';

const STATUS_BAR: Record<DeliveryStatus, string> = {
  PENDING: 'bg-amber-500',
  IN_PROGRESS: 'bg-blue-600',
  DELIVERED: 'bg-green-600',
  CANCELLED: 'bg-slate-400',
};

/** Statistiques du microservice Delivery. */
@Component({
  selector: 'app-admin-delivery-dashboard',
  imports: [RouterLink],
  template: `
    <div class="p-6">
      <h1 class="text-3xl font-bold mb-6">Statistiques</h1>

      @if (loading()) {
        <p class="text-slate-500">Chargement des statistiques...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
      } @else {
        <!-- Indicateurs clés -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          @for (k of kpis(); track k.label) {
            <div class="bg-white rounded-xl shadow p-5">
              <p class="text-sm text-slate-500">{{ k.label }}</p>
              <p class="text-3xl font-bold mt-1">{{ k.value }}</p>
              <p class="text-xs text-slate-500 mt-1">{{ k.hint }}</p>
            </div>
          }
        </div>

        <div class="grid lg:grid-cols-2 gap-6">
          <!-- Répartition par statut -->
          <div class="bg-white rounded-xl shadow p-6">
            <h2 class="font-semibold text-lg mb-4">Livraisons par statut</h2>
            <div class="flex h-4 rounded-full overflow-hidden bg-slate-100 mb-4">
              @for (s of byStatus(); track s.key) {
                <div [class]="s.bar" [style.width.%]="s.pct"></div>
              }
            </div>
            @for (s of byStatus(); track s.key) {
              <div class="flex items-center justify-between text-sm py-1">
                <span class="flex items-center gap-2"><span class="w-3 h-3 rounded-sm" [class]="s.bar"></span>{{ s.label }}</span>
                <span class="font-medium">{{ s.count }} <span class="text-slate-400">({{ s.pct }}%)</span></span>
              </div>
            }
          </div>

          <!-- Répartition par type -->
          <div class="bg-white rounded-xl shadow p-6">
            <h2 class="font-semibold text-lg mb-4">Livraisons par type</h2>
            @for (t of byType(); track t.label) {
              <div class="mb-3">
                <div class="flex justify-between text-sm mb-1"><span>{{ t.label }}</span><span class="font-medium">{{ t.count }}</span></div>
                <div class="h-2 bg-slate-100 rounded-full"><div class="h-2 bg-blue-600 rounded-full" [style.width.%]="t.pct"></div></div>
              </div>
            }
            <h2 class="font-semibold text-lg mt-6 mb-3">Top villes</h2>
            @for (c of topCities(); track c.city) {
              <div class="flex justify-between text-sm py-1 border-b last:border-0"><span>{{ c.city }}</span><span class="font-medium">{{ c.count }}</span></div>
            } @empty {
              <p class="text-sm text-slate-500">Aucune donnée.</p>
            }
          </div>

          <!-- Livreurs -->
          <div class="bg-white rounded-xl shadow p-6">
            <h2 class="font-semibold text-lg mb-4">Livreurs</h2>
            <div class="grid grid-cols-3 gap-3 text-center">
              <div class="bg-slate-50 rounded-lg p-3"><p class="text-2xl font-bold">{{ deliverers().length }}</p><p class="text-xs text-slate-500">Total</p></div>
              <div class="bg-green-50 rounded-lg p-3"><p class="text-2xl font-bold text-green-700">{{ availableCount() }}</p><p class="text-xs text-slate-500">Disponibles</p></div>
              <div class="bg-amber-50 rounded-lg p-3"><p class="text-2xl font-bold text-amber-700">{{ deliverers().length - availableCount() }}</p><p class="text-xs text-slate-500">En livraison</p></div>
            </div>
            <h3 class="text-sm font-medium text-slate-600 mt-5 mb-2">Livraisons effectuées par livreur</h3>
            @for (p of perDeliverer(); track p.name) {
              <div class="flex justify-between text-sm py-1 border-b last:border-0"><span>{{ p.name }}</span><span class="font-medium">{{ p.delivered }} / {{ p.total }}</span></div>
            } @empty {
              <p class="text-sm text-slate-500">Aucun livreur. <a routerLink="/admin/delivery/deliverers" class="text-blue-600">Ajouter un livreur</a></p>
            }
          </div>

          <!-- Véhicules -->
          <div class="bg-white rounded-xl shadow p-6">
            <h2 class="font-semibold text-lg mb-4">Véhicules</h2>
            <div class="grid grid-cols-3 gap-3 text-center">
              @for (v of byVehicle(); track v.label) {
                <div class="bg-slate-50 rounded-lg p-3"><p class="text-2xl font-bold">{{ v.count }}</p><p class="text-xs text-slate-500">{{ v.label }}</p></div>
              }
            </div>
            <p class="text-sm text-slate-600 mt-4">
              {{ vehicles().length }} véhicule(s) au total · {{ freeVehicles() }} sans livreur
            </p>
          </div>
        </div>

      }
    </div>
  `,
})
export class AdminDeliveryDashboardComponent implements OnInit {
  private api = inject(AdminDeliveryService);

  deliveries = signal<Delivery[]>([]);
  deliverers = signal<Deliverer[]>([]);
  vehicles = signal<Vehicle[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  private pct(n: number, total: number): number {
    return total ? Math.round((n / total) * 100) : 0;
  }

  kpis = computed(() => {
    const all = this.deliveries();
    const delivered = all.filter((d) => d.status === 'DELIVERED');
    const closed = all.filter((d) => d.status === 'DELIVERED' || d.status === 'CANCELLED').length;
    const revenue = delivered.reduce((sum, d) => sum + (d.deliveryFee ?? 0), 0);
    const avg = all.length ? all.reduce((s, d) => s + (d.deliveryFee ?? 0), 0) / all.length : 0;
    return [
      { label: 'Livraisons', value: all.length, hint: `${all.length - closed} active(s)` },
      { label: 'Taux de réussite', value: `${this.pct(delivered.length, closed)}%`, hint: 'livrées / clôturées' },
      { label: 'Revenus', value: `${revenue.toFixed(1)} TND`, hint: 'frais des livraisons livrées' },
      { label: 'Frais moyen', value: `${avg.toFixed(1)} TND`, hint: 'par livraison' },
    ];
  });

  byStatus = computed(() => {
    const all = this.deliveries();
    return (Object.keys(STATUS_LABELS) as DeliveryStatus[]).map((key) => {
      const count = all.filter((d) => d.status === key).length;
      return { key, label: STATUS_LABELS[key], count, pct: this.pct(count, all.length), bar: STATUS_BAR[key] };
    });
  });

  byType = computed(() => {
    const all = this.deliveries();
    return (Object.keys(TYPE_LABELS) as DeliveryType[]).map((key) => {
      const count = all.filter((d) => d.deliveryType === key).length;
      return { label: TYPE_LABELS[key], count, pct: this.pct(count, all.length) };
    });
  });

  topCities = computed(() => {
    const counts = new Map<string, number>();
    for (const d of this.deliveries()) {
      const city = d.city.trim();
      counts.set(city, (counts.get(city) ?? 0) + 1);
    }
    return [...counts.entries()]
      .map(([city, count]) => ({ city, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  });

  availableDeliverers = computed(() => this.deliverers().filter((d) => d.available));
  availableCount = computed(() => this.availableDeliverers().length);

  perDeliverer = computed(() =>
    this.deliverers().map((dl) => {
      const mine = this.deliveries().filter((d) => d.delivererId === dl.id);
      return { name: dl.fullName, total: mine.length, delivered: mine.filter((d) => d.status === 'DELIVERED').length };
    }),
  );

  byVehicle = computed(() =>
    (Object.keys(VEHICLE_LABELS) as VehicleType[]).map((key) => ({
      label: VEHICLE_LABELS[key],
      count: this.vehicles().filter((v) => v.type === key).length,
    })),
  );

  freeVehicles = computed(() => {
    const used = new Set(this.deliverers().map((d) => d.vehicleId));
    return this.vehicles().filter((v) => !used.has(v.id)).length;
  });

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    forkJoin({
      deliveries: this.api.listDeliveries(),
      deliverers: this.api.listDeliverers(),
      vehicles: this.api.listVehicles(),
    }).subscribe({
      next: ({ deliveries, deliverers, vehicles }) => {
        this.deliveries.set(deliveries ?? []);
        this.deliverers.set(deliverers ?? []);
        this.vehicles.set(vehicles ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Impossible de charger les statistiques'));
        this.loading.set(false);
      },
    });
  }
}
