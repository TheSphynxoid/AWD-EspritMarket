# Validation des communications (marketplace)

## 1. Communication SYNCHRONE (marketplace ↔ delivery) via Feign

### Principe
marketplace appelle delivery en **bloquant** (Feign) et attend la réponse. Logs préfixés `[SYNC]`.

### Endpoints de test (marketplace, port 8083)
- `POST /mic2/deliveries/sync`
- `GET /mic2/deliveries/sync/{id}`
- `GET /mic2/deliveries/sync`
- `PATCH /mic2/deliveries/sync/{id}/status?status=IN_PROGRESS`

### Étapes
1. Lancer discovery (8761), delivery (8084), marketplace (8083).
2. Vérifier dans Eureka que `DELIVERY` et `MARKETPLACE` sont enregistrés.
3. Appeler `POST /mic2/deliveries/sync` avec le JSON :
   ```json
   {
     "recipientName": "Ahmed Ben Ali",
     "address": "12 rue de la Liberte",
     "city": "Tunis",
     "postalCode": "1000",
     "phone": "+21612345678",
     "deliveryFee": 5.0,
     "deliveryType": "STANDARD"
   }
   ```
4. **Résultat attendu** : HTTP 200 + `DeliveryDto` avec `id`, `status: "PENDING"`, `createdAt`.
5. **Logs marketplace** : `[SYNC] Appel Feign vers Delivery...` puis `[SYNC] Reponse recue de Delivery, id=...`.
6. **Vérification côté delivery** : `GET http://localhost:8084/api/deliveries` doit contenir la livraison créée (même id).

---

## 2. Communication ASYNCHRONE (marketplace → notification) via RabbitMQ

### Principe
marketplace crée la boutique, puis publie un message `shop.created` dans RabbitMQ.
Le service Notification (dossier séparé) consomme la file et envoie l’email.
marketplace **ne bloque pas** sur l’envoi de l’email. Logs préfixés `[ASYNC]`.

### Étapes
1. Lancer RabbitMQ :
   ```powershell
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
   ```
2. Lancer marketplace (8083).
3. Appeler `POST /mic2/shops` :
   ```json
   {
     "name": "Ma boutique",
     "description": "Test",
     "ownerId": 1,
     "ownerEmail": "user@gmail.com"
   }
   ```
4. **Résultat attendu** : HTTP 201 + boutique avec `status: "ACTIVE"` (car email contient `@`).
5. **Logs marketplace** :
   - `[ASYNC] Boutique creee id=..., email=..., status=ACTIVE`
   - `[ASYNC] Message 'shop.created' publie dans RabbitMQ pour ...`
6. **Vérification RabbitMQ** : http://localhost:15672 → Queues → `shop.created` → *Get messages* → `user@gmail.com;Ma boutique;ACTIVE`.
7. **Vérification notification** : si le service Notification est lancé, il reçoit le message et envoie l’email à `user@gmail.com`.

### Résilience
- Arrêter RabbitMQ (`docker stop rabbitmq`), créer une boutique → la création réussit quand même, marketplace log un avertissement “RabbitMQ indisponible”.

---

## 3. Résumé pour la validation
| Communication | Type | Technologie | Preuve |
|---|---|---|---|
| marketplace → delivery | Synchrone | OpenFeign | Réponse HTTP immédiate + logs `[SYNC]` + données dans delivery |
| marketplace → notification | Asynchrone | RabbitMQ | Boutique créée tout de suite + message dans file `shop.created` + logs `[ASYNC]` |

## 4. Fichiers clés (marketplace uniquement)
- Sync : `client/DeliveryFeignClient.java`, `service/DeliveryIntegrationService.java`, `controller/DeliveryIntegrationController.java`
- Async : `config/RabbitConfig.java`, `messaging/ShopEventProducer.java`, `service/ShopService.java`