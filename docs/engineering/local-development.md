# Local Development & Environment Setup

This guide details how to set up the Android development environment and execute command-line builds for P2P Copier App.

---

## 1. Prerequisites & Tooling

- **JDK**: OpenJDK 11 or Android Studio JetBrains Runtime (JBR 17/21). Set `JAVA_HOME`.
- **Android SDK**: Build-Tools `35.0.0`, Platform SDK `android-35`, Platform-Tools.
- **Android Studio**: Android Studio Jellyfish (2023.3.1) or newer.

---

## 2. Setting Up `local.properties`

Create `local.properties` in the repository root (do **not** commit this file):
```ini
sdk.dir=/home/niccher/Android/Sdk
```

---

## 3. Essential Gradle Tasks

Run from the root of the repository using the included Gradle wrapper:

```bash
# Compile and build the debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Install debug APK onto the active device/emulator
./gradlew installDebug

# Execute local JVM unit tests
./gradlew test

# Execute connected instrumented tests on an emulator/device
./gradlew connectedAndroidTest

# Clean all build outputs and cached build artifacts
./gradlew clean
```
