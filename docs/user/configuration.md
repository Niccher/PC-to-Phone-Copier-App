# User Configuration Guide

This document describes client configuration options, network endpoints, and settings in P2P Copier App.

---

## 1. Automated Server Discovery via QR Code

The app dynamically sets its server target directly when scanning the WebApp's pairing QR code. The QR code contains the dynamic server host and port, automatically configuring the client connection with zero manual input.

### Dynamic Discovery Flow

1. **WebApp Host Resolution**: WebApp determines its active URL (e.g. `http://<LAN_IP>:80` or `https://p2p.chegecache.co.ke`) from HTTP headers and encodes it into the QR code.
2. **Scan & Configure**: When scanned in the app, `Konstants.saveBackendFromUrl()` persists the URL and `ServiceGenerator.rebuildService()` immediately switches the active Retrofit client.
3. **Instant Pairing**: The app registers the device on the detected server and establishes the session handshake in a single step.

### Network Presets by Environment

| Environment | Base URL Setting | Notes |
|---|---|---|
| **Android Virtual Device (AVD)** | `http://10.0.2.2:9004/` | `10.0.2.2` routes directly to 127.0.0.1 on the development machine. |
| **Physical Phone via USB** | `http://127.0.0.1:9004/` | Requires running `adb reverse tcp:9004 tcp:9004` on the host. |
| **Physical Phone over Wi-Fi** | `http://<Host-LAN-IP>:9004/` | Host PC and Android device must share the same subnet. |
| **Remote / Cloud Staging** | `https://p2p.example.com/` | Requires standard HTTPS endpoint. |

---

## 2. In-App Security Settings

- **Biometric Gate**: Can be toggled in **Settings**. When enabled, the app prompts for fingerprint or biometric verification on every app resume via `BiometricPrompt`.
- **Session Reset**: Accessible in **Profile**. Clears all cached session tokens, device fingerprints, and local transfer history.
