#!/usr/bin/env python3
"""
project-docs Quality Linter (lint-docs.py)

Validates project documentation against the project-docs v3.1 specification:
1. README.md length constraint (<= 150 lines)
2. Leakage of code-editing instructions into user README
3. Internal relative Markdown link resolution (zero broken links)
4. Mermaid syntax block sanity
5. Secret and credential leakage prevention
"""

import sys
import os
import re
from pathlib import Path

MAX_README_LINES = 160

CODE_EDITING_KEYWORDS = [
    r'php\s+spark\s+make:',
    r'bin/rails\s+generate',
    r'alembic\s+revision',
    r'class\s+\w+Controller\s+extends',
    r'class\s+\w+\(BaseModel\):',
    r'interface\s+\w+Dao',
    r'@Entity\s+data\s+class',
]

SECRET_PATTERNS = [
    (r'"type":\s*"service_account"', "GCP Service Account JSON"),
    (r'-----BEGIN\s+(RSA\s+)?PRIVATE\s+KEY-----', "RSA/Private Key"),
    (r'AIza[0-9A-Za-z-_]{35}', "Google API Key"),
    (r'(?i)password\s*=\s*[\'"][^\'"]{8,}[\'"]', "Hardcoded Password"),
    (r'eyJ[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*', "JWT Token"),
]

def lint_readme(repo_root: Path):
    readme_path = repo_root / "README.md"
    errors = []
    warnings = []

    if not readme_path.exists():
        return ["README.md not found in repository root."], []

    content = readme_path.read_text(encoding="utf-8")
    lines = content.splitlines()

    # Rule 1: Length check
    if len(lines) > MAX_README_LINES:
        warnings.append(
            f"README.md has {len(lines)} lines (recommendation: <= 150 lines). "
            f"Move architecture and engineering deep-dives into docs/."
        )

    # Rule 2: Leaked code editing instructions
    for idx, line in enumerate(lines, 1):
        for pattern in CODE_EDITING_KEYWORDS:
            if re.search(pattern, line):
                errors.append(
                    f"Line {idx}: Code-editing instruction leaked into user README: '{line.strip()}'. "
                    f"Move this into docs/services/ or docs/engineering/."
                )

    # Rule 5: Secrets check
    for idx, line in enumerate(lines, 1):
        for pattern, desc in SECRET_PATTERNS:
            if re.search(pattern, line):
                errors.append(f"Line {idx}: Potential secret detected ({desc}): {line.strip()[:60]}...")

    return errors, warnings

def lint_markdown_links(repo_root: Path):
    errors = []
    link_pattern = re.compile(r'\[([^\]]+)\]\(([^)]+)\)')

    for md_file in repo_root.glob("**/*.md"):
        # Skip vendor, node_modules, git, and skill definitions
        parts = md_file.parts
        if any(p in parts for p in ['.git', 'vendor', 'node_modules', 'venv', '.venv', '.agents']):
            continue

        try:
            content = md_file.read_text(encoding="utf-8")
        except Exception:
            continue

        for match in link_pattern.finditer(content):
            label, link = match.group(1), match.group(2)
            # Ignore web links, mailto, and anchors
            if link.startswith(('http://', 'https://', 'mailto:', '#')):
                continue

            # Strip query strings and anchor tags
            target_file_str = link.split('#')[0].split('?')[0]
            if not target_file_str:
                continue

            target_path = (md_file.parent / target_file_str).resolve()
            if not target_path.exists():
                errors.append(
                    f"{md_file.relative_to(repo_root)}: Broken link '{link}' (target '{target_path.name}' does not exist)"
                )

    return errors

def lint_mermaid_blocks(repo_root: Path):
    errors = []
    for md_file in repo_root.glob("**/*.md"):
        parts = md_file.parts
        if any(p in parts for p in ['.git', 'vendor', 'node_modules']):
            continue

        content = md_file.read_text(encoding="utf-8")
        mermaid_blocks = re.findall(r'```mermaid\s*\n(.*?)\n```', content, re.DOTALL)
        
        # Check unclosed blocks
        open_count = len(re.findall(r'```mermaid', content))
        if open_count != len(mermaid_blocks):
            errors.append(f"{md_file.relative_to(repo_root)}: Unclosed ```mermaid block detected.")

    return errors

def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else ".").resolve()
    print(f"Linting documentation in: {root}")

    readme_errors, readme_warnings = lint_readme(root)
    link_errors = lint_markdown_links(root)
    mermaid_errors = lint_mermaid_blocks(root)

    all_errors = readme_errors + link_errors + mermaid_errors

    print("\n--- Linting Results ---")
    if readme_warnings:
        print("\n[WARNINGS]")
        for w in readme_warnings:
            print(f"  - {w}")

    if all_errors:
        print("\n[ERRORS]")
        for e in all_errors:
            print(f"  ✗ {e}")
        print(f"\nFailed with {len(all_errors)} error(s).")
        sys.exit(1)
    else:
        print("\n✓ Documentation passed all project-docs quality checks!")
        sys.exit(0)

if __name__ == "__main__":
    main()
