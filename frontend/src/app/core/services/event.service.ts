import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface EventItem {
  id?: number;
  date?: string;
  location?: string;
  meetingLink?: string;
  name: string;
  nbTickets?: number;
  online?: boolean;
  status?: string;
  userId?: number;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  constructor(private http: HttpClient, private api: ApiService) {}

  list(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(this.api.url('/event/mic1/events'));
  }

  get(id: number): Observable<EventItem> {
    return this.http.get<EventItem>(this.api.url(`/event/mic1/events/${id}`));
  }

  create(event: EventItem): Observable<EventItem> {
    return this.http.post<EventItem>(this.api.url('/event/mic1/events'), event);
  }

  update(id: number, event: EventItem): Observable<EventItem> {
    return this.http.put<EventItem>(this.api.url(`/event/mic1/events/${id}`), event);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/event/mic1/events/${id}`));
  }

  byStatus(status: string): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(this.api.url(`/event/mic1/events/by-status/${status}`));
  }

  byUser(userId: number): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(this.api.url(`/event/mic1/events/by-user/${userId}`));
  }
}