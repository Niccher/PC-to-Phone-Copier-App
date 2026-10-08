# Developer Troubleshooting Guide

Solutions for development, compilation, and Android tooling issues in P2P Copier App.

---

## 1. Room Annotation Processor or KSP Compilation Errors

**Symptom**: `Cannot find implementation for Database_Impl` or compilation errors involving Room schemas.

**Remediation**:
Clean the Gradle build cache and recompile:
```bash
./gradlew clean --no-build-cache
./gradlew assembleDebug
```

---

## 2. Gradle JVM / JDK Compatibility

**Symptom**: `Unsupported class file major version` or Gradle daemon fails to start.

**Remediation**:
AGP 8.6.0 requires JDK 17 or 21 to run the Gradle build tool.
Ensure `JAVA_HOME` points to a compatible JDK:
```bash
export JAVA_HOME=/path/to/android-studio/jbr
./gradlew --version
```

---

## 3. ADB Daemon Port Conflict / Device Offline

**Symptom**: `adb: error: cannot connect to daemon` or device is stuck in `offline` state.

**Remediation**:
Restart the ADB server:
```bash
adb kill-server
adb start-server
adb devices
```
If using USB reverse forwarding:
```bash
adb reverse --remove-all
adb reverse tcp:9004 tcp:9004
```
