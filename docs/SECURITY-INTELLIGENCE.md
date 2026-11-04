# Security Intelligence — Phase 3

## Scope
Phase 3 adds a local, evidence-based review checklist for devices the user owns or is explicitly authorized to assess.

## What it does
- Collects vendor, model, and firmware details supplied by the assessor.
- Tracks review of firmware, official vendor advisories, update lifecycle, pairing/bonding, unused features, and physical access considerations.
- Shows **checklist completion**, not a risk score.
- Produces a local review summary from user-entered information.

## What it does not do
- It does not query CVE databases or automatically assert vulnerability matches.
- It does not infer vulnerability from Bluetooth name, address, RSSI, manufacturer label, or checklist completion.
- It does not connect to a device to read private data or bypass pairing.
- It does not transmit test traffic, exploit payloads, flood, jam, or disconnect devices.

## Evidence workflow
1. Confirm ownership or explicit authorization.
2. Identify the exact manufacturer, model, and firmware version.
3. Use official vendor support/security advisories and trustworthy public sources.
4. Record source URLs, dates, and exact version applicability in assessor notes.
5. Separate confirmed facts, unknowns, and hypotheses.
6. Verify findings manually before reporting.

## Interpretation
Checklist completion is a measure of how much review was performed. It is not a device safety rating, vulnerability severity, or certification. Missing information should be reported as **unknown** rather than “vulnerable.”

## Next phase
Potential follow-up: export a structured report (JSON/CSV) with assessor notes, evidence references, timestamp, and explicit limitations.
