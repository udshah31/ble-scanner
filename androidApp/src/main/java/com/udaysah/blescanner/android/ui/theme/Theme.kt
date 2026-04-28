package com.udaysah.blescanner.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * BLE Scanner Theme
 *
 * Dark-only theme designed for a professional, medical-device aesthetic.
 * The dark background reduces eye strain during prolonged scanning sessions
 * and makes the RSSI color indicators pop visually.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Teal500,
    onPrimary = DarkBackground,
    primaryContainer = Teal700,
    onPrimaryContainer = Teal200,

    secondary = Cyan400,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = DarkBackground,

    outline = TextMuted
)

@Composable
fun BLEScannerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = BleTypography,
        content = content
    )
}
