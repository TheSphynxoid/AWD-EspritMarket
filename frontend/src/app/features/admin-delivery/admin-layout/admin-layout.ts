import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

/** Mise en page de l'administration : barre latérale à gauche + page active à droite. */
@Component({
  selector: 'app-admin-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="flex flex-col md:flex-row min-h-[calc(100vh-4rem)]">
      <aside class="md:w-60 shrink-0 bg-white border-b md:border-b-0 md:border-r p-4">
        <p class="text-xs font-semibold uppercase tracking-wide text-slate-400 px-3 mb-3">Admin livraison</p>
        <nav class="flex md:flex-col gap-1 overflow-x-auto">
          @for (item of menu; track item.link) {
            <a
              [routerLink]="item.link"
              routerLinkActive="bg-blue-50 text-blue-700 font-semibold"
              class="flex items-center gap-3 px-3 py-2 rounded-lg text-slate-600 hover:bg-slate-100 whitespace-nowrap"
            >
              <span class="text-lg">{{ item.icon }}</span>{{ item.label }}
            </a>
          }
        </nav>
      </aside>
      <main class="flex-1 min-w-0">
        <router-outlet />
      </main>
    </div>
  `,
})
export class AdminLayoutComponent {
  menu = [
    { label: 'Livraisons', link: '/admin/delivery/deliveries', icon: '📦' },
    { label: 'Statistiques', link: '/admin/delivery/stats', icon: '📊' },
    { label: 'Livreurs', link: '/admin/delivery/deliverers', icon: '🧑' },
    { label: 'Voitures', link: '/admin/delivery/vehicles', icon: '🚚' },
    { label: 'Frais de livraison', link: '/admin/delivery/fees', icon: '💰' },
  ];
}
