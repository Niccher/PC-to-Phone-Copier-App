# P2P Copier Android App

The mobile client for the P2P Copier ecosystem, enabling bidirectional file transfers, typed notes, camera OCR extractions, and QR code sharing with desktop web browsers.

Stack: Kotlin 1.9, Java 11, Android SDK 24–35, Retrofit, ML Kit OCR, ZXing.

**If you only need to run the app, this page is enough.**  
Software engineers: [docs/README.md](docs/README.md).

## What “running” looks like

| Screen / Flow | How to Open | Expected Behavior |
|---|---|---|
| **Splash / Launcher** | Launch App | Checks existing session; redirects to setup or main hub |
| **Session Pairing** | Initial Launch / Profile | Displays QR Scanner and 6-digit numeric entry dialog |
| **Unified Feed** | "Uploaded" Tab | Lists synced files, text items, and OCR extractions |
| **Backend Config** | Launcher Menu / Settings | In-app tester screen to set backend server URL and port |

## Prerequisites

- **Android Device** (Android 7.0+ / API 24+) or **Android Emulator**
- **Android Studio** Jellyfish+ with JDK 11+ (or command-line Gradle 8.7)
- Running [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) backend (on local network or Docker)

## Setup and run

From a fresh machine, numbered, copy-pasteable:

1. Clone the repository:
   ```bash
   git clone https://github.com/niccher/PC-to-Phone-Copier-App.git
   cd P2P_Copier_App
   ```
2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Install on a connected Android device or running emulator:
   ```bash
   ./gradlew installDebug
   ```
4. Connect to your backend:
   - **On Android Emulator**: Open the in-app **Backend Configuration** screen and set URL to `http://10.0.2.2:9004/`.
   - **On Physical Device (USB)**: Run `adb reverse tcp:9004 tcp:9004`, then set URL to `http://127.0.0.1:9004/`.
   - **On Physical Device (Wi-Fi)**: Set URL to your PC's local LAN address (e.g. `http://192.168.1.50:9004/`).
5. Scan the QR code or enter the 6-digit code displayed on the WebApp landing page.

## Configuration users may change

| Setting | Location | Purpose |
|---|---|---|
| **Backend Server URL** | In-app Backend Configuration | Hostname or IP address of the relay server |
| **Backend Port** | In-app Backend Configuration | Published port of the WebApp server (`9004`) |
| **Biometric Gate** | App Settings Tab | Enables fingerprint or face lock on launch |

Full configuration reference: [docs/user/configuration.md](docs/user/configuration.md).

## Something went wrong?

- **App cannot reach server on Emulator**: Ensure the URL uses `10.0.2.2:9004` (not `localhost`).
- **Physical phone cannot reach server over Wi-Fi**: Verify both phone and PC are connected to the same Wi-Fi network and firewall allows port 9004.
- **Camera permission denied**: Grant Camera permission when prompted to enable QR pairing and OCR text recognition.

Detailed troubleshooting: [docs/user/troubleshooting.md](docs/user/troubleshooting.md).

## Sibling Ecosystem Repositories

| Component | Responsibility | Repository URL | Documentation |
|---|---|---|---|
| **Web App** | CodeIgniter 4 Relay Server & Web UI | [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) | [docs/](https://github.com/niccher/P2P_Copier_WebApp/tree/main/docs) |
| **Android App** | Kotlin/Java Client (ML Kit OCR, ZXing QR) | [P2P_Copier_App](https://github.com/niccher/PC-to-Phone-Copier-App) | [docs/](docs/README.md) |

## Software engineers

- Master Index: [docs/README.md](docs/README.md)
- Architecture Overview: [docs/architecture/overview.md](docs/architecture/overview.md)
- Client Communication & Retrofit: [docs/architecture/communication.md](docs/architecture/communication.md)
- Android Service Guide: [docs/services/android.md](docs/services/android.md)
- Making Changes Safely: [docs/engineering/making-changes.md](docs/engineering/making-changes.md)
- Local Development & Tooling: [docs/engineering/local-development.md](docs/engineering/local-development.md)
