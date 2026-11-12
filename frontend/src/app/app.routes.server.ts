import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'services', renderMode: RenderMode.Client },
  { path: 'services/**', renderMode: RenderMode.Client },
  { path: 'events', renderMode: RenderMode.Client },
  { path: 'events/**', renderMode: RenderMode.Client },
  { path: 'tickets', renderMode: RenderMode.Client },
  { path: 'tickets/**', renderMode: RenderMode.Client },
  { path: 'delivery', renderMode: RenderMode.Client },
  { path: 'delivery/**', renderMode: RenderMode.Client },
  { path: 'marketplace', renderMode: RenderMode.Client },
  { path: 'marketplace/**', renderMode: RenderMode.Client },
  { path: 'partnership', renderMode: RenderMode.Client },
  { path: 'partnership/**', renderMode: RenderMode.Client },
  { path: 'admin/**', renderMode: RenderMode.Client },
  // Pages protégées par authGuard : pas de prérendu (au build personne n'est connecté,
  // le prérendu figerait une redirection vers /login)
  { path: 'profile', renderMode: RenderMode.Client },
  { path: 'role-select', renderMode: RenderMode.Client },
  { path: '**', renderMode: RenderMode.Prerender },
];
