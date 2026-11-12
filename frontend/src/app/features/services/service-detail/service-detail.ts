import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ServiceOffer, ServiceOfferService } from '../../../core/services/service-offer.service';
import { AuthService } from '../../../core/services/auth.service';

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
            <button (click)="book()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500">Book this service</button>
            <a routerLink="/services" class="text-blue-600">Back</a>
            <button (click)="remove()" class="text-red-600">Delete</button>
          </div>
          @if (bookingStatus()) { <p class="text-sm text-slate-600">{{ bookingStatus() }}</p> }
        </div>
      }
    </div>
  `,
})
export class ServiceDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private serviceApi = inject(ServiceOfferService);
  private auth = inject(AuthService);

  service = signal<ServiceOffer | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  bookingStatus = signal<string | null>(null);

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

  book(): void {
    const id = this.service()?.id;
    const bookerId = this.auth.userId;
    if (!id || !bookerId) {
      this.bookingStatus.set('Please log in to book');
      return;
    }
    this.bookingStatus.set('Booking...');
    this.serviceApi.book(id, bookerId).subscribe({
      next: () => this.bookingStatus.set('Booked! Notification sent.'),
      error: () => this.bookingStatus.set('Booking failed'),
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
