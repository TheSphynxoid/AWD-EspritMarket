import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { Cart, CartItem } from '../models/cart.model';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-cart',
  imports: [RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent implements OnInit {
  private service = inject(MarketplaceService);
  cart = signal<Cart | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  private auth = inject(AuthService);

  /** Utilisateur connecté (sub Keycloak) ; la route est protégée par authGuard. */
  private get userId(): string {
    return this.auth.userId ?? '';
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.service.getCart(this.userId).subscribe({
      next: (c) => {
        this.cart.set(c);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load cart');
        this.loading.set(false);
      },
    });
  }

  remove(item: CartItem): void {
    if (!item.productId) return;
    this.service.removeFromCart(this.userId, item.productId).subscribe({
      next: () => this.load(),
      error: () => alert('Could not remove item'),
    });
  }

  clear(): void {
    this.service.clearCart(this.userId).subscribe({
      next: () => this.load(),
      error: () => alert('Could not clear cart'),
    });
  }
}