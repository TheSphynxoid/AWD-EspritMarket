import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Shop, ShopRequest } from '../models/shop.model';
import { Product, ProductRequest } from '../models/product.model';
import { Cart } from '../models/cart.model';
import { Delivery } from '../models/delivery.model';

/** Via la gateway (même origine : proxy ng serve en dev, nginx en Docker). */
const BASE = '/gw';

@Injectable({ providedIn: 'root' })
export class MarketplaceService {
  constructor(private http: HttpClient) {}

  // Shops
  getShops(): Observable<Shop[]> {
    return this.http.get<Shop[]>(`${BASE}/mic2/shops`);
  }
  getShop(id: number): Observable<Shop> {
    return this.http.get<Shop>(`${BASE}/mic2/shops/${id}`);
  }
  createShop(shop: ShopRequest): Observable<Shop> {
    return this.http.post<Shop>(`${BASE}/mic2/shops`, shop);
  }
  deleteShop(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/mic2/shops/${id}`);
  }

  // Products
  getProducts(shopId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${BASE}/mic2/shops/${shopId}/products`);
  }
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${BASE}/mic2/products`);
  }
  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${BASE}/mic2/products/${id}`);
  }
  createProduct(shopId: number, product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(`${BASE}/mic2/shops/${shopId}/products`, product);
  }
  updateProduct(id: number, product: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${BASE}/mic2/products/${id}`, product);
  }
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/mic2/products/${id}`);
  }

  // Cart
  getCart(userId: string): Observable<Cart> {
    return this.http.get<Cart>(`${BASE}/mic2/paniers/${userId}`);
  }
  addToCart(userId: string, productId: number, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${BASE}/mic2/paniers/${userId}/items`, { productId, quantity });
  }
  removeFromCart(userId: string, productId: number): Observable<void> {
    return this.http.delete<void>(`${BASE}/mic2/paniers/${userId}/items/${productId}`);
  }
  clearCart(userId: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/mic2/paniers/${userId}`);
  }

  // Delivery (synchrone via Feign)
  createDeliverySync(d: Delivery): Observable<Delivery> {
    return this.http.post<Delivery>(`${BASE}/mic2/deliveries/sync`, d);
  }
  getDeliverySync(id: number): Observable<Delivery> {
    return this.http.get<Delivery>(`${BASE}/mic2/deliveries/sync/${id}`);
  }
  listDeliveriesSync(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(`${BASE}/mic2/deliveries/sync`);
  }
  updateDeliveryStatusSync(id: number, status: string): Observable<Delivery> {
    return this.http.patch<Delivery>(`${BASE}/mic2/deliveries/sync/${id}/status?status=${status}`, {});
  }
}