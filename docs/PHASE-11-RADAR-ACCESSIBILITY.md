# Phase 11 — Radar accessibility polish

## Changes
- Added screen-reader semantics to RSSI values so assistive technology announces signal strength in a meaningful unit.
- Updated dashboard documentation comment to reflect its current role as the passive BLE observation dashboard.
- Preserved the existing passive discovery scope, scan duration, and explicit scan stop control.

## Validation
- GitHub Actions CI is the source of truth for the Android debug build and unit-test workflow.
- Accessibility behavior still requires a manual TalkBack check on an Android device.
- RSSI is an approximate radio signal measurement; it is not a distance estimate or security verdict.
