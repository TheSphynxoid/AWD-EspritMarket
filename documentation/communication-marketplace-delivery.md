# Communication marketplace -> delivery (cote marketplace uniquement)

Le microservice **delivery** n'est jamais modifie. Marketplace consomme son API REST
existante (`/api/deliveries`) via Eureka (service id `Delivery`, port 8084).

Communication **synchrone** implementee avec **OpenFeign** dans `net.thesphynx.marketplace`.

## 1. Ajout de la dependance (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
Et `@EnableFeignClients` sur `MarketplaceApplication`.

## 2. Interface Feign (client/DeliveryFeignClient.java)
```java
@FeignClient(name = "Delivery")   // service id Eureka
public interface DeliveryFeignClient {
    @PostMapping("/api/deliveries")
    DeliveryDto create(@RequestBody DeliveryDto dto);

    @GetMapping("/api/deliveries/{id}")
    DeliveryDto findById(@PathVariable("id") Long id);

    @GetMapping("/api/deliveries")
    List<DeliveryDto> findAll();

    @PatchMapping("/api/deliveries/{id}/status")
    DeliveryDto updateStatus(@PathVariable("id") Long id, @RequestBody DeliveryStatusDto dto);
}
```
Spring genere automatiquement l'implementation HTTP. Plus besoin d'ecrire RestTemplate.

## 3. Service (service/DeliveryIntegrationService.java)
```java
@Service
public class DeliveryIntegrationService {
    private final DeliveryFeignClient feign;

    public DeliveryDto createDeliverySync(DeliveryDto dto) {
        return feign.create(dto);   // bloquant : attend la reponse
    }
    public DeliveryDto getDeliverySync(Long id) { return feign.findById(id); }
    public List<DeliveryDto> listDeliveriesSync() { return feign.findAll(); }
    public DeliveryDto updateDeliveryStatusSync(Long id, DeliveryStatus status) {
        return feign.updateStatus(id, new DeliveryStatusDto(status));
    }
}
```

## 4. Principe synchrone
marketplace envoie la requete a delivery et **attend la reponse** avant de continuer.
- Avantage : resultat immediat (id de livraison), erreurs propagees.
- Limite : couplage temporel fort (si delivery est lent, marketplace attend).

## 5. Endpoints de demo (/mic2/deliveries)
| Methode | Chemin | Role |
|---|---|---|
| POST | `/sync` | Creer une livraison (bloquant) |
| GET | `/sync/{id}` | Recuperer une livraison |
| GET | `/sync` | Lister les livraisons |
| PATCH | `/sync/{id}/status?status=...` | Changer le statut |

## 6. Exemple de payload (POST /mic2/deliveries/sync)
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

## 7. Configuration
`delivery.service.url` n'est plus necessaire pour Feign : le nom `"Delivery"` est resolu directement par Eureka.
`application.properties` garde `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`.