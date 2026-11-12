import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'access_token';
  readonly isAuthenticated = signal<boolean>(
    typeof localStorage !== 'undefined' && !!localStorage.getItem(this.tokenKey)
  );

  constructor(private http: HttpClient) {}

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post('http://localhost:8088/user/register', {
      username,
      email,
      password,
    });
  }

  get token(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.tokenKey);
  }

  get userId(): string | null {
    const t = this.token;
    if (!t) return null;
    try {
      const payload = JSON.parse(atob(t.split('.')[1]));
      return payload.sub ?? null;
    } catch {
      return null;
    }
  }

  login(username: string, password: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', 'user-service');
    body.set('username', username);
    body.set('password', password);

    return this.http
      .post('http://localhost:8180/realms/espritmarket/protocol/openid-connect/token', body.toString(), {
        headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
      })
      .pipe(
        tap((res: any) => {
          if (res?.access_token) {
            if (typeof localStorage !== 'undefined') localStorage.setItem(this.tokenKey, res.access_token);
            this.isAuthenticated.set(true);
          }
        })
      );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') localStorage.removeItem(this.tokenKey);
    this.isAuthenticated.set(false);
  }
}
