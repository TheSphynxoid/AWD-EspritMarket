import os
import socket
import threading
import time
import requests


EUREKA_URL = os.getenv("EUREKA_URL", "http://localhost:8761/eureka")
APP_NAME = os.getenv("APP_NAME", "NOTIFICATION")
PORT = int(os.getenv("PORT", "8090"))
HOST = os.getenv("HOST", "localhost")
INSTANCE_ID = os.getenv("INSTANCE_ID", f"{HOST}:{APP_NAME.lower()}:{PORT}")


def _instance_payload() -> dict:
    return {
        "instance": {
            "instanceId": INSTANCE_ID,
            "hostName": HOST,
            "app": APP_NAME,
            "ipAddr": "127.0.0.1",
            "status": "UP",
            "port": {"$": PORT, "@enabled": "true"},
            "securePort": {"$": 443, "@enabled": "false"},
            "homePageUrl": f"http://{HOST}:{PORT}/",
            "statusPageUrl": f"http://{HOST}:{PORT}/health",
            "healthCheckUrl": f"http://{HOST}:{PORT}/health",
            "vipAddress": APP_NAME,
            "secureVipAddress": APP_NAME,
            "dataCenterInfo": {
                "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name": "MyOwn",
            },
        }
    }


def register() -> bool:
    url = f"{EUREKA_URL}/apps/{APP_NAME}"
    try:
        response = requests.post(
            url,
            json=_instance_payload(),
            headers={"Content-Type": "application/json", "Accept": "application/json"},
            timeout=5,
        )
        return response.status_code in (200, 204)
    except requests.RequestException:
        return False


def heartbeat() -> bool:
    url = f"{EUREKA_URL}/apps/{APP_NAME}/{INSTANCE_ID}"
    try:
        response = requests.put(url, timeout=5)
        return response.status_code in (200, 204)
    except requests.RequestException:
        return False


def deregister() -> bool:
    url = f"{EUREKA_URL}/apps/{APP_NAME}/{INSTANCE_ID}"
    try:
        response = requests.delete(url, timeout=5)
        return response.status_code in (200, 204)
    except requests.RequestException:
        return False


def _heartbeat_loop(stop_event: threading.Event, interval: float = 30.0) -> None:
    while not stop_event.wait(interval):
        if not heartbeat():
            register()


def start_registration(stop_event: threading.Event) -> threading.Thread:
    # initial registration with retries
    for _ in range(10):
        if register():
            break
        time.sleep(2)
    thread = threading.Thread(target=_heartbeat_loop, args=(stop_event,), daemon=True)
    thread.start()
    return thread
