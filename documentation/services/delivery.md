# Delivery — Livraisons

Livraisons, livreurs, véhicules et tarifs ; l'espace d'administration du frontend est réservé au rôle `ADMIN_DELIVERY`.

| | |
|---|---|
| Port | `8084` |
| Nom Eureka | `Delivery` |
| Base de données | PostgreSQL `delivery_db` (JPA) |
| Via la gateway | `/delivery/**`, `/api/deliveries/**`, `/api/deliverers/**`, `/api/vehicles/**` |
| Swagger | onglet `delivery` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`), PostgreSQL et RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `delivery` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| CRUD | `/api/deliveries` | Livraisons (`PATCH /{id}/status`, `PUT /{id}/deliverer/{delivererId}`) |
| CRUD | `/api/deliverers` | Livreurs |
| CRUD | `/api/vehicles` | Véhicules |
| GET / PUT | `/api/deliveries/fees[/{type}]` | Tarifs par type de livraison |

## Communication

- **Synchrone** : OpenFeign vers marketplace (`MarketplaceClient`)
- **Asynchrone** : publie dans RabbitMQ (`DeliveryService`)
