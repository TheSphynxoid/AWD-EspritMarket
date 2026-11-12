import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminDeliveryService } from '../admin-delivery.service';
import { DeliveryService } from '../../delivery/delivery.service';
import { DeliveryFee, TYPE_LABELS, apiError } from '../../delivery/delivery.models';

const TYPE_HINTS: Record<string, string> = {
  STANDARD: 'Livraison sous 2 à 4 jours',
  EXPRESS: 'Livraison sous 24 à 48 h',
  SAME_DAY: 'Livraison le jour même',
};

/** L'admin fixe le tarif de chaque type de livraison ; appliqué aux nouvelles livraisons. */
@Component({
  selector: 'app-admin-fees',
  imports: [FormsModule],
  template: `
    <div class="p-6">
      <h1 class="text-3xl font-bold mb-2">Frais de livraison</h1>
      <p class="text-slate-500 mb-6">
        Ces tarifs sont appliqués automatiquement à chaque nouvelle livraison selon son type.
        Les livraisons déjà créées gardent leur tarif.
      </p>

      @if (error()) {
        <p class="text-red-700 bg-red-50 rounded p-3 mb-4">{{ error() }}</p>
      }

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else {
        <div class="grid md:grid-cols-3 gap-6 max-w-4xl">
          @for (fee of fees(); track fee.deliveryType) {
            <div class="bg-white rounded-xl shadow p-6">
              <h2 class="font-semibold text-lg">{{ typeLabels[fee.deliveryType] }}</h2>
              <p class="text-sm text-slate-500 mb-4">{{ hints[fee.deliveryType] }}</p>
              <label class="block text-sm text-slate-600">Montant (TND)</label>
              <input type="number" min="0" step="0.5" [(ngModel)]="drafts[fee.deliveryType]"
                     class="w-full border rounded px-3 py-2 mt-1 text-lg font-semibold" />
              <button (click)="save(fee)" [disabled]="!changed(fee)"
                      class="mt-4 w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 disabled:opacity-40">
                Enregistrer
              </button>
              @if (saved() === fee.deliveryType) {
                <p class="text-sm text-green-700 mt-2">Tarif enregistré ✓</p>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class AdminFeesComponent implements OnInit {
  private adminApi = inject(AdminDeliveryService);
  private deliveryApi = inject(DeliveryService);
  readonly typeLabels = TYPE_LABELS;
  readonly hints = TYPE_HINTS;

  fees = signal<DeliveryFee[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  saved = signal<string | null>(null);
  drafts: Record<string, number | null> = {};

  ngOnInit(): void {
    this.deliveryApi.listFees().subscribe({
      next: (list) => {
        this.fees.set(list ?? []);
        for (const f of list ?? []) this.drafts[f.deliveryType] = f.amount;
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Impossible de charger les tarifs'));
        this.loading.set(false);
      },
    });
  }

  changed(fee: DeliveryFee): boolean {
    const v = this.drafts[fee.deliveryType];
    return v != null && v >= 0 && v !== fee.amount;
  }

  save(fee: DeliveryFee): void {
    const amount = this.drafts[fee.deliveryType];
    if (amount == null) return;
    this.error.set(null);
    this.adminApi.updateFee(fee.deliveryType, amount).subscribe({
      next: (updated) => {
        this.fees.update((list) => list.map((f) => (f.deliveryType === updated.deliveryType ? updated : f)));
        this.saved.set(updated.deliveryType);
      },
      error: (err) => this.error.set(apiError(err, 'Enregistrement impossible')),
    });
  }
}
