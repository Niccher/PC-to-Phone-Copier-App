# Security, Authentication & Cryptography

This document details the security architecture, biometric authentication gate, and data protection mechanisms in the P2P Copier Android client.

---

## 1. Biometric Authentication Gate

The application provides an optional biometric gate (`activities/BiometricLockActivity.kt`) leveraging `androidx.biometric:biometric:1.1.0`.

### Security Guarantees
- Uses Android Keystore and hardware-backed biometric prompts (`BIOMETRIC_STRONG` / `DEVICE_CREDENTIAL`).
- Prevents unauthorized access when resuming the app if the user enables the Biometric Gate toggle in settings.

---

## 2. Token & Identifier Storage

- **Device UUID (`dev_uuid`)**: Generated once upon initial device registration and persisted in Jetpack DataStore (`DevicePreferences.kt`).
- **Session Tokens (`session_uuid`)**: Received upon QR code or 6-digit code validation and stored asynchronously in DataStore (`AuthPreferences.kt`).
- **Session Reset**: Invoking `Helpers.deleteAllAppDataAndReset()` purges all DataStore preferences and local cache directories, ensuring no residual session tokens linger after logout.

---

## 3. Network Security & Logging Hygiene

- **Logging Level**: `HttpLoggingInterceptor` is configured in `ServiceGenerator.java`. In production release variants, ensure logging is downgraded to `Level.NONE` or `Level.BASIC` to prevent leaking session tokens or file binary payloads into `logcat`.
- **Cleartext Traffic**: While `android:usesCleartextTraffic="true"` is enabled for local development (e.g. `http://10.0.2.2:9004`), production builds communicating over public internet should enforce HTTPS TLS 1.3 endpoints.
