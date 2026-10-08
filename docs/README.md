# Engineering Documentation

Welcome to the internal engineering documentation for **P2P Copier App** (Kotlin/Java Android Client).

## Documentation Navigation

| I want to… | Go here |
|---|---|
| **Run the app without coding** | [../README.md](../README.md) |
| **Understand mobile client architecture & components** | [architecture/overview.md](architecture/overview.md) |
| **See how Android and WebApp communicate** | [architecture/communication.md](architecture/communication.md) |
| **Inspect Room database & local persistence** | [architecture/data-and-storage.md](architecture/data-and-storage.md) |
| **Review Mobile Threat Model & security mitigations** | [architecture/threat-model.md](architecture/threat-model.md) |
| **Work on Android Activities, ViewModels & ML Kit** | [services/android.md](services/android.md) |
| **Set up local development machine (SDK, Studio, JDK)** | [engineering/local-development.md](engineering/local-development.md) |
| **Change behavior safely (Task routing & DoD)** | [engineering/making-changes.md](engineering/making-changes.md) |
| **Manage Room database, DAOs & migrations** | [engineering/database.md](engineering/database.md) |
| **Run automated unit and instrumented tests** | [engineering/testing.md](engineering/testing.md) |
| **Continuous integration & build pipelines** | [engineering/ci.md](engineering/ci.md) |
| **Version matrix, SDK targets & release checklist** | [engineering/release.md](engineering/release.md) |
| **Biometric lock, DataStore & security model** | [engineering/security.md](engineering/security.md) |
| **Troubleshoot Gradle, Room, and build errors** | [engineering/troubleshooting.md](engineering/troubleshooting.md) |
| **Contributing guidelines & PR workflow** | [engineering/contributing.md](engineering/contributing.md) |
| **ADR-0001: Hybrid QR & Numeric Pairing** | [adr/0001-hybrid-qr-numeric-pairing.md](adr/0001-hybrid-qr-numeric-pairing.md) |
| **ADR-0002: Unified Feed Aggregation** | [adr/0002-unified-feed-aggregation.md](adr/0002-unified-feed-aggregation.md) |
| **ADR-0003: On-Device ML Kit OCR Processing** | [adr/0003-on-device-ocr-via-ml-kit.md](adr/0003-on-device-ocr-via-ml-kit.md) |

## Polyrepo Ecosystem Links

| Component | Responsibility | Repository URL | Documentation |
|---|---|---|---|
| **Backend Relay** | CodeIgniter 4 Web UI, API & Relay | [P2P Copier WebApp](https://github.com/niccher/P2P_Copier_WebApp) | [docs/](https://github.com/niccher/P2P_Copier_WebApp/tree/main/docs) |
| **Android Client** | Kotlin/Java Android Mobile Client | [P2P_Copier_App](https://github.com/niccher/PC-to-Phone-Copier-App) | [docs/](README.md) |
