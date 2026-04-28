package com.udaysah.blescanner.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * BLE Scanner Color Palette
 *
 * A modern, professional color system inspired by connected medical device UIs.
 * Dark-mode primary design with teal/cyan accents for a "tech-forward" feel.
 */

// ── Primary Brand Colors ───────────────────────────────────────────
val Teal200 = Color(0xFF80CBC4)
val Teal500 = Color(0xFF009688)
val Teal700 = Color(0xFF00796B)
val Cyan400 = Color(0xFF26C6DA)

// ── Dark Theme Surface Colors ──────────────────────────────────────
val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF161B22)
val DarkSurfaceVariant = Color(0xFF21262D)
val DarkCardBackground = Color(0xFF1C2128)

// ── Text Colors ────────────────────────────────────────────────────
val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)
val TextMuted = Color(0xFF6E7681)

// ── RSSI Signal Strength Colors ────────────────────────────────────
val RssiExcellent = Color(0xFF3FB950)   // Green  — > -60 dBm (close)
val RssiGood = Color(0xFF58A6FF)       // Blue   — -60 to -70 dBm
val RssiFair = Color(0xFFD29922)       // Amber  — -70 to -80 dBm
val RssiWeak = Color(0xFFF85149)       // Red    — < -80 dBm (far away)

// ── Accent / Status Colors ─────────────────────────────────────────
val ScanningPulse = Color(0xFF26C6DA)  // Cyan pulse for scanning indicator
val ErrorRed = Color(0xFFF85149)
val SuccessGreen = Color(0xFF3FB950)

/**
 * Returns the appropriate color for a given RSSI value.
 *
 * RSSI (Received Signal Strength Indicator) ranges:
 * - Excellent (> -60 dBm): Device is very close, strong signal
 * - Good (-60 to -70 dBm): Reliable connection range
 * - Fair (-70 to -80 dBm): Moderate signal, may be intermittent
 * - Weak (< -80 dBm): Device is far away, unreliable
 */
fun rssiColor(rssi: Int): Color = when {
    rssi > -60 -> RssiExcellent
    rssi > -70 -> RssiGood
    rssi > -80 -> RssiFair
    else -> RssiWeak
}

/**
 * Returns a human-readable label for the RSSI signal strength.
 */
fun rssiLabel(rssi: Int): String = when {
    rssi > -60 -> "Excellent"
    rssi > -70 -> "Good"
    rssi > -80 -> "Fair"
    else -> "Weak"
}
