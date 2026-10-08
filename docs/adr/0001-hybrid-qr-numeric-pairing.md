# ADR-0001: Hybrid QR Code & 6-Digit Numeric Pairing

- **Status**: Accepted
- **Date**: 2026-08-14
- **Deciders**: Architecture Team

---

## Context and Problem Statement

The mobile client needs a secure, frictionless mechanism to pair with an active desktop web session.
Relying solely on camera scanning fails during emulator development, on devices with damaged cameras, or in low-light environments. Relying on user accounts introduces unnecessary friction for local utility transfers.

---

## Decision Drivers

- Immediate setup with zero account registration.
- Flawless testing capabilities on Android Virtual Devices (AVD).
- Resilient fallback mechanism for diverse mobile hardware.

---

## Decision Outcome

Chosen option: **Hybrid Pairing via QR Scanning and 6-Digit Fallback**.

### Implementation Details
- `AuthSession.java` integrates the ZXing scanner (`IntentIntegrator`) to scan desktop QR codes.
- It concurrently provides a numeric input dialog allowing manual entry of the 6-digit PIN.
- Upon entry, the app dispatches `POST /api/v1/auth/pair` with `dev_uuid` and `pairing_code`.

### Consequences
- **Positive**: 100% testable on emulators without virtual scene camera configuration.
- **Positive**: Works seamlessly across all Android devices regardless of camera capabilities.
