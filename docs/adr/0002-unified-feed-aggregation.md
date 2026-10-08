# ADR-0002: Server-Side Unified Feed Aggregation (`/api/v1/uploaded`)

- **Status**: Accepted
- **Date**: 2026-08-16
- **Deciders**: Architecture Team

---

## Context and Problem Statement

The Android client presents a single historical list ("Uploaded") of all items transferred between the smartphone and desktop browser (files, text notes, OCR text, and QR scans).
Fetching files and text from separate REST endpoints would require client-side concurrency orchestration, client-side sorting, and duplicate network requests.

---

## Decision Drivers

- Minimizing radio/cellular battery consumption.
- Simplified RecyclerView adapter architecture (`Adapter_Uploaded_Files.java`).
- Elimination of client-side race conditions.

---

## Decision Outcome

Chosen option: **Consume Server-Aggregated Unified Feed (`POST /api/v1/uploaded`)**.

### Implementation Details
- `Fragment_History_Files.java` calls `POST /api/v1/uploaded` passing `session_uuid`.
- Retrofit deserializes the response directly into `UploadedEnvelope` and `Mod_List_File_Uploaded` objects.
- `Adapter_Uploaded_Files` binds either file cards (PDF, image, video) or text cards with interactive modals based on the polymorphic item type.

### Consequences
- **Positive**: Single HTTP roundtrip dramatically accelerates feed rendering.
- **Positive**: Simplifies client view models and eliminates client-side sort overhead.
