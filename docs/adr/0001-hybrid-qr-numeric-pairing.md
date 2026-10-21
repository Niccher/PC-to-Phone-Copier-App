# ADR-0001: QR Auto-Discovery & Zero-Config Pairing

- **Status**: Superseded (Updated to Pure QR Auto-Discovery)
- **Date**: 2026-08-14 (Updated 2026-10-21)
- **Deciders**: Architecture Team

---

## Context and Problem Statement

The mobile client needs a secure, frictionless mechanism to pair with an active desktop web session.
Manual server URL entry (IP and port) and manual PIN entry introduced cognitive friction, typo errors, and connection misconfigurations across varying network topologies (local Wi-Fi, emulators, custom domains, and GCP deployments).

---

## Decision Drivers

- Immediate setup with zero manual server configuration.
- Seamless connection across local IP, emulator, and public/GCP hosts.
- Single-action mobile experience (point camera and connect).

---

## Decision Outcome

Chosen option: **Pure QR Scanning with Dynamic Server Auto-Discovery**.

### Implementation Details
- The WebApp dynamically generates a structured JSON QR code payload:
  `{"v": 1, "srv": "http://<active-host>:<port>", "code": "...", "sess": "..."}`
- `AuthSession.java` integrates the ZXing scanner (`IntentIntegrator`) to scan the QR code.
- Upon scanning, the Android app extracts the server URL, updates `Konstants.saveBackendFromUrl()`, rebuilds Retrofit (`ServiceGenerator.rebuildService()`), registers the device hardware fingerprint, and completes the pairing handshake automatically.
- All manual server configuration activities and buttons have been removed.

### Consequences
- **Positive**: Zero manual IP/port configuration required by users.
- **Positive**: Eliminates connection errors caused by host/port mismatches.
- **Positive**: WebApp backend REST API (`POST /api/v1/auth/pair`) preserves underlying numeric code pairing compatibility.
