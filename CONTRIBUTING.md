# Contributing to BLE Scanner

Thank you for your interest in contributing to the BLE Scanner project! We welcome contributions from everyone. This document provides guidelines and instructions for contributing.

## Code of Conduct

Please be respectful and professional in all interactions. We are committed to providing a welcoming and inclusive environment for all contributors.

## How to Contribute

### 1. Reporting Bugs

Before submitting a bug report, please check the issue tracker to ensure the bug hasn't already been reported.

When reporting a bug, include:
- **Title**: Clear and concise description
- **Description**: Detailed explanation of the issue
- **Steps to Reproduce**: 
  - Platform (Android/iOS)
  - Device/Emulator version
  - Exact steps to reproduce
- **Expected Behavior**: What you expected to happen
- **Actual Behavior**: What actually happened
- **Screenshots/Logs**: If applicable, include screenshots or error logs
- **Environment**:
  - Kotlin version
  - Android SDK version / iOS deployment target
  - Device or emulator model

### 2. Suggesting Features

Feature suggestions are always welcome! When suggesting a feature:
- Use a clear, descriptive title
- Provide a detailed description of the feature
- Explain the use case and benefits
- Include examples of how you'd use it

### 3. Pull Requests

We love pull requests! Here's the process:

#### Step 1: Fork and Branch
```bash
# Fork the repository on GitHub
git clone https://github.com/YOUR_USERNAME/ble-scanner.git
cd ble-scanner

# Create a feature branch
git checkout -b feature/your-feature-name
```

#### Step 2: Make Your Changes
- Keep commits focused and atomic
- Write clear, descriptive commit messages
- Follow the code style guide (see below)
- Add comments for complex logic

#### Step 3: Test Your Changes

**Android:**
```bash
# Build Android app
./gradlew androidApp:build

# Run tests
./gradlew test

# Test on emulator/device
./gradlew androidApp:installDebug
```

**iOS:**
```bash
# Build shared framework
./gradlew shared:build

# Build iOS app
xcodebuild -scheme iosApp -configuration Debug -derivedDataPath build
```

#### Step 4: Commit and Push
```bash
# Add your changes
git add .

# Commit with clear message
git commit -m "feat: add device filtering feature"

# Push to your fork
git push origin feature/your-feature-name
```

#### Step 5: Create Pull Request
1. Go to the original repository
2. Click "New Pull Request"
3. Select your branch
4. Provide a clear description:
   - What changes were made
   - Why were they made
   - How to test the changes
   - Any breaking changes or notes

### Commit Message Format

Follow conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type**: Must be one of the following:
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that don't affect code meaning (formatting, semicolons, etc.)
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `perf`: Code change that improves performance
- `test`: Adding missing tests or correcting existing tests
- `chore`: Changes to build process, dependencies, etc.

**Scope**: The module affected:
- `shared`: Changes in shared module
- `android`: Changes in Android app
- `ios`: Changes in iOS app

**Examples:**
```
feat(android): add device search filter
fix(shared): correct RSSI color calculation
docs: update contributing guidelines
```

## Code Style Guide

### Kotlin Style

We follow the [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).

**Key points:**
- Use 4 spaces for indentation
- Max line length: 120 characters
- Use meaningful variable names
- Add KDoc comments for public functions and classes

```kotlin
/**
 * Represents a discovered BLE device.
 *
 * @property name The advertised device name
 * @property macAddress The unique device identifier
 * @property rssi Signal strength in dBm
 */
data class BleDevice(
    val name: String?,
    val macAddress: String,
    val rssi: Int
)

/**
 * Starts BLE scanning for nearby devices.
 * 
 * Clears the previous device list before starting.
 */
fun startScan() {
    // Implementation
}
```

### Compose Code Style

- Use meaningful composable names (PascalCase)
- Extract reusable components
- Use `Modifier` as the first parameter
- Group related composables together

```kotlin
@Composable
fun DeviceRow(
    device: BleDevice,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // Content
    }
}
```

### Swift Code Style

We follow [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/).

- Use camelCase for properties and methods
- Use PascalCase for classes and types
- Keep lines reasonable length (80-100 characters)
- Use proper documentation comments

```swift
/// Discovers nearby BLE devices using CoreBluetooth.
class IOSBleScanner: NSObject, CBCentralManagerDelegate {
    
    /// Starts a new BLE scan session.
    func startScan() {
        // Implementation
    }
}
```

## Project Structure

When adding new features, follow the existing structure:

```
shared/src/commonMain/kotlin/com/udaysah/blescanner/
└── feature/
    ├── Feature.kt          (Interface/Main logic)
    └── FeatureImpl.kt       (If needed)

androidApp/src/main/java/com/udaysah/blescanner/android/
└── feature/
    ├── FeatureViewModel.kt
    ├── FeatureScreen.kt
    └── components/
        └── FeatureComponent.kt

iosApp/iosApp/
└── Feature/
    └── FeatureView.swift
```

## Testing

### Unit Tests

```kotlin
// In shared/src/commonTest/kotlin/

class BleDeviceTest {
    @Test
    fun testDeviceCreation() {
        val device = BleDevice("Device", "AA:BB:CC:DD:EE:FF", -70)
        assertEquals(-70, device.rssi)
    }
}
```

### Android Tests

```kotlin
// In androidApp/test/java/

class BleViewModelTest {
    @Test
    fun testToggleScan() {
        // Test implementation
    }
}
```

## Documentation

When adding features:

1. **Update relevant documentation**:
   - README.md (if it's a major feature)
   - ARCHITECTURE.md (if it changes architecture)
   - Code comments (for implementation details)

2. **Add KDoc/JSDoc comments**:
   ```kotlin
   /**
    * Scans for nearby BLE devices.
    *
    * Clears the previous device list and starts fresh.
    * The scan continues until [stopScan] is called.
    *
    * @throws BluetoothNotAvailableException if Bluetooth is not available
    * @see stopScan
    */
   fun startScan() { }
   ```

3. **Examples in documentation**:
   - Show how to use new features
   - Include before/after code samples

## Development Setup

### Prerequisites

- Kotlin 1.9.22+
- Gradle 8.5+
- Android Studio or JetBrains IntelliJ IDEA
- Xcode 15+ (for iOS development)
- Android SDK 34+

### Setup Steps

```bash
# Clone repository
git clone https://github.com/udaysah/ble-scanner.git
cd ble-scanner

# Build all modules
./gradlew build

# To build only shared module
./gradlew shared:build

# To run Android tests
./gradlew test

# To open in Android Studio
open -a "Android Studio" .
```

## Branch Naming Convention

Use descriptive branch names:

- Feature: `feature/short-description`
- Bug fix: `fix/short-description`
- Documentation: `docs/short-description`
- Improvement: `improve/short-description`

Examples:
- `feature/add-device-filtering`
- `fix/rssi-color-bug`
- `docs/update-readme`

## Review Process

1. **Pull Request Submitted**: Your PR will be reviewed by maintainers
2. **Review Comments**: We may ask for changes or clarifications
3. **Address Feedback**: Make requested changes in new commits
4. **Approval**: Once approved, PR will be merged
5. **Merge**: Your contribution is now part of the project!

## Performance Guidelines

When contributing, keep performance in mind:

- Avoid O(n²) algorithms where possible
- Use `StateFlow` instead of `LiveData` for reactive updates
- Always use `withContext` when switching coroutine contexts
- Profile your code before and after changes
- Document any performance trade-offs

## Security Considerations

- Do not hardcode secrets or API keys
- Use Android Keystore for sensitive data
- Validate all user input
- Follow OWASP guidelines for secure coding
- Report security vulnerabilities privately

## Questions?

- 📖 Check the [ARCHITECTURE.md](ARCHITECTURE.md)
- 📚 Read the [SETUP_GUIDE.md](SETUP_GUIDE.md)
- 💬 Open an issue with your question
- 📧 Email the maintainers

## Recognition

Contributors will be recognized in:
- Release notes
- CONTRIBUTORS.md file
- GitHub contributors page

Thank you for contributing to BLE Scanner! 🚀

