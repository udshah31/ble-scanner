package com.udaysah.blescanner.android.viewmodel

sealed interface BleScannerAction {
    data object OnToggleScan : BleScannerAction
    data class OnPermissionsResult(val permissions: Map<String, Boolean>) : BleScannerAction
}
