import os
import threading
import uuid
from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

from eureka import deregister, start_registration
from rabbitmq import QUEUE_NAME, consume, publish, send_email

APP_NAME = os.getenv("APP_NAME", "NOTIFICATION")
PORT = int(os.getenv("PORT", "8090"))

notifications: list[dict] = []
_pending: list[dict] = []
_lock = threading.Lock()


class NotificationIn(BaseModel):
    recipient: str
    message: str
    type: str = "info"


def handle_message(notification: dict) -> None:
    send_email(notification)
    with _lock:
        notification.setdefault("id", len(notifications) + 1)
        notifications.append(notification)


@asynccontextmanager
async def lifespan(app: FastAPI):
    stop_event = threading.Event()
    start_registration(stop_event)
    app.state.stop_event = stop_event

    # RabbitMQ consumer (async processing)
    consumer_thread = threading.Thread(
        target=consume, args=(stop_event, handle_message), daemon=True
    )
    consumer_thread.start()

    # Fallback in-memory consumer if broker unavailable
    def fallback_loop():
        while not stop_event.is_set():
            with _lock:
                batch = list(_pending)
                _pending.clear()
            for item in batch:
                handle_message(item)
            stop_event.wait(1)

    fallback_thread = threading.Thread(target=fallback_loop, daemon=True)
    fallback_thread.start()

    yield
    stop_event.set()
    deregister()


app = FastAPI(title="Notification Service", lifespan=lifespan)


@app.get("/health")
def health():
    return {"status": "UP", "service": APP_NAME, "queue": QUEUE_NAME}


@app.get("/notification")
def list_notifications():
    with _lock:
        return list(notifications)


@app.post("/notification", status_code=202)
def create_notification(payload: NotificationIn):
    item = {"id": str(uuid.uuid4()), **payload.model_dump()}
    if publish(item):
        return {"status": "queued", "id": item["id"]}
    # fallback to in-memory queue
    with _lock:
        _pending.append(item)
    return {"status": "queued-local", "id": item["id"]}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("app:app", host="0.0.0.0", port=PORT, reload=False)
