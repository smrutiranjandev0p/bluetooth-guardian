# Bluetooth Guardian — Linux Research Engine

A companion Linux engine for the Bluetooth Guardian Android app. This first milestone provides safe BLE discovery, local resilience simulation, and JSON event output. It does **not** transmit interference, flood connection requests, or disconnect third-party devices.

## Supported environment

- Kali Linux (or Debian-based Linux)
- Python 3.10+
- Built-in Bluetooth adapter with BLE support
- BlueZ (`bluetoothctl`, `btmgmt`)

## Install

```bash
sudo apt update
sudo apt install -y bluetooth bluez python3 python3-venv
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

If Bluetooth is disabled, enable it with your desktop Bluetooth settings or `sudo systemctl start bluetooth`. Avoid changing adapter state while using other Bluetooth devices.

## Run

```bash
python guardian.py status
python guardian.py scan --seconds 10
python guardian.py simulate-resilience --cycles 5 --recovery-ms 1200
```

`scan` performs passive BLE advertisement discovery using Bleak. `simulate-resilience` is a local model only; it does not manipulate radio connections. Output is JSON Lines in `logs/`.

## Android integration (planned)

The Android app can import exported JSONL/JSON reports using Android's Storage Access Framework. A future local bridge may use authenticated localhost HTTP or a user-selected file transfer. Do not expose a bridge on `0.0.0.0` or the public network.

## Safety / scope

Only test devices you own or have explicit permission to assess. Passive advertisement data is incomplete and does not prove device identity, authorization, or vulnerability. This project intentionally excludes radio jamming, BLE flooding, forced disconnection, and unauthorized connection manipulation.

## Troubleshooting

- `bluetoothctl show`: check adapter and powered state.
- `rfkill list bluetooth`: check whether the radio is blocked.
- BLE scan may require BlueZ permissions and a working adapter.
- Built-in laptop adapters may not support all BLE features; use a dedicated lab board for repeatable experiments.
