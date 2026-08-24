#!/usr/bin/env python3
"""Generate AetherVault mod textures from the *_meta.json specifications.

Theme: Crystalline lattice - Indigo (#190D3F) / Teal (#008080) / Gold (#FFD700).

Usage (from project root):
    python scripts/generate_textures.py
"""

import math
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
TEX_DIR = ROOT / "src" / "main" / "resources" / "assets" / "aethervault" / "textures"

# ---------------------------------------------------------------- palette ---
INDIGO = (25, 13, 63, 255)          # #190D3F
INDIGO_DARK = (13, 6, 37, 255)
INDIGO_LIGHT = (46, 31, 102, 255)
INDIGO_HIGHLIGHT = (69, 48, 127, 255)
TEAL = (0, 128, 128, 255)           # #008080
TEAL_LIGHT = (32, 160, 160, 255)
TEAL_GLOW = (64, 200, 200, 170)
GOLD = (255, 215, 0, 255)           # #FFD700
GOLD_DIM = (184, 150, 12, 255)


# ---------------------------------------------------------------- helpers ---
def noisy_fill(img, base, rng, amount=8):
    """Fill the image with base color plus per-pixel brightness jitter."""
    w, h = img.size
    px = img.load()
    for y in range(h):
        for x in range(w):
            d = rng.randint(-amount, amount)
            r, g, b, a = base
            px[x, y] = (min(255, max(0, r + d)),
                        min(255, max(0, g + d)),
                        min(255, max(0, b + d)), a)


def new_img(size):
    return Image.new("RGBA", (size, size), (0, 0, 0, 0))


def save(img, rel_path):
    out = TEX_DIR / rel_path
    out.parent.mkdir(parents=True, exist_ok=True)
    img.save(out)
    print(f"  wrote {out.relative_to(ROOT)}")


# ------------------------------------------------------------- generators ---
def gen_echo_vault_face(rng):
    """Main temporal face: swirling teal energy over deep indigo."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    # Two horizontal sine-wave energy streams in teal.
    for x in range(16):
        y1 = int(4 + 2 * math.sin(x / 2.5)) % 16
        y2 = int(11 + 2 * math.sin(x / 2.5 + math.pi)) % 16
        px[x, y1] = TEAL
        px[x, y2] = TEAL
        if x % 4 == 1:
            px[x, y1] = TEAL_LIGHT
        if x % 5 == 2:
            px[x, y2] = TEAL_LIGHT
    # Gold sparkles along the flows.
    for sx, sy in [(3, 4), (8, 3), (12, 5), (5, 11), (11, 12)]:
        px[sx, sy] = GOLD
    return img


def gen_echo_vault_top(rng):
    """Top surface: concentric gold echo rings."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            dist = math.hypot(x - cx, y - cy)
            if abs((dist % 4) - 2) < 0.85:
                px[x, y] = GOLD_DIM
            elif abs((dist % 4) - 2) < 1.05:
                px[x, y] = INDIGO_LIGHT
    # Bright gold pings on the rings.
    for sx, sy in [(7, 3), (12, 7), (7, 12), (2, 8)]:
        px[sx, sy] = GOLD
    return img


def gen_deep_indigo_base(rng):
    """Shared bottom/base texture: quiet deep indigo."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng, amount=6)
    px = img.load()
    for _ in range(10):
        x, y = rng.randrange(16), rng.randrange(16)
        px[x, y] = INDIGO_DARK
    return img


def gen_lattice_anchor_face(rng):
    """Crystalline face: diagonal teal lattice with gold nodes."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    for y in range(16):
        for x in range(16):
            diag_a = (x + y) % 8 == 0
            diag_b = (x - y) % 8 == 0
            if diag_a and diag_b:
                px[x, y] = GOLD          # lattice node
            elif diag_a or diag_b:
                px[x, y] = TEAL          # lattice strand
    # Inner glow accents beside strands.
    for sx, sy in [(4, 3), (11, 4), (3, 11), (12, 12)]:
        px[sx, sy] = TEAL_LIGHT
    return img


def gen_lattice_anchor_top(rng):
    """Top surface: glowing lattice grid, brightest at center."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    for y in range(16):
        for x in range(16):
            if x % 4 == 0 or y % 4 == 0:
                px[x, y] = TEAL
    for y in range(0, 16, 4):
        for x in range(0, 16, 4):
            px[x, y] = GOLD_DIM
    # Central crystal cluster glow.
    for cx, cy in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        px[cx, cy] = GOLD
    for sx, sy in [(6, 7), (9, 7), (7, 6), (7, 9), (8, 6), (8, 9), (6, 8), (9, 8)]:
        px[sx, sy] = TEAL_LIGHT
    return img


def gen_familiar_body(rng):
    """32x32 entity body: deep indigo hide with magical highlights."""
    img = new_img(32)
    noisy_fill(img, INDIGO, rng, amount=9)
    px = img.load()
    # Subtle darker underside gradient.
    for y in range(24, 32):
        for x in range(32):
            r, g, b, a = px[x, y]
            px[x, y] = (max(0, r - 6), max(0, g - 6), max(0, b - 6), a)
    # Magical speckles.
    for _ in range(46):
        x, y = rng.randrange(32), rng.randrange(32)
        px[x, y] = INDIGO_HIGHLIGHT
    for _ in range(18):
        x, y = rng.randrange(32), rng.randrange(32)
        px[x, y] = TEAL
    for _ in range(8):
        x, y = rng.randrange(32), rng.randrange(32)
        px[x, y] = GOLD
    return img


def gen_familiar_aura(rng):
    """32x32 aura overlay: soft radial teal glow with gold motes."""
    img = new_img(32)
    px = img.load()
    cx, cy = 15.5, 15.5
    for y in range(32):
        for x in range(32):
            dist = math.hypot(x - cx, y - cy)
            alpha = int(max(0, 190 - dist * 11))
            if alpha > 0:
                px[x, y] = (0, 128, 128, alpha)
    # Gold motes drifting near the core.
    for mx, my in [(15, 14), (17, 16), (14, 17), (16, 13), (13, 15), (18, 14)]:
        px[mx, my] = (255, 215, 0, 220)
    return img


def _node_shape(px, size=16):
    """Paint a rounded-square node body; returns list of interior pixels."""
    interior = []
    for y in range(size):
        for x in range(size):
            corner = (x < 2 or x > size - 3) and (y < 2 or y > size - 3)
            if corner:
                continue                      # transparent rounded corner
            edge = x in (0, size - 1) or y in (0, size - 1)
            inner_edge = x in (1, size - 2) or y in (1, size - 2)
            if edge:
                px[x, y] = INDIGO_DARK
            elif inner_edge:
                px[x, y] = INDIGO_LIGHT
            else:
                px[x, y] = INDIGO
                interior.append((x, y))
    return interior


def gen_node_base(rng):
    """Standard rune-program node: indigo rounded square."""
    img = new_img(16)
    px = img.load()
    _node_shape(px)
    # Top-left sheen.
    for x in range(3, 8):
        px[x, 2] = INDIGO_HIGHLIGHT
    return img


def gen_active_node(rng):
    """Active node state: gold-highlighted frame with teal core."""
    img = new_img(16)
    px = img.load()
    interior = _node_shape(px)
    # Recolor frame edges to gold.
    for y in range(16):
        for x in range(16):
            c = px[x, y]
            if c == INDIGO_DARK:
                px[x, y] = GOLD_DIM
            elif c == INDIGO_LIGHT:
                px[x, y] = GOLD
    # Teal energy core.
    for cx, cy in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        px[cx, cy] = TEAL_LIGHT
    return img


def _edge_line(core_color, glow_color=None):
    img = new_img(16)
    px = img.load()
    for x in range(16):
        px[x, 7] = core_color
        px[x, 8] = core_color
        if glow_color:
            px[x, 6] = glow_color
            px[x, 9] = glow_color
    return img


def gen_edge_line(rng):
    """Connecting edge: glowing teal conduit."""
    return _edge_line(TEAL, TEAL_GLOW)


def gen_inactive_edge(rng):
    """Inactive edge: dormant dim strand."""
    return _edge_line((42, 74, 74, 255))


# ------------------------------------------------------------------- main ---
GENERATORS = {
    "block/echo_vault_face.png": gen_echo_vault_face,
    "block/echo_vault_top.png": gen_echo_vault_top,
    "block/echo_vault_bottom.png": gen_deep_indigo_base,
    "block/lattice_anchor_face.png": gen_lattice_anchor_face,
    "block/lattice_anchor_top.png": gen_lattice_anchor_top,
    "block/lattice_anchor_bottom.png": gen_deep_indigo_base,
    "entity/familiar_body.png": gen_familiar_body,
    "entity/familiar_aura.png": gen_familiar_aura,
    "gui/node_base.png": gen_node_base,
    "gui/active_node.png": gen_active_node,
    "gui/edge_line.png": gen_edge_line,
    "gui/inactive_edge.png": gen_inactive_edge,
}


def main():
    print("Generating AetherVault textures...")
    for i, (rel_path, gen) in enumerate(sorted(GENERATORS.items())):
        rng = random.Random(f"aethervault::{rel_path}")   # deterministic output
        save(gen(rng), rel_path)
    print(f"Done: {len(GENERATORS)} textures written to {TEX_DIR}")


if __name__ == "__main__":
    main()