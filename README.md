<div align="center">

# P2P Copier Android App

**Transfer text, files, OCR extractions, and QR code scans between your Android device and a PC web browser via a CodeIgniter 4 relay server.**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![compileSdk](https://img.shields.io/badge/compileSdk-35-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.9.0-48B983?style=for-the-badge&logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

</div>

---

## 📖 1. About the Project

P2P Copier Android App is the mobile client of a two-repo ecosystem pairing with the [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) relay server. It enables seamless peer-to-peer data transfers — files, typed text, OCR camera extractions, and QR code scans — between an Android phone and a desktop browser over a local network or web host.

Session pairing is established via QR code scanning or a 6-digit numeric fallback code. Once paired, device UUIDs and session tokens persist across app launches using Jetpack DataStore and SharedPreferences.

---

## 🏛️ 2. Architecture

```
┌─────────────────────────────────────────────────────┐
│                 Android App                          │
│                                                      │
│  ┌────────────┐    ┌──────────────┐                  │
│  │ Activities │    │  Fragments   │                  │
│  │ AuthSession│───▶│ Fragment_Home│                  │
│  │ Handle_Files│   │ Fragment_    │                  │
│  │ Handle_    │    │   Uploaded   │                  │
│  │  Texts     │    └──────┬───────┘                  │
│  └─────┬──────┘           │                          │
│        │                  │                          │
│        ▼                  ▼                          │
│  ┌────────────────────────────────┐                   │
│  │      ViewModels                │                   │
│  │  (FileViewModel, TextViewModel,│                   │
│  │   HomeViewModel)               │                   │
│  └───────────┬────────────────────┘                   │
│              │                                        │
│              ▼                                        │
│  ┌────────────────────────────────┐                   │
│  │  ServiceGenerator (Retrofit)   │                   │
│  │  RetrofitInterface             │                   │
│  │  ┌──────────────────────────┐  │                   │
│  │  │ POST /api/v1/uploaded    │  │                   │
│  │  │ POST /api/v1/files/upload│  │                   │
│  │  │ POST /api/v1/files/delete│  │                   │
│  │  │ POST /api/v1/texts       │  │                   │
│  │  │ POST /api/v1/texts/delete│  │                   │
│  │  └──────────────────────────┘  │                   │
│  └───────────┬────────────────────┘                   │
│              │                                        │
│              ▼ HTTP / JSON                            │
│  http://192.168.100.80:9004/api/v1/...                │
└───────────────────────────────────────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────────────┐
│          P2P Copier WebApp (CodeIgniter 4)            │
│          + MySQL 8.4                                   │
└───────────────────────────────────────────────────────┘
```

The app uses **Retrofit + OkHttp** for HTTP communication. Base URL is configured inside `Konstants.kt` (defaults to local network `http://192.168.100.80:9004/`).

---

## 🤖 3. On-Device Machine Learning & Scanners

| Feature | Library / Tool | Implementation Detail |
|---|---|---|
| **OCR Text Extraction** | Firebase ML Kit (`com.google.mlkit:text-recognition:16.0.0`) | Camera photo text recognition in `Handle_Text_2_Image.java` |
| **QR Code Reader** | ZXing (`com.journeyapps:zxing-android-embedded:4.3.0`) | Camera-based session pairing & QR data scanner via `IntentIntegrator` |

---

## ✨ 4. Features

### 🔐 Authentication & Session Pairing
- **QR & Code Pairing**: Scan desktop QR code or manually enter 6-digit numeric fallback code.
- **Device Fingerprint Registration**: Submits hardware specs to `/api/v1/device/register` and receives persistent `dev_uuid`.
- **Active Session Verification**: Verifies session validity on server launch via `/api/v1/auth/session-status`.
- **Session Logout**: Clears session tokens and redirects to setup via `Helpers.logoutSession()`.
- **Full Reset**: Clears DataStore, SharedPreferences, and app cache directories via `Helpers.deleteAllAppDataAndReset()`.

### 📂 Unified Uploaded Feed ("Uploaded")
- **Single Endpoint Fetch**: `POST /api/v1/uploaded` retrieves both uploaded files AND text entries in 1 single HTTP request.
- **Color-Coded Badges**:
  - `TEXT` (Green badge): Typed/pasted text entries.
  - `OCR` (Purple badge): Camera text extractions.
  - `QR` (Teal badge): Scanned QR codes.
  - `PDF` / `IMG` / `VID` / `AUD` / `DOC`: Category file badges with icons.
- **Interactive Text Details Modal**: Tapping any text card launches an interactive dialog (`dialog_text_details.xml`) with selectable text, 📋 **Copy to Clipboard**, and 🗑️ **Delete Text** buttons.
- **Context Menu & Batch Selection**: Long-press triggers Download, Delete, or Multi-Select mode.

---

## 🛠️ 5. Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Languages** | Java + Kotlin | Java 11 / Kotlin 1.9.10 | Core Android application |
| **Build System** | Gradle | 8.7 (wrapper) | Build automation |
| **Android Plugin** | AGP | 8.6.0 | Build system integration |
| **SDK Range** | API 24 to 35 | Android 7.0–15 | Platform compatibility |
| **Networking** | Retrofit + OkHttp | 2.9.0 / 4.9.0 | REST API HTTP client |
| **Serialization** | Gson | 2.9.0 | JSON parsing & `@SerializedName` mapping |
| **Local Storage** | DataStore + SharedPreferences | 1.0.0 | Preferences persistence |
| **QR Reader** | ZXing (journeyapps) | 4.3.0 | QR code scanning |
| **OCR ML Engine** | Firebase ML Kit | 16.0.0 | On-device text recognition |
| **Biometrics** | AndroidX Biometric | 1.1.0 | Biometric lock prompt |
| **UI System** | Material Components 3 | 1.9.0 | Material 3 UI design |

---

## 📋 6. Prerequisites

- **Android Studio** Jellyfish or newer (AGP 8.6.0)
- **JDK** 11+ (e.g. `/home/niccher/android-studio/jbr`)
- **Gradle** 8.7 (wrapper included)
- Running **P2P Copier WebApp** backend on local network or server

---

## 🚀 7. Installation & Setup

### 1. Clone Repository
```bash
git clone https://github.com/niccher/PC-to-Phone-Copier-App.git
cd P2P_Copier_App
```

### 2. Configure Backend Server Base URL
Edit `app/src/main/java/com/niccher/p2p_copier_app/utils/Konstants.kt`:
```kotlin
var str_base_url: String = "http://192.168.100.80:9004/"
```

### 3. Build & Run via Gradle
```bash
JAVA_HOME=/path/to/jdk ./gradlew assembleDebug
```

---

## 🌐 8. Data Flow (v1 REST API Endpoints)

All endpoints are relative to `str_base_url` (`http://192.168.100.80:9004/`):

| Method | Path | Source Class | Purpose |
|---|---|---|---|
| POST | `/api/v1/device/register` | `AuthSession.java` | Register device fingerprint & obtain `dev_uuid` |
| POST | `/api/v1/auth/register` | `AuthSession.java` | Pair device with session via QR or code |
| POST | `/api/v1/auth/session-status` | `Fragment_Profile.java` | Verify session validity |
| POST | `/api/v1/uploaded` | `Fragment_History_Files.java` | Fetch unified files + text items in 1 request |
| POST | `/api/v1/files/upload` | `Adapter_Sel_Files.java` | Multipart file upload |
| POST | `/api/v1/files/download` | `Adapter_Uploaded_Files.java` | Download file to Downloads directory |
| POST | `/api/v1/files/delete` | `Adapter_Uploaded_Files.java` | Soft-delete a file |
| POST | `/api/v1/texts` | `TextViewModel.java` | Upload typed text, OCR text, or QR data |
| POST | `/api/v1/texts/list` | `Fragment_History_Files.java` | Fetch text entries |
| POST | `/api/v1/texts/delete` | `Adapter_Uploaded_Files.java` | Delete a text item |
| POST | `/api/v1/analytics/summary` | `Fragment_History_Overview.java` | Fetch transfer stats & event counts |

---

## 📁 9. Project Structure

```
app/src/main/java/com/niccher/p2p_copier_app/
├── activities/
│   ├── AuthSession.java          # QR scan, code entry & device registration
│   ├── Auth_New_Or_Continue.java # Session launcher & continuation
│   ├── BiometricLockActivity.kt  # Biometric lock gate
│   ├── Handle_Files.java         # File picker & upload handling
│   ├── Handle_Text_2_Image.java  # Camera → OCR → text upload
│   └── Handle_Texts.java         # Text entry & clipboard paste
├── adapters/
│   ├── Adapter_Sel_Files.java    # Upload queue adapter
│   └── Adapter_Uploaded_Files.java # Unified files/text adapter with modal & context menu
├── datastore/
│   ├── AuthPreferences.kt        # DataStore for auth session
│   └── DevicePreferences.kt      # DataStore for device UUID
├── fragments/
│   ├── Fragment_About.java       # App specs fragment
│   ├── Fragment_Credits.java     # Credits fragment
│   ├── Fragment_History_Files.java # "Uploaded" unified feed
│   ├── Fragment_History_Overview.java # Activity Log stats
│   ├── Fragment_Profile.java     # Device info, status check, logout & reset
│   └── Fragment_Settings.java    # Theme & security settings
├── interfaces/
│   └── RetrofitInterface.java    # v1 API Retrofit interface
├── model/
│   ├── Mod_List_File_Uploaded.java # Unified file/text model with GSON annotations
│   ├── Mod_Text_Uploaded.java    # Text model
│   └── api/                      # Data envelopes (UploadedEnvelope, TextDataEnvelope, ApiResponse)
├── utils/
│   ├── Helpers.java              # Logout, Reset, & Prefs helpers
│   ├── Konstants.kt              # Base URL configuration
│   └── ServiceGenerator.java     # Retrofit client generator
├── viewmodels/
│   ├── FileViewModel.java        # File upload state
│   ├── HomeViewModel.java        # Fragment navigation
│   └── TextViewModel.java        # Text upload state
└── HomePage.java                 # Main navigation hub
```

---

## 🤝 10. Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature-name`.
3. Commit your changes.
4. Push to the branch and open a Pull Request.

---

## 📄 11. License

Distributed under the MIT License. See `LICENSE` for details.

---

## 💬 12. Support / Contact

- **Website:** [chegecache.co.ke](https://chegecache.co.ke)
- **Sibling repo:** [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp)
