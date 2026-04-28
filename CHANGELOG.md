# Changelog

All notable changes to the BLE Scanner project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial Kotlin Multiplatform project setup
- Cross-platform BLE scanning for Android and iOS
- RSSI signal strength visualization with color coding
- Material Design 3 UI for Android with Jetpack Compose
- SwiftUI implementation for iOS
- Real-time device list with animations
- Permission handling for both platforms
- MVVM architecture with ViewModel pattern
- Reactive state management with StateFlow

### Planned
- Device filtering by name/RSSI
- Device connection and characteristic reading
- Service discovery display
- Background BLE scanning
- Device history/database
- Push notifications for nearby devices
- Unit and UI tests
- Multi-language support (i18n)
- Dark/Light theme toggle
- Device connection status indicator
- GATT characteristic browser

---

## [0.1.0] - 2026-04-27

### Added
- ✅ Kotlin Multiplatform setup with shared module
- ✅ Android BLE scanning using BluetoothLeScanner API
- ✅ iOS BLE scanning using CoreBluetooth framework
- ✅ Real-time device discovery and updates
- ✅ RSSI-based signal strength color coding
  - Green (-30 to -50 dBm): Excellent
  - Yellow (-50 to -70 dBm): Good
  - Orange (-70 to -85 dBm): Fair
  - Red (-85 dBm and below): Weak
- ✅ Jetpack Compose UI with Material Design 3
- ✅ SwiftUI integration for iOS
- ✅ Android permission handling:
  - Android 12+ granular permissions
  - Legacy permission support for Android 11 and below
- ✅ Device deduplication logic
- ✅ Animated UI state transitions
- ✅ ViewModel-based architecture
- ✅ Complete GitHub documentation:
  - README.md with feature overview
  - ARCHITECTURE.md with technical details
  - CONTRIBUTING.md with contributor guidelines
  - SETUP_GUIDE.md for development setup
  - LICENSE (MIT)
  - .gitignore for common artifacts

### Fixed
- Fixed Gradle 9.0.0 repository configuration conflict (downgraded to 8.5)
- Fixed Kotlin hierarchy template warnings
- Fixed missing Android launcher icons in manifest
- Resolved missing repository declarations in root build.gradle.kts

### Technical
- Kotlin 1.9.22
- Gradle 8.5
- Android SDK 34
- Jetpack Compose latest stable
- SwiftUI for iOS 14+
- Coroutines for async operations
- StateFlow for reactive state management

---

## Format Guide

- **Added** - New features
- **Changed** - Changes in existing functionality
- **Deprecated** - Soon-to-be removed features
- **Removed** - Now removed features
- **Fixed** - Any bug fixes
- **Security** - Security issue fixes
- **Technical** - Technical details (versions, dependencies)

---

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute to this project.

---

## Recognition

### Contributors
- Uday Sah (@udaysah) - Project creator and lead developer

### Special Thanks
- Kotlin team for the Multiplatform framework
- Google for Android development tools
- Apple for iOS and Xcode
- JetBrains for excellent IDEs

---

## Future Roadmap

### Phase 2 (Q3 2026)
- Device filtering and search
- Service discovery UI
- Device connection state management

### Phase 3 (Q4 2026)
- GATT characteristic browser
- Background scanning support
- Local device database

### Phase 4 (2027)
- Multi-language support
- Cloud sync capabilities
- Advanced analytics

---

## Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** - Incompatible API changes
- **MINOR** - Backwards-compatible functionality
- **PATCH** - Backwards-compatible bug fixes

Example: `v1.2.3`
- 1 = Major version
- 2 = Minor version
- 3 = Patch version

