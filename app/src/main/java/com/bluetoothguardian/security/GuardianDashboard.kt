package com.bluetoothguardian.security

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GuardianCyan = Color(0xFF45E6FF)
private val GuardianViolet = Color(0xFF9B7BFF)
private val GuardianBackground = Color(0xFF080B16)
private val GuardianPanel = Color(0xFF11182A)

/** UI-only dashboard preview. Connect real BLE observations in the app integration phase. */
data class GuardianRadarItem(
    val label: String,
    val detail: String,
    val rssi: Int,
    val isAuthorized: Boolean = false
)

@Composable
fun GuardianDashboard(
    devices: List<GuardianRadarItem> = emptyList(),
    scanActive: Boolean = false,
    onScanToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "guardianRadar")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarSweep"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(GuardianBackground, Color(0xFF10162A)))
            )
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("BLUETOOTH", color = GuardianCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("GUARDIAN", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text("LOCAL DEVICE AWARENESS", color = Color(0xFF9BAAC8), fontSize = 10.sp)
            }
            Surface(
                color = if (scanActive) Color(0xFF163B3B) else GuardianPanel,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    if (scanActive) "● SCANNING" else "○ STANDBY",
                    color = if (scanActive) GuardianCyan else Color.LightGray,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(270.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(250.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension * .46f
                for (factor in listOf(.32f, .62f, .92f)) {
                    drawCircle(
                        color = GuardianCyan.copy(alpha = .22f),
                        radius = radius * factor,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                drawLine(
                    color = GuardianViolet.copy(alpha = .45f),
                    start = Offset(center.x, 0f),
                    end = Offset(center.x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = GuardianViolet.copy(alpha = .45f),
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(Color.Transparent, GuardianCyan.copy(alpha = .02f), GuardianCyan.copy(alpha = .55f), Color.Transparent),
                        center = center
                    ),
                    startAngle = sweep,
                    sweepAngle = 68f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
                drawCircle(color = GuardianCyan, radius = 4.dp.toPx(), center = center)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(devices.size.toString(), color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold)
                Text("SIGNALS DETECTED", color = GuardianCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Nearby signals", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Advertisement observations • passive discovery", color = Color(0xFF9BAAC8), fontSize = 10.sp)
            }
            FilterChip(
                selected = scanActive,
                onClick = onScanToggle,
                label = { Text(if (scanActive) "Stop scan" else "Start scan") }
            )
        }

        Spacer(Modifier.height(10.dp))
        if (devices.isEmpty()) {
            Surface(
                color = GuardianPanel,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, GuardianCyan.copy(alpha = .15f), RoundedCornerShape(16.dp))
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Radar is ready", color = GuardianCyan, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Start a scan to display BLE observations here. Signal strength is approximate and does not indicate distance or security.",
                        color = Color(0xFFB8C4DB), fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                items(devices, key = { it.label + it.detail }) { item ->
                    SignalCard(item)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "PASSIVE BLE VIEW • No pairing, access attempts, or active testing",
            color = Color(0xFF7F8CA8),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun SignalCard(item: GuardianRadarItem) {
    Surface(
        color = GuardianPanel,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, GuardianViolet.copy(alpha = .2f), RoundedCornerShape(14.dp))
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(38.dp).background(GuardianCyan.copy(alpha = .12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⌁", color = GuardianCyan, fontSize = 23.sp)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(item.label.ifBlank { "Unknown BLE device" }, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(item.detail, color = Color(0xFF9BAAC8), fontSize = 10.sp)
                if (item.isAuthorized) Text("AUTHORIZED REVIEW DEVICE", color = GuardianCyan, fontSize = 9.sp)
            }
            Text("${item.rssi} dBm", color = GuardianCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
