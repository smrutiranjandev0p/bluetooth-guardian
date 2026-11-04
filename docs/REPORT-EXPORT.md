# Phase 4 — Assessment Report Intelligence

## Purpose
Define portable, evidence-aware reports for authorized Bluetooth device reviews. This phase establishes the report schema and export design; Android document creation/share integration and validation are subsequent work.

## Principles
- Local-first; no automatic cloud upload.
- Separate observed BLE data from user-entered details.
- Report checklist progress, never an unsupported vulnerability score.
- User review before sharing; identifiers and notes may be sensitive.
- No hidden collection or background upload.

## JSON schema (prototype)
```json
{
  "schemaVersion": "1.0",
  "reportType": "authorized-device-review",
  "generatedAt": "ISO-8601 timestamp",
  "device": {
    "displayName": "User-provided label",
    "manufacturer": "Unknown",
    "model": "Unknown",
    "firmware": "Unknown",
    "bleAddress": "Optional; include only after user review"
  },
  "authorization": {
    "confirmedByUser": true,
    "scopeNote": "User-provided authorization context"
  },
  "observations": [],
  "reviewChecklist": [],
  "summary": {
    "completedChecks": 0,
    "totalChecks": 0,
    "interpretation": "Checklist progress only; not a security rating."
  },
  "limitations": [
    "This report does not establish that the device is secure or vulnerable.",
    "No exploit or active traffic test was performed."
  ]
}
```

## CSV fields
generated_at, device_label, manufacturer, model, firmware, authorization_confirmed, scope_note, optional_ble_address, check_id, check_label, completed, evidence_note, limitations.

Use UTF-8 and RFC-style quoting: quote fields containing commas, double quotes, or newlines; double embedded quotes. One row per checklist item; produce a valid summary row if no checklist items exist.

## Android export plan
- Use the Storage Access Framework document picker for user-selected save location.
- Share only after explicit user action.
- Avoid broad storage permissions.
- Default to omitting BLE addresses unless the user opts in.
- Preview before sharing; handle cancellation and write errors clearly.

## Validation checklist
- JSON parses and includes schemaVersion.
- CSV handles commas, quotes, Unicode, and line breaks.
- Empty checklist remains valid.
- Cancellation never reports success.
- Failed writes show an error.
- No fabricated scan findings.
- Unknown and user-entered values are clearly labeled.
