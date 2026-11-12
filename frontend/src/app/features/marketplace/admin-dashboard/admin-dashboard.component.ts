import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MarketplaceService } from '../services/marketplace.service';
import { AuthService } from '../../../core/services/auth.service';
import { Shop } from '../models/shop.model';
import { Product, ProductRequest } from '../models/product.model';

@Component({
  selector: 'app-admin-dashboard',
  imports: [FormsModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css',
})
export class AdminDashboardComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private service = inject(MarketplaceService);
  private auth = inject(AuthService);
  shops = signal<Shop[]>([]);
  currentUserId = signal<number | null>(null);
  products = signal<Product[]>([]);
  selectedShopId = signal<number | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  // form state
  editingId: number | null = null;
  name = '';
  description = '';
  price: number | null = null;
  stock: number | null = null;
  categoryId: number | null = null;

  ngOnInit(): void {
    const uid = this.auth.userId;
    this.currentUserId.set(uid ? Number(uid) : null);

    const qp = this.route.snapshot.queryParamMap.get('shopId');
    if (qp) {
      this.success.set('Shop created successfully. A confirmation email has been sent (async).');
    }
    this.service.getShops().subscribe({
      next: (s) => {
        this.shops.set(s ?? []);
        if (qp) {
          const id = Number(qp);
          this.selectedShopId.set(id);
          this.checkOwnership(id);
          this.loadProducts();
        }
      },
      error: () => this.error.set('Could not load shops'),
    });
  }

  checkOwnership(shopId: number): void {
    // Verification d'appartenance desactivee pour la demo :
    // l'ID utilisateur Keycloak est un UUID, pas un nombre.
    // En production, le backend devrait verifier le proprietaire.
    this.error.set(null);
  }

  onShopChange(id: number): void {
    this.selectedShopId.set(id);
    this.checkOwnership(id);
    this.loadProducts();
  }

  loadProducts(): void {
    const id = this.selectedShopId();
    if (!id) return;
    this.loading.set(true);
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

  submit(): void {
    const shopId = this.selectedShopId();
    if (!shopId || !this.name || this.price == null || this.stock == null) {
      alert('Please fill required fields');
      return;
    }
    // Pas de blocage d'acces pour la demo
    const payload: ProductRequest = {
      name: this.name,
      description: this.description,
      price: this.price,
      stock: this.stock,
      categoryId: this.categoryId ?? undefined,
    };
    const req = this.editingId
      ? this.service.updateProduct(this.editingId, payload)
      : this.service.createProduct(shopId, payload);
    req.subscribe({
      next: () => {
        this.resetForm();
        this.loadProducts();
        this.success.set(this.editingId ? 'Product updated.' : 'Product created.');
        this.editingId = null;
      },
      error: () => alert('Operation failed'),
    });
  }

  edit(p: Product): void {
    this.editingId = p.id ?? null;
    this.name = p.name;
    this.description = p.description ?? '';
    this.price = p.price;
    this.stock = p.stock;
    this.categoryId = p.categoryId ?? null;
  }

  remove(p: Product): void {
    if (!p.id || !confirm('Delete this product?')) return;
    this.service.deleteProduct(p.id).subscribe({
      next: () => this.loadProducts(),
      error: () => alert('Could not delete product'),
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.name = '';
    this.description = '';
    this.price = null;
    this.stock = null;
    this.categoryId = null;
  }
}