import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/** Claims du JWT Keycloak renvoyés par GET /user/me. */
interface Me {
  sub: string;
  preferred_username?: string;
  name?: string;
  given_name?: string;
  family_name?: string;
  email?: string;
  email_verified?: boolean;
  realm_access?: { roles?: string[] };
  exp?: string | number;
}

/** Rôles techniques Keycloak masqués à l'affichage. */
const HIDDEN_ROLES = ['offline_access', 'uma_authorization'];

@Component({
  selector: 'app-profile',
  imports: [DatePipe, RouterLink],
  template: `
    <div class="max-w-2xl mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">Profil</h1>

      @if (loading()) {
        <p class="text-slate-500">Chargement...</p>
      } @else if (error()) {
        <p class="text-red-600">{{ error() }}</p>
      } @else if (me(); as m) {
        <div class="bg-white rounded-xl shadow p-6 space-y-5">
          <div class="flex items-center gap-4">
            <div class="h-14 w-14 rounded-full bg-slate-900 text-white flex items-center justify-center text-xl font-bold">
              {{ initial() }}
            </div>
            <div>
              <div class="text-xl font-semibold">{{ m.preferred_username }}</div>
              <div class="text-slate-500">{{ m.email || 'Pas d’email' }}
                @if (m.email_verified) { <span class="text-green-700 text-xs ml-1">✓ vérifié</span> }
              </div>
            </div>
          </div>

          <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
            <div><dt class="text-slate-500">Prénom</dt><dd class="font-medium">{{ m.given_name || '—' }}</dd></div>
            <div><dt class="text-slate-500">Nom</dt><dd class="font-medium">{{ m.family_name || '—' }}</dd></div>
            <div class="sm:col-span-2">
              <dt class="text-slate-500">Rôles</dt>
              <dd class="flex flex-wrap gap-2 mt-1">
                @for (r of roles(); track r) {
                  <span class="text-xs bg-slate-100 rounded-full px-2 py-0.5">{{ r }}</span>
                } @empty { <span class="text-slate-400">Aucun</span> }
              </dd>
            </div>
            <div class="sm:col-span-2"><dt class="text-slate-500">Identifiant</dt><dd class="font-mono text-xs break-all">{{ m.sub }}</dd></div>
            @if (m.exp) {
              <div class="sm:col-span-2"><dt class="text-slate-500">Session valable jusqu’à</dt><dd>{{ m.exp | date: 'HH:mm' }}</dd></div>
            }
          </dl>

          <div class="flex flex-wrap gap-3 pt-2 border-t text-sm">
            <a routerLink="/services/bookings" class="text-blue-600 hover:underline">Mes réservations</a>
            <a routerLink="/services/mine" class="text-blue-600 hover:underline">Mes services</a>
            <button (click)="auth.logout()" class="ml-auto text-red-600 hover:underline">Se déconnecter</button>
          </div>
        </div>
      }
    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private http = inject(HttpClient);
  auth = inject(AuthService);

  // Signaux : l'application est sans zone.js, une simple propriété ne redessine pas la page
  me = signal<Me | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  roles = signal<string[]>([]);
  initial = signal('?');

  ngOnInit(): void {
    this.http.get<Me>('/gw/user/me').subscribe({
      next: (m) => {
        this.me.set(m);
        this.roles.set(
          (m.realm_access?.roles ?? []).filter((r) => !HIDDEN_ROLES.includes(r) && !r.startsWith('default-roles'))
        );
        this.initial.set((m.preferred_username ?? m.email ?? '?').charAt(0).toUpperCase());
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger le profil. Reconnectez-vous.');
        this.loading.set(false);
      },
    });
  }
}
