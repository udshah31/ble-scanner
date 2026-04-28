# Development Setup Guide

This guide will help you set up your development environment to build and run the BLE Scanner project.

## Table of Contents

1. [System Requirements](#system-requirements)
2. [Android Development Setup](#android-development-setup)
3. [iOS Development Setup](#ios-development-setup)
4. [Project Setup](#project-setup)
5. [Building the Project](#building-the-project)
6. [Running the App](#running-the-app)
7. [Troubleshooting](#troubleshooting)

---

## System Requirements

### Minimum Requirements

- **macOS 12** or later (for iOS development)
- **8 GB RAM** (recommended 16 GB)
- **10 GB free disk space**

### Software

- **Git** - Version control
- **Kotlin** - 1.9.22 or later
- **Java** - JDK 11 or later (for Gradle)

---

## Android Development Setup

### Step 1: Install Android Studio

1. Download [Android Studio](https://developer.android.com/studio)
2. Run the installer
3. Follow the installation wizard
4. Complete the initial setup

### Step 2: Install Required SDK Components

```bash
# Using sdkmanager via Android Studio
# Or use Android Studio's SDK Manager (Tools > SDK Manager)

# Required:
# - Android SDK 34
# - Android SDK Build Tools 34.x
# - Android Emulator
# - Intel HAXM (for Intel processors)
```

### Step 3: Create an Android Emulator

1. Open Android Studio
2. Go to **Tools > Device Manager**
3. Click **Create Device**
4. Select **Pixel 6** or similar
5. Choose **Android 14 (API 34)** or higher
6. Complete the setup
7. Start the emulator

### Step 4: Set Environment Variables

**For macOS/Linux, add to `~/.zshrc` or `~/.bash_profile`:**

```bash
export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk
export PATH=$ANDROID_SDK_ROOT/platforms:$PATH
export PATH=$ANDROID_SDK_ROOT/platform-tools:$PATH
export PATH=$ANDROID_SDK_ROOT/tools:$PATH
export PATH=$ANDROID_SDK_ROOT/tools/bin:$PATH
```

Then reload:
```bash
source ~/.zshrc
```

### Step 5: Verify Installation

```bash
# Check Android SDK is properly set
echo $ANDROID_SDK_ROOT

# Verify emulator
emulator -list-avds

# Verify adb
adb devices
```

---

## iOS Development Setup

### Step 1: Install Xcode

```bash
# Install from App Store (easiest)
# Or from Apple Developer website

# Verify installation
xcode-select -p
# Should output: /Applications/Xcode.app/Contents/Developer
```

### Step 2: Accept Xcode License

```bash
sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer
sudo xcodebuild -license accept
```

### Step 3: Install Command Line Tools

```bash
xcode-select --install
```

### Step 4: Install Cocoapods (if needed)

```bash
sudo gem install cocoapods
```

### Step 5: Configure iOS Build Settings

Xcode settings are configured in the iosApp project:
- Deployment Target: iOS 14.0+
- Swift version: 5.9+

---

## Project Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/udaysah/ble-scanner.git
cd ble-scanner
```

### Step 2: Install Dependencies

The project uses Gradle Wrapper (no need to install Gradle separately).

```bash
# Make gradlew executable
chmod +x gradlew

# Download dependencies
./gradlew dependencies
```

### Step 3: Configure Local Properties

Create `local.properties` in the project root:

```properties
# Android SDK path
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk

# iOS SDK path (usually automatic)
# sdk.dir=/Applications/Xcode.app/Contents/Developer
```

### Step 4: Verify Setup

```bash
# Check if gradle build works
./gradlew build

# Check Kotlin version
./gradlew --version
```

---

## Building the Project

### Build All Modules

```bash
# Clean and build everything
./gradlew clean build
```

### Build Individual Modules

#### Shared Module (Kotlin Multiplatform)

```bash
# Build shared library
./gradlew shared:build

# Build shared framework for iOS
./gradlew shared:iosX64Binaries
./gradlew shared:iosArm64Binaries
./gradlew shared:iosSimulatorArm64Binaries
```

#### Android App

```bash
# Build debug APK
./gradlew androidApp:assembleDebug

# Build release APK
./gradlew androidApp:assembleRelease

# Install on connected device/emulator
./gradlew androidApp:installDebug
```

#### iOS App

```bash
# Build iOS framework
./gradlew shared:embedAndSignAppleFrameworkForXcode

# Then open and build in Xcode
open iosApp/iosApp.xcodeproj
```

---

## Running the App

### Android

#### Using Emulator

```bash
# List available emulators
emulator -list-avds

# Start an emulator
emulator -avd Pixel_6_API_34

# In another terminal, install and run app
./gradlew androidApp:installDebug
./gradlew androidApp:run

# Or using Android Studio
# Select "Run" > "Run 'androidApp'"
```

#### Using Physical Device

```bash
# Enable USB debugging on device:
# Settings > Developer Options > USB Debugging

# Verify device is connected
adb devices

# Install and run
./gradlew androidApp:installDebug
```

### iOS

#### Using Simulator

```bash
# Build and run in simulator
xcodebuild -scheme iosApp \
  -configuration Debug \
  -derivedDataPath build \
  -arch arm64 \
  -sdk iphonesimulator

# Or using Xcode
open iosApp/iosApp.xcodeproj
# Select target: iosApp
# Select simulator device
# Press Play button
```

#### Using Physical Device

```bash
# Connect iPhone
# Open iosApp.xcodeproj in Xcode
# Select your device as target
# Press Play button
# May need to configure signing certificate first
```

---

## IDE Setup

### Android Studio

#### Essential Plugins

1. **Kotlin** - Usually pre-installed
2. **Compose** - Pre-installed in latest versions
3. **Android** - Pre-installed

**To install plugins:**
- Android Studio > Preferences > Plugins
- Search and install from Marketplace

#### Useful Extensions

- **Detekt** - Kotlin static analysis
- **Ktlint** - Kotlin formatter
- **Rainbow Brackets** - Better code readability

#### Android Studio Settings

```
Preferences > Editor > General
- Show line numbers: ✓
- Code completion: ✓

Preferences > Editor > Code Style
- Kotlin: Follow standard

Preferences > Languages & Frameworks > Android SDK
- Configure SDK path
```

### Xcode

#### Recommended Settings

```
Xcode > Preferences > Accounts
- Add your Apple ID

Xcode > Preferences > Locations
- Set Command Line Tools to current Xcode version

Xcode > Preferences > Text Editing
- Display: Show line numbers ✓
```

---

## Testing Setup

### Android Unit Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "com.udaysah.blescanner.model.BleDeviceTest"

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

### Android UI Tests

```bash
# Run connected tests (requires device/emulator)
./gradlew connectedAndroidTest
```

### iOS Unit Tests

```bash
# Run iOS tests from command line
xcodebuild -scheme iosApp -configuration Debug \
  -derivedDataPath build test

# Or in Xcode: Product > Test (⌘U)
```

---

## Gradle Tasks

Useful Gradle commands:

```bash
# List all tasks
./gradlew tasks

# Build specific module
./gradlew :shared:build
./gradlew :androidApp:build

# Check dependencies
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates

# Format code (if Ktlint configured)
./gradlew ktlintFormat

# Run lint checks
./gradlew lint

# Clean build artifacts
./gradlew clean

# Run with debug info
./gradlew build --debug

# Parallel build (faster)
./gradlew build --parallel
```

---

## Performance Tips

### Fast Development Builds

```bash
# Skip tests during development
./gradlew build -x test

# Parallel builds
./gradlew build --parallel

# Build only specific module
./gradlew :androidApp:assembleDebug
```

### Enable Build Cache

Create `~/.gradle/gradle.properties`:

```properties
# Build cache
org.gradle.caching=true

# Parallel builds
org.gradle.parallel=true

# Number of parallel threads
org.gradle.workers.max=8

# Memory for Gradle
org.gradle.jvmargs=-Xmx4g
```

### IDE Performance

**Android Studio:**
- Disable real-time code analysis (for older machines)
- Reduce number of open files
- Close unused tool windows
- Restart IDE occasionally

---

## Troubleshooting

### Common Issues

#### 1. "Command not found: gradle"

```bash
# Solution: Use gradle wrapper
./gradlew build  # Instead of: gradle build
```

#### 2. Android SDK not found

```bash
# Set SDK path in local.properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk

# Or set environment variable
export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk
export ANDROID_HOME=$HOME/Library/Android/sdk
```

#### 3. Emulator not starting

```bash
# Ensure KVM is enabled (on Linux)
kvm-ok

# Restart ADB daemon
adb kill-server
adb start-server

# Try starting emulator manually
emulator -avd Pixel_6_API_34 -no-audio -no-boot-anim
```

#### 4. iOS build failures

```bash
# Clean derived data
rm -rf ~/Library/Developer/Xcode/DerivedData/*

# Clear build folder
rm -rf iosApp/build

# Rebuild
./gradlew shared:iosX64Binaries
```

#### 5. Permission errors on Linux/Mac

```bash
# Make gradlew executable
chmod +x gradlew

# Fix permission issues
chmod +x ~/.gradle
```

#### 6. Gradle daemon issues

```bash
# Stop all gradle daemons
./gradlew --stop

# Rebuild
./gradlew build
```

#### 7. Kotlin version mismatch

```bash
# Check Kotlin version
./gradlew -version

# Update gradle wrapper
./gradlew wrapper --gradle-version=8.5
```

### Debug Output

```bash
# Run with debug output
./gradlew build --debug

# Show stack traces
./gradlew build --stacktrace

# Show more verbose output
./gradlew build -i
```

---

## Getting Help

1. **Check the logs** - Most errors include helpful messages
2. **Read ARCHITECTURE.md** - Understand project structure
3. **Check GitHub Issues** - See if someone faced the same problem
4. **Android Studio Logcat** - Real-time app logs
5. **Xcode Console** - iOS app logs

---

## Next Steps

Once setup is complete:

1. ✅ Review [ARCHITECTURE.md](ARCHITECTURE.md)
2. ✅ Read [README.md](README.md)
3. ✅ Create a feature branch
4. ✅ Start coding!

Happy coding! 🚀

