package com.bluetoothguardian.security

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

data class DeviceRow(val name: String, val address: String, val rssi: Int)

class MainActivity : ComponentActivity() {
    private val rows = mutableStateListOf<DeviceRow>()
    private var callback: ScanCallback? = null

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
        if (permissions.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            permissionRequest.launch(permissions)
            return
        }

        val adapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter ?: return
        val scanner = adapter.bluetoothLeScanner ?: return
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

                if (rows.none { it.address == address }) {
                    rows.add(DeviceRow(name, address, result.rssi))
                }
            }
        }

        try {
            scanner.startScan(
                null,
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build(),
                callback
            )
        } catch (_: SecurityException) {
            // Permission may have been revoked while the screen was open.
        }
    }

    private fun stopScan() {
        try {
            val scanner =
                (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter?.bluetoothLeScanner
            callback?.let { scanner?.stopScan(it) }
        } catch (_: Exception) {
            // Scanner may already be stopped or Bluetooth may be unavailable.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home(rows = rows, scan = ::startScan, stop = ::stopScan)
            }
        }
    }

    override fun onStop() {
        stopScan()
        super.onStop()
    }
}

@Composable
private fun Home(rows: List<DeviceRow>, scan: () -> Unit, stop: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("BLUETOOTH GUARDIAN", style = MaterialTheme.typography.headlineSmall)
        Text("Research Edition • Authorized Lab")

        TabRow(selectedTabIndex = selectedTab) {
            listOf("Radar", "Assessment", "Lab").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                Row {
                    Button(onClick = scan) { Text("Scan BLE") }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = stop) { Text("Stop") }
                }
                Text("Discovered: ${rows.size}")
                LazyColumn {
                    items(rows) { device ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(device.name)
                                Text(device.address)
                                Text("${device.rssi} dBm")
                            }
                        }
                    }
                }
            }
            1 -> Assessment()
            else -> LabSimulator()
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Only test devices you own or are authorized to assess.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Assessment() {
    var deviceLabel by remember { mutableStateOf("") }
    var authorized by remember { mutableStateOf(false) }
    var firmwareVerified by remember { mutableStateOf(false) }
    var advisoriesReviewed by remember { mutableStateOf(false) }
    var pairingReviewed by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var reportGenerated by remember { mutableStateOf(false) }

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
            onClick = { reportGenerated = true }
        ) {
            Text("Generate assessment")
        }

        if (reportGenerated) {
            Text("PRELIMINARY REPORT: $deviceLabel")
            Text("This checklist is not a certified vulnerability scan.")
            val incomplete = listOf(
                "Firmware not verified" to !firmwareVerified,
                "Vendor advisories not reviewed" to !advisoriesReviewed,
                "Pairing review incomplete" to !pairingReviewed
            )
            incomplete.filter { it.second }.forEach {
                Text("• ${it.first} — review and document evidence.")
            }
            Text(notes)
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
