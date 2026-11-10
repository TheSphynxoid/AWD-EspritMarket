import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-event-detail',
  imports: [RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-red-50 p-6">
      <div class="max-w-3xl mx-auto">
        @if (loading()) {
          <div class="flex justify-center py-20">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
          </div>
        } @else if (error()) {
          <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">{{ error() }}</div>
          <a routerLink="/events" class="text-red-600 mt-4 inline-block">Retour</a>
        } @else if (event()) {
          <div class="bg-white rounded-2xl shadow-xl overflow-hidden">
            <div class="h-40 bg-gradient-to-r from-red-600 to-gray-800 flex items-center justify-center">
              <h1 class="text-4xl font-bold text-white drop-shadow">{{ event()!.name }}</h1>
            </div>
            <div class="p-8">
              <div class="flex items-center gap-3 mb-6">
                <span class="text-sm font-medium px-3 py-1 rounded-full bg-red-100 text-red-700">{{ event()!.status || '—' }}</span>
                @if (event()!.online) {
                  <span class="text-sm font-medium px-3 py-1 rounded-full bg-gray-100 text-gray-700">En ligne</span>
                }
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div class="bg-slate-50 rounded-lg p-4">
                  <p class="text-slate-500">Lieu</p>
                  <p class="font-semibold text-slate-800">{{ event()!.location || '—' }}</p>
                </div>
                <div class="bg-slate-50 rounded-lg p-4">
                  <p class="text-slate-500">Date</p>
                  <p class="font-semibold text-slate-800">{{ event()!.date || '—' }}</p>
                </div>
                <div class="bg-slate-50 rounded-lg p-4">
                  <p class="text-slate-500">Tickets</p>
                  <p class="font-semibold text-slate-800">{{ event()!.nbTickets ?? '—' }}</p>
                </div>
                <div class="bg-slate-50 rounded-lg p-4">
                  <p class="text-slate-500">Lien de réunion</p>
                  <p class="font-semibold text-slate-800 truncate">{{ event()!.meetingLink || '—' }}</p>
                </div>
              </div>

              <div class="flex flex-wrap gap-3 mt-8">
                <a routerLink="/events" class="px-4 py-2 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 transition">Retour</a>
                <a [routerLink]="['/tickets']" [queryParams]="{ eventId: event()!.id }" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition">Tickets</a>
                @if (role.isAdmin()) {
                  <a [routerLink]="['/events', event()!.id, 'edit']" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition">Modifier</a>
                  <button (click)="remove()" class="text-red-600 px-4 py-2 rounded-lg hover:bg-red-50 transition">Supprimer</button>
                }
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class EventDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private service = inject(EventService);
  role = inject(RoleService);

  event = signal<EventItem | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error.set('ID invalide');
      this.loading.set(false);
      return;
    }
    this.service.get(id).subscribe({
      next: (e) => {
        this.event.set(e);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger l’événement');
        this.loading.set(false);
      },
    });
  }

  remove(): void {
    const id = this.event()?.id;
    if (!id) return;
    this.service.delete(id).subscribe({
      next: () => this.router.navigate(['/events']),
      error: () => this.error.set('Suppression échouée'),
    });
  }
}