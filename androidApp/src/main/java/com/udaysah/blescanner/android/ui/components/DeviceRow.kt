package com.udaysah.blescanner.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.udaysah.blescanner.android.ui.theme.DarkCardBackground
import com.udaysah.blescanner.android.ui.theme.TextMuted
import com.udaysah.blescanner.android.ui.theme.TextSecondary
import com.udaysah.blescanner.android.ui.theme.rssiColor
import com.udaysah.blescanner.android.ui.theme.rssiLabel
import com.udaysah.blescanner.model.BleDevice

/**
 * A single row representing a discovered BLE device.
 *
 * Layout:
 * ┌─────────────────────────────────────────────────────────┐
 * │  ● │ Device Name                        │  -67 dBm      │
 * │    │ AA:BB:CC:DD:EE:FF                  │  Good         │
 * └─────────────────────────────────────────────────────────┘
 *
 * Features:
 * - RSSI signal dot animates color changes smoothly as signal fluctuates
 * - Device name falls back to "Unknown Device" when null
 * - MAC address displayed in a muted secondary style
 * - RSSI value + label right-aligned for quick scanning
 *
 * @param device The [BleDevice] to display.
 * @param modifier Optional [Modifier] for the card container.
 */
@Composable
fun DeviceRow(
    device: BleDevice,
    modifier: Modifier = Modifier
) {
    // Animate the RSSI color so transitions feel smooth as signal strength changes
    val signalColor by animateColorAsState(
        targetValue = rssiColor(device.rssi),
        animationSpec = tween(durationMillis = 500),
        label = "rssi_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Signal Strength Indicator Dot ──────────────────────
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(signalColor)
            )

            // ── Device Info (Name + MAC) ───────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = device.name ?: "Unknown Device",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (device.name != null) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        TextSecondary
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = device.macAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // ── RSSI Value + Label ─────────────────────────────────
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${device.rssi} dBm",
                    style = MaterialTheme.typography.titleMedium,
                    color = signalColor
                )

                Text(
                    text = rssiLabel(device.rssi),
                    style = MaterialTheme.typography.labelSmall,
                    color = signalColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
