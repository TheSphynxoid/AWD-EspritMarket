import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { Shop } from '../models/shop.model';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-shop-detail',
  imports: [RouterLink],
  templateUrl: './shop-detail.component.html',
  styleUrl: './shop-detail.component.css',
})
export class ShopDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private service = inject(MarketplaceService);
  shop = signal<Shop | null>(null);
  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.service.getShop(id).subscribe({
      next: (s) => this.shop.set(s),
      error: () => this.error.set('Could not load shop'),
    });
    this.service.getProducts(id).subscribe({
      next: (p) => {
        this.products.set(p ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load products');
        this.loading.set(false);
      },
    });
  }

  addToCart(product: Product): void {
    const userId = 1; // demo user
    if (!product.id) return;
    this.service.addToCart(userId, product.id, 1).subscribe({
      next: () => alert('Added to cart'),
      error: () => alert('Could not add to cart'),
    });
  }
}