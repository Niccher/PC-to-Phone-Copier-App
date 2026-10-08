# Contributing Guidelines

This document outlines the contribution workflow, branch conventions, and quality standards for the P2P Copier Android client.

---

## 1. Branching Strategy

- **`main`**: Production-ready branch.
- **`feature/<name>`**: New screens, activities, viewmodels, or integrations.
- **`bugfix/<name>`**: Bug fixes and patches.
- **`chore/<name>`**: Dependencies, Gradle updates, and documentation.

---

## 2. Contribution Workflow

1. Fork or clone the repository:
   ```bash
   git checkout -b feature/my-new-screen
   ```
2. Make your modifications adhering to Android architectural guidelines.
3. Ensure no hardcoded strings in layout XML (use `res/values/strings.xml`).
4. Validate documentation with the `project-docs` linter:
   ```bash
   python scripts/lint-docs.py .
   ```
5. Ensure `./gradlew assembleDebug` and `./gradlew test` pass.
6. Commit with clear, descriptive commit messages:
   ```bash
   git commit -m "feat(ui): add badge indicator to uploaded cards"
   ```
7. Push your branch and submit a Pull Request against `main`.
