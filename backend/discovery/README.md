# discovery — Eureka Server

Annuaire des services : chaque microservice (y compris Notification en Python) s'y enregistre,
et la gateway y résout les routes `lb://`.

| | |
|---|---|
| Port | `8761` |
| Tableau de bord | <http://localhost:8761> |

## Lancer

À démarrer **en premier**, avant tous les autres services.

```powershell
.\mvnw.cmd spring-boot:run
```

En Docker : service `discovery` de `deploy/docker-compose.yml`.

Les instances enregistrées apparaissent dans *Instances currently registered with Eureka*.
