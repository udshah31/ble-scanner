package com.udaysah.blescanner.scanner

import com.udaysah.blescanner.model.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS stub for the BLE Scanner interface.
 *
 * On iOS, the actual CoreBluetooth scanning logic is implemented in Swift
 * (IOSBleScanner.swift) because CoreBluetooth APIs are not available in Kotlin/Native.
 *
 * This Kotlin class serves as a bridge:
 * - Swift code calls [updateDevices] to push discovered devices into the StateFlow.
 * - The shared Kotlin interface remains satisfied.
 * - SwiftUI observes changes via the Kotlin StateFlow.
 *
 * Flow of data:
 * CoreBluetooth (Swift) → IOSBleScanner.swift → IosBleScanner.kt (this) → UI
 */
class IosBleScanner : BleScanner {

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    /**
     * Called from Swift to update the device list.
     * This is the bridge method that Swift uses to push CoreBluetooth results
     * into the Kotlin StateFlow.
     */
    fun updateDevices(devices: List<BleDevice>) {
        _scannedDevices.value = devices
    }

    /**
     * Called from Swift to update scanning state.
     */
    fun updateScanningState(scanning: Boolean) {
        _isScanning.value = scanning
    }

    override fun startScan() {
        // No-op in Kotlin — Swift handles the actual CoreBluetooth scanning.
        // The Swift layer calls updateScanningState(true) when scanning begins.
    }

    override fun stopScan() {
        // No-op in Kotlin — Swift handles the actual CoreBluetooth scanning.
        // The Swift layer calls updateScanningState(false) when scanning stops.
    }
}
