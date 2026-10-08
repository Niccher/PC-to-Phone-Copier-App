# Client-Server Communication & Networking

This document details the HTTP networking patterns, Retrofit client setup, and request lifecycle between P2P Copier Android App and the CodeIgniter 4 backend relay.

---

## 1. Network Protocol Flow

```mermaid
sequenceDiagram
    autonumber
    participant App as Android Client
    participant SG as ServiceGenerator / Retrofit
    participant Server as CI4 WebApp (Port 9004)

    Note over App,Server: 1. Handshake & Registration
    App->>SG: execute registerDevice(fingerprint, model)
    SG->>Server: POST /api/v1/device/register
    Server-->>SG: 200 OK {dev_uuid: "f47a..."}
    SG-->>App: Save dev_uuid in DevicePreferences DataStore

    Note over App,Server: 2. Session Pairing
    App->>SG: execute pairSession(dev_uuid, code)
    SG->>Server: POST /api/v1/auth/pair
    Server-->>SG: 200 OK {status: "paired", session_uuid: "e6a2..."}
    SG-->>App: Save session_uuid in AuthPreferences DataStore

    Note over App,Server: 3. Fetching Content (Unified Feed)
    App->>SG: execute getUploadedHistory(session_uuid)
    SG->>Server: POST /api/v1/uploaded
    Server-->>SG: 200 OK [items: files + text + OCR]
    SG-->>App: Render Adapter_Uploaded_Files RecyclerView
```

---

## 2. Retrofit Interface Mapping

Configured in `com.niccher.p2p_copier_app.interfaces.RetrofitInterface`:

| Method | Endpoint | Triggering Component | Purpose |
|---|---|---|---|
| `POST` | `/api/v1/device/register` | `AuthSession.java` | Register device hardware fingerprint |
| `POST` | `/api/v1/auth/pair` | `AuthSession.java` | Validate QR or 6-digit numeric pairing code |
| `POST` | `/api/v1/auth/session-status` | `Fragment_Profile.java` | Check whether current session remains active |
| `POST` | `/api/v1/uploaded` | `Fragment_History_Files.java` | Single merged response of files + texts |
| `POST` | `/api/v1/files/upload` | `Adapter_Sel_Files.java` | Multipart binary file upload |
| `POST` | `/api/v1/files/download` | `Adapter_Uploaded_Files.java` | Download binary payload to device Downloads folder |
| `POST` | `/api/v1/files/delete` | `Adapter_Uploaded_Files.java` | Soft-delete a file record |
| `POST` | `/api/v1/texts` | `TextViewModel.java` | Upload typed text, OCR text, or QR scan data |
| `POST` | `/api/v1/texts/delete` | `Adapter_Uploaded_Files.java` | Soft-delete a text record |
| `POST` | `/api/v1/analytics/summary` | `Fragment_History_Overview.java` | Retrieve transfer statistics & event telemetry |

---

## 3. Base URL & Dynamic Switching

Base URL resolution is managed dynamically via `ServiceGenerator.java` and `Konstants.kt`:
- **Default Base URL**: Set initially via `Konstants.str_base_url`.
- **Runtime Override**: The user or tester can switch host and port at runtime via `BackendConfigActivity`. The updated URL is persisted and reinitializes the OkHttpClient instance with updated connection pools and logging interceptors.
