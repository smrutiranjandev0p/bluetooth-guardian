#!/usr/bin/env python3
"""Bluetooth Guardian Linux companion: passive BLE scan + local-only simulator."""
from __future__ import annotations
import argparse
import asyncio
import json
import platform
import random
import shutil
import subprocess
import time
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parent
LOG_DIR = ROOT / "logs"
LOG_DIR.mkdir(exist_ok=True)


def emit(event: dict) -> None:
    event.setdefault("timestamp", datetime.now(timezone.utc).isoformat())
    path = LOG_DIR / f"guardian-{datetime.now().strftime('%Y%m%d')}.jsonl"
    with path.open("a", encoding="utf-8") as f:
        f.write(json.dumps(event, ensure_ascii=False) + "\n")
    print(json.dumps(event, ensure_ascii=False))


def status() -> int:
    result = {"type": "system_status", "platform": platform.platform(),
              "bluetoothctl_available": shutil.which("bluetoothctl") is not None,
              "bleak_available": False, "adapter_status": "unknown"}
    try:
        import bleak  # noqa: F401
        result["bleak_available"] = True
    except ImportError:
        pass
    if shutil.which("bluetoothctl"):
        try:
            p = subprocess.run(["bluetoothctl", "show"], capture_output=True,
                               text=True, timeout=5, check=False)
            result["adapter_status"] = "powered" if "Powered: yes" in p.stdout else "not powered or unavailable"
            result["bluetoothctl_summary"] = p.stdout[:1200]
        except (subprocess.SubprocessError, OSError) as exc:
            result["adapter_status"] = f"error: {exc}"
    emit(result)
    return 0


async def scan(seconds: int) -> int:
    try:
        from bleak import BleakScanner
    except ImportError:
        print("Missing dependency. Run: pip install -r requirements.txt", flush=True)
        return 2
    print(f"Passive BLE scan for {seconds}s. Press Ctrl+C to stop.")
    seen: dict[str, dict] = {}

    def callback(device, advertisement):
        item = {"type": "ble_advertisement", "address": device.address,
                "name": device.name or advertisement.local_name,
                "rssi": getattr(advertisement, "rssi", None),
                "service_uuids": list(advertisement.service_uuids or []),
                "manufacturer_data_ids": [str(k) for k in (advertisement.manufacturer_data or {}).keys()],
                "note": "Passive advertisement observation; identity is not verified."}
        # Keep only latest observation per address during this scan.
        seen[device.address] = item

    scanner = BleakScanner(detection_callback=callback)
    try:
        await scanner.start()
        await asyncio.sleep(seconds)
    except asyncio.CancelledError:
        pass
    finally:
        await scanner.stop()
    for item in seen.values():
        emit(item)
    emit({"type": "scan_summary", "duration_seconds": seconds, "devices_seen": len(seen)})
    return 0


def simulate(cycles: int, recovery_ms: int) -> int:
    """A deterministic-format local model; no Bluetooth hardware is touched."""
    print("SIMULATION ONLY — no radio traffic or real device connections are affected.")
    for index in range(1, cycles + 1):
        outage = random.randint(100, 900)
        recovered = max(0, recovery_ms + random.randint(-250, 250))
        emit({"type": "resilience_simulation", "cycle": index,
              "simulated_outage_ms": outage, "simulated_recovery_ms": recovered,
              "mode": "local_simulation_only"})
    emit({"type": "simulation_summary", "cycles": cycles,
          "baseline_recovery_ms": recovery_ms, "mode": "local_simulation_only"})
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Bluetooth Guardian Linux research engine")
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("status", help="Show Linux and Bluetooth adapter status")
    s = sub.add_parser("scan", help="Passively observe BLE advertisements")
    s.add_argument("--seconds", type=int, default=10)
    sim = sub.add_parser("simulate-resilience", help="Run local-only resilience simulation")
    sim.add_argument("--cycles", type=int, default=5)
    sim.add_argument("--recovery-ms", type=int, default=1200)
    args = parser.parse_args()
    if args.command == "status":
        return status()
    if args.command == "scan":
        if not 1 <= args.seconds <= 60:
            parser.error("--seconds must be between 1 and 60")
        return asyncio.run(scan(args.seconds))
    if args.command == "simulate-resilience":
        if not 1 <= args.cycles <= 100 or not 0 <= args.recovery_ms <= 60000:
            parser.error("cycles must be 1..100 and recovery-ms 0..60000")
        return simulate(args.cycles, args.recovery_ms)
    return 1

if __name__ == "__main__":
    raise SystemExit(main())
