# Phase 12 — Android device validation checklist

This checklist is for a manual test on an Android phone or emulator. CI success does not replace device validation.

## Install and launch
- [ ] Install the CI-generated debug APK.
- [ ] Launch Bluetooth Guardian and confirm the dashboard renders without a crash.
- [ ] Confirm the app requests only the permissions needed for BLE scanning on this Android version.
- [ ] Deny permission once; verify the app explains the limitation and remains usable without crashing.
- [ ] Grant permission and verify scanning can start.

## Scan lifecycle
- [ ] Start a scan and verify the status changes to SCANNING.
- [ ] Verify nearby BLE advertisements appear, if any are available.
- [ ] Stop scan manually and verify the status returns to STANDBY.
- [ ] Allow the bounded scan to finish and verify it stops automatically.
- [ ] Navigate away / background the app during scan; verify scanning is stopped or cleaned up as intended.
- [ ] Toggle Bluetooth off during scan; verify graceful error handling.

## Accessibility and UI
- [ ] Use TalkBack to navigate the scan control and device cards.
- [ ] Confirm RSSI is announced as a signal-strength measurement, not distance.
- [ ] Verify text remains readable at large font/display size.
- [ ] Check the dashboard in portrait and landscape.
- [ ] Confirm no UI labels imply nearby devices are vulnerable or authorized.

## Report export
- [ ] In Assessment, confirm authorization is required before report preparation.
- [ ] Export JSON and CSV using Android's document picker.
- [ ] Open both files and check fields, escaping, and optional address behavior.
- [ ] Cancel the picker and verify the app handles cancellation normally.

## Privacy and safety
- [ ] Confirm no report is uploaded automatically.
- [ ] Confirm scan data is not presented as a security verdict.
- [ ] Test only devices you own or have explicit permission to assess.

## Record results
Device model: ____________________
Android version: ____________________
App commit / APK build: ____________________
Tester and date: ____________________
Issues found: ____________________
