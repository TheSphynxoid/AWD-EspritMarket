import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JobApplication, JobOffer, PartnershipService } from '../../../core/services/partnership.service';

@Component({
  selector: 'app-application-list',
  imports: [FormsModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-indigo-50 p-6">
      <div class="max-w-6xl mx-auto">

        <!-- Header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Candidatures</h1>
            <p class="text-slate-500 mt-1">Gérez les candidatures aux offres d'emploi</p>
          </div>
          <button (click)="showForm.set(!showForm())" class="bg-indigo-600 text-white px-5 py-2.5 rounded-lg shadow hover:bg-indigo-700 transition">
            {{ showForm() ? '✕ Annuler' : '+ Nouvelle candidature' }}
          </button>
        </div>

        <!-- Create Form -->
        @if (showForm()) {
          <div class="bg-white rounded-2xl shadow-md p-6 mb-8">
            <h2 class="text-lg font-semibold text-slate-800 mb-4">Ajouter une candidature</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input [(ngModel)]="form.candidateName" placeholder="Nom du candidat *" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <input [(ngModel)]="form.candidateEmail" placeholder="Email du candidat" type="email" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <input [(ngModel)]="form.resumeUrl" placeholder="URL du CV" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <select [(ngModel)]="form.status" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="">— Statut —</option>
                <option value="PENDING">PENDING</option>
                <option value="ACCEPTED">ACCEPTED</option>
                <option value="REJECTED">REJECTED</option>
              </select>
              <input [(ngModel)]="form.date" placeholder="Date (YYYY-MM-DD)" type="date" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <select [(ngModel)]="form.jobOfferId" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option [ngValue]="0">— Offre d'emploi * —</option>
                @for (o of jobOffers(); track o.id) {
                  <option [ngValue]="o.id">{{ o.title }}</option>
                }
              </select>
            </div>
            <div class="flex gap-3 mt-5">
              <button (click)="create()" class="bg-indigo-600 text-white px-6 py-2.5 rounded-lg hover:bg-indigo-700 transition text-sm font-medium">
                ➕ Ajouter
              </button>
            </div>
            @if (formError()) {
              <p class="text-red-600 text-sm mt-3">{{ formError() }}</p>
            }
          </div>
        }

        <!-- Loading / Error / Table -->
        @if (loading()) {
          <div class="flex justify-center py-20">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
          </div>
        } @else if (error()) {
          <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">{{ error() }}</div>
        } @else if (applications().length === 0) {
          <div class="text-center py-20 text-slate-500">Aucune candidature pour le moment.</div>
        } @else {
          <div class="bg-white rounded-2xl shadow-md overflow-hidden">
            <table class="w-full text-sm">
              <thead class="bg-slate-50 text-slate-600 uppercase text-xs">
                <tr>
                  <th class="px-6 py-4 text-left">Candidat</th>
                  <th class="px-6 py-4 text-left">Email</th>
                  <th class="px-6 py-4 text-left">Statut</th>
                  <th class="px-6 py-4 text-left">Date</th>
                  <th class="px-6 py-4 text-left">Offre ID</th>
                  <th class="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                @for (a of applications(); track a.id) {
                  <tr class="hover:bg-slate-50 transition">
                    <td class="px-6 py-4 font-medium text-slate-800">{{ a.candidateName }}</td>
                    <td class="px-6 py-4 text-slate-600">{{ a.candidateEmail || '—' }}</td>
                    <td class="px-6 py-4">
                      <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                        [class]="a.status === 'ACCEPTED' ? 'bg-emerald-100 text-emerald-700' :
                                 a.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                                 'bg-amber-100 text-amber-700'">
                        {{ a.status || '—' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-slate-600">{{ a.date || '—' }}</td>
                    <td class="px-6 py-4 text-slate-600">#{{ a.jobOfferId }}</td>
                    <td class="px-6 py-4 text-right">
                      <button (click)="remove(a.id!)" class="text-red-600 hover:text-red-800 text-xs font-medium">🗑 Supprimer</button>
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
export class ApplicationListComponent implements OnInit {
  private service = inject(PartnershipService);

  applications = signal<JobApplication[]>([]);
  jobOffers = signal<JobOffer[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  showForm = signal(false);
  formError = signal<string | null>(null);

  form: JobApplication = { candidateName: '', jobOfferId: 0 };

  ngOnInit(): void {
    this.load();
    this.service.listJobOffers().subscribe({
      next: (data) => this.jobOffers.set(data ?? []),
    });
  }

  load(): void {
    this.service.listApplications().subscribe({
      next: (data) => { this.applications.set(data ?? []); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les candidatures'); this.loading.set(false); },
    });
  }

  create(): void {
    if (!this.form.candidateName?.trim()) {
      this.formError.set('Le nom du candidat est obligatoire');
      return;
    }
    if (!this.form.jobOfferId) {
      this.formError.set("L'offre d'emploi est obligatoire");
      return;
    }
    this.formError.set(null);
    this.service.createApplication(this.form).subscribe({
      next: () => {
        this.form = { candidateName: '', jobOfferId: 0 };
        this.showForm.set(false);
        this.load();
      },
      error: () => this.formError.set("Échec de la création"),
    });
  }

  remove(id: number): void {
    if (!confirm('Supprimer cette candidature ?')) return;
    this.service.deleteApplication(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Échec de la suppression'),
    });
  }
}

