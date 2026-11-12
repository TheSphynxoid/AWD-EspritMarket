import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TicketItem, TicketService } from '../../core/services/ticket.service';
import { EventItem, EventService } from '../../core/services/event.service';

@Component({
  selector: 'app-tickets',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-red-50 p-6">
      <div class="max-w-4xl mx-auto">
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-4xl font-bold text-slate-800">Tickets</h1>
            <p class="text-slate-500 mt-1">Achetez et gérez vos tickets d'événement</p>
          </div>
          <a routerLink="/events" class="text-blue-600 hover:underline">← Retour aux événements</a>
        </div>

        @if (event()) {
          <div class="bg-white rounded-2xl shadow-md p-6 mb-6 border-l-4 border-red-500">
            <h2 class="text-2xl font-semibold text-slate-800">{{ event()!.name }}</h2>
            <p class="text-slate-600 mt-1">{{ event()!.location }} — {{ event()!.date }}</p>
          </div>
        }

        <div class="bg-white rounded-2xl shadow-md p-6 mb-6">
          <h2 class="text-xl font-bold text-slate-800 mb-4">Acheter un ticket</h2>
          <form (ngSubmit)="buy()" class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <select [(ngModel)]="model.type" name="type" required (change)="onTypeChange($any($event.target).value)"
                    class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none">
              <option value="" disabled>Choisir un type</option>
              <option value="STANDARD">STANDARD (25 €)</option>
              <option value="VIP">VIP (50 €)</option>
              <option value="PREMIUM">PREMIUM (100 €)</option>
            </select>
            <input [(ngModel)]="model.price" name="price" type="number" min="0" placeholder="Prix" required
                   class="border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none" />
            <div class="md:col-span-3 flex items-center gap-4">
              <button type="submit" class="bg-red-600 text-white px-6 py-2.5 rounded-lg hover:bg-red-700 transition shadow">Acheter</button>
              @if (message()) {
                <p class="text-red-600 text-sm font-medium">{{ message() }}</p>
              }
              @if (error()) {
                <p class="text-red-600 text-sm font-medium">{{ error() }}</p>
              }
            </div>
          </form>
        </div>

        <div class="bg-white rounded-2xl shadow-md p-6">
          <h2 class="text-xl font-bold text-slate-800 mb-4">Tickets existants</h2>
          @if (loading()) {
            <div class="flex justify-center py-10">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600"></div>
            </div>
          } @else if (tickets().length === 0) {
            <p class="text-slate-500 text-center py-10">Aucun ticket pour le moment.</p>
          } @else {
            <div class="space-y-3">
              @for (t of tickets(); track t.id) {
                <div class="border border-slate-200 rounded-xl p-4 flex items-center justify-between hover:shadow-md transition">
                  <div>
                    <p class="font-semibold text-slate-800">{{ t.type }} — {{ t.price }} €</p>
                    <p class="text-sm text-slate-500">Statut: {{ t.paymentStatus || '—' }} · Event #{{ t.eventId }}</p>
                  </div>
                  <button (click)="remove(t.id!)" class="text-red-500 hover:text-red-700 text-sm font-medium">Supprimer</button>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `,
})
export class TicketsComponent implements OnInit {
  private ticketService = inject(TicketService);
  private eventService = inject(EventService);
  private route = inject(ActivatedRoute);

  event = signal<EventItem | null>(null);
  tickets = signal<TicketItem[]>([]);
  loading = signal(true);
  message = signal<string | null>(null);
  error = signal<string | null>(null);

  model: TicketItem = { eventId: 0, type: 'STANDARD', price: 0 };

  ngOnInit(): void {
    const eventIdParam = this.route.snapshot.queryParamMap.get('eventId');
    const eventId = eventIdParam ? Number(eventIdParam) : null;
    if (eventId) {
      this.model.eventId = eventId;
      this.eventService.get(eventId).subscribe({
        next: (e) => this.event.set(e),
        error: () => {},
      });
    }
    this.loadTickets();
  }

  onTypeChange(type: string): void {
    const prices: Record<string, number> = { STANDARD: 25, VIP: 50, PREMIUM: 100 };
    if (prices[type] != null) {
      this.model.price = prices[type];
    }
  }

  loadTickets(): void {
    this.loading.set(true);
    this.ticketService.list().subscribe({
      next: (data) => {
        const eventId = this.model.eventId;
        const filtered = eventId ? (data ?? []).filter((t) => t.eventId === eventId) : (data ?? []);
        this.tickets.set(filtered);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les tickets');
        this.loading.set(false);
      },
    });
  }

  buy(): void {
    this.message.set(null);
    this.error.set(null);
    if (!this.model.eventId) {
      this.error.set('Event ID manquant');
      return;
    }
    this.ticketService.create(this.model).subscribe({
      next: () => {
        this.message.set('Ticket acheté avec succès');
        this.model = { eventId: this.model.eventId, type: 'STANDARD', price: 0 };
        this.loadTickets();
      },
      error: () => this.error.set("Échec de l'achat"),
    });
  }

  remove(id: number): void {
    this.ticketService.delete(id).subscribe({
      next: () => this.loadTickets(),
      error: () => this.error.set('Suppression échouée'),
    });
  }
}