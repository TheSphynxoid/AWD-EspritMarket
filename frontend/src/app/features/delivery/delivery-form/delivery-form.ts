import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DeliveryService } from '../delivery.service';
import { Delivery, DeliveryType, Product, TYPE_LABELS, apiError } from '../delivery.models';

/** Création (/delivery/new) et modification (/delivery/:id/edit) d'une livraison. */
@Component({
  selector: 'app-delivery-form',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="max-w-3xl mx-auto p-6">
      <a [routerLink]="editId ? ['/delivery', editId] : '/delivery'" class="text-blue-600 text-sm">← Retour</a>
      <h1 class="text-2xl font-bold mt-2 mb-6">{{ editId ? 'Modifier la livraison #' + editId : 'Nouvelle livraison' }}</h1>

      <form #f="ngForm" (ngSubmit)="submit()" class="space-y-6">
        <section class="bg-white rounded-xl shadow p-6 space-y-4">
          <h2 class="font-semibold text-lg">Produit à livrer</h2>
          @if (productsError()) {
            <p class="text-amber-700 bg-amber-50 rounded p-3 text-sm">{{ productsError() }}</p>
          }
          <select [(ngModel)]="model.productId" (ngModelChange)="productId.set($event)" name="productId" required
                  class="w-full border rounded px-3 py-2 bg-white">
            <option [ngValue]="null" disabled>Choisir un produit de la marketplace</option>
            @for (p of selectableProducts(); track p.id) {
              <option [ngValue]="p.id">{{ p.name }} · {{ p.shopName }} · {{ p.price }} TND (stock {{ p.stock }})</option>
            }
          </select>
          @if (selectedProduct(); as p) {
            <div class="grid grid-cols-3 gap-3 text-sm bg-slate-50 rounded p-3">
              <div><span class="text-slate-500">Boutique</span><br />{{ p.shopName }}</div>
              <div><span class="text-slate-500">Prix</span><br />{{ p.price }} TND</div>
              <div><span class="text-slate-500">Stock</span><br />{{ p.stock }}</div>
            </div>
          }
        </section>

        <section class="bg-white rounded-xl shadow p-6 space-y-4">
          <h2 class="font-semibold text-lg">Destinataire</h2>
          <div class="grid md:grid-cols-2 gap-4">
            <label class="block">
              <span class="text-sm text-slate-600">Nom complet</span>
              <input [(ngModel)]="model.recipientName" name="recipientName" required class="w-full border rounded px-3 py-2 mt-1" />
            </label>
            <label class="block">
              <span class="text-sm text-slate-600">Téléphone</span>
              <input [(ngModel)]="model.phone" name="phone" required pattern="^\\+?[0-9]{8,15}$" placeholder="+21612345678"
                     #phone="ngModel" class="w-full border rounded px-3 py-2 mt-1" />
              @if (phone.invalid && phone.touched) {
                <span class="text-xs text-red-600">8 à 15 chiffres, + facultatif</span>
              }
            </label>
            <label class="block md:col-span-2">
              <span class="text-sm text-slate-600">Adresse</span>
              <input [(ngModel)]="model.address" name="address" required class="w-full border rounded px-3 py-2 mt-1" />
            </label>
            <label class="block">
              <span class="text-sm text-slate-600">Ville</span>
              <input [(ngModel)]="model.city" name="city" required class="w-full border rounded px-3 py-2 mt-1" />
            </label>
            <label class="block">
              <span class="text-sm text-slate-600">Code postal</span>
              <input [(ngModel)]="model.postalCode" name="postalCode" required pattern="^[0-9]{4,5}$"
                     #postal="ngModel" class="w-full border rounded px-3 py-2 mt-1" />
              @if (postal.invalid && postal.touched) {
                <span class="text-xs text-red-600">4 ou 5 chiffres</span>
              }
            </label>
          </div>
        </section>

        <section class="bg-white rounded-xl shadow p-6 space-y-4">
          <h2 class="font-semibold text-lg">Livraison</h2>
          <div class="grid grid-cols-3 gap-3">
            @for (t of types; track t) {
              <button type="button" (click)="model.deliveryType = t"
                      class="border rounded-lg px-3 py-3 text-sm font-medium"
                      [class]="model.deliveryType === t ? 'border-blue-600 bg-blue-50 text-blue-700' : 'hover:bg-slate-50'">
                {{ typeLabels[t] }}
                @if (fees()[t] != null) {
                  <span class="block text-xs font-normal mt-1">{{ fees()[t] }} TND</span>
                }
              </button>
            }
          </div>
          <div class="flex items-center justify-between bg-slate-50 rounded p-3">
            <span class="text-sm text-slate-600">Frais de livraison (fixés par l'administrateur)</span>
            <span class="text-lg font-bold">{{ fees()[model.deliveryType] ?? '—' }} TND</span>
          </div>
          @if (editId && editType && model.deliveryType === editType) {
            <p class="text-xs text-slate-500">Tarif actuel de cette livraison : {{ model.deliveryFee }} TND (conservé si le type ne change pas).</p>
          }
        </section>

        @if (error()) {
          <p class="text-red-700 bg-red-50 rounded p-3">{{ error() }}</p>
        }
        <div class="flex gap-3">
          <button type="submit" [disabled]="f.invalid || saving()"
                  class="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-500 disabled:opacity-50">
            {{ saving() ? 'Enregistrement...' : editId ? 'Enregistrer' : 'Créer la livraison' }}
          </button>
          <a [routerLink]="editId ? ['/delivery', editId] : '/delivery'" class="px-4 py-2">Annuler</a>
        </div>
      </form>
    </div>
  `,
})
export class DeliveryFormComponent implements OnInit {
  private api = inject(DeliveryService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly types: DeliveryType[] = ['STANDARD', 'EXPRESS', 'SAME_DAY'];
  readonly typeLabels = TYPE_LABELS;

  editId: number | null = null;
  model: Delivery = {
    recipientName: '',
    address: '',
    city: '',
    postalCode: '',
    phone: '',
    deliveryFee: null,
    deliveryType: 'STANDARD',
    productId: null,
  };

  products = signal<Product[]>([]);
  fees = signal<Partial<Record<DeliveryType, number>>>({});
  editType: DeliveryType | null = null;
  productId = signal<number | null>(null);
  productsError = signal<string | null>(null);
  error = signal<string | null>(null);
  saving = signal(false);

  /** Produits actifs et en stock, plus le produit actuel en modification. */
  selectableProducts = computed(() =>
    this.products().filter((p) => (p.active && p.stock > 0) || p.id === this.productId()),
  );
  selectedProduct = computed(() => this.products().find((p) => p.id === this.productId()) ?? null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.editId = id;
      this.api.get(id).subscribe({
        next: (d) => {
          this.model = { ...d };
          this.editType = d.deliveryType;
          this.productId.set(d.productId);
        },
        error: (err) => this.error.set(apiError(err, 'Livraison introuvable')),
      });
    }
    this.api.listFees().subscribe({
      next: (list) => this.fees.set(Object.fromEntries(list.map((f) => [f.deliveryType, f.amount]))),
      error: () => {},
    });
    this.api.listProducts().subscribe({
      next: (list) => this.products.set(list ?? []),
      error: () => this.productsError.set('Impossible de charger les produits : le microservice marketplace est-il démarré ?'),
    });
  }

  submit(): void {
    this.saving.set(true);
    this.error.set(null);
    const req = this.editId ? this.api.update(this.editId, this.model) : this.api.create(this.model);
    req.subscribe({
      next: (d) => this.router.navigate(['/delivery', d.id]),
      error: (err) => {
        this.error.set(apiError(err, 'Enregistrement impossible'));
        this.saving.set(false);
      },
    });
  }
}
