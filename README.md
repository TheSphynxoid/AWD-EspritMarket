# EspritMarket – Microservices

**Projet : PIDEV 4SE1 (2025–2026)** — ESPRIT
Structure basée sur les workshops **Applications Web Distribuées (AWD)**

---

## Objectif du projet

Reprendre l'idée d'**EspritMarket** (place de marché de services pour la communauté
ESPRIT) et la reconstruire en **architecture microservices**, en suivant la structure
des workshops AWD :

- Un serveur **Eureka** (discovery) comme registre de services
- Des microservices **standalone** (Spring Boot + Maven wrapper par service)

---

## Architecture

![Architecture globale](documentation/diag/architecture.png)

> Source : [`documentation/diag/architecture.puml`](documentation/diag/architecture.puml)

| Service | Dossier | Port | Rôle |
|---------|---------|------|------|
| discovery | `backend/discovery/` | 8761 | Eureka Server — registre de services |
| SRV | `backend/microservices/SRV/` | 8082 | Premier microservice, client Eureka |

---

## Technologies utilisées

- Java 17
- Spring Boot
- Spring Cloud Netflix Eureka (server + client)
- Maven
- IntelliJ IDEA

---

## Démarrage

**Ordre important :** toujours démarrer le serveur Eureka avant les microservices.

```powershell
# Terminal 1 — Eureka Server sur :8761
cd backend/discovery
.\mvnw.cmd spring-boot:run

# Terminal 2 — SRV sur :8082
cd backend\microservices\SRV
.\mvnw.cmd spring-boot:run
```

Vérification : ouvrir <http://localhost:8761> → `SRV` doit apparaître dans
*Instances currently registered with Eureka*.
