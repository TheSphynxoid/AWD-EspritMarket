import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PartnerCompany, PartnershipService } from '../../../core/services/partnership.service';

@Component({
  selector: 'app-company-list',
  imports: [FormsModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-indigo-50 p-6">
      <div class="max-w-6xl mx-auto">

        <!-- Header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Entreprises Partenaires</h1>
            <p class="text-slate-500 mt-1">Gérez vos partenariats B2B</p>
          </div>
          <button (click)="showForm.set(!showForm())" class="bg-indigo-600 text-white px-5 py-2.5 rounded-lg shadow hover:bg-indigo-700 transition">
            {{ showForm() ? '✕ Annuler' : '+ Nouvelle entreprise' }}
          </button>
        </div>

        <!-- Create / Edit Form -->
        @if (showForm()) {
          <div class="bg-white rounded-2xl shadow-md p-6 mb-8">
            <h2 class="text-lg font-semibold text-slate-800 mb-4">{{ editing() ? 'Modifier' : 'Ajouter' }} une entreprise</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input [(ngModel)]="form.name" placeholder="Nom *" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
              <input [(ngModel)]="form.sector" placeholder="Secteur" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
              <input [(ngModel)]="form.contactEmail" placeholder="Email de contact" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
              <input [(ngModel)]="form.phone" placeholder="Téléphone" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
              <input [(ngModel)]="form.website" placeholder="Site web" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none" />
              <select [(ngModel)]="form.partnershipStatus" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none">
                <option value="">— Statut —</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="PENDING">PENDING</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
            </div>
            <div class="flex gap-3 mt-5">
              <button (click)="save()" class="bg-indigo-600 text-white px-6 py-2.5 rounded-lg hover:bg-indigo-700 transition text-sm font-medium">
                {{ editing() ? '💾 Enregistrer' : '➕ Ajouter' }}
              </button>
              @if (editing()) {
                <button (click)="cancelEdit()" class="bg-slate-200 text-slate-700 px-6 py-2.5 rounded-lg hover:bg-slate-300 transition text-sm font-medium">
                  Annuler
                </button>
              }
            </div>
            @if (formError()) {
              <p class="text-red-600 text-sm mt-3">{{ formError() }}</p>
            }
          </div>
        }

        <!-- Loading / Error -->
        @if (loading()) {
          <div class="flex justify-center py-20">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
          </div>
        } @else if (error()) {
          <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">{{ error() }}</div>
        } @else if (companies().length === 0) {
          <div class="text-center py-20 text-slate-500">Aucune entreprise partenaire pour le moment.</div>
        } @else {
          <!-- Table -->
          <div class="bg-white rounded-2xl shadow-md overflow-hidden">
            <table class="w-full text-sm">
              <thead class="bg-slate-50 text-slate-600 uppercase text-xs">
                <tr>
                  <th class="px-6 py-4 text-left">Nom</th>
                  <th class="px-6 py-4 text-left">Secteur</th>
                  <th class="px-6 py-4 text-left">Email</th>
                  <th class="px-6 py-4 text-left">Statut</th>
                  <th class="px-6 py-4 text-left">Téléphone</th>
                  <th class="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                @for (c of companies(); track c.id) {
                  <tr class="hover:bg-slate-50 transition">
                    <td class="px-6 py-4 font-medium text-slate-800">{{ c.name }}</td>
                    <td class="px-6 py-4 text-slate-600">{{ c.sector || '—' }}</td>
                    <td class="px-6 py-4 text-slate-600">{{ c.contactEmail || '—' }}</td>
                    <td class="px-6 py-4">
                      <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                        [class]="c.partnershipStatus === 'ACTIVE' ? 'bg-emerald-100 text-emerald-700' :
                                 c.partnershipStatus === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                                 'bg-slate-100 text-slate-600'">
                        {{ c.partnershipStatus || '—' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-slate-600">{{ c.phone || '—' }}</td>
                    <td class="px-6 py-4 text-right space-x-2">
                      <button (click)="startEdit(c)" class="text-indigo-600 hover:text-indigo-800 text-xs font-medium">✏️ Modifier</button>
                      <button (click)="remove(c.id!)" class="text-red-600 hover:text-red-800 text-xs font-medium">🗑 Supprimer</button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>
    </div>
  `,
})
export class CompanyListComponent implements OnInit {
  private service = inject(PartnershipService);

  companies = signal<PartnerCompany[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  showForm = signal(false);
  editing = signal(false);
  editingId = signal<number | null>(null);
  formError = signal<string | null>(null);

  form: PartnerCompany = { name: '' };

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.service.listCompanies().subscribe({
      next: (data) => { this.companies.set(data ?? []); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les entreprises'); this.loading.set(false); },
    });
  }

  save(): void {
    if (!this.form.name?.trim()) {
      this.formError.set('Le nom est obligatoire');
      return;
    }
    this.formError.set(null);

    const obs = this.editing()
      ? this.service.updateCompany(this.editingId()!, this.form)
      : this.service.createCompany(this.form);

    obs.subscribe({
      next: () => { this.resetForm(); this.load(); },
      error: () => this.formError.set('Échec de l\'enregistrement'),
    });
  }

  startEdit(c: PartnerCompany): void {
    this.form = { ...c };
    this.editingId.set(c.id!);
    this.editing.set(true);
    this.showForm.set(true);
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    if (!confirm('Supprimer cette entreprise ?')) return;
    this.service.deleteCompany(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Échec de la suppression'),
    });
  }

  private resetForm(): void {
    this.form = { name: '' };
    this.editing.set(false);
    this.editingId.set(null);
    this.showForm.set(false);
    this.formError.set(null);
  }
}

