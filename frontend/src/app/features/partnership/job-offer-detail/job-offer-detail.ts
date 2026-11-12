import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { JobOffer, PartnershipService } from '../../../core/services/partnership.service';

@Component({
  selector: 'app-job-offer-detail',
  imports: [RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-indigo-50 p-6">
      <div class="max-w-3xl mx-auto">

        <a routerLink="/partnership/job-offers" class="inline-flex items-center gap-1 text-indigo-600 hover:text-indigo-800 mb-6 text-sm font-medium">
          ← Retour aux offres
        </a>

        @if (loading()) {
          <div class="flex justify-center py-20">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
          </div>
        } @else if (error()) {
          <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">{{ error() }}</div>
        } @else if (offer()) {
          <!-- Job Offer Card -->
          <div class="bg-white rounded-2xl shadow-lg overflow-hidden">
            <!-- Header -->
            <div class="bg-gradient-to-r from-indigo-600 to-purple-700 px-8 py-6">
              <h1 class="text-3xl font-bold text-white">{{ offer()!.title }}</h1>
              <div class="flex items-center gap-4 mt-3">
                @if (offer()!.type) {
                  <span class="bg-white/20 text-white text-xs font-semibold px-3 py-1 rounded-full">{{ offer()!.type }}</span>
                }
                @if (offer()!.status) {
                  <span class="bg-white/20 text-white text-xs font-semibold px-3 py-1 rounded-full">{{ offer()!.status }}</span>
                }
              </div>
            </div>

            <div class="p-8 space-y-6">
              <!-- Details Grid -->
              <div class="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span class="text-slate-500 font-medium">📍 Location</span>
                  <p class="text-slate-800 mt-1">{{ offer()!.location || '—' }}</p>
                </div>
                <div>
                  <span class="text-slate-500 font-medium">🏢 Entreprise</span>
                  <p class="text-slate-800 mt-1">{{ companyName() || '—' }}</p>
                </div>
              </div>

              <!-- Description -->
              @if (offer()!.description) {
                <div>
                  <h3 class="text-sm font-medium text-slate-500 mb-2">📝 Description</h3>
                  <p class="text-slate-700 leading-relaxed">{{ offer()!.description }}</p>
                </div>
              }

              <!-- Linked event -->
              <div class="border-t pt-6">
                <h3 class="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
                  🔗 Événement lié
                </h3>

                @if (!offer()!.eventId) {
                  <!-- No event linked -->
                  <div class="bg-slate-50 border border-slate-200 rounded-xl p-5 text-center">
                    <p class="text-slate-500">Aucun événement lié à cette offre.</p>
                  </div>
                } @else if (offer()!.eventName) {
                  <div class="bg-emerald-50 border border-emerald-200 rounded-xl p-5">
                    <div class="space-y-3">
                      <div class="flex items-center gap-3">
                        <span class="text-2xl">📅</span>
                        <div>
                          <p class="font-semibold text-slate-800 text-lg">{{ offer()!.eventName }}</p>
                        </div>
                      </div>
                      <div class="grid grid-cols-2 gap-3 text-sm ml-10">
                        <div>
                          <span class="text-slate-500">📍 Lieu</span>
                          <p class="text-slate-800 font-medium">{{ offer()!.eventLocation || '—' }}</p>
                        </div>
                        <div>
                          <span class="text-slate-500">🗓 Date</span>
                          <p class="text-slate-800 font-medium">{{ offer()!.eventDate || '—' }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                } @else {
                  <div class="bg-amber-50 border border-amber-200 rounded-xl p-5">
                    <p class="text-amber-800 text-sm">
                      Les informations de l'événement lié sont momentanément indisponibles.
                    </p>
                  </div>
                }
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex gap-3 mt-6">
            <a routerLink="/partnership/job-offers" class="px-5 py-2.5 bg-slate-200 text-slate-700 rounded-lg hover:bg-slate-300 transition text-sm font-medium">
              ← Liste des offres
            </a>
            <button (click)="deleteOffer()" class="px-5 py-2.5 bg-red-600 text-white rounded-lg hover:bg-red-700 transition text-sm font-medium ml-auto">
              🗑 Supprimer
            </button>
          </div>
        }
      </div>
    </div>
  `,
})
export class JobOfferDetailComponent implements OnInit {
  private service = inject(PartnershipService);
  private route = inject(ActivatedRoute);

  offer = signal<JobOffer | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  companyName = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error.set('ID invalide');
      this.loading.set(false);
      return;
    }
    this.service.getJobOffer(id).subscribe({
      next: (data) => {
        this.offer.set(data);
        this.loading.set(false);
        if (data?.companyId) {
          this.service.getCompany(data.companyId).subscribe({
            next: (c) => this.companyName.set(c?.name ?? null),
            error: () => {},
          });
        }
      },
      error: () => {
        this.error.set("Impossible de charger l'offre d'emploi");
        this.loading.set(false);
      },
    });
  }

  deleteOffer(): void {
    const o = this.offer();
    if (!o?.id || !confirm('Supprimer cette offre ?')) return;
    this.service.deleteJobOffer(o.id).subscribe({
      next: () => window.history.back(),
      error: () => this.error.set('Échec de la suppression'),
    });
  }
}

