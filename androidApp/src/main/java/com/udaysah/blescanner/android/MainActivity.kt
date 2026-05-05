package com.udaysah.blescanner.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.udaysah.blescanner.android.ui.screen.BleScannerRoot
import com.udaysah.blescanner.android.ui.theme.BLEScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BLEScannerTheme {
                BleScannerRoot()
            }
        }
    }
}
