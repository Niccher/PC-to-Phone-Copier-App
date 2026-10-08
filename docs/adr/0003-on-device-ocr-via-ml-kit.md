# ADR-0003: On-Device ML Kit OCR Processing

- **Status**: Accepted
- **Date**: 2026-08-20
- **Deciders**: Architecture Team

---

## Context and Problem Statement

When scanning documents with the smartphone camera, extracting textual data can be performed either on-device or via server-side OCR.
Uploading multi-megabyte image bitmaps across local networks or public Wi-Fi introduces high latency, cellular data usage, and privacy concerns.

---

## Decision Drivers

- Immediate text recognition (< 1 second).
- Strict user privacy: personal documents (IDs, receipts, notes) must never leave the device in raw image format.
- Bandwidth reduction: transmitting a 1 KB text string vs. a 4 MB image bitmap.

---

## Decision Outcome

Chosen option: **On-Device Optical Character Recognition via Google ML Kit (`com.google.mlkit:text-recognition:16.0.0`)**.

### Implementation Details
- `Handle_Text_2_Image.java` captures the camera frame and constructs an `InputImage`.
- Google ML Kit's local Latin script recognition model extracts text blocks entirely in-memory.
- Only the extracted string is transmitted over the network to `POST /api/v1/texts`.

### Consequences
- **Positive**: Sub-second text extraction without network latency.
- **Positive**: Complete privacy guarantee: no images are stored on the server.
- **Negative**: Adds ~5 MB to the application APK footprint.
