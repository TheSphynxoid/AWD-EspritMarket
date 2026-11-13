# Partnership — Partenariats

Entreprises partenaires, offres d'emploi, candidatures et entretiens.

| | |
|---|---|
| Port | `8089` |
| Nom Eureka | `Patnership` |
| Base de données | PostgreSQL `partnership_db` (JPA) |
| Via la gateway | `/patnership/**`, `/api/partnership/**` |
| Swagger | onglet `partnership` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`), PostgreSQL et RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `patnership` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| CRUD | `/api/partnership/companies` | Entreprises |
| CRUD | `/api/partnership/job-offers` | Offres (`/company/{companyId}`) |
| CRUD | `/api/partnership/applications` | Candidatures (`/job-offer/{id}`, `/user/{userId}`) |
| CRUD | `/api/partnership/interviews` | Entretiens (`/application/{applicationId}`) |

## Communication

- **Synchrone** : OpenFeign vers Event (`EventClient`)
- **Asynchrone** : publie dans RabbitMQ (`PartnershipEventProducer`)
