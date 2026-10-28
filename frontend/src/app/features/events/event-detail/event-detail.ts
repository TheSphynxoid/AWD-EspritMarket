import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-event-detail',
  imports: [RouterLink],
  template: `
    <div class="max-w-3xl mx-auto p-6">
      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
        <a routerLink="/events" class="text-blue-600">Retour</a>
      } @else if (event()) {
        <div class="bg-white rounded-xl shadow p-6 space-y-4">
          <h1 class="text-3xl font-bold">{{ event()!.name }}</h1>
          <p class="text-slate-600">{{ event()!.location }}</p>
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div><span class="font-medium">Date:</span> {{ event()!.date || '—' }}</div>
            <div><span class="font-medium">Tickets:</span> {{ event()!.nbTickets ?? '—' }}</div>
            <div><span class="font-medium">Online:</span> {{ event()!.online ? 'Oui' : 'Non' }}</div>
            <div><span class="font-medium">Status:</span> {{ event()!.status || '—' }}</div>
            <div><span class="font-medium">Lien:</span> {{ event()!.meetingLink || '—' }}</div>
            
          </div>
          <div class="flex gap-3">
            <a routerLink="/events" class="text-blue-600">Retour</a>
            @if (role.isAdmin()) {
              <a [routerLink]="['/events', event()!.id, 'edit']" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Modifier</a>
              <button (click)="remove()" class="text-red-600">Supprimer</button>
            }
          </div>
        </div>
      }
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