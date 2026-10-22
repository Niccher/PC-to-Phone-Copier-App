# Changelog

All notable changes to the P2P Copier Mobile Application are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.0] - 2026-10-22

### Added
- Root `VERSION` file as the single source of truth for Gradle `versionCode` and `versionName`.
- Dynamic version telemetry headers (`X-App-Version`, `X-App-Build`) sent in all HTTP requests via OkHttp.
- Dynamic version display on the About screen.

### Fixed
- Fixed device registration API response parsing to correctly decode `ApiResponse<Mod_Device_Id>`.
- Resolved issue where `dev_uuid` was not persisted upon initial registration, causing pairing failure with 401 Unauthorized.
- Added default no-arg constructor to `Mod_Device_Id` for safe Gson deserialization.

## [2.0.0] - 2026-10-15

### Added
- Unified REST API v1 communication architecture (`ApiV1Interface`).
- Jetpack DataStore preferences migration (`DevicePreferences`, `AuthPreferences`).
- Modern UI overhaul with Material Design 3 and responsive layouts.
- In-app camera OCR text extraction.
- Automatic backend discovery and server URL reconfiguration via QR scan.

## [1.1.0] - 2026-09-01

### Added
- Local network pairing and manual 6-digit session pairing code.
- File transfer and clipboard text synchronization support.

## [1.0.0] - 2026-08-10

### Added
- Initial release of P2P Copier Android client.
