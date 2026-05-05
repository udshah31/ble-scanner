@file:OptIn(ExperimentalMaterial3Api::class)

package com.udaysah.blescanner.android.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udaysah.blescanner.android.R
import com.udaysah.blescanner.android.permissions.BlePermissions
import com.udaysah.blescanner.android.ui.components.DeviceRow
import com.udaysah.blescanner.android.ui.theme.BLEScannerTheme
import com.udaysah.blescanner.android.ui.theme.DarkBackground
import com.udaysah.blescanner.android.ui.theme.ScanningPulse
import com.udaysah.blescanner.android.ui.theme.TextMuted
import com.udaysah.blescanner.android.ui.theme.TextSecondary
import com.udaysah.blescanner.android.viewmodel.BleScannerAction
import com.udaysah.blescanner.android.viewmodel.BleScannerState
import com.udaysah.blescanner.android.viewmodel.BleViewModel
import com.udaysah.blescanner.model.BleDevice
import org.koin.androidx.compose.koinViewModel

@Composable
fun BleScannerRoot(viewModel: BleViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.onAction(BleScannerAction.OnPermissionsResult(permissions))
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(BlePermissions.requiredPermissions)
    }

    BleScannerScreen(
        state = state,
        onAction = viewModel::onAction,
        onRequestPermissions = { permissionLauncher.launch(BlePermissions.requiredPermissions) }
    )
}

@Composable
fun BleScannerScreen(
    state: BleScannerState,
    onAction: (BleScannerAction) -> Unit,
    onRequestPermissions: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isScanning) Icons.Default.BluetoothSearching else Icons.Default.Bluetooth,
                            contentDescription = stringResource(
                                if (state.isScanning) R.string.cd_scanning else R.string.cd_bluetooth_idle
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("BLE Scanner", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.permissionsDenied -> PermissionDeniedContent(onRetry = onRequestPermissions)
                state.permissionsGranted -> {
                    ScanControlBar(
                        isScanning = state.isScanning,
                        deviceCount = state.devices.size,
                        onToggleScan = { onAction(BleScannerAction.OnToggleScan) }
                    )
                    if (state.devices.isEmpty()) {
                        EmptyStateContent(isScanning = state.isScanning)
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(items = state.devices, key = { it.macAddress }) { DeviceRow(it) }
                        }
                    }
                }
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Requesting permissions…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanControlBar(isScanning: Boolean, deviceCount: Int, onToggleScan: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (deviceCount == 0) "No devices found" else "$deviceCount device${if (deviceCount != 1) "s" else ""} found",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            AnimatedVisibility(visible = isScanning, enter = fadeIn(), exit = fadeOut()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PulsingDot()
                    Text("Scanning…", style = MaterialTheme.typography.labelSmall, color = ScanningPulse)
                }
            }
        }
        Button(
            onClick = onToggleScan,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Default.Stop else Icons.Default.BluetoothSearching,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isScanning) "Stop Scan" else "Start Scan", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        Modifier
            .size(8.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clip(CircleShape)
            .background(ScanningPulse)
    )
}

@Composable
private fun EmptyStateContent(isScanning: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Default.BluetoothSearching else Icons.Outlined.BluetoothDisabled,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextMuted
            )
            Text(
                text = if (isScanning) "Searching for devices…" else "Tap \"Start Scan\" to discover\nnearby BLE devices",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                "Permissions Required",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                BlePermissions.PERMISSION_RATIONALE,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
                Text("Grant Permissions")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BleScannerScreenScanningPreview() {
    BLEScannerTheme {
        BleScannerScreen(
            state = BleScannerState(
                permissionsGranted = true,
                isScanning = true,
                devices = listOf(
                    BleDevice("My Headphones", "AA:BB:CC:DD:EE:FF", -55),
                    BleDevice(null, "11:22:33:44:55:66", -82)
                )
            ),
            onAction = {},
            onRequestPermissions = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BleScannerScreenEmptyPreview() {
    BLEScannerTheme {
        BleScannerScreen(
            state = BleScannerState(permissionsGranted = true, isScanning = false),
            onAction = {},
            onRequestPermissions = {}
        )
    }
}
