# frontend — Application Angular

Interface web d'EspritMarket (Angular 21, standalone, signals, sans zone.js, Tailwind CSS).

| | |
|---|---|
| Dev | <http://localhost:4200> |
| Production | nginx, publié sur `8280` (`deploy/docker-compose.yml`) |

## Lancer

```bash
npm ci
npm start
```

Le proxy de dev (`proxy.conf.json`) renvoie `/gw` vers la gateway (`:8088`) et `/auth` vers Keycloak (`:8180`).
En production, `nginx.conf` fait la même chose.

## Structure

- `src/app/core/` : services d'API, `auth.interceptor` (ajoute le JWT), `auth.guard`
- `src/app/features/` : une page par domaine (services, marketplace, delivery, events, partnership, profile...)

Build de production : `npm run build` (avec rendu serveur / prérendu des pages publiques).
