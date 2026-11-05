# Phase 13 — Release Readiness & QA Gate

## Purpose
This phase converts the current research prototype into a testable release candidate without claiming device validation prematurely.

## Current verified baseline
- GitHub Actions Android CI has completed successfully for the README status refresh commit.
- CI builds the debug APK, runs the configured unit-test task, and uploads the APK artifact.
- CI does **not** prove runtime behavior on a physical Android device.
- No signed production/release APK or Play Store publication is claimed.

## Release gates

| Gate | Required evidence | Status |
|---|---|---|
| Source / CI | Successful latest Android CI run | PASS (CI) |
| Install and launch | App installs and opens on target Android device | NOT TESTED |
| BLE permissions | Grant, deny, and re-grant behavior checked | NOT TESTED |
| BLE scan lifecycle | Start, stop, 15-second timeout, app background/foreground | NOT TESTED |
| Bluetooth disabled | Clear handling when Bluetooth is off | NOT TESTED |
| Dashboard | Radar state and discovered device list match scanner | NOT TESTED |
| Assessment flow | Authorization gate and checklist behavior | NOT TESTED |
| JSON export | Save, reopen, and inspect exported JSON | NOT TESTED |
| CSV export | Save, reopen, verify commas/quotes/newlines | NOT TESTED |
| Accessibility | TalkBack labels and navigation | NOT TESTED |
| Privacy | Confirm no unexpected network/account/analytics behavior | NOT TESTED |
| Release signing | Release keystore and signed build process | NOT CONFIGURED |
| Store readiness | Privacy policy, listing assets, Play Console checks | NOT STARTED |

## Tester record
Complete this after testing on a real device or emulator.

- Device model:
- Android version:
- App commit / APK build:
- Test date:
- BLE permission result:
- Scan start/stop/timeout:
- Bluetooth-off result:
- Report JSON export:
- Report CSV export:
- TalkBack / font scaling:
- Crashes or defects:
- Screenshots or logs (avoid publishing personal BLE identifiers):
- Tester conclusion: PASS / PASS WITH ISSUES / FAIL

## Defect handling
1. Record the exact device, Android version, steps, expected behavior, and observed behavior.
2. Avoid including nearby people's BLE identifiers or addresses in public bug reports.
3. Fix one reproducible issue at a time.
4. Re-run CI after each source change.
5. Re-test the affected scenario on device before marking it resolved.

## Release decision
Do not label the app production-ready until all critical device tests pass, the release build is signed securely, and privacy/store requirements are reviewed. Keep the current app labeled as a research prototype until then.

## Safe scope
Testing is limited to passive BLE discovery and devices owned by the tester or explicitly authorized for assessment. Discovery and signal strength are not proof of a vulnerability or authorization.
