# User & QA Troubleshooting Guide

Solutions for common runtime issues encountered when testing or operating P2P Copier App.

---

## 1. Connection Refused (`java.net.ConnectException`)

**Symptom**: Network calls fail immediately when testing on an emulator or physical device.

**Remediation**:
- **If running in an Emulator**: Do not use `localhost` or `127.0.0.1`. Inside an Android emulator, `127.0.0.1` refers to the emulator itself. Configure the server URL as:
  `http://10.0.2.2:9004/`
- **If running on a Physical Phone via USB**: Run this command on your host computer:
  ```bash
  adb reverse tcp:9004 tcp:9004
  ```
  Then configure the app URL to `http://127.0.0.1:9004/`.
- **If running on Wi-Fi**: Ensure the host PC firewall permits incoming TCP connections on port 9004.

---

## 2. Cleartext HTTP Communication Blocked

**Symptom**: App logs show `java.io.IOException: Cleartext HTTP traffic to ... not permitted`.

**Remediation**:
Android 9.0 (API 28)+ blocks unencrypted HTTP traffic by default.
- P2P Copier includes `android:usesCleartextTraffic="true"` in `AndroidManifest.xml`.
- If compiling custom variants, verify that `android:usesCleartextTraffic="true"` is maintained or configure an explicit network security config under `res/xml/network_security_config.xml`.

---

## 3. Camera Scanner Not Launching (QR / OCR)

**Symptom**: Pressing the scan button fails or screen stays blank.

**Remediation**:
- Grant Camera permission in **Android Settings > Apps > P2P Copier > Permissions > Camera**.
- If running on an emulator, ensure virtual camera is set to "VirtualScene" or "Webcam0" in AVD settings.

---

## 4. Session Invalidated / Pairing Stuck

**Symptom**: App reports `Session Expired` or fails to retrieve files.

**Remediation**:
1. Open the app **Profile** tab.
2. Tap **Reset All App Data**.
3. Re-scan the QR code currently displayed on the desktop WebApp landing page.
