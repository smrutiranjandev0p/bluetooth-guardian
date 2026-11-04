package com.bluetoothguardian.security

import org.json.JSONArray
import org.json.JSONObject

/**
 * Evidence-aware report data for an authorized, user-led device review.
 * Completion counts are checklist progress, not a vulnerability score.
 */
data class AssessmentReport(
    val generatedAt: String,
    val deviceLabel: String,
    val manufacturer: String = "",
    val model: String = "",
    val firmware: String = "",
    val authorizationConfirmed: Boolean,
    val scopeNote: String = "",
    val includeBleAddress: Boolean = false,
    val bleAddress: String = "",
    val observations: List<ReportObservation> = emptyList(),
    val checks: List<ReportCheck> = emptyList()
) {
    fun toJson(): String {
        val completed = checks.count { it.completed }
        return JSONObject().apply {
            put("schemaVersion", "1.0")
            put("reportType", "authorized-device-review")
            put("generatedAt", generatedAt)
            put("device", JSONObject().apply {
                put("displayName", deviceLabel)
                put("manufacturer", manufacturer.ifBlank { "Unknown" })
                put("model", model.ifBlank { "Unknown" })
                put("firmware", firmware.ifBlank { "Unknown" })
                if (includeBleAddress && bleAddress.isNotBlank()) put("bleAddress", bleAddress)
            })
            put("authorization", JSONObject().apply {
                put("confirmedByUser", authorizationConfirmed)
                put("scopeNote", scopeNote)
            })
            put("observations", JSONArray().apply {
                observations.forEach { item ->
                    put(JSONObject().apply {
                        put("type", item.type)
                        put("source", item.source)
                        put("value", item.value)
                        put("observedAt", item.observedAt)
                    })
                }
            })
            put("reviewChecklist", JSONArray().apply {
                checks.forEach { item ->
                    put(JSONObject().apply {
                        put("id", item.id)
                        put("label", item.label)
                        put("completed", item.completed)
                        put("evidenceNote", item.evidenceNote)
                    })
                }
            })
            put("summary", JSONObject().apply {
                put("completedChecks", completed)
                put("totalChecks", checks.size)
                put("interpretation", "Checklist progress only; not a security rating.")
            })
            put("limitations", JSONArray().apply {
                put("This report does not establish that the device is secure or vulnerable.")
                put("No exploit or active traffic test is implied.")
                put("User-entered details may not have been independently verified.")
            })
        }.toString(2)
    }

    fun toCsv(): String {
        val header = listOf(
            "generated_at", "device_label", "manufacturer", "model", "firmware",
            "authorization_confirmed", "scope_note", "ble_address",
            "check_id", "check_label", "completed", "evidence_note", "limitations"
        )
        val limitation = "Checklist only; not a security verdict. No exploit or active traffic test implied."
        val rows: List<ReportCheck?> = if (checks.isEmpty()) listOf(null) else checks
        return buildString {
            appendLine(header.joinToString(",") { escapeCsv(it) })
            rows.forEach { check ->
                val fields = listOf(
                    generatedAt, deviceLabel, manufacturer, model, firmware,
                    authorizationConfirmed.toString(), scopeNote,
                    if (includeBleAddress) bleAddress else "",
                    check?.id.orEmpty(), check?.label.orEmpty(),
                    check?.completed?.toString().orEmpty(), check?.evidenceNote.orEmpty(),
                    limitation
                )
                appendLine(fields.joinToString(",") { escapeCsv(it) })
            }
        }
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"$escaped\"" else escaped
    }
}

data class ReportObservation(
    val type: String,
    val source: String,
    val value: String,
    val observedAt: String
)

data class ReportCheck(
    val id: String,
    val label: String,
    val completed: Boolean,
    val evidenceNote: String = ""
)
