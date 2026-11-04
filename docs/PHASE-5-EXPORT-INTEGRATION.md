# Phase 5 — Android Export Engine

## Added source
`ReportExport.kt` provides:
- JSON and CSV MIME types/extensions
- safe filename generation from a user label
- writing through a user-selected Android document URI
- cancellation-safe behavior (null URI means cancelled)
- clear write/permission error messages

## Compose integration pattern
The app should register one launcher per format with Android's Storage Access Framework:

```kotlin
val jsonLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("application/json")
) { uri ->
    val error = writeReportToUri(
        context.contentResolver, uri, report, ReportFormat.JSON
    )
    if (uri != null) {
        // Show success only when error == null.
        // Show error text when error != null.
    }
}

val csvLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("text/csv")
) { uri ->
    val error = writeReportToUri(
        context.contentResolver, uri, report, ReportFormat.CSV
    )
    // Handle cancellation/error the same way as JSON.
}

// On explicit user action:
jsonLauncher.launch(reportFileName(report.deviceLabel, ReportFormat.JSON))
csvLauncher.launch(reportFileName(report.deviceLabel, ReportFormat.CSV))
```

## Privacy and UX requirements
- Launch document creation only after the user taps Export.
- Do not request broad storage permissions.
- Do not include BLE address unless report.includeBleAddress is true.
- Show a review/preview before share or export.
- Never display “Saved” when the picker was cancelled or a write failed.
- Avoid exporting reports automatically or in the background.

## Integration status
The helper is committed, but this repository snapshot does not currently contain a MainActivity or complete Gradle Android project to wire and run the UI. Integration and Android build verification remain pending.
