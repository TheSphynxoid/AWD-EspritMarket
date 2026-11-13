# Notification — Emails

Service Python (FastAPI) qui consomme la file RabbitMQ `notifications` et envoie les emails.

| | |
|---|---|
| Port | `8090` |
| Nom Eureka | `NOTIFICATION` (enregistrement via `eureka.py`) |
| Via la gateway | `/notification/**` |

## Lancer

Prérequis : RabbitMQ (`:5672`) et Eureka (`:8761`).

```powershell
pip install -r requirements.txt
python app.py
```

En Docker : service `notification` de `deploy/docker-compose.yml`.

## Emails

Configurés par variables d'environnement (ou `.env` à côté de `app.py`, voir `.env.example`) :

- **API Mailtrap** (prioritaire) : `MAILTRAP_API_TOKEN`, `MAILTRAP_INBOX_ID`
- **SMTP** : `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_TLS`
- sinon les emails sont seulement écrits dans les logs

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| GET | `/health` | Santé + mode d'envoi des emails |
| GET | `/notification` | Dernières notifications reçues |
| POST | `/notification` | Mettre une notification en file |

## Communication

- **Asynchrone** : consomme la file `notifications`, alimentée par SRV, marketplace, Delivery, Event et Partnership
