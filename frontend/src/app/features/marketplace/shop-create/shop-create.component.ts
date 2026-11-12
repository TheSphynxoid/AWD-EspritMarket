import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { AuthService } from '../../../core/services/auth.service';
import { ShopRequest } from '../models/shop.model';

@Component({
  selector: 'app-shop-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './shop-create.component.html',
  styleUrl: './shop-create.component.css',
})
export class ShopCreateComponent {
  private service = inject(MarketplaceService);
  private auth = inject(AuthService);
  private router = inject(Router);

  name = '';
  description = '';
  ownerEmail = '';
  loading = signal(false);
  error = signal<string | null>(null);

  get ownerId(): number {
    const id = this.auth.userId;
    return id ? Number(id) : 1;
  }

  get isLoggedIn(): boolean {
    return this.auth.isAuthenticated();
  }

  submit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    if (!this.name || !this.ownerEmail) {
      this.error.set('Please fill required fields');
      return;
    }
    const payload: ShopRequest = {
      name: this.name,
      description: this.description,
      ownerId: 1,
      ownerEmail: this.ownerEmail,
    };
    this.loading.set(true);
    this.service.createShop(payload).subscribe({
      next: (shop) => {
        this.loading.set(false);
        // Redirection vers le dashboard admin de la boutique creee
        this.router.navigate(['/marketplace/admin'], { queryParams: { shopId: shop.id } });
      },
      error: () => {
        this.error.set('Could not create shop');
        this.loading.set(false);
      },
    });
  }
}