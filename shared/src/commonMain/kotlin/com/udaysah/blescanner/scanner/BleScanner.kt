package com.udaysah.blescanner.scanner

import com.udaysah.blescanner.model.BleDevice
import kotlinx.coroutines.flow.StateFlow

/**
 * Shared BLE Scanner interface — the contract between shared and platform code.
 *
 * Each platform (Android, iOS) provides its own implementation using native
 * Bluetooth APIs while exposing a unified reactive stream of discovered devices.
 *
 * Design Decisions:
 * - StateFlow is used instead of SharedFlow because consumers always need
 *   the current snapshot of discovered devices, not just new emissions.
 * - The list is cumulative: once a device is discovered, it stays in the list
 *   until scanning is stopped and a new scan begins.
 * - Duplicate devices are handled by the platform implementation — when a device
 *   is re-discovered, its RSSI (and optionally name) is updated in-place.
 */
interface BleScanner {

    /**
     * A read-only [StateFlow] emitting the current list of discovered BLE devices.
     * The list updates in real-time as devices are found or their RSSI changes.
     */
    val scannedDevices: StateFlow<List<BleDevice>>

    /**
     * Whether the scanner is currently performing a BLE scan.
     */
    val isScanning: StateFlow<Boolean>

    /**
     * Start scanning for nearby BLE devices.
     *
     * Calling this while already scanning is a no-op.
     * The previous device list is cleared to start fresh.
     * Implementations must ensure thread safety when updating [scannedDevices].
     */
    fun startScan()

    /**
     * Stop the active BLE scan.
     *
     * Calling this while not scanning is a no-op.
     * The discovered device list is preserved after stopping.
     */
    fun stopScan()
}
