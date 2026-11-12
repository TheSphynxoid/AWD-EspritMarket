import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { Shop } from '../models/shop.model';

@Component({
  selector: 'app-shop-list',
  imports: [RouterLink],
  templateUrl: './shop-list.component.html',
  styleUrl: './shop-list.component.css',
})
export class ShopListComponent implements OnInit {
  private service = inject(MarketplaceService);
  shops = signal<Shop[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.service.getShops().subscribe({
      next: (data) => {
        this.shops.set(data ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load shops');
        this.loading.set(false);
      },
    });
  }
}