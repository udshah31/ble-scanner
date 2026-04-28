package com.udaysah.blescanner.android.permissions

import android.Manifest
import android.os.Build

/**
 * BLE Permission Utilities for Android.
 *
 * Android's Bluetooth permission model changed significantly in Android 12 (API 31):
 *
 * ┌─────────────────────┬────────────────────┬────────────────────┐
 * │ Permission           │ Android 12+ (31+)  │ Android 11- (≤30)  │
 * ├─────────────────────┼────────────────────┼────────────────────┤
 * │ BLUETOOTH_SCAN       │ ✅ Required         │ ❌ N/A              │
 * │ BLUETOOTH_CONNECT    │ ✅ Required         │ ❌ N/A              │
 * │ ACCESS_FINE_LOCATION │ ❌ Not needed*      │ ✅ Required         │
 * │ ACCESS_COARSE_LOCATION│ ❌ Not needed      │ ✅ Required         │
 * └─────────────────────┴────────────────────┴────────────────────┘
 *
 * * On Android 12+, BLUETOOTH_SCAN with `usesPermissionFlags="neverForLocation"`
 *   in the manifest eliminates the location permission requirement for scanning.
 *
 * This utility provides a single [requiredPermissions] array that dynamically
 * returns the correct set of permissions based on the running OS version.
 */
object BlePermissions {

    /**
     * Returns the list of permissions required for BLE scanning on the current device.
     */
    val requiredPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ (API 31+): New granular Bluetooth permissions
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Android 11 and below: Location permission required for BLE scanning
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    /**
     * Human-readable rationale message for permission requests.
     * Shown to the user when they initially deny permissions.
     */
    const val PERMISSION_RATIONALE =
        "Bluetooth and Location permissions are required to scan for " +
        "nearby BLE devices. Without these permissions, the app cannot " +
        "discover Bluetooth Low Energy devices in your area."

    /**
     * Message shown when permissions are permanently denied.
     * The user must manually enable them in Settings.
     */
    const val PERMISSION_DENIED_MESSAGE =
        "Permissions have been denied. Please enable Bluetooth and Location " +
        "permissions in your device Settings to use the BLE Scanner."
}
