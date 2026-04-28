# Project Architecture

## Overview

BLE Scanner is a Kotlin Multiplatform Mobile (KMM) application that demonstrates best practices for building cross-platform apps with shared business logic and platform-specific UI implementations.

---

## Directory Structure

```
BLE Scanner/
├── shared/                          # Multiplatform shared module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/              # Code shared between Android and iOS
│       │   └── kotlin/
│       │       └── com/udaysah/blescanner/
│       │           ├── model/
│       │           │   └── BleDevice.kt         # Shared data model
│       │           └── scanner/
│       │               └── BleScanner.kt        # Shared interface
│       ├── androidMain/             # Android-specific implementations
│       │   └── kotlin/
│       │       └── com/udaysah/blescanner/
│       │           └── scanner/
│       │               └── AndroidBleScanner.kt
│       └── iosMain/                 # iOS-specific implementations
│           └── kotlin/
│               └── com/udaysah/blescanner/
│                   └── scanner/
│                       └── IosBleScanner.kt
│
├── androidApp/                      # Android application
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml      # Permissions and app configuration
│   │   └── java/com/udaysah/blescanner/android/
│   │       ├── MainActivity.kt       # Entry point (Single Activity)
│   │       ├── permissions/
│   │       │   └── BlePermissions.kt # Permission handling
│   │       ├── viewmodel/
│   │       │   ├── BleViewModel.kt   # MVVM ViewModel
│   │       │   └── BleViewModelFactory.kt
│   │       └── ui/
│   │           ├── screen/
│   │           │   └── BleScannerScreen.kt  # Main composable
│   │           ├── components/
│   │           │   └── DeviceRow.kt         # Reusable card component
│   │           └── theme/
│   │               ├── Theme.kt
│   │               ├── Color.kt
│   │               └── Type.kt
│   └── res/                         # Android resources
│
├── iosApp/                          # iOS application
│   └── iosApp/
│       ├── iOSApp.swift             # SwiftUI app entry point
│       ├── ContentView.swift        # Root SwiftUI view
│       ├── IOSBleScanner.swift      # CoreBluetooth bridge
│       ├── Info.plist               # iOS configuration
│       └── iOSApp.swift
│
├── build.gradle.kts                 # Root gradle build script
├── settings.gradle.kts              # Gradle settings
├── gradle.properties                # Gradle properties
├── gradle/                          # Gradle wrapper and plugins
│   └── libs.versions.toml           # Centralized dependency versions
│
└── Documentation/
    ├── README.md                    # Main project documentation
    ├── ARCHITECTURE.md              # This file
    ├── CONTRIBUTING.md              # Contribution guidelines
    ├── SETUP_GUIDE.md               # Development setup
    └── LICENSE                      # MIT License
```

---

## Architectural Patterns

### 1. Kotlin Multiplatform Architecture

The project leverages KMP to share business logic while maintaining platform-specific UI implementations.

```
┌─────────────────────────────────────────────────────────┐
│              ANDROID UI (Jetpack Compose)               │
├─────────────────────────────────────────────────────────┤
│              iOS UI (SwiftUI)                           │
├─────────────────────────────────────────────────────────┤
│  SHARED BUSINESS LOGIC (Kotlin Multiplatform)           │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  Common Interfaces & Data Models               │    │
│  │  - BleScanner (Interface)                      │    │
│  │  - BleDevice (Data Model)                      │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  Android Implementation                         │    │
│  │  - AndroidBleScanner (BluetoothLeScanner)      │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  iOS Implementation                             │    │
│  │  - IosBleScanner (Kotlin wrapper)              │    │
│  │  - IOSBleScanner (Swift + CoreBluetooth)       │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
          │                              │
          ▼                              ▼
   ┌────────────────┐           ┌────────────────┐
   │ BluetoothLeAPI │           │ CoreBluetooth  │
   └────────────────┘           └────────────────┘
```

### 2. MVVM Pattern

Android uses the Model-View-ViewModel pattern for clean architecture:

```
┌─────────────────────────────────────┐
│         Compose UI Layer             │
│                                       │
│  ┌─────────────────────────────┐    │
│  │ BleScannerScreen            │    │
│  │ - Displays device list      │    │
│  │ - Handles user interactions │    │
│  └─────────────────────────────┘    │
│                 │                     │
│                 ▼                     │
│  ┌─────────────────────────────┐    │
│  │ BleViewModel                │    │
│  │ - Manages UI state          │    │
│  │ - Exposes StateFlows        │    │
│  │ - Delegates to BleScanner   │    │
│  └─────────────────────────────┘    │
│                 │                     │
│                 ▼                     │
│  ┌─────────────────────────────┐    │
│  │ BleScanner (Interface)      │    │
│  │                              │    │
│  │ ┌─────────────────────────┐ │    │
│  │ │ scannedDevices          │ │    │
│  │ │ StateFlow<List<Device>> │ │    │
│  │ └─────────────────────────┘ │    │
│  └─────────────────────────────┘    │
│                 │                     │
│                 ▼                     │
│  ┌─────────────────────────────┐    │
│  │ AndroidBleScanner           │    │
│  │ (Platform Implementation)    │    │
│  └─────────────────────────────┘    │
│                 │                     │
│                 ▼                     │
│  ┌─────────────────────────────┐    │
│  │ Android BluetoothLeScanner  │    │
│  │ (System API)                │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
```

### 3. Reactive Architecture with StateFlow

Device discovery and state changes are communicated through reactive streams:

```
┌─────────────────────────────────────┐
│ BleDevice Discovery                 │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│ Update State in StateFlow            │
│ scannedDevices.emit(newList)         │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│ Subscribers observe the change       │
│ collectAsState() in Compose          │
└──────────────┬──────────────────────┘
               ▼
┌─────────────────────────────────────┐
│ UI recomposes automatically          │
│ New DeviceRow instances rendered     │
└─────────────────────────────────────┘
```

---

## Core Modules

### Shared Module (`shared/`)

**Purpose**: Contains platform-agnostic business logic and platform-specific implementations.

#### commonMain

**BleDevice.kt**
- Shared data class representing a discovered BLE device
- Contains: name, macAddress, RSSI
- Available to both Android and iOS

**BleScanner.kt**
- Interface defining the contract for all BLE scanner implementations
- Exposes `scannedDevices` and `isScanning` as StateFlows
- Methods: `startScan()`, `stopScan()`
- Ensures consistency across platforms

#### androidMain

**AndroidBleScanner.kt**
- Implements `BleScanner` interface
- Uses Android's `BluetoothAdapter` and `BluetoothLeScanner`
- Manages scan callbacks and device discovery
- Handles RSSI updates and device deduplication
- Runs on main thread to avoid threading issues

#### iosMain

**IosBleScanner.kt**
- Kotlin wrapper bridging Swift and Kotlin/Native
- Implements `BleScanner` interface
- Communicates with Swift's `IOSBleScanner` via Kotlin/Native interop
- Updates StateFlow from Swift callbacks

---

### Android Module (`androidApp/`)

**Architecture**: Single Activity + Compose

#### MainActivity.kt
- Entry point of the Android app
- Creates `AndroidBleScanner` with application context
- Initializes ViewModel with dependency injection
- Sets Compose content with theming
- Stops scanning on destroy for resource cleanup

#### BleViewModel.kt
- Inherits from `androidx.lifecycle.ViewModel`
- Exposes scanner's StateFlows directly to UI
- Provides convenience methods: `startScan()`, `stopScan()`, `toggleScan()`
- Cleans up resources in `onCleared()` callback

#### BlePermissions.kt
- Centralized permission management
- Defines required permissions based on Android API level
- Provides permission rationale for users

#### UI Components

**BleScannerScreen.kt**
- Main composable function
- Orchestrates permission requests
- Shows appropriate UI state (loading, scanning, empty, results)
- Animates between states smoothly

**DeviceRow.kt**
- Reusable card component for single device
- Displays device name, MAC, and RSSI
- Animates RSSI color changes
- RSSI color indicates signal strength

**Theme Files**
- `Theme.kt` - Material Design 3 theming
- `Color.kt` - Color palette and RSSI color functions
- `Type.kt` - Typography definitions

---

### iOS Module (`iosApp/`)

**Architecture**: SwiftUI with CoreBluetooth

#### iOSApp.swift
- SwiftUI app entry point
- Sets up the root scene

#### ContentView.swift
- Root SwiftUI view
- Displays the scanner UI

#### IOSBleScanner.swift
- Swift class implementing `CBCentralManagerDelegate`
- Manages CoreBluetooth scanning
- Bridges discoveries to Kotlin's StateFlow
- Handles device deduplication

---

## Data Flow

### Scanning Flow

```
User                Compose UI           ViewModel           Scanner          System
 │                     │                    │                   │               │
 ├─ Tap "Start" ────> BleScannerScreen     │                   │               │
 │                     │                    │                   │               │
 │                     ├─ toggleScan() ──> BleViewModel         │               │
 │                     │                    │                   │               │
 │                     │                    ├─ startScan() ──> BleScanner      │
 │                     │                    │                   │               │
 │                     │                    │                   ├─ Request ──> BluetoothAPI
 │                     │                    │                   │               │
 │                     │               ┌────────────────────────┴─ Device Found
 │                     │               │   │                   │               │
 │                     │               │   │               Update StateFlow    │
 │                     │               │   │                   │               │
 │                     ◄──────────────────────────────────────────────────────┤
 │                     │      scannedDevices updated                          │
 │                     │               │                   │               │
 │                     ├─ collectAsState()                  │               │
 │                     │               │                   │               │
 │                     ├─ Recompose                         │               │
 │                     │               │                   │               │
 │                     └─ Render DeviceRow                  │               │
 │
```

### Device Deduplication

```
Device UUID: AA:BB:CC:DD:EE:FF
First Discovery:
  ├─ RSSI: -70 dBm
  ├─ Name: "Device1"
  └─ Action: Add to list

Re-discovery (RSSI changed):
  ├─ RSSI: -65 dBm (updated)
  ├─ Name: "Device1"
  └─ Action: Update existing entry (same index)
              No duplicate created
```

---

## State Management

### StateFlow Usage

```kotlin
// In BleScanner interface
val scannedDevices: StateFlow<List<BleDevice>> = MutableStateFlow(emptyList())
val isScanning: StateFlow<Boolean> = MutableStateFlow(false)

// Updates in AndroidBleScanner
scannedDevices.value = updatedDeviceList
isScanning.value = scanningState

// Observation in Compose
val devices by viewModel.scannedDevices.collectAsState()
val isScanning by viewModel.isScanning.collectAsState()
```

**Why StateFlow?**
- Always emits current state to new subscribers
- Thread-safe by default in Kotlin Coroutines
- Perfect for reactive UI updates
- Better than SharedFlow when you need "current value"

---

## Permission Handling

### Android 12+ (API 31+)
- `BLUETOOTH_SCAN` - Permission to discover BLE devices
- `BLUETOOTH_CONNECT` - Permission to interact with devices
- `neverForLocation` flag - Indicates we don't use BLE for location

### Android 11 and below (API ≤ 30)
- `ACCESS_FINE_LOCATION` - Required for BLE scanning
- `ACCESS_COARSE_LOCATION` - Fallback location permission
- `maxSdkVersion="30"` - Permissions not requested on Android 12+

### iOS
- Apps must declare Bluetooth usage in Info.plist
- `NSBluetoothPeripheralUsageDescription` required
- User grants permission on first use

---

## Build System

### Gradle Configuration

**Root `build.gradle.kts`**
- Applies KMP plugin
- Declares shared repositories
- Configures subprojects

**`gradle/libs.versions.toml`**
- Centralized dependency version management
- Single source of truth for library versions

**Key Versions**
- Kotlin: 1.9.22
- Gradle: 8.5
- Android Gradle Plugin: Latest
- Compose: Latest stable

---

## Performance Considerations

1. **Threading**: Scanner runs on main thread (Android requirement)
2. **Memory**: Unbounded device list - consider pagination for large numbers
3. **Battery**: Continuous scanning uses significant battery (design limitation)
4. **Deduplication**: O(n) lookup - acceptable for typical use (< 50 devices)

---

## Security & Privacy

- No data sent to external servers
- Device discovery uses only system APIs
- iOS uses UUID instead of MAC (Apple privacy policy)
- Permissions requested transparently

---

## Testing (Future Enhancement)

Recommended test structure:

```
shared/
├── commonTest/
│   └── kotlin/com/udaysah/blescanner/
│       ├── model/BleDeviceTest.kt
│       └── scanner/BleScannerTest.kt
└── androidTest/
    └── kotlin/com/udaysah/blescanner/
        └── scanner/AndroidBleScannerTest.kt

androidApp/
└── test/
    └── java/com/udaysah/blescanner/android/
        ├── viewmodel/BleViewModelTest.kt
        └── ui/BleScannerScreenTest.kt
```

---

## Future Enhancements

1. **Device Filtering** - Filter by RSSI, name patterns
2. **Device Connection** - Connect to and read characteristics
3. **Service Discovery** - Discover and display services/characteristics
4. **Background Scanning** - Scan in background on both platforms
5. **Device History** - Save discovered devices to local database
6. **Notifications** - Alert when specific devices are discovered
7. **Testing** - Unit tests, UI tests, integration tests
8. **Localization** - Multi-language support

---

## References

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android BluetoothLeScanner](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeScanner)
- [iOS CoreBluetooth](https://developer.apple.com/documentation/corebluetooth)
- [SwiftUI](https://developer.apple.com/swiftui/)
- [Material Design 3](https://m3.material.io/)

