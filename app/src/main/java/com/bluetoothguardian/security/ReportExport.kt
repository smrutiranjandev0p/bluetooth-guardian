package com.bluetoothguardian.security

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException

enum class ReportFormat(val mimeType: String, val extension: String) {
    JSON("application/json", "json"),
    CSV("text/csv", "csv")
}

/**
 * User-mediated export helpers.
 *
 * Register the launcher in a Composable with rememberLauncherForActivityResult:
 * CreateDocument(format.mimeType)
 * and call writeReportToUri after the user chooses a destination.
 */
fun reportFileName(label: String, format: ReportFormat): String {
    val safeLabel = label.trim()
        .replace(Regex("[^A-Za-z0-9._-]+"), "_")
        .trim('_')
        .take(48)
        .ifBlank { "bluetooth_review" }
    return "${safeLabel}_assessment.${format.extension}"
}

/**
 * Writes the report to a URI selected by the Android system document picker.
 * Returns null on success or a human-readable error message on failure.
 * A null URI (picker cancelled) is not an error and should not show success.
 */
fun writeReportToUri(
    resolver: ContentResolver,
    uri: Uri?,
    report: AssessmentReport,
    format: ReportFormat
): String? {
    if (uri == null) return null // User cancelled.
    return try {
        resolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { writer ->
            writer.write(if (format == ReportFormat.JSON) report.toJson() else report.toCsv())
        } ?: return "Could not open the selected destination."
        null
    } catch (e: IOException) {
        "Export failed while writing the file. Please choose another location and retry."
    } catch (e: SecurityException) {
        "Android denied access to the selected destination."
    }
}
