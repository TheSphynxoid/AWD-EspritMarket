import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AdminDeliveryService } from '../admin-delivery.service';
import { Deliverer, VEHICLE_LABELS, Vehicle, VehicleType, apiError } from '../../delivery/delivery.models';

@Component({
  selector: 'app-vehicle-list',
  imports: [FormsModule],
  template: `
    <div class="p-6">
      <h1 class="text-3xl font-bold mb-6">Voitures</h1>

      <div class="grid lg:grid-cols-3 gap-6">
        <form #f="ngForm" (ngSubmit)="save(f)" class="bg-white rounded-xl shadow p-6 space-y-4 h-fit">
          <h2 class="font-semibold text-lg">{{ editId ? 'Modifier le véhicule' : 'Ajouter un véhicule' }}</h2>
          <div class="grid grid-cols-3 gap-2">
            @for (t of types; track t) {
              <button type="button" (click)="model.type = t" class="border rounded-lg px-2 py-2 text-sm font-medium"
                      [class]="model.type === t ? 'border-blue-600 bg-blue-50 text-blue-700' : 'hover:bg-slate-50'">
                {{ vehicleLabels[t] }}
              </button>
            }
          </div>
          <input [(ngModel)]="model.registrationNumber" name="registrationNumber" required
                 placeholder="Immatriculation (ex. 123 TU 4567)" class="w-full border rounded px-3 py-2" />
          <label class="flex items-center gap-2 text-sm">
            <input type="checkbox" [(ngModel)]="model.available" name="available" /> Disponible
          </label>
          @if (formError()) {
            <p class="text-red-700 bg-red-50 rounded p-2 text-sm">{{ formError() }}</p>
          }
          <div class="flex gap-2">
            <button type="submit" [disabled]="f.invalid" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 disabled:opacity-50">
              {{ editId ? 'Enregistrer' : 'Ajouter' }}
            </button>
            @if (editId) {
              <button type="button" (click)="reset(f)" class="px-4 py-2">Annuler</button>
            }
          </div>
        </form>

        <div class="lg:col-span-2">
          @if (error()) {
            <p class="text-red-600 mb-3">{{ error() }}</p>
          }
          @if (loading()) {
            <p class="text-slate-500">Chargement...</p>
          } @else if (vehicles().length === 0) {
            <div class="bg-white rounded-xl shadow p-10 text-center text-slate-500">Aucun véhicule.</div>
          } @else {
            <div class="bg-white rounded-xl shadow overflow-x-auto">
              <table class="w-full text-sm">
                <thead class="bg-slate-50 text-left text-slate-500">
                  <tr>
                    <th class="px-4 py-3">#</th>
                    <th class="px-4 py-3">Type</th>
                    <th class="px-4 py-3">Immatriculation</th>
                    <th class="px-4 py-3">Livreur</th>
                    <th class="px-4 py-3">Disponibilité</th>
                    <th class="px-4 py-3"></th>
                  </tr>
                </thead>
                <tbody>
                  @for (v of vehicles(); track v.id) {
                    <tr class="border-t">
                      <td class="px-4 py-3 text-slate-400">{{ v.id }}</td>
                      <td class="px-4 py-3 font-medium">{{ vehicleLabels[v.type] }}</td>
                      <td class="px-4 py-3 font-mono">{{ v.registrationNumber }}</td>
                      <td class="px-4 py-3">{{ ownerOf(v) }}</td>
                      <td class="px-4 py-3">
                        <span class="px-2 py-1 rounded-full text-xs font-medium"
                              [class]="v.available ? 'bg-green-100 text-green-800' : 'bg-slate-200 text-slate-700'">
                          {{ v.available ? 'Disponible' : 'Indisponible' }}
                        </span>
                      </td>
                      <td class="px-4 py-3 text-right whitespace-nowrap">
                        <button (click)="edit(v)" class="text-blue-600 mr-3">Modifier</button>
                        <button (click)="remove(v)" class="text-red-600">Supprimer</button>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      </div>
    </div>
  `,
})
export class VehicleListComponent implements OnInit {
  private api = inject(AdminDeliveryService);
  readonly types: VehicleType[] = ['CAR', 'MOTORCYCLE', 'TRUCK'];
  readonly vehicleLabels = VEHICLE_LABELS;

  vehicles = signal<Vehicle[]>([]);
  deliverers = signal<Deliverer[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  formError = signal<string | null>(null);
  editId: number | null = null;
  model: Vehicle = this.empty();

  ngOnInit(): void {
    this.load();
  }

  ownerOf(v: Vehicle): string {
    return this.deliverers().find((d) => d.vehicleId === v.id)?.fullName ?? '—';
  }

  edit(v: Vehicle): void {
    this.editId = v.id!;
    this.model = { ...v };
    this.formError.set(null);
  }

  save(f: NgForm): void {
    this.formError.set(null);
    const req = this.editId ? this.api.updateVehicle(this.editId, this.model) : this.api.createVehicle(this.model);
    req.subscribe({
      next: () => {
        this.reset(f);
        this.load();
      },
      error: (err) => this.formError.set(apiError(err, 'Enregistrement impossible')),
    });
  }

  remove(v: Vehicle): void {
    if (!confirm(`Supprimer le véhicule ${v.registrationNumber} ?`)) return;
    this.api.deleteVehicle(v.id!).subscribe({
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
    this.error.set(null);
    this.api.listDeliverers().subscribe({ next: (d) => this.deliverers.set(d ?? []), error: () => {} });
    this.api.listVehicles().subscribe({
      next: (list) => {
        this.vehicles.set(list ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiError(err, 'Impossible de charger les véhicules'));
        this.loading.set(false);
      },
    });
  }

  private empty(): Vehicle {
    return { type: 'CAR', registrationNumber: '', available: true };
  }
}
