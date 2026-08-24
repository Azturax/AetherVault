# AetherVault

A Minecraft Forge mod that introduces aetheric storage systems and a visual rune-programming framework.

## Features

- **Echo Vault** – A storage block entity supporting temporal snapshots of its contents.
- **Lattice Anchor** – A spatial block entity for anchoring bounded lattice regions.
- **Rune Programming** – A node-based visual programming system (`InputNode`, `FilterNode`, `OutputNode`) evaluated by `FlowEvaluator`, with conditions such as item tags and durability thresholds.
- **Familiar Entity** – A companion entity driven by compiled rune programs.
- **Rune Orb & Rune Chips** – Items representing portable programs and instructions.

## Project Structure

```
src/main/java/com/aethervault/
├── core/       # Core interfaces (IAetherStorage) and items (RuneOrb)
├── entities/   # Entities (FamiliarEntity, SimpleInventory)
├── gui/        # Node graph editor (nodes, canvas, serializer)
├── logic/      # Rune programs, flow evaluation, conditions
└── storage/    # Echo Vault and Lattice Anchor block entities
```

## Status

Work in progress — the mod skeleton is under active development; registration logic and block implementations are being built out.

## License

All rights reserved unless otherwise specified.