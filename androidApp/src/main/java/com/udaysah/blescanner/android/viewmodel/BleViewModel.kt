package com.udaysah.blescanner.android.viewmodel

import androidx.lifecycle.ViewModel
import com.udaysah.blescanner.scanner.BleScanner
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel that bridges the [BleScanner] to the Compose UI layer.
 *
 * Responsibilities:
 * - Exposes the scanner's [scannedDevices] and [isScanning] StateFlows
 *   directly to the UI for observation.
 * - Provides safe start/stop functions that delegate to the scanner.
 * - Ensures scanning stops when the ViewModel is cleared (e.g., Activity destroyed).
 *
 * Dependency Injection:
 * We use a simple [ViewModelFactory] instead of Hilt/Dagger to keep the project
 * lightweight and focused on BLE/KMP concepts. In a production app, you'd
 * typically use Hilt's @HiltViewModel annotation.
 *
 * @param bleScanner The platform-specific BLE scanner implementation.
 */
class BleViewModel(
    private val bleScanner: BleScanner
) : ViewModel() {

    /**
     * Real-time list of discovered BLE devices.
     * The UI observes this flow and recomposes when devices are added or updated.
     */
    val scannedDevices = bleScanner.scannedDevices

    /**
     * Whether the scanner is currently performing a BLE scan.
     * Used to toggle the Start/Stop button state in the UI.
     */
    val isScanning: StateFlow<Boolean> = bleScanner.isScanning

    /**
     * Toggles the scan state.
     * If currently scanning → stops. If idle → starts.
     */
    fun toggleScan() {
        if (isScanning.value) {
            bleScanner.stopScan()
        } else {
            bleScanner.startScan()
        }
    }

    /**
     * Start scanning for BLE devices.
     * Delegates directly to the scanner. Safe to call if already scanning (no-op).
     */
    fun startScan() {
        bleScanner.startScan()
    }

    /**
     * Stop scanning for BLE devices.
     * Delegates directly to the scanner. Safe to call if not scanning (no-op).
     */
    fun stopScan() {
        bleScanner.stopScan()
    }

    /**
     * Lifecycle cleanup — stop scanning when the ViewModel is destroyed.
     * This prevents the ScanCallback from leaking when the Activity is finished.
     */
    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
