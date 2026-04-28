import SwiftUI
import shared // Import the KMP shared framework

/// Main SwiftUI view for the BLE Scanner iOS app.
///
/// Observes the Kotlin StateFlow via a polling timer (since Swift cannot
/// directly subscribe to Kotlin's StateFlow reactively without a wrapper).
///
/// Architecture note:
/// Kotlin StateFlow → polled every 0.5s → @Published → SwiftUI recomposition
struct ContentView: View {

    @StateObject private var viewModel = BleDeviceViewModel()

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // ── Scan Control Bar ──────────────────────────────
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(viewModel.devices.isEmpty
                             ? "No devices found"
                             : "\(viewModel.devices.count) device\(viewModel.devices.count == 1 ? "" : "s") found")
                            .font(.subheadline)
                            .foregroundColor(.secondary)

                        if viewModel.isScanning {
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(Color.cyan)
                                    .frame(width: 8, height: 8)
                                    .opacity(viewModel.pulseOpacity)
                                Text("Scanning…")
                                    .font(.caption)
                                    .foregroundColor(.cyan)
                            }
                        }
                    }

                    Spacer()

                    Button(action: { viewModel.toggleScan() }) {
                        HStack(spacing: 6) {
                            Image(systemName: viewModel.isScanning ? "stop.fill" : "antenna.radiowaves.left.and.right")
                                .font(.system(size: 14))
                            Text(viewModel.isScanning ? "Stop Scan" : "Start Scan")
                                .font(.subheadline.weight(.semibold))
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(viewModel.isScanning ? Color.red : Color.teal)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                }
                .padding(.horizontal)
                .padding(.vertical, 12)

                Divider()

                // ── Device List ───────────────────────────────────
                if viewModel.devices.isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: viewModel.isScanning
                              ? "antenna.radiowaves.left.and.right"
                              : "bluetooth")
                            .font(.system(size: 48))
                            .foregroundColor(.gray.opacity(0.5))
                        Text(viewModel.isScanning
                             ? "Searching for devices…"
                             : "Tap \"Start Scan\" to discover\nnearby BLE devices")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    Spacer()
                } else {
                    List(viewModel.devices, id: \.macAddress) { device in
                        DeviceRowView(device: device)
                            .listRowBackground(Color(white: 0.11))
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("BLE Scanner")
            .navigationBarTitleDisplayMode(.inline)
            .preferredColorScheme(.dark)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// Device Row
// ═══════════════════════════════════════════════════════════════════════

struct DeviceRowView: View {
    let device: BleDevice

    var body: some View {
        HStack(spacing: 12) {
            // Signal dot
            Circle()
                .fill(rssiColor(Int(device.rssi)))
                .frame(width: 10, height: 10)

            // Device info
            VStack(alignment: .leading, spacing: 2) {
                Text(device.name ?? "Unknown Device")
                    .font(.system(.body, weight: .semibold))
                    .foregroundColor(device.name != nil ? .white : .gray)
                    .lineLimit(1)

                Text(device.macAddress)
                    .font(.caption)
                    .foregroundColor(.gray)
                    .lineLimit(1)
            }

            Spacer()

            // RSSI
            VStack(alignment: .trailing, spacing: 2) {
                Text("\(device.rssi) dBm")
                    .font(.system(.body, weight: .semibold))
                    .foregroundColor(rssiColor(Int(device.rssi)))

                Text(rssiLabel(Int(device.rssi)))
                    .font(.caption2)
                    .foregroundColor(rssiColor(Int(device.rssi)).opacity(0.7))
            }
        }
        .padding(.vertical, 8)
    }

    private func rssiColor(_ rssi: Int) -> Color {
        switch rssi {
        case (-59)...: return .green
        case (-69)...: return .blue
        case (-79)...: return .yellow
        default: return .red
        }
    }

    private func rssiLabel(_ rssi: Int) -> String {
        switch rssi {
        case (-59)...: return "Excellent"
        case (-69)...: return "Good"
        case (-79)...: return "Fair"
        default: return "Weak"
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// ViewModel (ObservableObject)
// ═══════════════════════════════════════════════════════════════════════

/// SwiftUI ViewModel that bridges the Kotlin StateFlow to @Published properties.
///
/// Since Swift cannot natively subscribe to Kotlin's StateFlow, we use a Timer
/// to poll the current value every 0.5 seconds. In a production app, you might
/// use SKIE or KMP-NativeCoroutines for proper Flow collection in Swift.
class BleDeviceViewModel: ObservableObject {

    @Published var devices: [BleDevice] = []
    @Published var isScanning: Bool = false
    @Published var pulseOpacity: Double = 1.0

    private let kotlinScanner = IosBleScanner()
    private var swiftScanner: IOSBleScanner!
    private var pollTimer: Timer?
    private var pulseTimer: Timer?

    init() {
        swiftScanner = IOSBleScanner(kotlinScanner: kotlinScanner)
        startPolling()
    }

    func toggleScan() {
        if isScanning {
            swiftScanner.stopScan()
        } else {
            swiftScanner.startScan()
        }
    }

    /// Poll the Kotlin StateFlow every 0.5s to sync state
    private func startPolling() {
        pollTimer = Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            DispatchQueue.main.async {
                self.devices = self.kotlinScanner.scannedDevices.value as! [BleDevice]
                self.isScanning = self.kotlinScanner.isScanning.value as! Bool
            }
        }

        // Pulse animation for scanning indicator
        pulseTimer = Timer.scheduledTimer(withTimeInterval: 0.8, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            DispatchQueue.main.async {
                withAnimation(.easeInOut(duration: 0.8)) {
                    self.pulseOpacity = self.pulseOpacity == 1.0 ? 0.4 : 1.0
                }
            }
        }
    }

    deinit {
        pollTimer?.invalidate()
        pulseTimer?.invalidate()
        swiftScanner.stopScan()
    }
}
