import Foundation
import CoreBluetooth
import shared // Import the KMP shared framework

/// iOS BLE Scanner implementation using CoreBluetooth.
///
/// This class acts as a `CBCentralManagerDelegate` to discover nearby BLE devices
/// and bridges the results into the Kotlin shared module's `IosBleScanner` which
/// exposes them via a `StateFlow`.
///
/// Architecture:
/// ```
/// CoreBluetooth → IOSBleScanner (Swift) → IosBleScanner (Kotlin) → StateFlow → SwiftUI
/// ```
///
/// Duplicate Handling:
/// Like the Android implementation, when a device is re-discovered, we update
/// its RSSI and name in the local array, then push the entire array to Kotlin.
class IOSBleScanner: NSObject, CBCentralManagerDelegate {

    // ── Dependencies ──────────────────────────────────────────────────

    /// The Kotlin bridge that holds the StateFlow
    private let kotlinScanner: IosBleScanner

    /// CoreBluetooth central manager for scanning
    private var centralManager: CBCentralManager!

    /// Local cache of discovered devices (Swift-side)
    private var discoveredDevices: [BleDevice] = []

    /// Track whether we're currently scanning
    private var scanning = false

    // ── Initialization ────────────────────────────────────────────────

    init(kotlinScanner: IosBleScanner) {
        self.kotlinScanner = kotlinScanner
        super.init()
        // Initialize CBCentralManager — this triggers the delegate callback
        // for Bluetooth state (poweredOn, poweredOff, etc.)
        self.centralManager = CBCentralManager(delegate: self, queue: nil)
    }

    // ── Public API ────────────────────────────────────────────────────

    /// Start scanning for nearby BLE devices.
    /// Clears the previous device list for a fresh scan session.
    func startScan() {
        guard !scanning else { return }
        guard centralManager.state == .poweredOn else {
            print("⚠️ Bluetooth is not powered on. Current state: \(centralManager.state.rawValue)")
            return
        }

        // Clear previous results
        discoveredDevices.removeAll()
        kotlinScanner.updateDevices(devices: [])

        scanning = true
        kotlinScanner.updateScanningState(scanning: true)

        // allowDuplicates: true ensures we get RSSI updates for already-discovered devices
        centralManager.scanForPeripherals(
            withServices: nil,
            options: [CBCentralManagerScanOptionAllowDuplicatesKey: true]
        )
    }

    /// Stop the active BLE scan.
    func stopScan() {
        guard scanning else { return }

        centralManager.stopScan()
        scanning = false
        kotlinScanner.updateScanningState(scanning: false)
    }

    // ── CBCentralManagerDelegate ──────────────────────────────────────

    /// Called when the Bluetooth state changes (e.g., powered on/off).
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            print("✅ Bluetooth is powered on and ready.")
        case .poweredOff:
            print("⚠️ Bluetooth is powered off.")
            if scanning {
                stopScan()
            }
        case .unauthorized:
            print("❌ Bluetooth permission denied.")
        case .unsupported:
            print("❌ Bluetooth LE is not supported on this device.")
        default:
            print("ℹ️ Bluetooth state: \(central.state.rawValue)")
        }
    }

    /// Called when a peripheral is discovered during scanning.
    ///
    /// Duplicate Handling:
    /// - Check if a device with the same UUID already exists in our array.
    /// - If yes → update its RSSI (and name if newly available).
    /// - If no → append it as a new device.
    /// - Push the updated array to the Kotlin StateFlow.
    func centralManager(
        _ central: CBCentralManager,
        didDiscover peripheral: CBPeripheral,
        advertisementData: [String: Any],
        rssi RSSI: NSNumber
    ) {
        let uuid = peripheral.identifier.uuidString
        let name = peripheral.name
        let rssi = RSSI.int32Value

        // Map to the shared Kotlin BleDevice model
        // Note: iOS doesn't expose MAC addresses, so we use the CBPeripheral UUID
        let device = BleDevice(
            name: name,
            macAddress: uuid,
            rssi: rssi
        )

        // ── Duplicate handling ────────────────────────────────────
        if let existingIndex = discoveredDevices.firstIndex(where: { $0.macAddress == uuid }) {
            // Update existing device with new RSSI and potentially new name
            discoveredDevices[existingIndex] = BleDevice(
                name: name ?? discoveredDevices[existingIndex].name,
                macAddress: uuid,
                rssi: rssi
            )
        } else {
            // New device — append
            discoveredDevices.append(device)
        }

        // Push the updated list to Kotlin's StateFlow
        kotlinScanner.updateDevices(devices: discoveredDevices)
    }
}
