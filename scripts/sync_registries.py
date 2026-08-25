#!/usr/bin/env python3
"""Rewrite platform registry classes (NeoForge + Fabric) with Familiar registration,
then mirror them into the 26.x modules."""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

NEOFORGE_REGISTRY = '''package com.aethervault.neoforge;

import java.util.function.Supplier;

import com.aethervault.AetherVault;
import com.aethervault.block.EchoVaultBlock;
import com.aethervault.block.LatticeAnchorBlock;
import com.aethervault.core.RuneOrbItem;
import com.aethervault.core.RuneProgramTabletItem;
import com.aethervault.entities.FamiliarEntity;
import com.aethervault.storage.echo.EchoVaultBlockEntity;
import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * NeoForge registry wiring for all AetherVault blocks, items, entities, and block entity types.
 */
public final class ModRegistry {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AetherVault.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AetherVault.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AetherVault.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AetherVault.MOD_ID);

    // --- Entities ---

    public static final Supplier<EntityType<FamiliarEntity>> FAMILIAR =
            ENTITY_TYPES.register("familiar", () -> EntityType.Builder.of(FamiliarEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F)
                    .build("familiar"));

    // --- Block entity types (reference their blocks lazily) ---

    public static final Supplier<BlockEntityType<EchoVaultBlockEntity>> ECHO_VAULT_TYPE =
            BLOCK_ENTITIES.register("echo_vault", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new EchoVaultBlockEntity(ECHO_VAULT_TYPE.get(), pos),
                    ECHO_VAULT.get()).build(null));

    public static final Supplier<BlockEntityType<LatticeAnchorBlockEntity>> LATTICE_ANCHOR_TYPE =
            BLOCK_ENTITIES.register("lattice_anchor", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new LatticeAnchorBlockEntity(LATTICE_ANCHOR_TYPE.get(), pos),
                    LATTICE_ANCHOR.get()).build(null));

    // --- Blocks ---

    public static final DeferredBlock<EchoVaultBlock> ECHO_VAULT =
            BLOCKS.register("echo_vault", () -> new EchoVaultBlock(ECHO_VAULT_TYPE,
                    BlockBehaviour.Properties.of().strength(3.5f, 6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<LatticeAnchorBlock> LATTICE_ANCHOR =
            BLOCKS.register("lattice_anchor", () -> new LatticeAnchorBlock(LATTICE_ANCHOR_TYPE,
                    BlockBehaviour.Properties.of().strength(3.0f, 6.0f).requiresCorrectToolForDrops()));

    // --- Items ---

    public static final DeferredItem<BlockItem> ECHO_VAULT_ITEM =
            ITEMS.registerBlockItem("echo_vault", ECHO_VAULT);

    public static final DeferredItem<BlockItem> LATTICE_ANCHOR_ITEM =
            ITEMS.registerBlockItem("lattice_anchor", LATTICE_ANCHOR);

    public static final DeferredItem<RuneOrbItem> RUNE_ORB =
            ITEMS.register("rune_orb", () -> new RuneOrbItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<RuneProgramTabletItem> RUNE_PROGRAM_TABLET =
            ITEMS.register("rune_program_tablet", () -> new RuneProgramTabletItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<SpawnEggItem> FAMILIAR_SPAWN_EGG =
            ITEMS.register("familiar_spawn_egg", () -> new SpawnEggItem(
                    FAMILIAR.get(), 0x190D3F, 0x4FE3E3, new Item.Properties()));

    private ModRegistry() {
    }

    /** Registers all registrars on the mod event bus. Call from mod construction. */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModRegistry::addCreative);
        modEventBus.addListener(ModRegistry::registerAttributes);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.putEntityType(FAMILIAR.get(), FamiliarEntity.createAttributes().build());
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ECHO_VAULT_ITEM);
            event.accept(LATTICE_ANCHOR_ITEM);
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(RUNE_ORB);
            event.accept(RUNE_PROGRAM_TABLET);
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(FAMILIAR_SPAWN_EGG);
        }
    }
}
'''

FABRIC_REGISTRY = '''package com.aethervault.fabric;

import com.aethervault.AetherVault;
import com.aethervault.block.EchoVaultBlock;
import com.aethervault.block.LatticeAnchorBlock;
import com.aethervault.core.RuneOrbItem;
import com.aethervault.core.RuneProgramTabletItem;
import com.aethervault.entities.FamiliarEntity;
import com.aethervault.storage.echo.EchoVaultBlockEntity;
import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Fabric registry wiring for all AetherVault blocks, items, entities, and block entity types.
 */
public final class ModRegistry {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AetherVault.MOD_ID, path);
    }

    // --- Blocks (registered before their block entity types) ---

    public static final EchoVaultBlock ECHO_VAULT = Registry.register(
            BuiltInRegistries.BLOCK, id("echo_vault"),
            new EchoVaultBlock(() -> ECHO_VAULT_TYPE,
                    BlockBehaviour.Properties.of().strength(3.5f, 6.0f).requiresCorrectToolForDrops()));

    public static final LatticeAnchorBlock LATTICE_ANCHOR = Registry.register(
            BuiltInRegistries.BLOCK, id("lattice_anchor"),
            new LatticeAnchorBlock(() -> LATTICE_ANCHOR_TYPE,
                    BlockBehaviour.Properties.of().strength(3.0f, 6.0f).requiresCorrectToolForDrops()));

    // --- Entities ---

    public static final EntityType<FamiliarEntity> FAMILIAR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, id("familiar"),
            EntityType.Builder.of(FamiliarEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F)
                    .build("familiar"));

    // --- Block entity types ---

    public static final BlockEntityType<EchoVaultBlockEntity> ECHO_VAULT_TYPE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, id("echo_vault"),
            BlockEntityType.Builder.of((pos, state) -> new EchoVaultBlockEntity(ECHO_VAULT_TYPE, pos), ECHO_VAULT)
                    .build(null));

    public static final BlockEntityType<LatticeAnchorBlockEntity> LATTICE_ANCHOR_TYPE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, id("lattice_anchor"),
            BlockEntityType.Builder.of((pos, state) -> new LatticeAnchorBlockEntity(LATTICE_ANCHOR_TYPE, pos), LATTICE_ANCHOR)
                    .build(null));

    // --- Items ---

    public static final Item ECHO_VAULT_ITEM = Registry.register(
            BuiltInRegistries.ITEM, id("echo_vault"), new BlockItem(ECHO_VAULT, new Item.Properties()));

    public static final Item LATTICE_ANCHOR_ITEM = Registry.register(
            BuiltInRegistries.ITEM, id("lattice_anchor"), new BlockItem(LATTICE_ANCHOR, new Item.Properties()));

    public static final Item RUNE_ORB = Registry.register(
            BuiltInRegistries.ITEM, id("rune_orb"), new RuneOrbItem(new Item.Properties().stacksTo(1)));

    public static final Item RUNE_PROGRAM_TABLET = Registry.register(
            BuiltInRegistries.ITEM, id("rune_program_tablet"), new RuneProgramTabletItem(new Item.Properties().stacksTo(1)));

    public static final Item FAMILIAR_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, id("familiar_spawn_egg"),
            new SpawnEggItem(FAMILIAR, 0x190D3F, 0x4FE3E3, new Item.Properties()));

    private ModRegistry() {
    }

    /** Performs registration, attributes, and creative-tab placement. Call from onInitialize. */
    public static void register() {
        FabricDefaultAttributeRegistry.register(FAMILIAR, FamiliarEntity.createAttributes().build());

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(ECHO_VAULT_ITEM);
            entries.accept(LATTICE_ANCHOR_ITEM);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(RUNE_ORB);
            entries.accept(RUNE_PROGRAM_TABLET);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(FAMILIAR_SPAWN_EGG);
        });
    }
}
'''

targets = [
    (NEOFORGE_REGISTRY, "neoforge-1.21.1/src/main/java/com/aethervault/neoforge/ModRegistry.java"),
    (NEOFORGE_REGISTRY, "neoforge-26.x/src/main/java/com/aethervault/neoforge/ModRegistry.java"),
    (FABRIC_REGISTRY, "fabric-1.21.1/src/main/java/com/aethervault/fabric/ModRegistry.java"),
    (FABRIC_REGISTRY, "fabric-26.x/src/main/java/com/aethervault/fabric/ModRegistry.java"),
]

for content, rel in targets:
    path = ROOT / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8", newline="\n")
    print(f"wrote {rel} ({len(content)} chars)")