#!/usr/bin/env python3
"""Generate AetherVault mod textures from the *_meta.json specifications.

Style guide (tech-magic reference art):
  - Echo Vault: bronze-banded cylinder framing a swirling cyan/teal vortex portal
  - Lattice Anchor: deep indigo faces, glowing cyan runes, gold corner brackets
  - Familiar Carrier: glass dome head, bronze chassis, glowing cyan core, teal wings
  - Rune Program GUI: dark navy panels, glowing cyan nodes/edges, gold active state

Usage (from project root):
    python scripts/generate_textures.py
"""

import math
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
TEX_DIR = ROOT / "common" / "src" / "main" / "resources" / "assets" / "aethervault" / "textures"

# ---------------------------------------------------------------- palette ---
INDIGO = (25, 13, 63, 255)          # #190D3F
INDIGO_DARK = (13, 6, 37, 255)
INDIGO_LIGHT = (46, 31, 102, 255)
PURPLE_DEEP = (61, 43, 102, 255)    # #3D2B66
BRONZE = (160, 104, 43, 255)        # #A0682B
BRONZE_DARK = (92, 58, 30, 255)     # #5C3A1E
BRONZE_LIGHT = (201, 147, 43, 255)  # #C9932B
GOLD = (255, 215, 0, 255)           # #FFD700
GOLD_DIM = (184, 150, 12, 255)
TEAL = (0, 128, 128, 255)           # #008080
TEAL_LIGHT = (32, 160, 160, 255)
CYAN = (79, 227, 227, 255)          # #4FE3E3
CYAN_BRIGHT = (160, 245, 255, 255)  # #A0F5FF
CYAN_GLOW = (79, 227, 227, 120)
VORTEX_CORE = (8, 5, 22, 255)       # near-black swirl eye
NAVY = (13, 27, 42, 255)            # #0D1B2A GUI panel fill
NAVY_BORDER = (46, 143, 163, 255)   # dim cyan node border
GLASS = (127, 212, 232, 225)        # dome glass #7FD4E8


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


def vortex_px(px, x, y, cx, cy, core_r, swirl_scale=1.9, arms=3):
    """Paint one pixel of a swirling vortex portal."""
    dx, dy = x - cx, y - cy
    dist = math.hypot(dx, dy)
    ang = math.atan2(dy, dx)
    if dist < core_r:
        px[x, y] = VORTEX_CORE
        return
    swirl = math.sin(arms * ang + dist * swirl_scale)
    if swirl > 0.45:
        px[x, y] = CYAN if dist < core_r + 2.6 else TEAL
    elif swirl > 0.1:
        px[x, y] = TEAL if dist < core_r + 3.4 else PURPLE_DEEP
    elif swirl < -0.82:
        px[x, y] = PURPLE_DEEP
    else:
        px[x, y] = INDIGO


def corner_brackets(px, size=16):
    """Gold corner brackets with cyan gems (Lattice Anchor frame)."""
    for bx, by, dx, dy in [(1, 1, 1, 1), (size - 2, 1, -1, 1),
                           (1, size - 2, 1, -1), (size - 2, size - 2, -1, -1)]:
        for i in range(3):
            px[bx + dx * i, by] = BRONZE_LIGHT
            px[bx, by + dy * i] = BRONZE_LIGHT
        px[bx + dx, by + dy] = CYAN                    # gem
        px[bx, by] = GOLD                              # bright tip


# ------------------------------------------------------------- generators ---
def gen_echo_vault_face(rng):
    """Vortex portal face: swirling cyan/teal arms framed in bronze."""
    img = new_img(16)
    px = img.load()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or y in (0, 15):
                px[x, y] = BRONZE_DARK
            elif x in (1, 14) or y in (1, 14):
                px[x, y] = BRONZE
            else:
                vortex_px(px, x, y, cx, cy, core_r=1.7)
    # Frame rivets.
    for rx, ry in [(1, 1), (14, 1), (1, 14), (14, 14)]:
        px[rx, ry] = GOLD_DIM
    # Bright swirl sparkles.
    px[5, 5] = CYAN_BRIGHT
    px[10, 9] = CYAN_BRIGHT
    return img


def gen_echo_vault_top(rng):
    """Top band: bronze ring with rune ticks around a vortex opening."""
    img = new_img(16)
    noisy_fill(img, BRONZE, rng, amount=10)
    px = img.load()
    for i in range(16):
        px[i, 0] = px[i, 15] = BRONZE_DARK
        px[0, i] = px[15, i] = BRONZE_DARK
    # Engraved rune ticks on the band.
    for gx in (2, 5, 8, 11, 14):
        px[gx, 3] = px[gx, 4] = (58, 36, 16, 255)
        px[gx, 11] = px[gx, 12] = (58, 36, 16, 255)
    # Recessed vortex opening in the center.
    cx, cy = 7.5, 7.5
    for y in range(4, 12):
        for x in range(4, 12):
            dist = math.hypot(x - cx, y - cy)
            if dist < 4.3:
                vortex_px(px, x, y, cx, cy, core_r=1.4, swirl_scale=2.2)
            else:
                px[x, y] = BRONZE_DARK
    return img


def gen_echo_vault_bottom(rng):
    """Bottom band: bronze plate with rivets."""
    img = new_img(16)
    noisy_fill(img, BRONZE, rng, amount=8)
    px = img.load()
    for i in range(16):
        px[i, 0] = px[i, 15] = BRONZE_DARK
        px[0, i] = px[15, i] = BRONZE_DARK
    for rx, ry in [(3, 3), (12, 3), (3, 12), (12, 12)]:
        px[rx, ry] = BRONZE_DARK
        px[rx + 1, ry + 1] = BRONZE_LIGHT
    return img


def gen_lattice_anchor_face(rng):
    """Indigo face with central glowing rune and gold corner brackets."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    # Faint circuit traces.
    for y in range(16):
        for x in range(16):
            if (x + y) % 8 == 4 and 3 < x < 12 and 3 < y < 12:
                px[x, y] = INDIGO_LIGHT
    # Central rune glyph (raidho-like) in glowing cyan.
    rune = [(7, 4), (7, 5), (7, 6), (7, 7), (7, 8), (7, 9), (7, 10), (7, 11),
            (8, 4), (9, 5), (9, 6), (8, 7),
            (8, 8), (9, 9), (9, 10), (8, 11)]
    for rx, ry in rune:
        px[rx, ry] = CYAN
    px[7, 6] = CYAN_BRIGHT
    corner_brackets(px)
    return img


def gen_lattice_anchor_top(rng):
    """Top surface: cyan rune circle with gold ticks and corner brackets."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng)
    px = img.load()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            dist = math.hypot(x - cx, y - cy)
            if abs(dist - 4.6) < 0.55:
                px[x, y] = CYAN
            elif abs(dist - 4.6) < 0.95:
                px[x, y] = INDIGO_LIGHT
    # Glyph ticks at the compass points.
    for tx, ty in [(7, 2), (8, 2), (7, 13), (8, 13),
                   (2, 7), (2, 8), (13, 7), (13, 8)]:
        px[tx, ty] = GOLD_DIM
    # Glowing core.
    px[7, 7] = CYAN_BRIGHT
    px[8, 7] = px[7, 8] = px[8, 8] = CYAN
    corner_brackets(px)
    return img


def gen_lattice_anchor_bottom(rng):
    """Base: quiet deep indigo with corner brackets."""
    img = new_img(16)
    noisy_fill(img, INDIGO, rng, amount=6)
    px = img.load()
    for _ in range(10):
        x, y = rng.randrange(16), rng.randrange(16)
        px[x, y] = INDIGO_DARK
    corner_brackets(px)
    return img


def gen_familiar_body(rng):
    """32x32 Familiar Carrier: glass dome, bronze chassis, cyan core, wings."""
    img = new_img(32)
    px = img.load()
    # Glass dome head (rounded).
    for y in range(1, 11):
        for x in range(8, 24):
            if (x < 11 or x > 20) and y < 4:
                continue
            shade = rng.randint(-8, 8)
            px[x, y] = (min(255, 127 + shade), min(255, 212 + shade),
                        min(255, 232 + shade), 225)
    for x in range(8, 24):                       # dome rim
        px[x, 10] = (60, 140, 170, 235)
    px[11, 3] = px[12, 3] = (235, 252, 255, 240)  # glass highlight
    px[11, 4] = (200, 240, 250, 235)
    # Bronze chassis (tapered).
    for y in range(11, 28):
        inset = 6 if y < 24 else 8
        for x in range(inset, 32 - inset):
            shade = rng.randint(-10, 10)
            px[x, y] = (min(255, max(0, 139 + shade)),
                        min(255, max(0, 90 + shade)),
                        min(255, max(0, 43 + shade)), 255)
    for y in (13, 14):                           # dark seam band
        for x in range(6, 26):
            px[x, y] = BRONZE_DARK
    # Glowing cyan core/eye with halo.
    for gx, gy in [(14, 18), (17, 18), (14, 19), (17, 19),
                   (15, 17), (16, 17), (15, 20), (16, 20)]:
        px[gx, gy] = (40, 160, 170, 255)
    for cx, cy in [(15, 18), (16, 18), (15, 19), (16, 19)]:
        px[cx, cy] = CYAN
    px[15, 18] = (210, 255, 255, 255)
    # Teal wings (diagonal strokes on both flanks).
    for i in range(7):
        lx, rx = max(0, 5 - i), min(31, 26 + i)
        px[lx, 12 + i] = TEAL
        px[max(0, lx - 1), 12 + i] = TEAL_LIGHT if i % 2 else TEAL
        px[rx, 12 + i] = TEAL
        px[min(31, rx + 1), 12 + i] = TEAL_LIGHT if i % 2 else TEAL
    # Feet.
    for lx in (10, 11, 20, 21):
        px[lx, 28] = BRONZE_DARK
        px[lx, 29] = (60, 38, 20, 255)
    return img


def gen_familiar_aura(rng):
    """32x32 aura overlay: soft radial cyan glow with gold motes."""
    img = new_img(32)
    px = img.load()
    cx, cy = 15.5, 15.5
    for y in range(32):
        for x in range(32):
            dist = math.hypot(x - cx, y - cy)
            alpha = int(max(0, 200 - dist * 10))
            if alpha > 0:
                px[x, y] = (79, 227, 227, alpha)
    for mx, my in [(15, 14), (17, 16), (14, 17), (16, 13), (13, 15), (18, 14)]:
        px[mx, my] = (255, 215, 0, 220)
    return img


def _node_shape(px, border, inner_edge, fill, size=16):
    """Paint a rounded-square node body."""
    for y in range(size):
        for x in range(size):
            corner = (x < 2 or x > size - 3) and (y < 2 or y > size - 3)
            if corner:
                continue                          # transparent rounded corner
            edge = x in (0, size - 1) or y in (0, size - 1)
            inner = x in (1, size - 2) or y in (1, size - 2)
            if edge:
                px[x, y] = border
            elif inner:
                px[x, y] = inner_edge
            else:
                px[x, y] = fill


def gen_node_base(rng):
    """Standard node: dark navy panel with cyan border glow."""
    img = new_img(16)
    px = img.load()
    _node_shape(px, NAVY_BORDER, (30, 74, 95, 255), NAVY)
    px[3, 2] = px[4, 2] = CYAN                   # top-left sheen
    return img


def gen_active_node(rng):
    """Active node: gold highlight frame with cyan energy core."""
    img = new_img(16)
    px = img.load()
    _node_shape(px, GOLD_DIM, GOLD, NAVY)
    for cx, cy in [(7, 7), (8, 7), (7, 8), (8, 8)]:
        px[cx, cy] = CYAN
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
    """Connecting edge: glowing cyan conduit."""
    return _edge_line(CYAN, CYAN_GLOW)


def gen_inactive_edge(rng):
    """Inactive edge: dormant dim strand."""
    return _edge_line((42, 58, 85, 255))


def gen_item_rune_orb(rng):
    """16x16 item sprite: glowing cyan orb with a gold rune ring."""
    img = new_img(16)
    px = img.load()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            dist = math.hypot(x - cx, y - cy)
            if dist < 2.0:
                px[x, y] = CYAN_BRIGHT                      # hot core
            elif dist < 4.6:
                shade = rng.randint(-14, 14)
                px[x, y] = (min(255, 79 + shade), min(255, 227 + shade),
                            min(255, 227 + shade), 255)     # orb body
            elif dist < 5.4:
                px[x, y] = GOLD_DIM                         # rune ring
    # Gold ring pips at the compass points.
    for tx, ty in [(7, 2), (8, 2), (7, 13), (8, 13), (2, 7), (2, 8), (13, 7), (13, 8)]:
        px[tx, ty] = GOLD
    # Sparkles.
    px[5, 5] = (235, 252, 255, 255)
    px[10, 9] = (235, 252, 255, 255)
    return img


def gen_item_rune_tablet(rng):
    """16x16 item sprite: purple/bronze tablet with a cyan rune screen."""
    img = new_img(16)
    px = img.load()
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or y in (0, 15):
                px[x, y] = BRONZE_DARK
            elif x in (1, 14) or y in (1, 14):
                px[x, y] = BRONZE_LIGHT
            elif x in (2, 13) or y in (2, 13):
                px[x, y] = PURPLE_DEEP
            else:
                px[x, y] = VORTEX_CORE                      # dark screen
    # Cyan rune glyph on the screen (simplified raidho).
    rune = [(7, 5), (7, 6), (7, 7), (7, 8), (7, 9), (7, 10),
            (8, 5), (9, 6), (9, 7), (8, 8), (8, 9), (9, 10)]
    for rx, ry in rune:
        px[rx, ry] = CYAN
    px[7, 6] = CYAN_BRIGHT
    # Corner gems.
    for gx, gy in [(2, 2), (13, 2), (2, 13), (13, 13)]:
        px[gx, gy] = GOLD
    return img


# ------------------------------------------------------------------- main ---
GENERATORS = {
    "block/echo_vault_face.png": gen_echo_vault_face,
    "block/echo_vault_top.png": gen_echo_vault_top,
    "block/echo_vault_bottom.png": gen_echo_vault_bottom,
    "block/lattice_anchor_face.png": gen_lattice_anchor_face,
    "block/lattice_anchor_top.png": gen_lattice_anchor_top,
    "block/lattice_anchor_bottom.png": gen_lattice_anchor_bottom,
    "entity/familiar_body.png": gen_familiar_body,
    "entity/familiar_aura.png": gen_familiar_aura,
    "gui/node_base.png": gen_node_base,
    "gui/active_node.png": gen_active_node,
    "gui/edge_line.png": gen_edge_line,
    "gui/inactive_edge.png": gen_inactive_edge,
    "item/rune_orb.png": gen_item_rune_orb,
    "item/rune_program_tablet.png": gen_item_rune_tablet,
}


def main():
    print("Generating AetherVault textures (tech-magic reference style)...")
    for rel_path, gen in sorted(GENERATORS.items()):
        rng = random.Random(f"aethervault::{rel_path}")   # deterministic output
        save(gen(rng), rel_path)
    print(f"Done: {len(GENERATORS)} textures written to {TEX_DIR}")


if __name__ == "__main__":
    main()