@file:OptIn(ExperimentalMaterial3Api::class)

package com.udaysah.blescanner.android.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udaysah.blescanner.android.permissions.BlePermissions
import com.udaysah.blescanner.android.ui.components.DeviceRow
import com.udaysah.blescanner.android.ui.theme.*
import com.udaysah.blescanner.android.viewmodel.BleViewModel

@Composable
fun BleScannerScreen(viewModel: BleViewModel) {
    val devices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    var permissionsGranted by remember { mutableStateOf(false) }
    var permissionsDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.values.all { it }
        permissionsDenied = !permissionsGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(BlePermissions.requiredPermissions)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(if (isScanning) Icons.Default.BluetoothSearching else Icons.Default.Bluetooth, null, tint = MaterialTheme.colorScheme.primary)
                        Text("BLE Scanner", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBackground, titleContentColor = MaterialTheme.colorScheme.onBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when {
                permissionsDenied -> PermissionDeniedContent { permissionLauncher.launch(BlePermissions.requiredPermissions) }
                permissionsGranted -> {
                    ScanControlBar(isScanning, devices.size) { viewModel.toggleScan() }
                    if (devices.isEmpty()) EmptyStateContent(isScanning)
                    else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(items = devices, key = { it.macAddress }) { DeviceRow(it) }
                    }
                }
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Requesting permissions…", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ScanControlBar(isScanning: Boolean, deviceCount: Int, onToggleScan: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(if (deviceCount == 0) "No devices found" else "$deviceCount device${if (deviceCount != 1) "s" else ""} found", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            AnimatedVisibility(isScanning, enter = fadeIn(), exit = fadeOut()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PulsingDot()
                    Text("Scanning…", style = MaterialTheme.typography.labelSmall, color = ScanningPulse)
                }
            }
        }
        Button(onClick = onToggleScan, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)) {
            Icon(if (isScanning) Icons.Default.Stop else Icons.Default.BluetoothSearching, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(if (isScanning) "Stop Scan" else "Start Scan", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(0.8f, 1.2f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse), label = "s")
    val alpha by transition.animateFloat(1f, 0.4f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse), label = "a")
    Box(Modifier.size(8.dp).scale(scale).alpha(alpha).clip(CircleShape).background(ScanningPulse))
}

@Composable
private fun EmptyStateContent(isScanning: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(if (isScanning) Icons.Default.BluetoothSearching else Icons.Outlined.BluetoothDisabled, null, Modifier.size(64.dp), tint = TextMuted)
            Text(if (isScanning) "Searching for devices…" else "Tap \"Start Scan\" to discover\nnearby BLE devices", style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Outlined.Info, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
            Text("Permissions Required", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(BlePermissions.PERMISSION_RATIONALE, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Grant Permissions") }
        }
    }
}
