import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';

@Component({
  selector: 'app-event-form',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-50 to-red-50 p-6">
      <div class="max-w-2xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl p-8">
          <h1 class="text-3xl font-bold text-slate-800 mb-6">{{ isEdit ? 'Modifier' : 'Nouvel' }} événement</h1>
          <form #f="ngForm" (ngSubmit)="submit(f)" class="space-y-5">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Nom</label>
              <input [(ngModel)]="model.name" name="name" #name="ngModel" placeholder="Nom de l'événement" required minlength="5"
                     class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none" />
              @if (name.invalid && (name.touched || f.submitted)) {
                <p class="text-red-600 text-sm mt-1">
                  @if (name.errors?.['required']) { Le nom est obligatoire. }
                  @else if (name.errors?.['minlength']) { Le nom doit contenir au moins 5 lettres. }
                </p>
              }
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Date</label>
                <input [(ngModel)]="model.date" name="date" #date="ngModel" type="date" [min]="today" required
                       class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none" />
                @if (date.invalid && (date.touched || f.submitted)) {
                  <p class="text-red-600 text-sm mt-1">
                    @if (date.errors?.['required']) { La date est obligatoire. }
                    @else { La date doit être aujourd'hui ou dans le futur. }
                  </p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Lieu (Bloc)</label>
                <select [(ngModel)]="model.location" name="location" #location="ngModel" required (ngModelChange)="onLocationChange($event)"
                        class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none">
                  <option value="" disabled>Choisir un bloc</option>
                  <option value="A">Bloc A</option>
                  <option value="B">Bloc B</option>
                  <option value="C">Bloc C</option>
                  <option value="D">Bloc D</option>
                  <option value="E">Bloc E</option>
                  <option value="G">Bloc G</option>
                  <option value="H">Bloc H</option>
                  <option value="M">Bloc M</option>
                </select>
                @if (location.invalid && (location.touched || f.submitted)) {
                  <p class="text-red-600 text-sm mt-1">Le lieu est obligatoire.</p>
                }
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Lien de réunion</label>
              <input [(ngModel)]="model.meetingLink" name="meetingLink" placeholder="https://meet.google.com/..." class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Nombre de tickets</label>
                <input [(ngModel)]="model.nbTickets" name="nbTickets" #nbTickets="ngModel" type="number" min="0" required
                       placeholder="0" class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none" />
                @if (nbTickets.invalid && (nbTickets.touched || f.submitted)) {
                  <p class="text-red-600 text-sm mt-1">
                    @if (nbTickets.errors?.['required']) { Le nombre de tickets est obligatoire. }
                    @else { Le nombre de tickets ne peut pas être négatif. }
                  </p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Statut</label>
                <select [(ngModel)]="model.status" name="status" #status="ngModel" required
                        class="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-red-500 focus:border-red-500 outline-none">
                  <option value="" disabled>Choisir un statut</option>
                  <option value="PENDING">PENDING</option>
                  <option value="OPEN">OPEN</option>
                  <option value="PLANNED">PLANNED</option>
                </select>
                @if (status.invalid && (status.touched || f.submitted)) {
                  <p class="text-red-600 text-sm mt-1">Le statut est obligatoire.</p>
                }
              </div>
            </div>

            <label class="flex items-center gap-2 text-slate-700">
              <input [(ngModel)]="model.online" name="online" type="checkbox" class="rounded" (ngModelChange)="onOnlineChange($event)" />
              En ligne
            </label>

            <div class="flex gap-3 pt-2">
              <button type="submit" class="bg-red-600 text-white px-6 py-2.5 rounded-lg hover:bg-red-700 transition shadow">Enregistrer</button>
              <a routerLink="/events" class="px-6 py-2.5 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 transition">Annuler</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class EventFormComponent implements OnInit {
  private service = inject(EventService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isEdit = false;
  id: number | null = null;
  model: EventItem = { name: '' };
  today = new Date().toISOString().split('T')[0];

  onLocationChange(location: string): void {
    if (!this.model.online) {
      this.model.meetingLink = location ? `Bloc ${location}` : '';
    }
  }

  onOnlineChange(online: boolean): void {
    if (online) {
      this.model.meetingLink = 'https://meet.google.com/abc-defg-hij';
    } else {
      this.model.meetingLink = this.model.location ? `Bloc ${this.model.location}` : '';
    }
  }

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      this.isEdit = true;
      this.id = Number(param);
      this.service.get(this.id).subscribe({
        next: (e) => (this.model = e),
        error: () => {},
      });
    }
  }

  submit(f: any): void {
    if (f.invalid) return;
    if (this.isEdit && this.id) {
      this.service.update(this.id, this.model).subscribe({
        next: () => this.router.navigate(['/events', this.id]),
      });
    } else {
      this.service.create(this.model).subscribe({
        next: (created) => this.router.navigate(['/tickets'], { queryParams: { eventId: created.id } }),
      });
    }
  }
}