# AetherVault

A Minecraft mod that introduces aetheric storage systems and a visual rune-programming framework.

## Features

- **Echo Vault** – A bronze-banded storage block entity with a swirling temporal vortex, supporting temporal snapshots of its contents.
- **Lattice Anchor** – A spatial block entity with glowing cyan runes and gold corner brackets, anchoring bounded lattice regions.
- **Rune Programming** – A node-based visual programming system (`InputNode`, `FilterNode`, `OutputNode`) evaluated by `FlowEvaluator`, with conditions such as item tags and durability thresholds.
- **Familiar Carrier** – A construct companion entity (glass dome, bronze chassis, glowing cyan core, teal wings) driven by compiled rune programs.
- **Rune Orb & Rune Chips** – Items representing portable programs and instructions.

## Supported Versions

| Minecraft | NeoForge | Fabric | Status |
|-----------|----------|--------|--------|
| 1.21.1    | ✅ (`neoforge-1.21.1`) | ✅ (`fabric-1.21.1`) | Supported — last 1.x-scheme version |
| 26.x      | ✅ (`neoforge-26.x`)   | ✅ (`fabric-26.x`)   | Prepared — pin artifacts in `gradle.properties` when the 26.x line publishes |

## Project Structure

```
common/               # Shared, loader-agnostic code and assets
└── src/main/
    ├── java/com/aethervault/
    │   ├── core/       # Core interfaces (IAetherStorage) and items (RuneOrb)
    │   ├── entities/   # Entities (FamiliarEntity, SimpleInventory)
    │   ├── gui/        # Node graph editor (nodes, canvas, serializer)
    │   ├── logic/      # Rune programs, flow evaluation, conditions
    │   └── storage/    # Echo Vault and Lattice Anchor block entities
    └── resources/      # pack.mcmeta, models, textures
neoforge-1.21.1/      # NeoForge build for Minecraft 1.21.1
fabric-1.21.1/        # Fabric build for Minecraft 1.21.1
neoforge-26.x/        # NeoForge build for the Minecraft 26.x line
fabric-26.x/          # Fabric build for the Minecraft 26.x line
scripts/              # Texture generation and asset validation tooling
```

Each platform module compiles the shared `common/` sources against its own loader
and Minecraft artifacts, and contributes its loader entry point
(`com.aethervault.neoforge.AetherVaultNeoForge` / `com.aethervault.fabric.AetherVaultFabric`).

## Building

Requires JDK 21 (auto-provisioned via Gradle toolchains).

```bash
# Build every platform
./gradlew build

# Build a single platform
./gradlew :neoforge-1.21.1:build
./gradlew :fabric-1.21.1:build

# Run a development client (1.21.1)
./gradlew :neoforge-1.21.1:runClient
./gradlew :fabric-1.21.1:runClient
```

Output jars land in each module's `build/libs/`.

> **26.x note:** the 26.x modules are structurally complete; pin the exact
> Minecraft/NeoForge/Fabric versions in `gradle.properties` once the 26.x line
> publishes stable artifacts.

## Asset Tooling

```bash
python scripts/generate_textures.py   # regenerate all textures (deterministic)
python scripts/validate_assets.py     # validate models, metas, texture refs
```

## Status

Work in progress — registration logic and block implementations are being built out on top of the multiloader skeleton.

## License

All rights reserved unless otherwise specified.