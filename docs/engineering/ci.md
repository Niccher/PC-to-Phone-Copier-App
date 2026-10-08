# Continuous Integration & Build Automation

This document details the Continuous Integration (CI) workflows, documentation linters, and automated Gradle build verification configured for the **P2P Copier Android App**.

---

## 1. CI Workflow Inventory

The repository uses GitHub Actions located in `.github/workflows/`:

| Workflow File | Trigger Events | Purpose | Execution Environment |
|---|---|---|---|
| [`.github/workflows/docs-verify.yml`](../../.github/workflows/docs-verify.yml) | Push & PR (`main`, `master`) touching `README.md`, `docs/**` | Documentation quality linter (line counts, link integrity, Mermaid syntax, secret detection) | `ubuntu-latest` (Python 3.12) |

---

## 2. Documentation Quality Linter (`docs-verify.yml`)

The documentation verification workflow validates that all engineering and user documents adhere to the `project-docs` v3.1 specification:

```yaml
name: Verify Documentation Quality

on:
  push:
    branches: [ main, master ]
    paths:
      - 'README.md'
      - 'docs/**'
  pull_request:
    paths:
      - 'README.md'
      - 'docs/**'

jobs:
  lint-docs:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: '3.12'

      - name: Run project-docs Quality Linter
        run: |
          python3 scripts/lint-docs.py .
```

---

## 3. Running Verification Locally

Before pushing commits or submitting a Pull Request:

### A. Run Documentation Quality Linter
```bash
python3 scripts/lint-docs.py .
```

### B. Run Android Unit Tests
```bash
./gradlew test
```

### C. Build Debug APK
```bash
./gradlew assembleDebug
```
