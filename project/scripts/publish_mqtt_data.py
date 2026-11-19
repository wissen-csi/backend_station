#!/usr/bin/env python3
"""Publish synthetic meteorological readings to the station MQTT broker."""

from __future__ import annotations

import argparse
import json
import os
import sys
import threading
import time
import uuid
from datetime import datetime
from typing import Any

try:
    import paho.mqtt.client as mqtt
    from paho.mqtt import MQTTException
except ImportError as exc:
    raise SystemExit(
        "Missing MQTT dependency. Install it with: "
        "python -m pip install -r scripts/requirements.txt"
    ) from exc


def positive_int(value: str) -> int:
    number = int(value)
    if number < 1:
        raise argparse.ArgumentTypeError("must be greater than zero")
    return number


def non_negative_float(value: str) -> float:
    number = float(value)
    if number < 0:
        raise argparse.ArgumentTypeError("must be zero or greater")
    return number


def build_payload(sensor_id: str, sensor_name: str, sample_index: int) -> dict[str, Any]:
    return {
        "id": str(uuid.uuid4()),
        "sensor": {
            "id": sensor_id,
            "name": sensor_name,
        },
        "windSpeed": round(5.0 + (sample_index % 10) / 10, 1),
        "windDirection": 180.0,
        "precipitation": 0.0,
        "temperature": 22.0,
        "altitude": 12.0,
        "humidity": 60.0,
        "pressure": 1013.25,
        "measuredAt": datetime.now().replace(microsecond=0).isoformat(),
    }


def publish(args: argparse.Namespace) -> None:
    connected = threading.Event()
    connection_failure: list[str] = []

    def on_connect(
        client: mqtt.Client,
        userdata: Any,
        flags: Any,
        reason_code: Any,
        properties: Any,
    ) -> None:
        if reason_code.is_failure:
            connection_failure.append(str(reason_code))
        connected.set()

    client = mqtt.Client(
        mqtt.CallbackAPIVersion.VERSION2,
        client_id=f"python-publisher-{uuid.uuid4().hex[:12]}",
    )
    client.username_pw_set(args.username, args.token)
    client.on_connect = on_connect

    client.connect(args.host, args.port, keepalive=30)
    client.loop_start()
    try:
        if not connected.wait(timeout=10):
            raise TimeoutError("Timed out waiting for the MQTT connection")
        if connection_failure:
            raise ConnectionError(f"Broker rejected the connection: {connection_failure[0]}")

        for sample_index in range(args.count):
            payload = build_payload(args.sensor_id, args.sensor_name, sample_index)
            info = client.publish(
                args.topic,
                json.dumps(payload, separators=(",", ":"), ensure_ascii=False),
                qos=1,
                retain=False,
            )
            if info.rc != mqtt.MQTT_ERR_SUCCESS:
                raise RuntimeError(f"MQTT publish failed with result code {info.rc}")

            info.wait_for_publish(timeout=10)
            if not info.is_published():
                raise TimeoutError("Timed out waiting for the broker to acknowledge a message")

            print(f"Published {sample_index + 1}/{args.count} to {args.topic}")
            if sample_index + 1 < args.count:
                time.sleep(args.interval)
    finally:
        client.disconnect()
        client.loop_stop()


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--host", default=os.getenv("MQTT_HOST", "localhost"))
    parser.add_argument("--port", type=int, default=int(os.getenv("MQTT_PORT", "1883")))
    parser.add_argument("--topic", default=os.getenv("MQTT_TOPIC", "sensores/boya-001"))
    parser.add_argument("--username", default=os.getenv("MQTT_USERNAME", "boya-001"))
    parser.add_argument("--sensor-id", default=os.getenv("SENSOR_ID", "boya-001"))
    parser.add_argument("--sensor-name", default=os.getenv("SENSOR_NAME", "Boya simulada"))
    parser.add_argument("--count", type=positive_int, default=1)
    parser.add_argument("--interval", type=non_negative_float, default=2.0)
    args = parser.parse_args()
    args.token = os.getenv("MQTT_TOKEN")

    if not args.token:
        parser.error("set MQTT_TOKEN before running")
    if not args.topic.startswith("sensores/"):
        parser.error("topic must be under sensores/ to match the broker ACL")

    return args


def main() -> int:
    args = parse_args()
    try:
        publish(args)
    except (OSError, RuntimeError, TimeoutError, ConnectionError, MQTTException) as exc:
        print(f"MQTT publish failed: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
