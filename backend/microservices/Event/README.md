# Event — Événements

Événements, billets, réservations, stands et équipements.

| | |
|---|---|
| Port | `8085` |
| Nom Eureka | `event` |
| Base de données | PostgreSQL `event_db` (JPA) |
| Via la gateway | `/event/**`, `/mic1/**` |
| Swagger | onglet `event` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`), PostgreSQL et RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `event` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| CRUD | `/mic1/events` | Événements (`/by-status/{status}`, `/by-user/{userId}`) |
| CRUD | `/mic1/tickets` | Billets |
| CRUD | `/mic1/reservations` | Réservations |
| CRUD | `/mic1/stalls` | Stands |
| CRUD | `/mic1/equipments` | Équipements |

## Communication

- **Synchrone** : appelé par Partnership via OpenFeign
- **Asynchrone** : publie dans RabbitMQ à la création d'un événement (`EventService`)
