# Making Changes Safely

This guide maps common Android client feature additions to their source files and defines the Definition of Done (DoD) for mobile updates.

---

## 1. Task Routing Table

| If you want to… | Touch these files / directories |
|---|---|
| **Add a new REST API endpoint** | `interfaces/RetrofitInterface.java`<br>`model/api/` (create or update response envelope)<br>`viewmodels/`<br>`P2P Copier WebApp` API contract |
| **Add a new UI tab or fragment** | `HomePage.java`<br>`fragments/`<br>`res/layout/`<br>`res/menu/bottom_nav_menu.xml` |
| **Modify on-device OCR logic** | `activities/Handle_Text_2_Image.java`<br>`AndroidManifest.xml` (ML Kit dependencies) |
| **Change QR scanning behavior** | `activities/AuthSession.java`<br>ZXing `IntentIntegrator` parameters |
| **Modify local persistence** | `datastore/AuthPreferences.kt`<br>`datastore/DevicePreferences.kt`<br>`utils/Helpers.java` |
| **Alter network error handling / logging** | `utils/ServiceGenerator.java`<br>`okhttp3.logging.HttpLoggingInterceptor` |

---

## 2. Definition of Done (Mobile Changes)

Before merging any PR or tagging a release:

- [ ] **Code Compiles**: Passes `./gradlew assembleDebug` with zero compilation errors.
- [ ] **API Contract Compliant**: JSON keys and `@SerializedName` annotations match the WebApp `docs/api/contract.md`.
- [ ] **Clean Data Models**: No raw JSON parsing; models leverage Gson annotations.
- [ ] **Zero-Copy File Handling**: Large files streamed via `ContentResolver` rather than read completely into memory.
- [ ] **Permissions Handled**: Runtime permissions checked before accessing camera or media.
- [ ] **Documentation Linter**: Passes `python scripts/lint-docs.py .` with zero errors.
- [ ] **Tests Passing**: `./gradlew test` passes cleanly.
