import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { Product } from '../models/product.model';
import { Shop } from '../models/shop.model';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-product-list',
  imports: [RouterLink],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  private service = inject(MarketplaceService);
  private auth = inject(AuthService);
  private router = inject(Router);
  products = signal<Product[]>([]);
  shops = signal<Shop[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.service.getShops().subscribe({
      next: (s) => this.shops.set(s ?? []),
      error: () => {},
    });
    this.service.getAllProducts().subscribe({
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

  shopName(shopId?: number): string {
    const s = this.shops().find((x) => x.id === shopId);
    return s ? s.name : `Shop #${shopId}`;
  }

  addToCart(product: Product): void {
    const userId = this.auth.userId;
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }
    if (!product.id) return;
    this.service.addToCart(userId, product.id, 1).subscribe({
      next: () => alert('Added to cart'),
      error: () => alert('Could not add to cart'),
    });
  }
}