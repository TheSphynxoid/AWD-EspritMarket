# marketplace — Place de marché

Boutiques, produits, catégories et panier.

| | |
|---|---|
| Port | `8083` |
| Nom Eureka | `marketplace` |
| Base de données | PostgreSQL `marketplace_db` (JPA) |
| Via la gateway | `/marketplace/**`, `/mic2/**` |
| Swagger | onglet `marketplace` de <http://localhost:8088/swagger-ui.html> |

## Lancer

Prérequis : Eureka (`:8761`), PostgreSQL et RabbitMQ.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `marketplace` de `deploy/docker-compose.yml`.

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| GET / POST | `/mic2/shops` | Boutiques |
| GET / PUT / DELETE | `/mic2/shops/{id}` | Une boutique (`PATCH /{id}/status` : statut) |
| GET / POST | `/mic2/shops/{shopId}/products` | Produits d'une boutique |
| GET | `/mic2/products, /mic2/products/{id}` | Catalogue produits |
| GET / POST | `/mic2/categories` | Catégories |
| GET / DELETE | `/mic2/paniers/{userId}` | Panier de l'utilisateur |
| POST / PUT / DELETE | `/mic2/paniers/{userId}/items[/{productId}]` | Lignes du panier |
| GET / POST / PATCH | `/mic2/deliveries/sync[/{id}]` | Livraisons créées dans Delivery |

## Communication

- **Synchrone** : OpenFeign vers Delivery (`DeliveryFeignClient`)
- **Asynchrone** : publie dans RabbitMQ (`ShopEventProducer`)
