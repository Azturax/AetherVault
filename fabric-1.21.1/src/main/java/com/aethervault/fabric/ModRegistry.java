package com.aethervault.fabric;

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
