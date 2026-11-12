import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'access_token';
  readonly isAuthenticated = signal<boolean>(this.readValidToken() !== null);

  constructor(private http: HttpClient) {}

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post('/gw/user/register', {
      username,
      email,
      password,
    });
  }

  get token(): string | null {
    const t = this.readValidToken();
    if (!t && this.isAuthenticated()) this.logout();
    return t;
  }

  /** Token stocké, ou null s'il est absent / expiré (le token Keycloak dure peu). */
  private readValidToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    const t = localStorage.getItem(this.tokenKey);
    if (!t) return null;
    try {
      const b64 = t.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const exp = JSON.parse(atob(b64.padEnd(Math.ceil(b64.length / 4) * 4, '='))).exp;
      if (typeof exp === 'number' && exp * 1000 <= Date.now()) {
        localStorage.removeItem(this.tokenKey);
        return null;
      }
    } catch {
      return null;
    }
    return t;
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

  /** Rôle de realm Keycloak présent dans le token (realm_access.roles). */
  hasRole(role: string): boolean {
    const t = this.token;
    if (!t) return false;
    try {
      const payload = JSON.parse(atob(t.split('.')[1]));
      return (payload.realm_access?.roles ?? []).includes(role);
    } catch {
      return false;
    }
  }

  login(username: string, password: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', 'user-service');
    body.set('username', username);
    body.set('password', password);

    return this.http
      .post('/auth/realms/espritmarket/protocol/openid-connect/token', body.toString(), {
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
