package com.udaysah.blescanner.android.fakes

import com.udaysah.blescanner.model.BleDevice
import com.udaysah.blescanner.scanner.BleScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBleScanner : BleScanner {

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    var startScanCallCount = 0
        private set
    var stopScanCallCount = 0
        private set

    override fun startScan() {
        startScanCallCount++
        _isScanning.value = true
        _scannedDevices.value = emptyList()
    }

    override fun stopScan() {
        stopScanCallCount++
        _isScanning.value = false
    }

    fun emitDevices(devices: List<BleDevice>) {
        _scannedDevices.value = devices
    }
}
