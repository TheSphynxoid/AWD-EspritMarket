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
  { path: 'admin/**', renderMode: RenderMode.Client },
  { path: 'partnership', renderMode: RenderMode.Client },
  { path: 'partnership/**', renderMode: RenderMode.Client },
  { path: '**', renderMode: RenderMode.Prerender },
];
