package com.udaysah.blescanner.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.udaysah.blescanner.android.ui.screen.BleScannerScreen
import com.udaysah.blescanner.android.ui.theme.BLEScannerTheme
import com.udaysah.blescanner.android.viewmodel.BleViewModel
import com.udaysah.blescanner.android.viewmodel.BleViewModelFactory
import com.udaysah.blescanner.scanner.AndroidBleScanner

/**
 * Single-Activity architecture entry point for the BLE Scanner app.
 *
 * Wiring:
 * 1. Creates the [AndroidBleScanner] with the application context.
 * 2. Uses [BleViewModelFactory] to inject the scanner into [BleViewModel].
 * 3. Sets the Compose content with the [BLEScannerTheme] and [BleScannerScreen].
 */
class MainActivity : ComponentActivity() {

    // Create the scanner with applicationContext to avoid Activity leaks
    private val bleScanner by lazy { AndroidBleScanner(applicationContext) }

    // ViewModel with manual factory injection
    private val viewModel: BleViewModel by viewModels {
        BleViewModelFactory(bleScanner)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BLEScannerTheme {
                BleScannerScreen(viewModel = viewModel)
            }
        }
    }

    /**
     * Stop scanning when the Activity is destroyed to prevent
     * resource leaks. The ViewModel's onCleared also handles this,
     * but this is an extra safety net.
     */
    override fun onDestroy() {
        super.onDestroy()
        bleScanner.stopScan()
    }
}
