package com.udaysah.blescanner.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udaysah.blescanner.scanner.BleScanner

/**
 * Factory for creating [BleViewModel] instances with a [BleScanner] dependency.
 *
 * We use a manual factory instead of Hilt to keep the project dependency-light.
 * This factory is used in MainActivity to create the ViewModel with the
 * platform-specific scanner implementation injected.
 *
 * Usage:
 * ```kotlin
 * val viewModel: BleViewModel by viewModels {
 *     BleViewModelFactory(AndroidBleScanner(applicationContext))
 * }
 * ```
 */
class BleViewModelFactory(
    private val bleScanner: BleScanner
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BleViewModel::class.java)) {
            return BleViewModel(bleScanner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
