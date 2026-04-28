package com.udaysah.blescanner.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.udaysah.blescanner.model.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Android-specific implementation of [BleScanner].
 *
 * Uses Android's [BluetoothLeScanner] to discover nearby BLE devices and
 * publishes results through a [StateFlow] that the UI layer observes.
 *
 * Threading Model:
 * - [ScanCallback] is invoked on a Binder thread (not the main thread).
 * - [MutableStateFlow.update] is thread-safe (uses CAS internally), so we
 *   can safely update the flow from the callback without explicit synchronization.
 *
 * @param context Android [Context] used to access [BluetoothManager] system service.
 */
class AndroidBleScanner(context: Context) : BleScanner {

    // ── Bluetooth System Services ──────────────────────────────────────

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? =
        bluetoothManager?.adapter

    private val bleScanner: BluetoothLeScanner?
        get() = bluetoothAdapter?.bluetoothLeScanner

    // ── State ──────────────────────────────────────────────────────────

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // ── Scan Callback ──────────────────────────────────────────────────

    /**
     * Handles incoming BLE scan results from the system.
     *
     * Duplicate Handling Strategy:
     * When a scan result arrives, we check the current device list by MAC address.
     * - If the device already exists → update its RSSI and name (in case name
     *   was previously null but is now advertised, or has changed).
     * - If the device is new → append it to the list.
     *
     * This ensures the UI always shows the most up-to-date signal strength
     * without duplicating entries.
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val device = result.device
            val address = device.address
            @SuppressLint("MissingPermission")
            val name = device.name
            val rssi = result.rssi

            // Thread-safe update using MutableStateFlow.update {}
            // This uses compare-and-set (CAS) internally, guaranteeing atomicity
            // even when called from the Binder thread.
            _scannedDevices.update { currentList ->
                val existingIndex = currentList.indexOfFirst { it.macAddress == address }

                if (existingIndex != -1) {
                    // ── Device already discovered: update RSSI and name ──
                    // We create a new list (immutable data pattern) so that
                    // StateFlow detects the change and triggers recomposition.
                    currentList.toMutableList().apply {
                        this[existingIndex] = BleDevice(
                            name = name ?: currentList[existingIndex].name, // Prefer new name, fallback to existing
                            macAddress = address,
                            rssi = rssi
                        )
                    }
                } else {
                    // ── New device: append to the list ──
                    currentList + BleDevice(
                        name = name,
                        macAddress = address,
                        rssi = rssi
                    )
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            _isScanning.value = false
            // In a production app, you'd emit an error state here.
            // For this portfolio project, we silently stop scanning.
        }
    }

    // ── Public API ─────────────────────────────────────────────────────

    /**
     * Start BLE scanning.
     *
     * Guards:
     * 1. No-op if already scanning (prevents duplicate callbacks).
     * 2. No-op if Bluetooth is disabled or the scanner is unavailable.
     *
     * Clears the previous device list to start a fresh scan session.
     */
    @SuppressLint("MissingPermission")
    override fun startScan() {
        // Guard: already scanning
        if (_isScanning.value) return

        // Guard: Bluetooth not available or disabled
        val scanner = bleScanner ?: return

        // Clear previous results for a fresh scan
        _scannedDevices.value = emptyList()
        _isScanning.value = true

        scanner.startScan(scanCallback)
    }

    /**
     * Stop BLE scanning.
     *
     * Safe to call even if not currently scanning — the guard prevents
     * calling stopScan on the system scanner when no scan is active.
     */
    @SuppressLint("MissingPermission")
    override fun stopScan() {
        // Guard: not scanning
        if (!_isScanning.value) return

        bleScanner?.stopScan(scanCallback)
        _isScanning.value = false
    }
}
