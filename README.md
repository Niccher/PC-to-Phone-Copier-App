<div align="center">

# P2P Copier Android App

**Transfer text, files, and clipboard contents between your Android device and a PC browser via a relay server.**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![compileSdk](https://img.shields.io/badge/compileSdk-35-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.9.0-48B983?style=for-the-badge&logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

</div>

---

## About the Project

P2P Copier is the Android client in a two-repo ecosystem. It pairs with the [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) — a CodeIgniter 4 relay server — to let users push and pull files, text, and clipboard content between a phone and a desktop browser.

Authentication is one-time: the server generates a QR code (and 6-digit numeric fallback) that the app scans or enters. After pairing, both devices share a session and can upload/download independently.

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                 Android App                          │
│                                                      │
│  ┌────────────┐    ┌──────────────┐                  │
│  │ Activities  │    │  Fragments   │                  │
│  │             │    │              │                  │
│  │ AuthSession│───▶│ Fragment_Home│                  │
│  │ Handle_Files│   │ Fragment_    │                  │
│  │ Handle_     │    │ History_Files│                  │
│  │  Texts      │    └──────┬───────┘                  │
│  └─────┬───────┘           │                          │
│        │                   │                          │
│        ▼                   ▼                          │
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
│  │  │ POST /auth/register      │  │                   │
│  │  │ POST /device/register    │  │                   │
│  │  │ POST /home/phone/upload  │  │                   │
│  │  │ POST /home/phone/        │  │                   │
│  │  │   get_files_uploaded_by_ │  │                   │
│  │  │   session                │  │                   │
│  │  │ POST /home/phone/        │  │                   │
│  │  │   get_files_uploaded_by_ │  │                   │
│  │  │   session_download       │  │                   │
│  │  └──────────────────────────┘  │                   │
│  └───────────┬────────────────────┘                   │
│              │                                        │
│              ▼ HTTPS / JSON                           │
│  https://p2p.chegecache.co.ke/...                     │
└───────────────────────────────────────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────────────┐
│          P2P Copier WebApp (backend)                   │
│          + MySQL 8.4                                   │
└───────────────────────────────────────────────────────┘
```

The app uses **Retrofit + OkHttp** to communicate with the backend. The base URL is hardcoded in `Konstants.kt` as `https://p2p.chegecache.co.ke`. Device and session preferences are persisted via Jetpack DataStore. No DI framework is used — all dependencies are manually wired.

---

## Machine Learning / Algorithms

The app includes **on-device Optical Character Recognition (OCR)** for extracting text from a camera photo.

| Feature | Library | Approach |
|---|---|---|
| **OCR Text Extraction** | Firebase ML Kit (`com.google.mlkit:text-recognition:16.0.0`) | On-device Latin-script text recognition via `FirebaseVisionTextRecognizer` |
| **Barcode / QR Scanning** | ZXing (`com.journeyapps:zxing-android-embedded:4.3.0`) | Camera-based scanning via `IntentIntegrator` |

The OCR path is used in `Handle_Text_2_Image.java` — the user takes a photo (via `MediaStore.ACTION_IMAGE_CAPTURE`), and the recognized text is surfaced in the UI for review and upload. Processing happens entirely on-device; no image data is sent to a cloud API.

---

## Features

### Authentication & Pairing
| Feature | Description |
|---|---|
| **QR Scan** | Scans the server-generated QR code via ZXing `IntentIntegrator` |
| **Manual Code Entry** | Text field for the 6-digit numeric fallback code |
| **Device Registration** | Sends full Build fingerprint (board, brand, device, model, serial, etc.) to `/device/register`; backend returns a persistent device UUID |
| **Session Persistence** | Auth code ID and type stored in DataStore; survives app restart |
| **Biometric Lock** | Optional AndroidX BiometricPrompt (`BIOMETRIC_STRONG \| DEVICE_CREDENTIAL`) shown on app launch after first auth |

### Text Transfer
| Feature | Description |
|---|---|
| **Manual Text Input** | Free-form text entry in `Handle_Texts` activity |
| **Clipboard Paste** | Read text from Android clipboard (supports `text/plain`, `text/html`, URI lists) |
| **OCR Capture** | Take a photo; extract text via Firebase ML Kit on-device |
| **Upload to Server** | Text sent via `POST /home/phone/set_files_to_delete` (shared endpoint in Retrofit interface) |

### File Transfer
| Feature | Description |
|---|---|
| **File Picker** | `ACTION_GET_CONTENT` with `*/*` MIME filter; multi-select |
| **Bulk Upload** | Multipart POST via Retrofit with device ID, session ID, and file payload |
| **File List** | `Fragment_History_Files` fetches list from `/home/phone/get_files_uploaded_by_session` |
| **Download** | Sequential batch download to device's `Downloads` directory |
| **Delete** | Soft-delete via `POST /home/phone/set_files_to_delete` |

### History & Stats
| Feature | Description |
|---|---|
| **File History** | RecyclerView with name, type, size, date; checkboxes for batch selection |
| **Transfer Stats** | Transfer count, file count, text count persisted in SharedPreferences |

### Security & Privacy
| Feature | Description |
|---|---|
| **Biometric App Lock** | Optional toggle in Settings; uses `BiometricPrompt` with `BiometricManager.Authenticators.BIOMETRIC_STRONG` |
| **SSL Pinning** | *(None — ServiceGenerator trusts all certificates; development/debug feature)* |
| **Cleartext Traffic** | `usesCleartextTraffic="true"` in manifest for local network use |
| **No Permissions Storage** | `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO` for Android 13+, CAMERA for QR and OCR |

### UI & Theming
| Feature | Description |
|---|---|
| **Dark Mode** | Toggle in Settings persisted via SharedPreferences; applied with `AppCompatDelegate.setDefaultNightMode` |
| **Bottom Navigation** | Home and History tabs |
| **Material Design** | MaterialToolbar, Material 3 theming, adaptive layouts |

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Kotlin + Java | Kotlin 1.9.10, Java 11 |
| **Build System** | Gradle | 8.7 (wrapper) |
| **Android Plugin** | AGP | 8.6.0 |
| **Min / Target SDK** | API 24 / 35 | Android 7.0–15 |
| **Networking** | Retrofit + OkHttp | 2.9.0 / 4.9.0 |
| **Serialization** | Gson converter | 2.9.0 |
| **Local Storage** | DataStore Preferences | 1.0.0 |
| **QR Scanning** | ZXing (journeyapps) | 4.3.0 |
| **OCR** | Firebase ML Kit text-recognition | 16.0.0 |
| **Biometrics** | AndroidX Biometric | 1.1.0 |
| **UI Components** | Material, ConstraintLayout, SDP, CircleImageView | 1.9.0, 2.1.4, 1.1.0, 3.0.0 |
| **Async** | Kotlinx Coroutines | 1.7.3 |
| **Architecture** | MVVM + LiveData | — |

---

## Prerequisites

- **Android Studio** Giraffe or newer (AGP 8.6.0)
- **JDK** 11+
- **Gradle** 8.7 (wrapper included)
- The **P2P Copier WebApp** backend running (see sibling repo setup)

---

## Installation & Setup

### 1. Clone the repository

```bash
git clone <repo-url>
cd P2P_Copier_App
```

### 2. Set up the backend

Follow the Docker setup in the [P2P Copier WebApp repo](https://github.com/niccher/P2P_Copier_WebApp):

```bash
git clone <webapp-repo-url>
cd "P2P Copier WebApp"
docker compose up --build -d
```

### 3. Configure the Android app

The base URL is hardcoded in `app/src/main/java/com/niccher/p2p_copier_app/utils/Konstants.kt`:

```kotlin
var str_base_url: String = "https://p2p.chegecache.co.ke"
```

**For local development against Docker:**

1. Find your host machine's LAN IP (e.g., `192.168.1.212`).
2. Build the Docker backend on that host.
3. Edit `Konstants.kt` to point to your local IP and port:

```kotlin
var str_base_url: String = "http://192.168.1.212:9004"
```

> If using the Android Emulator, use `10.0.2.2` in place of the host IP.

### 4. Build & Run

Open the project in Android Studio, sync Gradle, and run on a device or emulator.

---

## Data Flow (Endpoints)

All endpoints are relative to `str_base_url` (`https://p2p.chegecache.co.ke`).

| HTTP | Path (relative) | Source | Purpose |
|---|---|---|---|
| POST | `/device/register` | `AuthSession.java` | Register device fingerprint; receive `dev_uuid` |
| POST | `/auth/register` | `AuthSession.java` | Validate QR or text code; receive `auth_code_id` |
| POST | `/home/phone/upload` | `Adapter_Sel_Files.java` | Upload file (multipart) |
| POST | `/home/phone/get_files_uploaded_by_session` | `Fragment_History_Files.java` | List session files |
| POST | `/home/phone/get_files_uploaded_by_session_download` | `Adapter_Uploaded_Files.java` | Download a file |
| POST | `/home/phone/set_files_to_delete` | `Adapter_Uploaded_Files.java`, `Handle_Texts.java` | Delete file / upload text |

The Retrofit service interface is defined in `RetrofitInterface.java` with concrete `@POST("...")` annotations — no path concatenation at runtime.

---

## Project Structure

```
app/src/main/java/com/niccher/p2p_copier_app/
├── activities/
│   ├── AuthSession.java          # QR scan + code entry + device registration
│   ├── Auth_New_Or_Continue.java # Resume or start new session
│   ├── BiometricLockActivity.kt  # Biometric gate on app resume
│   ├── Handle_Files.java         # File picker + upload
│   ├── Handle_Text_2_Image.java  # Camera → OCR → text upload
│   ├── Handle_Texts.java         # Text input + clipboard paste
│   └── Regista.java              # Welcome / get-started screen
├── adapters/
│   ├── Adapter_Rando.java        # Text history dummy adapter
│   ├── Adapter_Sel_Files.java    # Files selected for upload
│   └── Adapter_Uploaded_Files.java # Uploaded file list with batch ops
├── datastore/
│   ├── AuthPreferences.kt        # DataStore for session auth data
│   └── DevicePreferences.kt      # DataStore for device UUID
├── fragments/
│   ├── Fragment_About.java
│   ├── Fragment_Credits.java
│   ├── Fragment_History_Files.java # Uploaded files with download/delete
│   ├── Fragment_History_Text.java  # Text history (dummy data)
│   ├── Fragment_Home.java          # Quick actions dashboard
│   ├── Fragment_QR.java            # QR placeholder fragment
│   └── Fragment_Settings.java     # Biometric + dark mode toggles
├── interfaces/
│   └── RetrofitInterface.java     # Retrofit API contract
├── model/
│   ├── Mod_Auth.java
│   ├── Mod_Device.java
│   ├── Mod_Device_Id.java
│   ├── Mod_File_Delete.java
│   ├── Mod_File_Uploaded.java
│   ├── Mod_File_info.java
│   ├── Mod_File_rando.java
│   └── Mod_List_File_Uploaded.java
├── splash/
│   └── Splasher.kt               # Launcher activity, auth check
├── utils/
│   ├── BiometricHelper.kt        # BiometricPrompt wrapper
│   ├── FileUtils.java            # URI-to-path resolution
│   ├── Helpers.java              # SharedPrefs/DataStore helpers
│   ├── Konstants.kt              # Base URL + endpoint groups
│   ├── LocalStorageProvider.java # DocumentsProvider for local files
│   ├── ResponseSummarizer.java   # File list API response wrapper
│   ├── ServiceGenerator.java     # Retrofit singleton (trust-all SSL)
│   └── SharedPrefs.java          # Generic SharedPreferences wrapper
├── viewmodels/
│   ├── FileViewModel.java        # Selected files + upload state
│   ├── HomeViewModel.java        # Fragment navigation state
│   └── TextViewModel.java        # Text content + upload state
├── HomePage.java                 # Main hub (toolbar + bottom nav)
└── MainActivity.java             # Legacy home (unused)
```

---

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature-name`.
3. Commit your changes.
4. Push to the branch.
5. Open a Pull Request.

---

## License

Distributed under the MIT License. See `LICENSE` for details.

---

## Support / Contact

- **Website:** [chegecache.co.ke](https://chegecache.co.ke)
- **Email:** [info@chegecache.co.ke](mailto:info@chegecache.co.ke)
- **Sibling repo:** [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp)
