# User Configuration Guide

This document describes client configuration options, network endpoints, and settings in P2P Copier App.

---

## 1. Backend Server Configuration

The app includes an in-app configuration screen (`BackendConfigActivity`) that allows runtime adjustment of backend connections without modifying code.

### Configuration Fields

| Field | Example Value | Description |
|---|---|---|
| **Backend Base URL** | `http://10.0.2.2` | The protocol and hostname/IP of the CodeIgniter 4 WebApp server. |
| **Backend Port** | `9004` | Published port of the WebApp server. |
| **Connection Test** | Button ("Test Connection") | Sends an HTTP ping to `/api/v1/ping` and reports status code and latency. |

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
