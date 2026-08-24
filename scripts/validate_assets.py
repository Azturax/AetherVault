#!/usr/bin/env python3
"""Validate AetherVault asset JSONs and texture references."""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "aethervault"

errors = []

# 1. All model JSONs parse and their texture refs resolve to PNG files.
for model_path in sorted((ASSETS / "models" / "entity").glob("*.json")):
    try:
        model = json.loads(model_path.read_text(encoding="utf-8"))
        print(f"MODEL OK: {model_path.name}")
    except json.JSONDecodeError as exc:
        errors.append(f"{model_path.name}: invalid JSON ({exc})")
        continue
    for key, ref in model.get("textures", {}).items():
        namespace, _, rel = ref.partition(":")
        tex_file = ASSETS / "textures" / f"{rel}.png"
        if not tex_file.exists():
            errors.append(f"{model_path.name}: texture '{key}' -> {ref} missing ({tex_file.name})")
        else:
            print(f"  tex OK: {key} -> {ref}")

# 2. All meta JSONs parse.
for meta_path in sorted((ASSETS / "textures").glob("*_meta.json")):
    try:
        json.loads(meta_path.read_text(encoding="utf-8"))
        print(f"META OK: {meta_path.name}")
    except json.JSONDecodeError as exc:
        errors.append(f"{meta_path.name}: invalid JSON ({exc})")

if errors:
    print("\nERRORS:")
    for e in errors:
        print(" -", e)
    raise SystemExit(1)
print("\nAll asset checks passed.")