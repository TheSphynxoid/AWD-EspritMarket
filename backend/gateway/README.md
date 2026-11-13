# gateway — API Gateway

Point d'entrée unique de l'API (Spring Cloud Gateway, WebFlux).

| | |
|---|---|
| Port | `8088` |
| Swagger centralisé | <http://localhost:8088/swagger-ui.html> (un onglet par service) |

## Rôle

- **Routage** : `lb://<SERVICE>` résolu via Eureka (`GatewayRoutes.java`)
- **Sécurité** : valide le JWT Keycloak ; lectures publiques (catalogues), écritures authentifiées (`SecurityConfig.java`)
- **CORS** : `CorsConfig.java`

## Routes

| Préfixe | Service |
|---|---|
| `/srv/**` | SRV |
| `/marketplace/**`, `/mic2/**` | marketplace (`/mic2/deliveries` : intégration livraison) |
| `/delivery/**`, `/api/deliveries/**`, `/api/deliverers/**`, `/api/vehicles/**` | Delivery |
| `/event/**`, `/mic1/**` | Event |
| `/patnership/**`, `/api/partnership/**` | Partnership |
| `/user/**` | User |
| `/notification/**` | Notification |

## Lancer

Prérequis : Eureka (`:8761`) et Keycloak (`:8180`). À démarrer **en dernier**.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `gateway` de `deploy/docker-compose.yml` (joignable via le frontend sur `/gw`).
