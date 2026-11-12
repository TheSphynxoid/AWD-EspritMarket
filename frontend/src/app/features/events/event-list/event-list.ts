import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-event-list',
  imports: [RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-red-50 p-6">
      <div class="max-w-6xl mx-auto">
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Événements</h1>
            <p class="text-slate-500 mt-1">Découvrez et gérez les événements EspritMarket</p>
          </div>
          @if (role.isAdmin()) {
            <a routerLink="/events/new" class="bg-red-600 text-white px-5 py-2.5 rounded-lg shadow hover:bg-red-700 transition">+ Nouvel événement</a>
          }
        </div>

        @if (loading()) {
          <div class="flex justify-center py-20">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
          </div>
        } @else if (error()) {
          <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">{{ error() }}</div>
        } @else if (events().length === 0) {
          <div class="text-center py-20 text-slate-500">Aucun événement pour le moment.</div>
        } @else {
          <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            @for (e of events(); track e.id) {
              <a [routerLink]="['/events', e.id]" class="group bg-white rounded-2xl shadow-md hover:shadow-xl transition overflow-hidden">
                <div class="h-32 bg-gradient-to-r from-red-600 to-gray-800 flex items-center justify-center">
                  <span class="text-white text-4xl font-bold opacity-80">{{ (e.name || '?').charAt(0) }}</span>
                </div>
                <div class="p-5">
                  <div class="flex items-center justify-between mb-2">
                    <h2 class="text-xl font-semibold text-slate-800 group-hover:text-red-600 transition">{{ e.name }}</h2>
                    <span class="text-xs font-medium px-2 py-1 rounded-full bg-red-100 text-red-700">{{ e.status || '—' }}</span>
                  </div>
                  <p class="text-slate-500 text-sm flex items-center gap-1">
                    <span>📍</span> {{ e.location || '—' }}
                  </p>
                  <div class="mt-4 flex items-center justify-between text-sm">
                    <span class="text-slate-600">{{ e.date || '—' }}</span>
                    <span class="font-bold text-red-600">{{ e.nbTickets != null ? (e.nbTickets + ' tickets') : '—' }}</span>
                  </div>
                </div>
              </a>
            }
          </div>
        }
      </div>
    </div>
  `,
})
export class EventListComponent implements OnInit {
  private service = inject(EventService);
  role = inject(RoleService);
  events = signal<EventItem[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.service.list().subscribe({
      next: (data) => {
        this.events.set(data ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les événements');
        this.loading.set(false);
      },
    });
  }
}