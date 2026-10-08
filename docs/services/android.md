# Android Service & Component Guide

This guide details the internal structure, dependencies, UI components, and on-device ML integrations for the **P2P Copier Android App**.

---

## 1. Project Specifications

- **Languages**: Kotlin 1.9.10 & Java 11
- **Android Gradle Plugin**: AGP 8.6.0
- **Gradle Version**: 8.7
- **SDK Target**: `compileSdk = 35`, `targetSdk = 35`, `minSdk = 24`
- **Build Features**: DataBinding & ViewBinding enabled

---

## 2. Codebase Hierarchy

```
app/src/main/java/com/niccher/p2p_copier_app/
├── activities/
│   ├── AuthSession.java            # QR scanning, 6-digit code pairing & registration
│   ├── Auth_New_Or_Continue.java   # First-run launcher and session chooser
│   ├── BackendConfigActivity.java  # Runtime backend URL and port configurator
│   ├── BiometricLockActivity.kt    # AndroidX Biometric gate
│   ├── Handle_Files.java           # Document/media picker and upload manager
│   ├── Handle_Text_2_Image.java    # Camera snapshot to ML Kit OCR pipeline
│   └── Handle_Texts.java           # Typed notes and clipboard text submission
├── adapters/
│   ├── Adapter_Sel_Files.java      # Pending file upload queue adapter
│   └── Adapter_Uploaded_Files.java # Unified history feed adapter with modal dialogs
├── datastore/
│   ├── AuthPreferences.kt          # Jetpack DataStore for session tokens
│   └── DevicePreferences.kt        # Jetpack DataStore for device UUIDs
├── fragments/
│   ├── Fragment_History_Files.java # "Uploaded" unified feed screen
│   ├── Fragment_History_Overview.java # Activity log and transfer analytics
│   ├── Fragment_Profile.java       # Device info, ping test, logout & reset
│   └── Fragment_Settings.java      # Theme toggle and biometric lock switch
├── interfaces/
│   └── RetrofitInterface.java      # REST API v1 endpoint signatures
├── model/
│   ├── Mod_List_File_Uploaded.java # Unified file and text DTO model
│   ├── Mod_Text_Uploaded.java      # Text and OCR record model
│   └── api/                        # API envelopes (UploadedEnvelope, ApiResponse)
├── utils/
│   ├── Helpers.java                # Reset, logout, and shared preference utilities
│   ├── Konstants.kt                # Default network addresses and keys
│   └── ServiceGenerator.java       # Retrofit HTTP client builder
└── HomePage.java                   # Main bottom navigation hub
```

---

## 3. On-Device Machine Learning & Scanners

### A. Firebase / Google ML Kit OCR Text Recognition
Implemented in `activities/Handle_Text_2_Image.java`:
- Uses `com.google.mlkit:text-recognition:16.0.0` to process camera photos on-device without cloud network latency.
- Converts camera bitmap to an `InputImage` object, runs `TextRecognizer.process()`, and extracts blocks and lines of recognized Latin text.
- Formats extracted text and submits directly to `POST /api/v1/texts` with `source: "OCR"`.

### B. ZXing Barcode & QR Scanner
Implemented in `activities/AuthSession.java`:
- Uses `com.journeyapps:zxing-android-embedded:4.3.0`.
- Launches camera scanning via `IntentIntegrator`.
- Decodes the 6-digit pairing token or JSON session envelope from the desktop WebApp screen.

---

## 4. Developer Recipes

### Recipe A: Adding a New Retrofit API Call
1. In `interfaces/RetrofitInterface.java`:
   ```java
   @POST("api/v1/custom/endpoint")
   Call<ApiResponse> customAction(@Body CustomRequestPayload payload);
   ```
2. Invoke using `ServiceGenerator`:
   ```java
   RetrofitInterface api = ServiceGenerator.createService(RetrofitInterface.class);
   api.customAction(payload).enqueue(new Callback<ApiResponse>() {
       @Override
       public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
           if (response.isSuccessful()) {
               // Handle success
           }
       }
       @Override
       public void onFailure(Call<ApiResponse> call, Throwable t) {
           // Handle network failure
       }
   });
   ```

### Recipe B: Storing a Value in Jetpack DataStore
In `datastore/AuthPreferences.kt`:
```kotlin
suspend fun saveCustomKey(value: String) {
    context.dataStore.edit { preferences ->
        preferences[CUSTOM_KEY] = value
    }
}
```
