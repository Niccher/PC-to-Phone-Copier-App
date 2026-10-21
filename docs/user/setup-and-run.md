# Setup and Run Guide

This guide details how to build, install, and run the P2P Copier Android App for users, testers, and QA.

---

## 1. Prerequisites

- **Host Machine**: Linux, macOS, or Windows
- **JDK**: Java 11 or higher (OpenJDK or Android Studio bundled JBR)
- **Android Device or Emulator**: Android 7.0 (API level 24) or newer
- **Backend Relay**: Running instance of [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) on port 9004

---

## 2. Building the Application

### Step 1: Clone Repository
```bash
git clone https://github.com/niccher/PC-to-Phone-Copier-App.git
cd P2P_Copier_App
```

### Step 2: Assemble Debug APK
```bash
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 3. Installing on a Device or Emulator

### Option A: Via ADB Command Line
With your device attached or emulator running:
```bash
./gradlew installDebug
```
Or directly using `adb`:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Option B: Via Android Studio
1. Open the project folder in Android Studio Jellyfish or newer.
2. Allow Gradle sync to finish.
3. Select your target physical device or Virtual Device (AVD).
4. Click **Run 'app'** (`Shift + F10`).

---

## 4. Connecting to the Backend

1. Ensure the P2P Copier WebApp server is running.
2. Launch P2P Copier App on your phone/emulator.
3. Open the WebApp in your browser (e.g. `http://localhost`, `http://<LAN_IP>`, or your public/GCP domain).
4. On initial launch, tap **Scan QR Code** and point your device camera at the QR code displayed on the WebApp landing page.
5. The app automatically detects the host and port, registers the hardware fingerprint, and establishes the pairing session immediately.
