import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ServiceOffer, ServiceOfferService } from '../../../core/services/service-offer.service';

@Component({
  selector: 'app-service-new',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="max-w-xl mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">New service</h1>
      <form (ngSubmit)="submit()" class="bg-white rounded-xl shadow p-6 space-y-4">
        <input [(ngModel)]="model.name" name="name" placeholder="Name" required class="w-full border rounded px-3 py-2" />
        <textarea [(ngModel)]="model.description" name="description" placeholder="Description" class="w-full border rounded px-3 py-2"></textarea>
        <input [(ngModel)]="model.category" name="category" placeholder="Category" class="w-full border rounded px-3 py-2" />
        <input [(ngModel)]="model.price" name="price" type="number" placeholder="Price" class="w-full border rounded px-3 py-2" />
        <div class="flex gap-3">
          <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Create</button>
          <a routerLink="/services" class="px-4 py-2">Cancel</a>
        </div>
      </form>
    </div>
  `,
})
export class ServiceNewComponent {
  private api = inject(ServiceOfferService);
  private router = inject(Router);

  model: ServiceOffer = { name: '' };

  submit(): void {
    this.api.create(this.model).subscribe({ next: () => this.router.navigate(['/services']) });
  }
}
