import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { Deliverer, Delivery, DeliveryFee, DeliveryStatus, Product } from './delivery.models';

@Injectable({ providedIn: 'root' })
export class DeliveryService {
  private http = inject(HttpClient);
  private api = inject(ApiService);

  // Livraisons
  list(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(this.api.url('/api/deliveries'));
  }

  get(id: number): Observable<Delivery> {
    return this.http.get<Delivery>(this.api.url(`/api/deliveries/${id}`));
  }

  create(delivery: Delivery): Observable<Delivery> {
    return this.http.post<Delivery>(this.api.url('/api/deliveries'), delivery);
  }

  update(id: number, delivery: Delivery): Observable<Delivery> {
    return this.http.put<Delivery>(this.api.url(`/api/deliveries/${id}`), delivery);
  }

  updateStatus(id: number, status: DeliveryStatus): Observable<Delivery> {
    return this.http.patch<Delivery>(this.api.url(`/api/deliveries/${id}/status`), { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/api/deliveries/${id}`));
  }

  // Tarifs fixés par l'admin (lecture)
  listFees(): Observable<DeliveryFee[]> {
    return this.http.get<DeliveryFee[]>(this.api.url('/api/deliveries/fees'));
  }

  // Livreur affecté (lecture seule ; la gestion est dans admin-delivery)
  getDeliverer(id: number): Observable<Deliverer> {
    return this.http.get<Deliverer>(this.api.url(`/api/deliverers/${id}`));
  }

  // Produits du microservice marketplace (pour choisir le produit à livrer)
  listProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.api.url('/mic2/products'));
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(this.api.url(`/mic2/products/${id}`));
  }
}
