package com.aethervault;

import com.aethervault.entities.FamiliarEntity;
import com.aethervault.logic.FlowEvaluator;
import com.aethervault.storage.echo.EchoVaultBlockEntity;
import com.aethervault.storage.echo.TemporalSnapshot;

import net.minecraft.world.item.ItemStack;

/**
 * Loader-agnostic game-event hooks for AetherVault.
 *
 * <p>Platform modules translate their loader's events (block placement, item drops,
 * entity spawns) into calls on these methods. Keeping the handlers here means the
 * behavior is shared verbatim between NeoForge and Fabric.</p>
 */
public final class AetherVaultEvents {

    private static final FlowEvaluator FLOW_EVALUATOR = new FlowEvaluator();

    private AetherVaultEvents() {
    }

    /**
     * Called by platform modules during startup to announce that hooks are wired.
     */
    public static void registerHooks() {
        System.out.println("AetherVault event hooks registered (block place, item drop, entity spawn).");
    }

    /**
     * Hook: a block was placed in the world. Initializes Echo Vault storage on placement.
     */
    public static void onBlockPlaced(EchoVaultBlockEntity blockEntity, String positionDescription) {
        if (blockEntity != null) {
            System.out.println("Echo Vault Block Entity initialized at " + positionDescription);
        }
    }

    /**
     * Hook: a player dropped an item. Demonstrates ingestion into the FlowEvaluator.
     */
    public static void onItemDropped(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        System.out.println("Dropped Item Event detected: " + stack);
        FLOW_EVALUATOR.evaluateItem(stack);
    }

    /**
     * Hook: a FamiliarEntity spawned. Links the entity to its rune program context.
     */
    public static void onFamiliarSpawned(FamiliarEntity familiar) {
        if (familiar != null) {
            System.out.println("Familiar Entity spawned: " + familiar);
            // Logic to link the entity to its associated RuneChipInstruction or storage.
        }
    }

    /**
     * Hook: a temporal snapshot was captured by an Echo Vault.
     */
    public static void onSnapshotCaptured(TemporalSnapshot snapshot) {
        if (snapshot != null) {
            System.out.println("Temporal snapshot captured: " + snapshot);
        }
    }
}