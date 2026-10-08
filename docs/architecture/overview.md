# Client Architecture Overview

This document details the software architecture, component layers, and data flow of the P2P Copier Android client.

---

## 1. High-Level Client Architecture

The application is architected around Android Jetpack patterns (MVVM), incorporating Retrofit for networking, Jetpack DataStore for local session persistence, Google ML Kit for on-device OCR, and ZXing for QR barcode decoding.

```mermaid
flowchart TD
    subgraph Presentation["Presentation Tier (UI & Views)"]
        Splasher["Splasher / AuthSession Activity"]
        HomeActivity["HomePage Activity\n(Bottom Navigation Hub)"]
        FrgHistory["Fragment_History_Files\n(Unified Feed)"]
        FrgOverview["Fragment_History_Overview\n(Analytics Summary)"]
        FrgProfile["Fragment_Profile\n(Device Info & Session)"]
        OCRActivity["Handle_Text_2_Image\n(Camera Photo to OCR)"]
    end

    subgraph State["State & Business Logic Tier"]
        FileVM["FileViewModel\n(Upload Queue & State)"]
        TextVM["TextViewModel\n(Text / OCR Uploads)"]
        HomeVM["HomeViewModel\n(Navigation Events)"]
    end

    subgraph StorageEngine["Local Persistence Tier"]
        AuthPrefs["AuthPreferences\n(Jetpack DataStore)"]
        DevicePrefs["DevicePreferences\n(Jetpack DataStore)"]
        SharedPrefs["SharedPreferences\n(Legacy Session Storage)"]
    end

    subgraph Hardware["On-Device ML & Hardware Engines"]
        MLKit["Google ML Kit Text Recognition\n(com.google.mlkit:text-recognition)"]
        ZXing["ZXing Android Embedded\n(Barcode / QR Scanner)"]
        BioPrompt["AndroidX BiometricPrompt\n(Biometric Gate)"]
    end

    subgraph NetworkClient["Networking Tier"]
        ServiceGen["ServiceGenerator\n(OkHttp Client & Logging)"]
        Retrofit["RetrofitInterface\n(REST API v1 Client)"]
    end

    Presentation --> State
    State --> NetworkClient
    Presentation --> Hardware
    Presentation --> StorageEngine
    NetworkClient -->|HTTP / JSON| RemoteServer["CodeIgniter 4 WebApp\n(Port 9004)"]
```

---

## 2. Component Responsibility Matrix

| Component Layer | Classes | Responsibility |
|---|---|---|
| **Entry & Gateway** | `Splasher`, `Auth_New_Or_Continue`, `BiometricLockActivity` | Validates biometric lock, checks active session in DataStore, and routes to setup or main hub. |
| **Session & Auth** | `AuthSession.java` | Initiates device registration (`/device/register`) and pairing verification (`/auth/pair`). |
| **Content Handlers** | `Handle_Files.java`, `Handle_Texts.java`, `Handle_Text_2_Image.java` | Pick documents/media, capture typed text from clipboard, and run ML Kit OCR on camera snapshots. |
| **Adapters & UI** | `Adapter_Uploaded_Files.java`, `Adapter_Sel_Files.java` | Renders unified feed cards (color-coded badges for OCR, Text, QR, PDF, Images) and interactive modals. |
| **Networking** | `ServiceGenerator.java`, `RetrofitInterface.java` | Configures OkHttp logging, timeouts, Base URL switching, and REST endpoints. |
| **Local Storage** | `AuthPreferences.kt`, `DevicePreferences.kt` | Asynchronously stores and observes session tokens and device UUIDs via Coroutines Flow. |
