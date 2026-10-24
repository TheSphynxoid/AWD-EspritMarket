import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ServiceOffer, ServiceOfferService } from '../../../core/services/service-offer.service';

@Component({
  selector: 'app-service-list',
  imports: [RouterLink],
  template: `
    <div class="max-w-6xl mx-auto p-6">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-3xl font-bold">Services</h1>
        <a routerLink="/services/new" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">New service</a>
      </div>
      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        @for (s of services; track s.id) {
          <a [routerLink]="['/services', s.id]" class="block bg-white rounded-xl shadow p-5 hover:shadow-lg transition">
            <h2 class="text-xl font-semibold">{{ s.name }}</h2>
            <p class="text-slate-600 text-sm mt-2 line-clamp-3">{{ s.description }}</p>
            <div class="mt-4 flex items-center justify-between text-sm">
              <span class="text-blue-600 font-medium">{{ s.category || 'General' }}</span>
              <span class="font-bold">{{ s.price != null ? (s.price + ' TND') : '—' }}</span>
            </div>
          </a>
        } @empty {
          <p class="text-slate-500">No services yet.</p>
        }
      </div>
    </div>
  `,
})
export class ServiceListComponent implements OnInit {
  private service = inject(ServiceOfferService);
  services: ServiceOffer[] = [];

  ngOnInit(): void {
    this.service.list().subscribe({ next: (data) => (this.services = data) });
  }
}
