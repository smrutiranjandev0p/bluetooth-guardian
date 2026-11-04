# Phase 9 — Assessment Report Export Integration

## What changed
The Assessment tab now connects the report model and Android Storage Access Framework:

- User must confirm device ownership or explicit authorization before preparing a report.
- Report includes device label, authorization scope, checklist states, and optional user evidence note.
- User explicitly chooses JSON or CSV and then chooses the save destination through Android's document picker.
- Cancelling the picker does not show a false success message.
- Export stays local and user-initiated; no report is uploaded automatically.

## Privacy and interpretation
- Evidence notes should not contain passwords, secrets, or unrelated personal information.
- BLE addresses are not included in this assessment export path.
- Checklist completion is progress only—not a vulnerability score or security certification.
- Only assess devices you own or have explicit permission to review.

## Validation
Source integration committed. Android CI/build result and device-level picker validation are still pending.
