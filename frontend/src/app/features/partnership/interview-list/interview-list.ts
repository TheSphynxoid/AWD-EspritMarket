import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Interview, JobApplication, PartnershipService } from '../../../core/services/partnership.service';

@Component({
  selector: 'app-interview-list',
  imports: [FormsModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-indigo-50 p-6">
      <div class="max-w-6xl mx-auto">

        <!-- Header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Entretiens</h1>
            <p class="text-slate-500 mt-1">Planifiez et suivez les entretiens</p>
          </div>
          <button (click)="showForm.set(!showForm())" class="bg-indigo-600 text-white px-5 py-2.5 rounded-lg shadow hover:bg-indigo-700 transition">
            {{ showForm() ? '✕ Annuler' : '+ Nouvel entretien' }}
          </button>
        </div>

        <!-- Create Form -->
        @if (showForm()) {
          <div class="bg-white rounded-2xl shadow-md p-6 mb-8">
            <h2 class="text-lg font-semibold text-slate-800 mb-4">Planifier un entretien</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input [(ngModel)]="form.date" placeholder="Date" type="date" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <input [(ngModel)]="form.location" placeholder="Lieu" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <select [(ngModel)]="form.type" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="">— Type —</option>
                <option value="ON_SITE">ON_SITE</option>
                <option value="REMOTE">REMOTE</option>
                <option value="PHONE">PHONE</option>
              </select>
              <select [(ngModel)]="form.status" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="">— Statut —</option>
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
              <select [(ngModel)]="form.applicationId" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option [ngValue]="0">— Candidature * —</option>
                @for (a of appList(); track a.id) {
                  <option [ngValue]="a.id">{{ a.candidateName }} (#{{ a.id }})</option>
                }
              </select>
              <textarea [(ngModel)]="form.notes" placeholder="Notes" rows="2" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none"></textarea>
            </div>
            <div class="flex gap-3 mt-5">
              <button (click)="create()" class="bg-indigo-600 text-white px-6 py-2.5 rounded-lg hover:bg-indigo-700 transition text-sm font-medium">
                ➕ Planifier
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
        } @else if (interviews().length === 0) {
          <div class="text-center py-20 text-slate-500">Aucun entretien pour le moment.</div>
        } @else {
          <div class="bg-white rounded-2xl shadow-md overflow-hidden">
            <table class="w-full text-sm">
              <thead class="bg-slate-50 text-slate-600 uppercase text-xs">
                <tr>
                  <th class="px-6 py-4 text-left">Date</th>
                  <th class="px-6 py-4 text-left">Lieu</th>
                  <th class="px-6 py-4 text-left">Type</th>
                  <th class="px-6 py-4 text-left">Statut</th>
                  <th class="px-6 py-4 text-left">Notes</th>
                  <th class="px-6 py-4 text-left">Candidature</th>
                  <th class="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                @for (i of interviews(); track i.id) {
                  <tr class="hover:bg-slate-50 transition">
                    <td class="px-6 py-4 text-slate-800">{{ i.date || '—' }}</td>
                    <td class="px-6 py-4 text-slate-600">{{ i.location || '—' }}</td>
                    <td class="px-6 py-4 text-slate-600">{{ i.type || '—' }}</td>
                    <td class="px-6 py-4">
                      <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                        [class]="i.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-700' :
                                 i.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                                 'bg-blue-100 text-blue-700'">
                        {{ i.status || '—' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-slate-600 max-w-[200px] truncate">{{ i.notes || '—' }}</td>
                    <td class="px-6 py-4 text-slate-600">#{{ i.applicationId }}</td>
                    <td class="px-6 py-4 text-right">
                      <button (click)="remove(i.id!)" class="text-red-600 hover:text-red-800 text-xs font-medium">🗑 Supprimer</button>
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
export class InterviewListComponent implements OnInit {
  private service = inject(PartnershipService);

  interviews = signal<Interview[]>([]);
  appList = signal<JobApplication[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  showForm = signal(false);
  formError = signal<string | null>(null);

  form: Interview = { applicationId: 0 };

  ngOnInit(): void {
    this.load();
    this.service.listApplications().subscribe({
      next: (data) => this.appList.set(data ?? []),
    });
  }

  load(): void {
    this.service.listInterviews().subscribe({
      next: (data) => { this.interviews.set(data ?? []); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les entretiens'); this.loading.set(false); },
    });
  }

  create(): void {
    if (!this.form.applicationId) {
      this.formError.set('La candidature est obligatoire');
      return;
    }
    this.formError.set(null);
    this.service.createInterview(this.form).subscribe({
      next: () => {
        this.form = { applicationId: 0 };
        this.showForm.set(false);
        this.load();
      },
      error: () => this.formError.set("Échec de la création"),
    });
  }

  remove(id: number): void {
    if (!confirm('Supprimer cet entretien ?')) return;
    this.service.deleteInterview(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Échec de la suppression'),
    });
  }
}

