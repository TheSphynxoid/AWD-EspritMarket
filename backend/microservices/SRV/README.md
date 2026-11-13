# SRV — Services & réservations

Les étudiants publient des offres de services ; les autres les réservent, et le prestataire
accepte, refuse ou termine la réservation.

| | |
|---|---|
| Port | `8082` |
| Nom Eureka | `SRV` |
| Base de données | PostgreSQL `srv_db` (JPA) |
| Via la gateway | `/srv/**` |
| Swagger | onglet `srv` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`), PostgreSQL, Keycloak (`:8180`) et RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `srv` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| GET | `/srv?q=&category=&minPrice=&maxPrice=` | Catalogue filtré (public) |
| GET | `/srv/categories` | Catégories (public) |
| GET | `/srv/{id}` | Détail d'un service (public) |
| GET | `/srv/mine` | Mes services |
| POST / PUT / DELETE | `/srv, /srv/{id}` | Créer / modifier / supprimer (propriétaire) |
| POST | `/srv/bookings` | Réserver un service |
| GET | `/srv/bookings/mine` | Mes réservations |
| GET | `/srv/bookings/received?status=` | Réservations reçues (prestataire) |
| PATCH | `/srv/bookings/{id}/status` | Accepter, refuser, terminer, annuler |

## Communication

- **Sécurité** : resource server OAuth2, le JWT Keycloak identifie le prestataire / client
- **Asynchrone** : publie dans la file RabbitMQ `notifications` à chaque réservation et changement de statut (`NotificationPublisher`)
