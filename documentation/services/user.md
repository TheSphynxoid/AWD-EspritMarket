# User — Utilisateurs

Inscription et profil ; les comptes sont stockés dans Keycloak (realm `espritmarket`).

| | |
|---|---|
| Port | `8087` |
| Nom Eureka | `USER` |
| Base de données | aucune (Keycloak) |
| Via la gateway | `/user/**` |
| Swagger | onglet `user` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`) et Keycloak (`:8180`).

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `user` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| POST | `/user/register` | Créer un compte (via l'API admin Keycloak) |
| GET | `/user/me` | Profil de l'utilisateur connecté (claims du JWT) |
| GET | `/user/health` | Santé (public) |

## Communication

- **Keycloak** : API admin pour créer les comptes ; JWT validé avec les clés du realm
