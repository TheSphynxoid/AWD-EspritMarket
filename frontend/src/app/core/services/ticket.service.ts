import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface TicketItem {
  id?: number;
  paymentStatus?: string;
  price?: number;
  type?: string;
  eventId: number;
  userId?: number;
}

@Injectable({ providedIn: 'root' })
export class TicketService {
  constructor(private http: HttpClient, private api: ApiService) {}

  list(): Observable<TicketItem[]> {
    return this.http.get<TicketItem[]>(this.api.url('/mic1/tickets'));
  }

  byEvent(eventId: number): Observable<TicketItem[]> {
    return this.http.get<TicketItem[]>(this.api.url(`/mic1/tickets`));
  }

  create(ticket: TicketItem): Observable<TicketItem> {
    return this.http.post<TicketItem>(this.api.url('/mic1/tickets'), ticket);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/mic1/tickets/${id}`));
  }
}