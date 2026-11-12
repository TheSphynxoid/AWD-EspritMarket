import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { JobOffer, PartnerCompany, PartnershipService } from '../../../core/services/partnership.service';
import { EventItem, EventService } from '../../../core/services/event.service';

@Component({
  selector: 'app-job-offer-list',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-indigo-50 p-6">
      <div class="max-w-6xl mx-auto">

        <!-- Header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Offres d'emploi</h1>
            <p class="text-slate-500 mt-1">Gérez les offres et liez-les aux événements via OpenFeign</p>
          </div>
          <button (click)="showForm.set(!showForm())" class="bg-indigo-600 text-white px-5 py-2.5 rounded-lg shadow hover:bg-indigo-700 transition">
            {{ showForm() ? '✕ Annuler' : '+ Nouvelle offre' }}
          </button>
        </div>

        <!-- Create Form -->
        @if (showForm()) {
          <div class="bg-white rounded-2xl shadow-md p-6 mb-8">
            <h2 class="text-lg font-semibold text-slate-800 mb-4">Créer une offre d'emploi</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input [(ngModel)]="form.title" placeholder="Titre *" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <select [(ngModel)]="form.type" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="">— Type —</option>
                <option value="FULL_TIME">FULL_TIME</option>
                <option value="PART_TIME">PART_TIME</option>
                <option value="INTERNSHIP">INTERNSHIP</option>
                <option value="FREELANCE">FREELANCE</option>
              </select>
              <input [(ngModel)]="form.location" placeholder="Localisation" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <select [(ngModel)]="form.status" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option value="">— Statut —</option>
                <option value="OPEN">OPEN</option>
                <option value="CLOSED">CLOSED</option>
                <option value="DRAFT">DRAFT</option>
              </select>
              <select [(ngModel)]="form.companyId" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none">
                <option [ngValue]="0">— Entreprise * —</option>
                @for (c of companies(); track c.id) {
                  <option [ngValue]="c.id">{{ c.name }}</option>
                }
              </select>
              <input [(ngModel)]="form.eventId" type="number" placeholder="Event ID pour Feign (ex: 1)" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none" />
              <textarea [(ngModel)]="form.description" placeholder="Description" rows="3" class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none md:col-span-2"></textarea>
            </div>
            <div class="flex gap-3 mt-5">
              <button (click)="create()" class="bg-indigo-600 text-white px-6 py-2.5 rounded-lg hover:bg-indigo-700 transition text-sm font-medium">
                ➕ Créer l'offre
              </button>
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
        } @else if (offers().length === 0) {
          <div class="text-center py-20 text-slate-500">Aucune offre d'emploi pour le moment.</div>
        } @else {
          <!-- Cards Grid -->
          <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            @for (o of offers(); track o.id) {
              <div class="group bg-white rounded-2xl shadow-md hover:shadow-xl transition overflow-hidden">
                <!-- Card Header -->
                <div class="h-24 flex items-center justify-between px-5"
                  [class]="o.eventName ? 'bg-gradient-to-r from-indigo-600 to-emerald-600' : 'bg-gradient-to-r from-indigo-600 to-purple-700'">
                  <span class="text-white text-3xl font-bold opacity-80">{{ (o.title || '?').charAt(0) }}</span>
                  @if (o.eventName) {
                    <span class="bg-white/20 text-white text-[10px] font-bold px-2 py-1 rounded-full">🔗 FEIGN</span>
                  }
                </div>
                <!-- Card Body -->
                <a [routerLink]="['/partnership/job-offers', o.id]" class="block p-5">
                  <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold text-slate-800 group-hover:text-indigo-600 transition">{{ o.title }}</h2>
                    <span class="text-xs font-medium px-2 py-1 rounded-full"
                      [class]="o.status === 'OPEN' ? 'bg-emerald-100 text-emerald-700' :
                               o.status === 'CLOSED' ? 'bg-red-100 text-red-700' :
                               'bg-slate-100 text-slate-600'">
                      {{ o.status || '—' }}
                    </span>
                  </div>
                  <p class="text-slate-500 text-sm">📍 {{ o.location || '—' }}</p>
                  <div class="mt-3 flex items-center justify-between text-xs text-slate-500">
                    <span>{{ o.type || '—' }}</span>
                    <span>🏢 Company #{{ o.companyId }}</span>
                  </div>
                  @if (o.eventName) {
                    <div class="mt-3 bg-emerald-50 border border-emerald-200 rounded-lg px-3 py-2 text-xs text-emerald-700">
                      📅 {{ o.eventName }} — {{ o.eventDate || '?' }}
                    </div>
                  }
                </a>
                <!-- Delete -->
                <div class="px-5 pb-4 flex justify-end">
                  <button (click)="remove(o.id!)" class="text-red-500 hover:text-red-700 text-xs font-medium">🗑 Supprimer</button>
                </div>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `,
})
export class JobOfferListComponent implements OnInit {
  private service = inject(PartnershipService);
  private eventService = inject(EventService);

  offers = signal<JobOffer[]>([]);
  companies = signal<PartnerCompany[]>([]);
  events = signal<EventItem[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  showForm = signal(false);
  formError = signal<string | null>(null);

  form: JobOffer = { title: '', companyId: 0, eventId: null };

  ngOnInit(): void {
    this.load();
    // Load companies for the dropdown
    this.service.listCompanies().subscribe({
      next: (data) => this.companies.set(data ?? []),
    });
    // Load events for the dropdown (from Event microservice)
    this.eventService.list().subscribe({
      next: (data) => this.events.set(data ?? []),
      error: () => {}, // Event service might be down — ignore
    });
  }

  load(): void {
    this.service.listJobOffers().subscribe({
      next: (data) => { this.offers.set(data ?? []); this.loading.set(false); },
      error: () => { this.error.set("Impossible de charger les offres d'emploi"); this.loading.set(false); },
    });
  }

  create(): void {
    if (!this.form.title?.trim()) {
      this.formError.set('Le titre est obligatoire');
      return;
    }
    if (!this.form.companyId) {
      this.formError.set("L'entreprise est obligatoire");
      return;
    }
    this.formError.set(null);
    this.service.createJobOffer(this.form).subscribe({
      next: () => {
        this.form = { title: '', companyId: 0, eventId: null };
        this.showForm.set(false);
        this.load();
      },
      error: () => this.formError.set("Échec de la création"),
    });
  }

  remove(id: number): void {
    if (!confirm('Supprimer cette offre ?')) return;
    this.service.deleteJobOffer(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Échec de la suppression'),
    });
  }
}

