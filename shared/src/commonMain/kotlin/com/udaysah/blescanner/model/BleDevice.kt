package com.udaysah.blescanner.model

/**
 * Shared BLE Device data model.
 *
 * This data class represents a discovered Bluetooth Low Energy device.
 * It lives in commonMain so both Android and iOS platforms share the same contract.
 *
 * @property name The advertised name of the device. Null if the device does not
 *                broadcast a local name in its advertisement packet.
 * @property macAddress The unique hardware identifier.
 *                      - Android: MAC address (e.g., "AA:BB:CC:DD:EE:FF")
 *                      - iOS: CBPeripheral UUID string (Apple does not expose MAC addresses)
 * @property rssi Received Signal Strength Indicator in dBm.
 *               Typical range: -30 (very close) to -100 (far away).
 */
data class BleDevice(
    val name: String?,
    val macAddress: String,
    val rssi: Int
)
