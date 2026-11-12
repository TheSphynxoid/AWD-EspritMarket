import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  SERVICE_MODE_LABEL,
  ServiceMode,
  ServiceOfferService,
  ServiceRequest,
  apiErrorMessage,
} from '../../../core/services/service-offer.service';
import { ServicesTabsComponent } from '../services-tabs';

/** Création (/services/new) et modification (/services/:id/edit) d'un service. */
@Component({
  selector: 'app-service-new',
  imports: [FormsModule, RouterLink, ServicesTabsComponent],
  template: `
    <div class="max-w-2xl mx-auto p-6">
      <app-services-tabs />
      <h1 class="text-2xl font-bold mb-4">{{ editId() ? 'Modifier le service' : 'Proposer un service' }}</h1>

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else {
        <form (ngSubmit)="submit()" class="bg-white rounded-xl shadow p-6 space-y-4">
          <label class="block text-sm">
            <span class="block text-slate-600 mb-1">Nom *</span>
            <input [(ngModel)]="model.name" name="name" required maxlength="150" class="w-full border rounded px-3 py-2" />
          </label>
          <label class="block text-sm">
            <span class="block text-slate-600 mb-1">Description</span>
            <textarea [(ngModel)]="model.description" name="description" rows="4" maxlength="2000"
                      class="w-full border rounded px-3 py-2"></textarea>
          </label>
          <div class="grid gap-4 md:grid-cols-2">
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Catégorie</span>
              <input [(ngModel)]="model.category" name="category" maxlength="80" list="srv-categories"
                     placeholder="Cours, Réparation, Design..." class="w-full border rounded px-3 py-2" />
              <datalist id="srv-categories">
                @for (c of categories(); track c) { <option [value]="c"></option> }
              </datalist>
            </label>
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Prix (TND) *</span>
              <input [(ngModel)]="model.price" name="price" type="number" min="0" step="0.5" required
                     class="w-full border rounded px-3 py-2" />
            </label>
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Durée (minutes)</span>
              <input [(ngModel)]="model.durationMinutes" name="durationMinutes" type="number" min="5" max="10080" step="5"
                     class="w-full border rounded px-3 py-2" />
            </label>
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Modalité</span>
              <select [(ngModel)]="model.mode" name="mode" class="w-full border rounded px-3 py-2 bg-white">
                <option [ngValue]="null">Non précisée</option>
                @for (m of modes; track m) { <option [ngValue]="m">{{ modeLabel[m] }}</option> }
              </select>
            </label>
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Lieu</span>
              <input [(ngModel)]="model.location" name="location" maxlength="150" placeholder="Ville, campus..."
                     class="w-full border rounded px-3 py-2" />
            </label>
            <label class="block text-sm">
              <span class="block text-slate-600 mb-1">Image (URL)</span>
              <input [(ngModel)]="model.imageUrl" name="imageUrl" type="url" maxlength="1000" placeholder="https://..."
                     class="w-full border rounded px-3 py-2" />
            </label>
          </div>
          @if (editId()) {
            <label class="flex items-center gap-2 text-sm">
              <input type="checkbox" [ngModel]="model.status === 'PAUSED'" (ngModelChange)="model.status = $event ? 'PAUSED' : 'ACTIVE'" name="paused" />
              En pause (n'apparaît plus dans le catalogue, plus de nouvelles réservations)
            </label>
          }
          @if (model.imageUrl) {
            <img [src]="model.imageUrl" alt="Aperçu" class="h-32 rounded object-cover bg-slate-100" />
          }

          @if (error()) { <p class="text-red-600 text-sm">{{ error() }}</p> }

          <div class="flex gap-3">
            <button type="submit" [disabled]="busy()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 disabled:opacity-50">
              {{ busy() ? 'Enregistrement...' : editId() ? 'Enregistrer' : 'Publier' }}
            </button>
            <a [routerLink]="editId() ? ['/services', editId()] : ['/services']" class="px-4 py-2">Annuler</a>
          </div>
        </form>
      }
    </div>
  `,
})
export class ServiceNewComponent implements OnInit {
  private api = inject(ServiceOfferService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly modeLabel = SERVICE_MODE_LABEL;
  readonly modes: ServiceMode[] = ['ON_SITE', 'REMOTE', 'BOTH'];

  editId = signal<number | null>(null);
  categories = signal<string[]>([]);
  loading = signal(false);
  busy = signal(false);
  error = signal<string | null>(null);

  model: ServiceRequest = {
    name: '',
    description: '',
    category: '',
    price: null,
    durationMinutes: 60,
    mode: null,
    location: '',
    imageUrl: '',
  };

  ngOnInit(): void {
    this.api.categories().subscribe({ next: (c) => this.categories.set(c ?? []), error: () => {} });
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;
    this.editId.set(id);
    this.loading.set(true);
    this.api.get(id).subscribe({
      next: (s) => {
        this.model = {
          name: s.name,
          description: s.description ?? '',
          category: s.category ?? '',
          price: s.price ?? null,
          durationMinutes: s.durationMinutes ?? null,
          mode: s.mode ?? null,
          location: s.location ?? '',
          imageUrl: s.imageUrl ?? '',
          status: s.status,
        };
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Impossible de charger ce service'));
        this.loading.set(false);
      },
    });
  }

  submit(): void {
    if (!this.model.name?.trim() || this.model.price == null) {
      this.error.set('Le nom et le prix sont obligatoires');
      return;
    }
    this.busy.set(true);
    this.error.set(null);
    const id = this.editId();
    const call = id ? this.api.update(id, this.model) : this.api.create(this.model);
    call.subscribe({
      next: (s) => this.router.navigate(['/services', s.id]),
      error: (e) => {
        this.error.set(apiErrorMessage(e, 'Enregistrement impossible'));
        this.busy.set(false);
      },
    });
  }
}
