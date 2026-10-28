import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-event-list',
  imports: [RouterLink],
  template: `
    <div class="max-w-6xl mx-auto p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-3xl font-bold">Événements</h1>
        @if (role.isAdmin()) {
          <a routerLink="/events/new" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Nouvel événement</a>
        }
      </div>
      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
      } @else if (events().length === 0) {
        <p class="text-slate-500">Aucun événement.</p>
      } @else {
        <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          @for (e of events(); track e.id) {
            <a [routerLink]="['/events', e.id]" class="block bg-white rounded-xl shadow p-5 hover:shadow-lg transition">
              <h2 class="text-xl font-semibold">{{ e.name }}</h2>
              <p class="text-slate-600 text-sm mt-2">{{ e.location }}</p>
              <div class="mt-4 flex items-center justify-between text-sm">
                <span class="text-blue-600 font-medium">{{ e.status || '—' }}</span>
                <span class="font-bold">{{ e.nbTickets != null ? (e.nbTickets + ' tickets') : '—' }}</span>
              </div>
            </a>
          }
        </div>
      }
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