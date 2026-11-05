# ⚡ Bluetooth Guardian
### Android BLE Security Research & Authorized Lab Assessment

> A standalone Android companion for passive Bluetooth Low Energy (BLE) discovery, evidence-based security review, and safe lab simulation.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4)
![Status](https://img.shields.io/badge/Status-Research%20Prototype-orange)
![Scope](https://img.shields.io/badge/Scope-Authorized%20Testing-blue)

Bluetooth Guardian is an independent Kotlin + Jetpack Compose Android project. It is **separate from WebChem**.

## 🛰️ Current capabilities

- **Live BLE radar:** passive advertisement discovery, device label/address when available, and RSSI display.
- **Bounded scanner:** 15-second scan window, manual stop, low-power mode, lifecycle cleanup, and defensive error handling.
- **Guardian Command Center:** animated radar dashboard connected to the scanner.
- **Assessment checklist:** firmware/advisory/pairing review, authorization confirmation, and evidence notes.
- **Local report export:** user-mediated JSON/CSV export through Android's document picker.
- **Device intelligence:** evidence-oriented review checklist; completion is not a vulnerability or safety score.
- **Local synthetic lab:** simulator values are local and do not transmit Bluetooth traffic.
- **Accessibility:** TalkBack description for signal strength; manual accessibility validation remains outstanding.

## 🧭 Development status

| Phase | Capability | Status |
|---|---|---|
| 01–03 | Android foundation, BLE discovery, checklist, device intelligence | Implemented in source |
| 04–05 | Report model, JSON/CSV and document-picker export helper | Implemented in source |
| 06 | Futuristic radar dashboard | Implemented |
| 07 | Scanner timeout, lifecycle cleanup, RSSI updates, defensive handling | Implemented |
| 08 | GitHub Actions build + unit-test task + debug APK artifact | CI passing |
| 09 | In-app assessment report export integration | Implemented in source |
| 10 | Radar dashboard connected to live scanner | Implemented |
| 11 | RSSI TalkBack accessibility semantics | Implemented in source |
| 12 | Android device validation checklist | Documentation added |

**Latest CI:** [Android CI run #10 — successful](https://github.com/smrutiranjandev0p/bluetooth-guardian/actions/runs/41843668535)

CI success verifies the automated build and workflow steps only. **Physical-device behavior, Bluetooth permission flows, TalkBack, and actual JSON/CSV export still require hands-on Android validation.**

## 🧰 Tech stack

- Kotlin 2.0.21
- Android SDK / BLE APIs
- Jetpack Compose + Material 3
- Gradle Kotlin DSL
- Minimum Android version: **Android 10 (API 29)**

## 🚀 Build locally

1. Install Android Studio and a compatible Android SDK.
2. Clone:
   ```bash
   git clone https://github.com/smrutiranjandev0p/bluetooth-guardian.git
   ```
3. Open the repository in Android Studio and sync Gradle.
4. Run on an Android device or emulator with Bluetooth support.

CI currently installs Gradle 8.9 on the runner; the repository does not yet include a Gradle wrapper.

## 📱 Device validation

Use the [Phase 12 Android device validation checklist](docs/PHASE-12-ANDROID-DEVICE-VALIDATION.md) to test installation, permissions, scan start/stop/timeout, Bluetooth-off behavior, lifecycle handling, TalkBack, font scaling, orientation, report export, and privacy.

## 🔐 Safety, ethics & limitations

Bluetooth Guardian is intended **only for devices you own or have explicit authorization to assess**.

It does not implement unauthorized pairing/access, Bluetooth jamming or flooding, exploit delivery, forced disconnection, or interception of private Bluetooth connections. Nearby discovery does not grant permission to access another device.

The assessment checklist is **not a certified vulnerability scanner**. Device names, addresses, and RSSI values alone do not prove a vulnerability. Findings must be evidence-backed and reviewed by a qualified tester.

## 🗺️ Project independence

This repository is the dedicated home for Bluetooth Guardian, with its own Android package, documentation, and release pipeline. **It is not part of WebChem.**

## 🤝 Contributions

Issues and pull requests are welcome for defensive features, documentation, accessibility, and safe lab workflows. Do not submit features intended to disrupt or access devices without authorization.

## 📄 License

MIT — see [LICENSE](LICENSE).

## Phase 13 — Release Readiness & QA Gate

A release gate now tracks CI, physical-device checks, BLE permission/lifecycle behavior, JSON/CSV export, accessibility, privacy review, and signing. The app remains a research prototype until the outstanding device and release checks are completed. See [Phase 13 release readiness](docs/PHASE-13-RELEASE-READINESS.md).
