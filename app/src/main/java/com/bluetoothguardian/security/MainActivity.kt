package com.bluetoothguardian.security

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.time.Instant

private const val SCAN_WINDOW_MS = 15_000L

data class DeviceRow(val name: String, val address: String, val rssi: Int)

class MainActivity : ComponentActivity() {
    private val rows = mutableStateListOf<DeviceRow>()
    private var callback: ScanCallback? = null
    private var scanActive by mutableStateOf(false)
    private val scanHandler = Handler(Looper.getMainLooper())
    private val scanTimeout = Runnable { stopScan() }

    private val permissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= 31) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) startScan()
        }

    private fun startScan() {
        if (scanActive) return
        if (permissions.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            permissionRequest.launch(permissions)
            return
        }

        val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = try { manager.adapter } catch (_: SecurityException) { null }
        if (adapter == null || !adapter.isEnabled) return
        val scanner = try { adapter.bluetoothLeScanner } catch (_: SecurityException) { null } ?: return
        rows.clear()

        callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val name = try {
                    device.name ?: result.scanRecord?.deviceName ?: "Unknown device"
                } catch (_: SecurityException) {
                    "Name restricted"
                }
                val address = try {
                    device.address
                } catch (_: SecurityException) {
                    "Address restricted"
                }

                val index = rows.indexOfFirst { it.address == address }
                val row = DeviceRow(name, address, result.rssi)
                if (index >= 0) rows[index] = row else rows.add(row)
            }

            override fun onScanFailed(errorCode: Int) {
                stopScan()
            }
        }

        try {
            scanner.startScan(
                null,
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build(),
                callback
            )
            scanActive = true
            scanHandler.removeCallbacks(scanTimeout)
            scanHandler.postDelayed(scanTimeout, SCAN_WINDOW_MS)
        } catch (_: SecurityException) {
            scanActive = false
            callback = null
        } catch (_: IllegalStateException) {
            scanActive = false
            callback = null
        }
    }

    private fun stopScan() {
        scanHandler.removeCallbacks(scanTimeout)
        try {
            val scanner =
                (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter?.bluetoothLeScanner
            callback?.let { scanner?.stopScan(it) }
        } catch (_: Exception) {
            // Bluetooth can be disabled or permission can be revoked mid-session.
        } finally {
            callback = null
            scanActive = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home(rows = rows, scanActive = scanActive, scan = ::startScan, stop = ::stopScan)
            }
        }
    }

    override fun onStop() {
        stopScan()
        super.onStop()
    }

    override fun onDestroy() {
        stopScan()
        scanHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}

@Composable
private fun Home(
    rows: List<DeviceRow>,
    scanActive: Boolean,
    scan: () -> Unit,
    stop: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("BLUETOOTH GUARDIAN", style = MaterialTheme.typography.headlineSmall)
        Text("Research Edition • Authorized Lab")

        TabRow(selectedTabIndex = selectedTab) {
            listOf("Radar", "Assessment", "Intel", "Lab").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                GuardianDashboard(
                    devices = rows.map { device ->
                        GuardianRadarItem(
                            label = device.name,
                            detail = device.address,
                            rssi = device.rssi
                        )
                    },
                    scanActive = scanActive,
                    onScanToggle = { if (scanActive) stop() else scan() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            1 -> Assessment()
            2 -> DeviceIntelligence()
            else -> LabSimulator()
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Only test devices you own or are authorized to assess. BLE scanning is passive discovery.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Assessment() {
    val context = LocalContext.current
    var deviceLabel by remember { mutableStateOf("") }
    var authorized by remember { mutableStateOf(false) }
    var firmwareVerified by remember { mutableStateOf(false) }
    var advisoriesReviewed by remember { mutableStateOf(false) }
    var pairingReviewed by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var pendingReport by remember { mutableStateOf<AssessmentReport?>(null) }
    var exportMessage by remember { mutableStateOf("") }

    fun createReport(): AssessmentReport = AssessmentReport(
        generatedAt = Instant.now().toString(),
        deviceLabel = deviceLabel.trim(),
        authorizationConfirmed = authorized,
        scopeNote = "User confirmed ownership or explicit authorization.",
        observations = if (notes.isBlank()) emptyList() else listOf(
            ReportObservation(
                type = "user-note",
                source = "in-app assessment",
                value = notes.trim(),
                observedAt = Instant.now().toString()
            )
        ),
        checks = listOf(
            ReportCheck("firmware", "Firmware verified", firmwareVerified),
            ReportCheck("advisories", "Vendor advisories reviewed", advisoriesReviewed),
            ReportCheck("pairing", "Pairing settings reviewed", pairingReviewed)
        )
    )

    val jsonLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val report = pendingReport
        if (uri != null && report != null) {
            exportMessage = writeReportToUri(context.contentResolver, uri, report, ReportFormat.JSON)
                ?: "JSON report saved."
        } else if (uri == null) {
            exportMessage = "Export cancelled."
        }
    }

    val csvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val report = pendingReport
        if (uri != null && report != null) {
            exportMessage = writeReportToUri(context.contentResolver, uri, report, ReportFormat.CSV)
                ?: "CSV report saved."
        } else if (uri == null) {
            exportMessage = "Export cancelled."
        }
    }

    Column {
        OutlinedTextField(
            value = deviceLabel,
            onValueChange = { deviceLabel = it },
            label = { Text("Device / model") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = authorized, onCheckedChange = { authorized = it })
            Text("I own this device or have permission to assess it")
        }

        listOf(
            "Firmware verified" to firmwareVerified,
            "Vendor advisories reviewed" to advisoriesReviewed,
            "Pairing settings reviewed" to pairingReviewed
        ).forEachIndexed { index, item ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = item.second,
                    onCheckedChange = { checked ->
                        when (index) {
                            0 -> firmwareVerified = checked
                            1 -> advisoriesReviewed = checked
                            else -> pairingReviewed = checked
                        }
                    }
                )
                Text(item.first)
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Evidence notes (do not include secrets)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            enabled = authorized && deviceLabel.isNotBlank(),
            onClick = {
                pendingReport = createReport()
                exportMessage = "Report prepared. Choose an export format."
            }
        ) {
            Text("Prepare assessment report")
        }

        if (pendingReport != null) {
            Text("PRELIMINARY REPORT: $deviceLabel")
            Text("Checklist progress only—not a certified vulnerability scan.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    pendingReport = createReport()
                    jsonLauncher.launch(reportFileName(deviceLabel, ReportFormat.JSON))
                }) { Text("Export JSON") }
                OutlinedButton(onClick = {
                    pendingReport = createReport()
                    csvLauncher.launch(reportFileName(deviceLabel, ReportFormat.CSV))
                }) { Text("Export CSV") }
            }
        }

        if (exportMessage.isNotBlank()) {
            Text(exportMessage, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LabSimulator() {
    var simulatedWorkload by remember { mutableFloatStateOf(20f) }
    var simulatedLoss by remember { mutableFloatStateOf(10f) }

    Text("Synthetic local lab only — no Bluetooth transmission.")
    Text("Simulated workload: ${simulatedWorkload.toInt()} units/sec")
    Slider(
        value = simulatedWorkload,
        onValueChange = { simulatedWorkload = it },
        valueRange = 1f..100f
    )

    Text("Simulated loss: ${simulatedLoss.toInt()}%")
    Slider(
        value = simulatedLoss,
        onValueChange = { simulatedLoss = it },
        valueRange = 0f..100f
    )
    Text("Synthetic success estimate: ${(100 - simulatedLoss).toInt()}%")
}
