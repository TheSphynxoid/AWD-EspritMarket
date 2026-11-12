import json
import os
import smtplib
import threading
import time
from email.message import EmailMessage

RABBITMQ_URL = os.getenv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/")
QUEUE_NAME = os.getenv("RABBITMQ_QUEUE", "notifications")

SMTP_HOST = os.getenv("SMTP_HOST")
SMTP_PORT = int(os.getenv("SMTP_PORT", "587"))
SMTP_USER = os.getenv("SMTP_USER")
SMTP_PASSWORD = os.getenv("SMTP_PASSWORD")
SMTP_FROM = os.getenv("SMTP_FROM", SMTP_USER or "notifications@espritmarket.local")
SMTP_TLS = os.getenv("SMTP_TLS", "true").lower() == "true"


def send_email(notification: dict) -> bool:
    recipient = notification.get("recipient")
    message = notification.get("message", "")
    subject = f"[EspritMarket] {notification.get('type', 'notification').title()}"
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
