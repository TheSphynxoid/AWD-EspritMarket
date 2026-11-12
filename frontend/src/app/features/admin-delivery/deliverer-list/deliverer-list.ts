import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { switchMap } from 'rxjs';
import { AdminDeliveryService } from '../admin-delivery.service';
import { Deliverer, VEHICLE_LABELS, Vehicle, VehicleType, apiError } from '../../delivery/delivery.models';

@Component({
  selector: 'app-deliverer-list',
  imports: [FormsModule],
  template: `
    <div class="p-6">
      <h1 class="text-3xl font-bold mb-6">Livreurs</h1>

      <div class="grid lg:grid-cols-3 gap-6">
        <!-- Formulaire livreur -->
        <form #f="ngForm" (ngSubmit)="save(f)" class="bg-white rounded-xl shadow p-6 space-y-4 h-fit">
          <h2 class="font-semibold text-lg">{{ editId ? 'Modifier le livreur' : 'Ajouter un livreur' }}</h2>
          <input [(ngModel)]="model.fullName" name="fullName" required placeholder="Nom complet"
                 class="w-full border rounded px-3 py-2" />
          <input [(ngModel)]="model.phone" name="phone" required pattern="^\\+?[0-9]{8,15}$" placeholder="Téléphone (+216...)"
                 #phone="ngModel" class="w-full border rounded px-3 py-2" />
          @if (phone.invalid && phone.touched) {
            <p class="text-xs text-red-600 -mt-2">8 à 15 chiffres, + facultatif</p>
          }
          <label class="block">
            <span class="text-sm text-slate-600">Véhicule (facultatif)</span>
            <select [(ngModel)]="model.vehicleId" name="vehicleId" class="w-full border rounded px-3 py-2 mt-1 bg-white">
              <option [ngValue]="null">Sans véhicule</option>
              @for (v of freeVehiclesFor(editId); track v.id) {
                <option [ngValue]="v.id">{{ vehicleLabels[v.type] }} · {{ v.registrationNumber }}</option>
              }
            </select>
          </label>
          <label class="flex items-center gap-2 text-sm">
            <input type="checkbox" [(ngModel)]="model.available" name="available" /> Disponible
          </label>
          @if (formError()) {
            <p class="text-red-700 bg-red-50 rounded p-2 text-sm">{{ formError() }}</p>
          }
          <div class="flex gap-2">
            <button type="submit" [disabled]="f.invalid" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 disabled:opacity-50">
              {{ editId ? 'Enregistrer' : 'Ajouter le livreur' }}
            </button>
            @if (editId) {
              <button type="button" (click)="reset(f)" class="px-4 py-2">Annuler</button>
            }
          </div>
        </form>

        <!-- Liste des livreurs -->
        <div class="lg:col-span-2">
          @if (error()) {
            <p class="text-red-700 bg-red-50 rounded p-3 mb-3">{{ error() }}</p>
          }
          @if (loading()) {
            <p class="text-slate-500">Chargement...</p>
          } @else if (deliverers().length === 0) {
            <div class="bg-white rounded-xl shadow p-10 text-center text-slate-500">Aucun livreur. Ajoutez-en un avec le formulaire.</div>
          } @else {
            <div class="grid sm:grid-cols-2 gap-4">
              @for (d of deliverers(); track d.id) {
                <div class="bg-white rounded-xl shadow p-5">
                  <div class="flex items-start justify-between">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded-full bg-slate-900 text-white flex items-center justify-center font-bold">
                        {{ d.fullName.charAt(0).toUpperCase() }}
                      </div>
                      <div>
                        <p class="font-semibold">{{ d.fullName }}</p>
                        <p class="text-sm text-slate-500">{{ d.phone }}</p>
                      </div>
                    </div>
                    <span class="px-2 py-1 rounded-full text-xs font-medium"
                          [class]="d.available ? 'bg-green-100 text-green-800' : 'bg-amber-100 text-amber-800'">
                      {{ d.available ? 'Disponible' : 'En livraison' }}
                    </span>
                  </div>

                  <!-- Véhicule du livreur -->
                  <div class="mt-4 bg-slate-50 rounded-lg p-3 text-sm">
                    @if (vehicleOf(d); as v) {
                      <div class="flex items-center justify-between">
                        <span>🚚 {{ vehicleLabels[v.type] }} · <span class="font-mono">{{ v.registrationNumber }}</span></span>
                        <button (click)="detachVehicle(d)" class="text-slate-500 hover:text-red-600 text-xs">Retirer</button>
                      </div>
                    } @else if (vehicleFormFor() === d.id) {
                      <div class="space-y-2">
                        <p class="font-medium">Ajouter un véhicule pour {{ d.fullName }}</p>
                        <div class="flex gap-1">
                          @for (t of types; track t) {
                            <button type="button" (click)="newVehicle.type = t" class="flex-1 border rounded px-2 py-1 text-xs"
                                    [class]="newVehicle.type === t ? 'border-blue-600 bg-blue-50 text-blue-700' : 'bg-white'">
                              {{ vehicleLabels[t] }}
                            </button>
                          }
                        </div>
                        <input [(ngModel)]="newVehicle.registrationNumber" placeholder="Immatriculation (ex. 123 TU 4567)"
                               class="w-full border rounded px-2 py-1 bg-white" />
                        <div class="flex gap-2">
                          <button (click)="addVehicleTo(d)" [disabled]="!newVehicle.registrationNumber.trim()"
                                  class="bg-blue-600 text-white px-3 py-1 rounded text-xs disabled:opacity-50">Créer et associer</button>
                          <button (click)="vehicleFormFor.set(null)" class="px-3 py-1 text-xs">Annuler</button>
                        </div>
                      </div>
                    } @else {
                      <div class="flex items-center justify-between">
                        <span class="text-slate-500">Aucun véhicule</span>
                        <button (click)="openVehicleForm(d)" class="text-blue-600 text-xs font-medium">+ Ajouter un véhicule</button>
                      </div>
                    }
                  </div>

                  <div class="flex gap-3 mt-4 text-sm">
                    <button (click)="edit(d)" class="text-blue-600">Modifier</button>
                    <button (click)="remove(d)" class="text-red-600">Supprimer</button>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `,
})
export class DelivererListComponent implements OnInit {
  private api = inject(AdminDeliveryService);
  readonly types: VehicleType[] = ['CAR', 'MOTORCYCLE', 'TRUCK'];
  readonly vehicleLabels = VEHICLE_LABELS;

  deliverers = signal<Deliverer[]>([]);
  vehicles = signal<Vehicle[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  formError = signal<string | null>(null);
  vehicleFormFor = signal<number | null>(null);
  editId: number | null = null;
  model: Deliverer = this.empty();
  newVehicle: Vehicle = { type: 'CAR', registrationNumber: '', available: true };

  private usedVehicleIds = computed(() => new Set(this.deliverers().map((d) => d.vehicleId)));

  ngOnInit(): void {
    this.load();
  }

  vehicleOf(d: Deliverer): Vehicle | null {
    return this.vehicles().find((v) => v.id === d.vehicleId) ?? null;
  }

  /** Véhicules libres, plus celui du livreur en cours de modification. */
  freeVehiclesFor(delivererId: number | null): Vehicle[] {
    const current = this.deliverers().find((d) => d.id === delivererId)?.vehicleId;
    return this.vehicles().filter((v) => !this.usedVehicleIds().has(v.id) || v.id === current);
  }

  edit(d: Deliverer): void {
    this.editId = d.id!;
    this.model = { ...d, vehicleId: d.vehicleId ?? null };
    this.formError.set(null);
  }

  save(f: NgForm): void {
    this.formError.set(null);
    const req = this.editId ? this.api.updateDeliverer(this.editId, this.model) : this.api.createDeliverer(this.model);
    req.subscribe({
      next: () => {
        this.reset(f);
        this.load();
      },
      error: (err) => this.formError.set(apiError(err, 'Enregistrement impossible')),
    });
  }

  openVehicleForm(d: Deliverer): void {
    this.newVehicle = { type: 'CAR', registrationNumber: '', available: true };
    this.vehicleFormFor.set(d.id!);
    this.error.set(null);
  }

  /** Crée le véhicule puis l'associe au livreur (PUT du livreur avec vehicleId). */
  addVehicleTo(d: Deliverer): void {
    this.error.set(null);
    this.api
      .createVehicle({ ...this.newVehicle, registrationNumber: this.newVehicle.registrationNumber.trim() })
      .pipe(switchMap((v) => this.api.updateDeliverer(d.id!, { ...d, vehicleId: v.id })))
      .subscribe({
        next: () => {
          this.vehicleFormFor.set(null);
          this.load();
        },
        error: (err) => this.error.set(apiError(err, "Impossible d'ajouter le véhicule")),
      });
  }

  detachVehicle(d: Deliverer): void {
    this.api.updateDeliverer(d.id!, { ...d, vehicleId: null }).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(apiError(err, 'Impossible de retirer le véhicule')),
    });
  }

  remove(d: Deliverer): void {
    if (!confirm(`Supprimer ${d.fullName} ?`)) return;
    this.api.deleteDeliverer(d.id!).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(apiError(err, 'Suppression impossible')),
    });
  }

  reset(f: NgForm): void {
    this.editId = null;
    f.resetForm(this.empty());
    this.model = this.empty();
  }

  private load(): void {
    this.api.listVehicles().subscribe({ next: (v) => this.vehicles.set(v ?? []), error: () => {} });
    this.api.listDeliverers().subscribe({
      next: (list) => {
        this.deliverers.set(list ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Impossible de charger les livreurs'));
        this.loading.set(false);
      },
    });
  }

  private empty(): Deliverer {
    return { fullName: '', phone: '', available: true, vehicleId: null };
  }
}
