# Phase 7 — Scanner Safety and App Integration

## Scanner hardening
- BLE scan now uses a bounded 15-second window.
- Explicit stop remains available while scanning.
- Scanning stops when the Activity leaves the foreground and on destruction.
- Duplicate observations update RSSI rather than creating duplicate rows.
- Low-power scan mode replaces continuous low-latency scanning.
- Scan failures and permission revocation are handled defensively.
- UI displays scan/standby state.

## Limitations
- No device distance estimate is calculated from RSSI.
- Scan results are local and not uploaded.
- A scan only observes BLE advertisements; it does not pair or access private data.
- Android compilation and real-device verification are still pending.
