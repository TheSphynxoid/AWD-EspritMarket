import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JsonPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { Delivery } from '../models/delivery.model';

@Component({
  selector: 'app-delivery-test',
  imports: [FormsModule, RouterLink, JsonPipe],
  templateUrl: './delivery-test.component.html',
  styleUrl: './delivery-test.component.css',
})
export class DeliveryTestComponent implements OnInit {
  private service = inject(MarketplaceService);

  // form
  recipientName = '';
  address = '';
  city = '';
  postalCode = '';
  phone = '';
  deliveryType: 'STANDARD' | 'EXPRESS' | 'SAME_DAY' = 'STANDARD';

  loading = signal(false);
  error = signal<string | null>(null);
  result = signal<Delivery | null>(null);
  deliveries = signal<Delivery[]>([]);

  ngOnInit(): void {
    this.loadList();
  }

  loadList(): void {
    this.service.listDeliveriesSync().subscribe({
      next: (d) => this.deliveries.set(d ?? []),
      error: () => {},
    });
  }

  submit(): void {
    if (!this.recipientName || !this.address || !this.city || !this.postalCode || !this.phone) {
      this.error.set('Please fill all required fields');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const payload: Delivery = {
      recipientName: this.recipientName,
      address: this.address,
      city: this.city,
      postalCode: this.postalCode,
      phone: this.phone,
      deliveryFee: 8,
      deliveryType: this.deliveryType,
    };
    this.service.createDeliverySync(payload).subscribe({
      next: (d) => {
        this.result.set(d);
        this.loading.set(false);
        this.loadList();
      },
      error: (e) => {
        this.error.set('Sync call failed: ' + (e?.message || 'unknown'));
        this.loading.set(false);
      },
    });
  }

  updateStatus(d: Delivery, status: string): void {
    if (!d.id) return;
    this.service.updateDeliveryStatusSync(d.id, status).subscribe({
      next: () => this.loadList(),
      error: () => alert('Could not update status'),
    });
  }

  reset(): void {
    this.recipientName = '';
    this.address = '';
    this.city = '';
    this.postalCode = '';
    this.phone = '';
    this.deliveryType = 'STANDARD';
    this.result.set(null);
    this.error.set(null);
  }
}