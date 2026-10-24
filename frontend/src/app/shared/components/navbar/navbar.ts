import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="bg-slate-900 text-white px-6 py-4 flex items-center justify-between">
      <a routerLink="/" class="text-xl font-bold">EspritMarket</a>
      <div class="flex gap-4 items-center">
        <a routerLink="/services" routerLinkActive="underline" class="hover:text-slate-300">Services</a>
        @if (auth.isAuthenticated()) {
          <a routerLink="/profile" class="hover:text-slate-300">Profile</a>
          <button (click)="auth.logout()" class="bg-slate-700 px-3 py-1 rounded hover:bg-slate-600">Logout</button>
        } @else {
          <a routerLink="/login" class="bg-blue-600 px-3 py-1 rounded hover:bg-blue-500">Login</a>
          <a routerLink="/register" class="bg-slate-700 px-3 py-1 rounded hover:bg-slate-600">Register</a>
        }
      </div>
    </nav>
  `,
})
export class NavbarComponent {
  auth = inject(AuthService);
}
