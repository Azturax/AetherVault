#!/usr/bin/env python3
"""Validate AetherVault asset JSONs, texture references, and meta requirements."""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "aethervault"
RESOURCES = ROOT / "src" / "main" / "resources"

errors = []

# 1. pack.mcmeta exists and parses.
pack_mcmeta = RESOURCES / "pack.mcmeta"
try:
    json.loads(pack_mcmeta.read_text(encoding="utf-8"))
    print("PACK OK: pack.mcmeta")
except (OSError, json.JSONDecodeError) as exc:
    errors.append(f"pack.mcmeta: missing or invalid ({exc})")

# 2. All model JSONs parse and their texture refs resolve to PNG files.
for model_path in sorted((ASSETS / "models" / "entity").glob("*.json")):
    try:
        model = json.loads(model_path.read_text(encoding="utf-8"))
        print(f"MODEL OK: {model_path.name}")
    except json.JSONDecodeError as exc:
        errors.append(f"{model_path.name}: invalid JSON ({exc})")
        continue
    for key, ref in model.get("textures", {}).items():
        _, _, rel = ref.partition(":")
        tex_file = ASSETS / "textures" / f"{rel}.png"
        if not tex_file.exists():
            errors.append(f"{model_path.name}: texture '{key}' -> {ref} missing ({tex_file.name})")
        else:
            print(f"  tex OK: {key} -> {ref}")

# 3. All meta JSONs parse and every required asset exists.
for meta_path in sorted((ASSETS / "textures").glob("*_meta.json")):
    try:
        meta = json.loads(meta_path.read_text(encoding="utf-8"))
        print(f"META OK: {meta_path.name}")
    except json.JSONDecodeError as exc:
        errors.append(f"{meta_path.name}: invalid JSON ({exc})")
        continue
    required = meta.get("required_textures", []) + meta.get("required_assets", [])
    for entry in required:
        name = entry.get("name", "")
        candidates = [
            ASSETS / "textures" / name,               # textures (png/gui/block/entity)
            ASSETS / "models" / "entity" / name,      # model definitions
        ]
        if any(c.exists() for c in candidates):
            print(f"  required OK: {name}")
        else:
            errors.append(f"{meta_path.name}: required asset '{name}' not found")

if errors:
    print("\nERRORS:")
    for e in errors:
        print(" -", e)
    raise SystemExit(1)
print("\nAll asset checks passed.")