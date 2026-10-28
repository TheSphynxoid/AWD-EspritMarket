import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventItem, EventService } from '../../../core/services/event.service';

@Component({
  selector: 'app-event-form',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="max-w-xl mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">{{ isEdit ? 'Modifier' : 'Nouvel' }} événement</h1>
      <form #f="ngForm" (ngSubmit)="submit(f)" class="bg-white rounded-xl shadow p-6 space-y-4">
        <div>
          <input [(ngModel)]="model.name" name="name" #name="ngModel" placeholder="Nom" required minlength="5"
                 class="w-full border rounded px-3 py-2" />
          @if (name.invalid && (name.touched || f.submitted)) {
            <p class="text-red-600 text-sm mt-1">
              @if (name.errors?.['required']) { Le nom est obligatoire. }
              @else if (name.errors?.['minlength']) { Le nom doit contenir au moins 5 lettres. }
            </p>
          }
        </div>
        <div>
          <input [(ngModel)]="model.date" name="date" #date="ngModel" type="date" [min]="today" required
                 class="w-full border rounded px-3 py-2" />
          @if (date.invalid && (date.touched || f.submitted)) {
            <p class="text-red-600 text-sm mt-1">
              @if (date.errors?.['required']) { La date est obligatoire. }
              @else { La date doit être aujourd'hui ou dans le futur. }
            </p>
          }
        </div>
        <div>
          <input [(ngModel)]="model.location" name="location" #location="ngModel" placeholder="Lieu (Bloc A, B, C, D, E, G, H, M)" required
                 pattern="^(Bloc )?[ABCDEGHM]$"
                 class="w-full border rounded px-3 py-2" />
          @if (location.invalid && (location.touched || f.submitted)) {
            <p class="text-red-600 text-sm mt-1">
              @if (location.errors?.['required']) { Le lieu est obligatoire. }
              @else { Le lieu doit être un bloc parmi A, B, C, D, E, G, H, M. }
            </p>
          }
        </div>
        <div>
          <input [(ngModel)]="model.meetingLink" name="meetingLink" placeholder="Lien de réunion" class="w-full border rounded px-3 py-2" />
        </div>
        <div>
          <input [(ngModel)]="model.nbTickets" name="nbTickets" #nbTickets="ngModel" type="number" min="0" required
                 placeholder="Nombre de tickets" class="w-full border rounded px-3 py-2" />
          @if (nbTickets.invalid && (nbTickets.touched || f.submitted)) {
            <p class="text-red-600 text-sm mt-1">
              @if (nbTickets.errors?.['required']) { Le nombre de tickets est obligatoire. }
              @else { Le nombre de tickets ne peut pas être négatif. }
            </p>
          }
        </div>
        <div>
          <input [(ngModel)]="model.status" name="status" #status="ngModel" placeholder="Statut (PENDING, OPEN, PLANNED)" required
                 pattern="PENDING|OPEN|PLANNED"
                 class="w-full border rounded px-3 py-2" />
          @if (status.invalid && (status.touched || f.submitted)) {
            <p class="text-red-600 text-sm mt-1">
              @if (status.errors?.['required']) { Le statut est obligatoire. }
              @else { Le statut doit être PENDING, OPEN ou PLANNED. }
            </p>
          }
        </div>
        <label class="flex items-center gap-2">
          <input [(ngModel)]="model.online" name="online" type="checkbox" />
          En ligne
        </label>
        <div class="flex gap-3">
          <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Enregistrer</button>
          <a routerLink="/events" class="px-4 py-2">Annuler</a>
        </div>
      </form>
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
        next: (created) => this.router.navigate(['/events', created.id]),
      });
    }
  }
}