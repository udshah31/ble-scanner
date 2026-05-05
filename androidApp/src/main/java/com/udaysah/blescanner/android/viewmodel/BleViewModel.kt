package com.udaysah.blescanner.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udaysah.blescanner.scanner.BleScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class BleViewModel(private val bleScanner: BleScanner) : ViewModel() {

    private val _state = MutableStateFlow(BleScannerState())
    val state = _state.asStateFlow()

    init {
        bleScanner.scannedDevices
            .onEach { devices -> _state.update { it.copy(devices = devices) } }
            .launchIn(viewModelScope)

        bleScanner.isScanning
            .onEach { isScanning -> _state.update { it.copy(isScanning = isScanning) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: BleScannerAction) {
        when (action) {
            is BleScannerAction.OnToggleScan -> {
                if (_state.value.isScanning) bleScanner.stopScan() else bleScanner.startScan()
            }
            is BleScannerAction.OnPermissionsResult -> {
                val granted = action.permissions.isNotEmpty() && action.permissions.values.all { it }
                _state.update { it.copy(permissionsGranted = granted, permissionsDenied = !granted) }
                if (granted) bleScanner.startScan()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
