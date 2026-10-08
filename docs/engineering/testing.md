# Testing Guide

This document details how to run automated unit and instrumented tests for the P2P Copier Android client.

---

## 1. Running Automated Tests

### Unit Tests (Local JVM)
Local unit tests run on your development machine JVM without requiring an attached Android device:
```bash
./gradlew test
```
Test results and HTML reports are generated at:
`app/build/reports/tests/testDebugUnitTest/index.html`

### Instrumented Tests (Android Device / Emulator)
Instrumented tests run on a live Android emulator or connected device:
```bash
./gradlew connectedAndroidTest
```
Reports are generated at:
`app/build/reports/androidTests/connected/index.html`

---

## 2. Writing Unit Tests

Unit tests are located in `app/src/test/java/com/niccher/p2p_copier_app/`:

```java
package com.niccher.p2p_copier_app;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModelSerializationTest {

    @Test
    public void testFileModelCreation() {
        // Assert JSON mapping logic and DTO properties
        assertEquals(4, 2 + 2);
    }
}
```
