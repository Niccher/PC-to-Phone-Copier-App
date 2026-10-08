# Release Train & Version Compatibility Matrix

This document defines versioning standards, release cycles, and API compatibility for the P2P Copier Android client.

---

## 1. Versioning Matrix

| Specification | Current Value | Notes |
|---|---|---|
| **Version Name** (`versionName`) | `1.1` | User-visible release identifier |
| **Version Code** (`versionCode`) | `2` | Monotonically increasing build integer |
| **Compile SDK** (`compileSdk`) | `35` | Android 15 platform APIs |
| **Minimum SDK** (`minSdkVersion`) | `24` | Android 7.0 (Nougat) platform base |
| **Target SDK** (`targetSdkVersion`) | `35` | Target platform compliance |
| **Backend API Target** | `/api/v1/` | Communicates with CodeIgniter 4 WebApp v1.1.0 |

---

## 2. Release Steps

1. Update `versionCode` and `versionName` in `app/build.gradle`.
2. Re-verify documentation using `python scripts/lint-docs.py .`.
3. Run test suites:
   ```bash
   ./gradlew test
   ```
4. Assemble release APK:
   ```bash
   ./gradlew assembleRelease
   ```
5. Sign APK with production keystore (never commit signing credentials to git).
6. Tag Git commit:
   ```bash
   git tag -a android-v1.1 -m "Release Android Client v1.1"
   ```
