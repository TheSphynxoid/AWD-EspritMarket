import json
import os
import smtplib
import threading
import time
from email.message import EmailMessage

import requests
from dotenv import load_dotenv

# Exécution locale : variables lues depuis un fichier .env à côté du service (ignoré par git).
# En Docker, elles viennent de deploy/.env via docker-compose.
load_dotenv(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".env"))

RABBITMQ_URL = os.getenv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/")
QUEUE_NAME = os.getenv("RABBITMQ_QUEUE", "notifications")

SMTP_HOST = os.getenv("SMTP_HOST")
SMTP_PORT = int(os.getenv("SMTP_PORT", "587"))
SMTP_USER = os.getenv("SMTP_USER")
SMTP_PASSWORD = os.getenv("SMTP_PASSWORD")
SMTP_FROM = os.getenv("SMTP_FROM", SMTP_USER or "notifications@espritmarket.local")
SMTP_TLS = os.getenv("SMTP_TLS", "true").lower() == "true"

# Mailtrap (API HTTP, prioritaire sur SMTP si le token est défini)
#  - MAILTRAP_INBOX_ID défini  -> Sandbox : les emails arrivent dans la boîte de test Mailtrap
#  - MAILTRAP_INBOX_ID vide    -> Envoi réel (domaine expéditeur vérifié dans Mailtrap requis)
MAILTRAP_API_TOKEN = os.getenv("MAILTRAP_API_TOKEN", "").strip()
MAILTRAP_INBOX_ID = os.getenv("MAILTRAP_INBOX_ID", "").strip()
MAILTRAP_FROM = os.getenv("MAILTRAP_FROM", "").strip() or "notifications@espritmarket.local"
MAILTRAP_FROM_NAME = os.getenv("MAILTRAP_FROM_NAME", "EspritMarket")

SUBJECTS = {"booking": "Réservation", "info": "Information"}


def email_mode() -> str:
    if MAILTRAP_API_TOKEN:
        return f"mailtrap-sandbox (inbox {MAILTRAP_INBOX_ID})" if MAILTRAP_INBOX_ID else "mailtrap-sending"
    return "smtp" if SMTP_HOST else "log-only"


def _send_mailtrap(recipient: str, subject: str, message: str, category: str) -> bool:
    url = (
        f"https://sandbox.api.mailtrap.io/api/send/{MAILTRAP_INBOX_ID}"
        if MAILTRAP_INBOX_ID
        else "https://send.api.mailtrap.io/api/send"
    )
    payload = {
        "from": {"email": MAILTRAP_FROM, "name": MAILTRAP_FROM_NAME},
        "to": [{"email": recipient}],
        "subject": subject,
        "text": message,
        "category": category,
    }
    try:
        response = requests.post(
            url,
            json=payload,
            headers={"Authorization": f"Bearer {MAILTRAP_API_TOKEN}"},
            timeout=10,
        )
        if response.ok:
            print(f"[notification] mailtrap email sent to={recipient}", flush=True)
            return True
        print(f"[notification] mailtrap failed ({response.status_code}): {response.text[:300]}", flush=True)
    except requests.RequestException as exc:
        print(f"[notification] mailtrap error: {exc}", flush=True)
    return False


def send_email(notification: dict) -> bool:
    recipient = notification.get("recipient")
    message = notification.get("message", "")
    category = notification.get("type", "notification")
    subject = f"[EspritMarket] {SUBJECTS.get(category, category.title())}"
    if MAILTRAP_API_TOKEN and recipient:
        return _send_mailtrap(recipient, subject, message, category)
    if not SMTP_HOST:
        print(f"[notification] (no SMTP configured) to={recipient}: {message}", flush=True)
        return True
    try:
        email = EmailMessage()
        email["From"] = SMTP_FROM
        email["To"] = recipient
        email["Subject"] = subject
        email.set_content(message)
        with smtplib.SMTP(SMTP_HOST, SMTP_PORT, timeout=10) as server:
            if SMTP_TLS:
                server.starttls()
            if SMTP_USER and SMTP_PASSWORD:
                server.login(SMTP_USER, SMTP_PASSWORD)
            server.send_message(email)
        print(f"[notification] email sent to={recipient}", flush=True)
        return True
    except Exception as exc:  # noqa: BLE001
        print(f"[notification] email failed: {exc}", flush=True)
        return False


def _rabbitmq_connection():
    import pika

    params = pika.URLParameters(RABBITMQ_URL)
    params.heartbeat = 30
    return pika.BlockingConnection(params)


def publish(notification: dict) -> bool:
    try:
        import pika

        connection = _rabbitmq_connection()
        channel = connection.channel()
        channel.queue_declare(queue=QUEUE_NAME, durable=True)
        channel.basic_publish(
            exchange="",
            routing_key=QUEUE_NAME,
            body=json.dumps(notification).encode("utf-8"),
            properties=pika.BasicProperties(delivery_mode=2),
        )
        connection.close()
        return True
    except Exception as exc:  # noqa: BLE001
        print(f"[rabbitmq] publish failed: {exc}", flush=True)
        return False


def consume(stop_event, on_message) -> None:
    import pika

    while not stop_event.is_set():
        try:
            connection = _rabbitmq_connection()
            channel = connection.channel()
            channel.queue_declare(queue=QUEUE_NAME, durable=True)
            channel.basic_qos(prefetch_count=1)

            def callback(ch, method, properties, body):
                try:
                    notification = json.loads(body.decode("utf-8"))
                except json.JSONDecodeError:
                    notification = {"message": body.decode("utf-8", errors="replace")}
                on_message(notification)
                ch.basic_ack(delivery_tag=method.delivery_tag)

            channel.basic_consume(queue=QUEUE_NAME, on_message_callback=callback)
            while not stop_event.is_set() and connection.is_open:
                connection.process_data_events(time_limit=1)
            connection.close()
        except Exception as exc:  # noqa: BLE001
            print(f"[rabbitmq] consumer error: {exc}", flush=True)
            if not stop_event.is_set():
                time.sleep(5)
