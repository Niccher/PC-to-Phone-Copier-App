# Threat Model & Mobile Security Hardening

This document provides a mobile threat model analysis (STRIDE) and security hardening guidelines for the P2P Copier Android client.

---

## 1. Mobile Threat Analysis (STRIDE Model)

| Threat Category | Mobile Attack Vector | Risk | Mitigation in Codebase |
|---|---|---|---|
| **Spoofing** | Rogue app attempts to access persisted tokens or impersonate device. | High | Hardware fingerprint persisted in Jetpack DataStore; device bound to unique `dev_uuid`. |
| **Tampering** | Man-in-the-Middle on untrusted Wi-Fi modifying payloads. | High | Cleartext enabled only for `10.0.2.2` / local development. TLS 1.3 enforced for public staging. |
| **Repudiation** | User denies uploading a file or sending clipboard text. | Low | All network actions dispatched with device UUID and session timestamps. |
| **Information Disclosure** | Unauthorized person picks up unlocked device and views transferred texts. | High | Optional biometric lock gate (`BiometricLockActivity.kt`) with Android Keystore. |
| **Information Disclosure** | Sensitive text or tokens leaked to system `logcat`. | Medium | `HttpLoggingInterceptor` set to `NONE` in release builds; no raw passwords/tokens printed. |
| **Denial of Service** | App crashes due to Out-Of-Memory when transferring multi-gigabyte files. | High | Files streamed using `ContentResolver` and Okio sinks rather than loading byte arrays into RAM. |

---

## 2. Biometric Security Gate

The optional biometric gate (`BiometricLockActivity.kt`):
- Leverages AndroidX `BiometricPrompt`.
- Protects access to historical clipboard entries and sensitive documents when resuming the application.
- Enforces hardware-level authentication keys without storing user biometric data inside the app.

---

## 3. DataStore & Local Storage Protection

- Session keys are stored asynchronously via Android Jetpack DataStore Preferences.
- Resetting the app via **Profile > Reset All App Data** executes `Helpers.deleteAllAppDataAndReset()`, immediately erasing tokens and preventing token persistence across distinct users.
