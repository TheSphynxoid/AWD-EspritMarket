import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ServiceOffer, ServiceOfferService } from '../../../core/services/service-offer.service';

@Component({
  selector: 'app-service-detail',
  imports: [RouterLink],
  template: `
    <div class="max-w-3xl mx-auto p-6">
      @if (service) {
        <div class="bg-white rounded-xl shadow p-6 space-y-4">
          <h1 class="text-3xl font-bold">{{ service.name }}</h1>
          <p class="text-slate-600">{{ service.description }}</p>
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div><span class="font-medium">Category:</span> {{ service.category || '—' }}</div>
            <div><span class="font-medium">Price:</span> {{ service.price != null ? (service.price + ' TND') : '—' }}</div>
            <div><span class="font-medium">Status:</span> {{ service.status || '—' }}</div>
            <div><span class="font-medium">Provider:</span> {{ service.providerId || '—' }}</div>
          </div>
          <div class="flex gap-3">
            <a routerLink="/services" class="text-blue-600">Back</a>
            <button (click)="remove()" class="text-red-600">Delete</button>
          </div>
        </div>
      } @else {
        <p class="text-slate-500">Loading...</p>
      }
    </div>
  `,
})
export class ServiceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private serviceApi = inject(ServiceOfferService);

  service: ServiceOffer | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.serviceApi.get(id).subscribe({ next: (s) => (this.service = s) });
  }

  remove(): void {
    if (!this.service?.id) return;
    this.serviceApi.delete(this.service.id).subscribe({
      next: () => this.router.navigate(['/services']),
    });
  }
}
