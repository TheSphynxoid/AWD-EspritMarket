import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ServiceOffer, ServiceOfferService } from '../../../core/services/service-offer.service';

@Component({
  selector: 'app-service-detail',
  imports: [RouterLink],
  template: `
    <div class="max-w-3xl mx-auto p-6">
      @if (loading()) {
        <p class="text-slate-500">Loading...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
        <a routerLink="/services" class="text-blue-600">Back</a>
      } @else if (service()) {
        <div class="bg-white rounded-xl shadow p-6 space-y-4">
          <h1 class="text-3xl font-bold">{{ service()!.name }}</h1>
          <p class="text-slate-600">{{ service()!.description }}</p>
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div><span class="font-medium">Category:</span> {{ service()!.category || '—' }}</div>
            <div><span class="font-medium">Price:</span> {{ service()!.price != null ? (service()!.price + ' TND') : '—' }}</div>
            <div><span class="font-medium">Status:</span> {{ service()!.status || '—' }}</div>
            <div><span class="font-medium">Provider:</span> {{ service()!.providerId || '—' }}</div>
          </div>
          <div class="flex gap-3">
            <a routerLink="/services" class="text-blue-600">Back</a>
            <button (click)="remove()" class="text-red-600">Delete</button>
          </div>
        </div>
      }
    </div>
  `,
})
export class ServiceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private serviceApi = inject(ServiceOfferService);

  service = signal<ServiceOffer | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error.set('Invalid service id');
      this.loading.set(false);
      return;
    }
    this.serviceApi.get(id).subscribe({
      next: (s) => {
        this.service.set(s);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load service');
        this.loading.set(false);
      },
    });
  }

  remove(): void {
    const id = this.service()?.id;
    if (!id) return;
    this.serviceApi.delete(id).subscribe({
      next: () => this.router.navigate(['/services']),
      error: () => this.error.set('Delete failed'),
    });
  }
}
