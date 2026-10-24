import { HttpErrorResponse } from '@angular/common/http';

export type DeliveryStatus = 'PENDING' | 'IN_PROGRESS' | 'DELIVERED' | 'CANCELLED';
export type DeliveryType = 'STANDARD' | 'EXPRESS' | 'SAME_DAY';
export type VehicleType = 'CAR' | 'MOTORCYCLE' | 'TRUCK';

export interface Delivery {
  id?: number;
  recipientName: string;
  address: string;
  city: string;
  postalCode: string;
  phone: string;
  deliveryFee: number | null;
  deliveryType: DeliveryType;
  productId: number | null;
  status?: DeliveryStatus;
  createdAt?: string;
  delivererId?: number | null;
}

export interface Deliverer {
  id?: number;
  fullName: string;
  phone: string;
  available?: boolean;
  vehicleId?: number | null;
}

export interface Vehicle {
  id?: number;
  type: VehicleType;
  registrationNumber: string;
  available?: boolean;
}

/** Tarif fixé par l'admin pour un type de livraison. */
export interface DeliveryFee {
  deliveryType: DeliveryType;
  amount: number;
}

/** Produit renvoyé par le microservice marketplace (GET /mic2/products). */
export interface Product {
  id: number;
  name: string;
  price: number;
  stock: number;
  active: boolean;
  shopId: number;
  shopName: string;
}

export const STATUS_LABELS: Record<DeliveryStatus, string> = {
  PENDING: 'En attente',
  IN_PROGRESS: 'En cours',
  DELIVERED: 'Livrée',
  CANCELLED: 'Annulée',
};

export const STATUS_CLASSES: Record<DeliveryStatus, string> = {
  PENDING: 'bg-amber-100 text-amber-800',
  IN_PROGRESS: 'bg-blue-100 text-blue-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-slate-200 text-slate-700',
};

export const TYPE_LABELS: Record<DeliveryType, string> = {
  STANDARD: 'Standard',
  EXPRESS: 'Express',
  SAME_DAY: 'Le jour même',
};

export const VEHICLE_LABELS: Record<VehicleType, string> = {
  CAR: 'Voiture',
  MOTORCYCLE: 'Moto',
  TRUCK: 'Camion',
};

/** Message lisible depuis une erreur HTTP (le backend renvoie { message }). */
export function apiError(err: unknown, fallback: string): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) return 'Serveur injoignable (gateway ou microservice arrêté)';
    return err.error?.message || fallback;
  }
  return fallback;
}
