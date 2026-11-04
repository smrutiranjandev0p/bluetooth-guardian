# ⚡ Bluetooth Guardian
### Android BLE Security Research & Authorized Lab Assessment

> **A standalone Android research companion for Bluetooth Low Energy (BLE) discovery, evidence-based security review, and safe lab simulation.**

Bluetooth Guardian is an independent Android project built with **Kotlin + Jetpack Compose**. It is intentionally separate from WebChem.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4)
![Status](https://img.shields.io/badge/Status-Research%20Prototype-orange)
![Scope](https://img.shields.io/badge/Scope-Authorized%20Testing-blue)

---

## 🛰️ Current capabilities

- **BLE Radar** — discover nearby BLE advertisements and display available device name, address (when permitted), and signal strength.
- **Evidence-first assessment** — review firmware status, vendor advisories, pairing configuration, and documented observations.
- **Device Intelligence (Phase 3)** — local review checklist for vendor/model/firmware, advisory checks, update status, pairing, unused services, and physical-access considerations. Reports checklist completion—not a vulnerability or safety score.
- **Authorization gate** — assessment report generation requires an ownership/permission confirmation.
- **Local synthetic lab** — adjustable simulated workload and packet-loss values. This simulator does not transmit Bluetooth traffic.
- **Privacy-conscious baseline** — no account, analytics, or cloud backend is included in this prototype.

## 🧭 Roadmap

| Phase | Planned capability | Status |
|---|---|---|
| 01 | Android app foundation and BLE discovery | Prototype |
| 02 | Security assessment checklist and local simulator | Prototype |
| 03 | Evidence-based device intelligence checklist | Implemented (prototype) |
| 04 | Evidence-aware JSON/CSV report model and schema | Implemented (prototype) |
| 05 | Android document-picker export helper | Implemented (integration pending) |
| 06 | Futuristic cyber dashboard and animated radar UI prototype | Implemented (UI prototype; integration pending) |
| 07 | BLE scan timeout, lifecycle cleanup, live RSSI updates, and app safety polish | Implemented (source; build validation pending) |\n| 08 | GitHub Actions Android build, unit-test task, and debug APK artifact | CI workflow added (first run pending) |

## 🧰 Tech stack

- Kotlin
- Android SDK / Android BLE APIs
- Jetpack Compose + Material 3
- Gradle Kotlin DSL
- Minimum Android version: **Android 10 (API 29)**

## 🚀 Build locally

1. Install Android Studio and a compatible Android SDK.
2. Clone this repository:
   ```bash
   git clone https://github.com/smrutiranjandev0p/bluetooth-guardian.git
   ```
3. Open the project folder in Android Studio.
4. Sync Gradle.
5. Run on an Android device or emulator with Bluetooth support.

> This is a research prototype. It has not yet been independently audited, device-tested across Android versions, or released as a signed APK.

## 🔐 Safety, ethics & limitations

Bluetooth Guardian is intended **only for devices you own or have explicit authorization to assess**.

It does **not** implement unauthorized pairing/access, Bluetooth jamming, flooding, exploit delivery, forced disconnection, or interception of private Bluetooth connections. Nearby discovery does not grant permission to access another device.

The assessment checklist is **not a certified vulnerability scanner**. Device names, addresses, and RSSI values alone do not prove a vulnerability. Findings must be supported by verifiable evidence and reviewed by a qualified tester.

## 🗺️ Project independence

This repository is the dedicated home for Bluetooth Guardian. It has its own codebase, Android application ID, documentation, and future release pipeline. **It is not part of WebChem.**

## 🤝 Contributions

Issues and pull requests are welcome for defensive features, documentation, accessibility, and safe lab workflows. Please do not submit features intended to disrupt or access devices without authorization.

## 📄 License

MIT — see [LICENSE](LICENSE).


## Phase 4 — Report Intelligence

The report model and JSON/CSV schema are documented in [`docs/REPORT-EXPORT.md`](docs/REPORT-EXPORT.md), with implementation in [`AssessmentReport.kt`](app/src/main/java/com/bluetoothguardian/security/AssessmentReport.kt). This is source-level work only; Android compilation and export validation are still pending.


## Phase 5 — Android Export Engine

User-mediated JSON/CSV save helpers are available in [`ReportExport.kt`](app/src/main/java/com/bluetoothguardian/security/ReportExport.kt), with integration guidance in [`PHASE-5-EXPORT-INTEGRATION.md`](docs/PHASE-5-EXPORT-INTEGRATION.md). MainActivity wiring and Android build validation remain pending.


## Phase 6 — Guardian Command Center

The Compose dashboard prototype is in [`GuardianDashboard.kt`](app/src/main/java/com/bluetoothguardian/security/GuardianDashboard.kt). Integration notes and accessibility tasks are in [`PHASE-6-GUARDIAN-DASHBOARD.md`](docs/PHASE-6-GUARDIAN-DASHBOARD.md). BLE callbacks, app entry-point wiring, reduced-motion settings, and build verification remain pending.


## Phase 7 — Scanner Safety

The BLE scanner now uses a bounded 15-second session, explicit stop, lifecycle cleanup, low-power scan mode, and updated RSSI observations. See [`MainActivity.kt`](app/src/main/java/com/bluetoothguardian/security/MainActivity.kt) and [`PHASE-7-SCANNER-HARDENING.md`](docs/PHASE-7-SCANNER-HARDENING.md). Android build and physical-device validation remain pending.


## Phase 8 — Automated Build Pipeline

GitHub Actions workflow: [android-ci.yml](.github/workflows/android-ci.yml)

On pushes to `main`, pull requests, or manual dispatch, CI configures JDK 17 and Gradle 8.9, builds the debug APK, runs the Android unit-test task, and uploads the APK as a short-retention artifact after successful completion.

**Current status:** workflow committed; first GitHub Actions run and Android compilation are not yet verified. This repository still does not include a Gradle wrapper, so CI currently uses the pinned Gradle installation on the runner.
