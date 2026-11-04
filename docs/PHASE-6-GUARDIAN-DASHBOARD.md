# Phase 6 — Guardian Command Center

## Dashboard prototype
`GuardianDashboard.kt` adds a Compose UI concept with:
- dark cyber-style gradient and cyan/violet accents
- animated radar sweep and concentric rings
- signal count and scan state indicator
- BLE observation cards with label/detail/RSSI
- empty-state messaging and scan toggle callback
- explicit passive-discovery safety footer

## Integration notes
This is a reusable UI composable, not a complete app entry point. The host app must provide actual BLE scan state and observations through `devices`, `scanActive`, and `onScanToggle`. Do not mark scanning active until Android BLE permissions and scanner callbacks confirm it.

## Important signal caveat
RSSI varies with device, antenna, orientation, and environment. It is not a reliable distance measurement and does not indicate device vulnerability.

## Accessibility and production polish still needed
- test contrast and text scaling on real devices
- add reduced-motion setting to disable radar animation
- ensure scan controls expose accessible labels/state
- validate layout on small screens and tablets
- wire app theme preferences (system/dark/light)
- run Android build and UI tests
