import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DeliveryService } from '../delivery.service';
import {
  Deliverer,
  Delivery,
  DeliveryStatus,
  Product,
  STATUS_CLASSES,
  STATUS_LABELS,
  TYPE_LABELS,
  apiError,
} from '../delivery.models';

@Component({
  selector: 'app-delivery-detail',
  imports: [RouterLink, DatePipe],
  template: `
    <div class="max-w-4xl mx-auto p-6">
      <button (click)="location.back()" class="text-blue-600 text-sm">← Retour</button>

      @if (loading()) {
        <p class="text-slate-500 mt-4">Chargement...</p>
      } @else if (!delivery()) {
        <p class="text-red-600 mt-4">{{ error() }}</p>
      } @else {
        @let d = delivery()!;
        <div class="flex flex-wrap items-center justify-between gap-4 mt-2 mb-6">
          <div class="flex items-center gap-3">
            <h1 class="text-3xl font-bold">Livraison #{{ d.id }}</h1>
            @if (d.status) {
              <span class="px-3 py-1 rounded-full text-sm font-medium" [class]="statusClasses[d.status]">
                {{ statusLabels[d.status] }}
              </span>
            }
          </div>
          @if (!closed()) {
            <div class="flex gap-2">
              <a [routerLink]="['/delivery', d.id, 'edit']" class="border px-4 py-2 rounded hover:bg-slate-50">Modifier</a>
              <button (click)="remove()" class="border border-red-200 text-red-600 px-4 py-2 rounded hover:bg-red-50">Supprimer</button>
            </div>
          }
        </div>

        @if (error()) {
          <p class="text-red-700 bg-red-50 rounded p-3 mb-4">{{ error() }}</p>
        }

        <!-- Suivi du statut -->
        <div class="bg-white rounded-xl shadow p-6 mb-6">
          <div class="flex items-center">
            @for (step of steps; track step; let i = $index; let last = $last) {
              <div class="flex flex-col items-center">
                <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold"
                     [class]="i <= stepIndex() ? 'bg-blue-600 text-white' : 'bg-slate-200 text-slate-500'">{{ i + 1 }}</div>
                <span class="text-xs mt-2 text-slate-600">{{ statusLabels[step] }}</span>
              </div>
              @if (!last) {
                <div class="flex-1 h-1 mx-2 mb-5 rounded" [class]="i < stepIndex() ? 'bg-blue-600' : 'bg-slate-200'"></div>
              }
            }
          </div>
          @if (d.status === 'CANCELLED') {
            <p class="text-sm text-slate-600 mt-4">Cette livraison a été annulée.</p>
          }
          @if (!closed()) {
            <div class="flex flex-wrap gap-2 mt-5">
              @if (d.status === 'PENDING') {
                <button (click)="setStatus('IN_PROGRESS')" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Démarrer la livraison</button>
              }
              @if (d.status === 'IN_PROGRESS') {
                <button (click)="setStatus('DELIVERED')" class="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-500">Marquer comme livrée</button>
              }
              <button (click)="setStatus('CANCELLED')" class="border px-4 py-2 rounded hover:bg-slate-50">Annuler la livraison</button>
            </div>
          }
        </div>

        <div class="grid md:grid-cols-2 gap-6">
          <div class="bg-white rounded-xl shadow p-6 space-y-3">
            <h2 class="font-semibold text-lg">Destinataire</h2>
            <p class="font-medium">{{ d.recipientName }}</p>
            <p class="text-slate-600">{{ d.address }}<br />{{ d.postalCode }} {{ d.city }}</p>
            <p class="text-slate-600">{{ d.phone }}</p>
            <hr />
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div><span class="text-slate-500">Type</span><br />{{ typeLabels[d.deliveryType] }}</div>
              <div><span class="text-slate-500">Frais</span><br />{{ d.deliveryFee }} TND</div>
              <div class="col-span-2"><span class="text-slate-500">Créée le</span><br />{{ d.createdAt | date: 'dd/MM/yyyy HH:mm' }}</div>
            </div>
          </div>

          <div class="space-y-6">
            <div class="bg-white rounded-xl shadow p-6 space-y-2">
              <h2 class="font-semibold text-lg">Produit (marketplace)</h2>
              @if (product(); as p) {
                <p class="font-medium">{{ p.name }}</p>
                <p class="text-sm text-slate-600">Boutique : {{ p.shopName }}</p>
                <p class="text-sm text-slate-600">Prix : {{ p.price }} TND · Stock : {{ p.stock }}</p>
              } @else {
                <p class="text-sm text-slate-500">Produit #{{ d.productId ?? '—' }} {{ productError() }}</p>
              }
            </div>

            <div class="bg-white rounded-xl shadow p-6 space-y-3">
              <h2 class="font-semibold text-lg">Livreur</h2>
              @if (deliverer(); as dl) {
                <p class="font-medium">{{ dl.fullName }}</p>
                <p class="text-sm text-slate-600">{{ dl.phone }}</p>
              } @else {
                <p class="text-sm text-slate-500">Aucun livreur affecté pour le moment.</p>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
})
export class DeliveryDetailComponent implements OnInit {
  private api = inject(DeliveryService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  readonly location = inject(Location);

  readonly steps: DeliveryStatus[] = ['PENDING', 'IN_PROGRESS', 'DELIVERED'];
  readonly statusLabels = STATUS_LABELS;
  readonly statusClasses = STATUS_CLASSES;
  readonly typeLabels = TYPE_LABELS;

  delivery = signal<Delivery | null>(null);
  product = signal<Product | null>(null);
  deliverer = signal<Deliverer | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  productError = signal('');

  closed = computed(() => ['DELIVERED', 'CANCELLED'].includes(this.delivery()?.status ?? ''));
  stepIndex = computed(() => {
    const s = this.delivery()?.status;
    return s === 'CANCELLED' ? -1 : this.steps.indexOf(s ?? 'PENDING');
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.get(id).subscribe({
      next: (d) => {
        this.delivery.set(d);
        this.loading.set(false);
        this.loadProduct(d.productId);
        this.loadDeliverer(d.delivererId);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Livraison introuvable'));
        this.loading.set(false);
      },
    });
  }

  setStatus(status: DeliveryStatus): void {
    if (status === 'CANCELLED' && !confirm('Annuler cette livraison ?')) return;
    this.run(this.api.updateStatus(this.delivery()!.id!, status));
  }

  remove(): void {
    if (!confirm('Supprimer définitivement cette livraison ?')) return;
    this.api.delete(this.delivery()!.id!).subscribe({
      next: () => this.router.navigate(['/delivery']),
      error: (err) => this.error.set(apiError(err, 'Suppression impossible')),
    });
  }

  /** Applique une action puis rafraîchit la livraison. */
  private run(req: ReturnType<DeliveryService['get']>): void {
    this.error.set(null);
    req.subscribe({
      next: (d) => {
        this.delivery.set(d);
        this.loadDeliverer(d.delivererId);
      },
      error: (err) => this.error.set(apiError(err, 'Action impossible')),
    });
  }

  private loadDeliverer(id: number | null | undefined): void {
    if (id == null) {
      this.deliverer.set(null);
      return;
    }
    this.api.getDeliverer(id).subscribe({ next: (dl) => this.deliverer.set(dl), error: () => {} });
  }

  private loadProduct(productId: number | null): void {
    if (productId == null) return;
    this.api.getProduct(productId).subscribe({
      next: (p) => this.product.set(p),
      error: () => this.productError.set('(informations marketplace indisponibles)'),
    });
  }
}
