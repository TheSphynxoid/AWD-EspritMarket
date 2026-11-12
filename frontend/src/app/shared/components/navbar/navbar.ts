import { Component, inject, signal } from '@angular/core';
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
        <a routerLink="/delivery" routerLinkActive="underline" class="hover:text-slate-300">Livraisons</a>


        <!-- Partnership dropdown -->
        <div class="relative">
          <button (click)="partnershipOpen.set(!partnershipOpen())"
            class="hover:text-slate-300 flex items-center gap-1">
            Partnership
            <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
          </button>
          @if (partnershipOpen()) {
            <div class="absolute top-full left-0 mt-2 w-48 bg-slate-800 rounded-lg shadow-xl py-2 z-50">
              <a routerLink="/partnership/companies" (click)="partnershipOpen.set(false)" class="block px-4 py-2 text-sm hover:bg-slate-700 transition">🏢 Entreprises</a>
              <a routerLink="/partnership/job-offers" (click)="partnershipOpen.set(false)" class="block px-4 py-2 text-sm hover:bg-slate-700 transition">💼 Offres d'emploi</a>
              <a routerLink="/partnership/applications" (click)="partnershipOpen.set(false)" class="block px-4 py-2 text-sm hover:bg-slate-700 transition">📄 Candidatures</a>
              <a routerLink="/partnership/interviews" (click)="partnershipOpen.set(false)" class="block px-4 py-2 text-sm hover:bg-slate-700 transition">🎤 Entretiens</a>
            </div>
          }
        </div>

        @if (auth.isAuthenticated()) {
          <a routerLink="/events" class="hover:text-slate-300">Events</a>
          @if (auth.hasRole('ADMIN_DELIVERY')) {
            <a routerLink="/admin/delivery" routerLinkActive="underline" class="hover:text-slate-300">Admin livraison</a>
          }
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
  partnershipOpen = signal(false);
}
