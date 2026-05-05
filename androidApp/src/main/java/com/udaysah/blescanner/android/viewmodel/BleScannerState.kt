package com.udaysah.blescanner.android.viewmodel

import com.udaysah.blescanner.model.BleDevice

data class BleScannerState(
    val devices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val permissionsGranted: Boolean = false,
    val permissionsDenied: Boolean = false
)
