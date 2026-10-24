import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface ServiceOffer {
  id?: number;
  name: string;
  description?: string;
  category?: string;
  price?: number;
  status?: string;
  providerId?: number;
}

@Injectable({ providedIn: 'root' })
export class ServiceOfferService {
  constructor(private http: HttpClient, private api: ApiService) {}

  list(): Observable<ServiceOffer[]> {
    return this.http.get<ServiceOffer[]>(this.api.url('/srv'));
  }

  get(id: number): Observable<ServiceOffer> {
    return this.http.get<ServiceOffer>(this.api.url(`/srv/${id}`));
  }

  create(offer: ServiceOffer): Observable<ServiceOffer> {
    return this.http.post<ServiceOffer>(this.api.url('/srv'), offer);
  }

  update(id: number, offer: ServiceOffer): Observable<ServiceOffer> {
    return this.http.put<ServiceOffer>(this.api.url(`/srv/${id}`), offer);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/srv/${id}`));
  }
}
