import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type ServiceMode = 'ON_SITE' | 'REMOTE' | 'BOTH';
export type ServiceStatus = 'ACTIVE' | 'PAUSED';
export type BookingStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'CANCELLED' | 'COMPLETED';

/** Service tel que renvoyé par SRV (le prestataire vient du token, côté serveur). */
export interface ServiceOffer {
  id: number;
  name: string;
  description?: string | null;
  category?: string | null;
  price?: number | null;
  durationMinutes?: number | null;
  mode?: ServiceMode | null;
  location?: string | null;
  imageUrl?: string | null;
  status: ServiceStatus;
  providerId?: string | null;
  providerName?: string | null;
  bookingCount: number;
  createdAt?: string;
  updatedAt?: string | null;
}

/** Création / modification. */
export interface ServiceRequest {
  name: string;
  description?: string | null;
  category?: string | null;
  price: number | null;
  durationMinutes?: number | null;
  mode?: ServiceMode | null;
  location?: string | null;
  imageUrl?: string | null;
  status?: ServiceStatus;
}

export interface ServiceFilters {
  q?: string;
  category?: string;
  minPrice?: number | null;
  maxPrice?: number | null;
}

export interface Booking {
  id: number;
  serviceId: number;
  serviceName: string;
  servicePrice?: number | null;
  serviceImageUrl?: string | null;
  providerId?: string | null;
  providerName?: string | null;
  bookerId: string;
  bookerName?: string | null;
  scheduledAt: string;
  note?: string | null;
  providerNote?: string | null;
  status: BookingStatus;
  createdAt: string;
  updatedAt?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ServiceOfferService {
  constructor(private http: HttpClient, private api: ApiService) {}

  // ── Services ──
  list(filters: ServiceFilters = {}): Observable<ServiceOffer[]> {
    let params = new HttpParams();
    if (filters.q?.trim()) params = params.set('q', filters.q.trim());
    if (filters.category) params = params.set('category', filters.category);
    if (filters.minPrice != null) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null) params = params.set('maxPrice', filters.maxPrice);
    return this.http.get<ServiceOffer[]>(this.api.url('/srv'), { params });
  }

  categories(): Observable<string[]> {
    return this.http.get<string[]>(this.api.url('/srv/categories'));
  }

  mine(): Observable<ServiceOffer[]> {
    return this.http.get<ServiceOffer[]>(this.api.url('/srv/mine'));
  }

  get(id: number): Observable<ServiceOffer> {
    return this.http.get<ServiceOffer>(this.api.url(`/srv/${id}`));
  }

  create(req: ServiceRequest): Observable<ServiceOffer> {
    return this.http.post<ServiceOffer>(this.api.url('/srv'), req);
  }

  update(id: number, req: ServiceRequest): Observable<ServiceOffer> {
    return this.http.put<ServiceOffer>(this.api.url(`/srv/${id}`), req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/srv/${id}`));
  }

  // ── Réservations ──
  book(serviceId: number, scheduledAt: string, note?: string): Observable<Booking> {
    return this.http.post<Booking>(this.api.url('/srv/bookings'), { serviceId, scheduledAt, note });
  }

  myBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.api.url('/srv/bookings/mine'));
  }

  receivedBookings(status?: BookingStatus): Observable<Booking[]> {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<Booking[]>(this.api.url('/srv/bookings/received'), { params });
  }

  changeBookingStatus(id: number, status: BookingStatus, note?: string): Observable<Booking> {
    return this.http.patch<Booking>(this.api.url(`/srv/bookings/${id}/status`), { status, note });
  }
}

/** Libellés et couleurs partagés par les pages SRV. */
export const BOOKING_STATUS_LABEL: Record<BookingStatus, string> = {
  PENDING: 'En attente',
  ACCEPTED: 'Acceptée',
  DECLINED: 'Refusée',
  CANCELLED: 'Annulée',
  COMPLETED: 'Terminée',
};

export const BOOKING_STATUS_CLASS: Record<BookingStatus, string> = {
  PENDING: 'bg-amber-100 text-amber-800',
  ACCEPTED: 'bg-green-100 text-green-800',
  DECLINED: 'bg-red-100 text-red-800',
  CANCELLED: 'bg-slate-200 text-slate-700',
  COMPLETED: 'bg-blue-100 text-blue-800',
};

export const SERVICE_MODE_LABEL: Record<ServiceMode, string> = {
  ON_SITE: 'Sur place',
  REMOTE: 'À distance',
  BOTH: 'Sur place ou à distance',
};

export function formatDuration(minutes?: number | null): string {
  if (!minutes) return '—';
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  if (h === 0) return `${m} min`;
  return m === 0 ? `${h} h` : `${h} h ${m}`;
}

/** Message lisible depuis une erreur HTTP de SRV ({ message, errors{champ: message} }). */
export function apiErrorMessage(err: unknown, fallback: string): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) return 'Service injoignable, réessayez.';
    if (err.status === 401) return 'Connectez-vous pour continuer.';
    const body = err.error;
    if (body?.errors && typeof body.errors === 'object') {
      const details = Object.values(body.errors).join(' · ');
      if (details) return details;
    }
    if (typeof body?.message === 'string' && body.message) return body.message;
  }
  return fallback;
}
