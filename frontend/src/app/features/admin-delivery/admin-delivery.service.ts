import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { Deliverer, Delivery, DeliveryFee, DeliveryType, Vehicle } from '../delivery/delivery.models';

/** Administration du microservice Delivery : livreurs, véhicules, affectations. */
@Injectable({ providedIn: 'root' })
export class AdminDeliveryService {
  private http = inject(HttpClient);
  private api = inject(ApiService);

  // Livraisons (statistiques et affectation)
  listDeliveries(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(this.api.url('/api/deliveries'));
  }

  assignDeliverer(deliveryId: number, delivererId: number): Observable<Delivery> {
    return this.http.put<Delivery>(this.api.url(`/api/deliveries/${deliveryId}/deliverer/${delivererId}`), {});
  }

  // Tarifs de livraison
  updateFee(type: DeliveryType, amount: number): Observable<DeliveryFee> {
    return this.http.put<DeliveryFee>(this.api.url(`/api/deliveries/fees/${type}`), { amount });
  }

  // Livreurs
  listDeliverers(): Observable<Deliverer[]> {
    return this.http.get<Deliverer[]>(this.api.url('/api/deliverers'));
  }

  createDeliverer(deliverer: Deliverer): Observable<Deliverer> {
    return this.http.post<Deliverer>(this.api.url('/api/deliverers'), deliverer);
  }

  updateDeliverer(id: number, deliverer: Deliverer): Observable<Deliverer> {
    return this.http.put<Deliverer>(this.api.url(`/api/deliverers/${id}`), deliverer);
  }

  deleteDeliverer(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/api/deliverers/${id}`));
  }

  // Véhicules
  listVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(this.api.url('/api/vehicles'));
  }

  createVehicle(vehicle: Vehicle): Observable<Vehicle> {
    return this.http.post<Vehicle>(this.api.url('/api/vehicles'), vehicle);
  }

  updateVehicle(id: number, vehicle: Vehicle): Observable<Vehicle> {
    return this.http.put<Vehicle>(this.api.url(`/api/vehicles/${id}`), vehicle);
  }

  deleteVehicle(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/api/vehicles/${id}`));
  }
}
