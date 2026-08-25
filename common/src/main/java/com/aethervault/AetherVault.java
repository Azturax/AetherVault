package com.aethervault;

import com.aethervault.storage.echo.EchoVaultBlockEntity;
import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

/**
 * Loader-agnostic bootstrap for the AetherVault mod.
 *
 * <p>Platform entry points ({@code com.aethervault.neoforge.AetherVaultNeoForge} and
 * {@code com.aethervault.fabric.AetherVaultFabric}) call {@link #init()} during their
 * respective mod-construction/initialization phases. All shared, loader-independent
 * orchestration lives here.</p>
 */
public final class AetherVault {
    public static final String MOD_ID = "aethervault";
    public static final String MOD_NAME = "AetherVault";
    public static final String VERSION = "0.1.0";

    private static boolean initialized;

    private AetherVault() {
    }

    /**
     * Initializes the mod. Safe to call from any loader; repeated calls are no-ops.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerBlockEntities();
        registerItemsAndEntities();
        AetherVaultEvents.registerHooks();

        System.out.println("AetherVault " + VERSION + " initialized.");
    }

    private static void registerBlockEntities() {
        // Register EchoVaultBlockEntity and LatticeAnchorBlockEntity as Block Entities
        // so they are recognized when placed in the world. Platform modules perform the
        // actual registry calls against their loader's registry API.
        System.out.println("Registering AetherVault Block Entities ("
                + EchoVaultBlockEntity.class.getSimpleName() + ", "
                + LatticeAnchorBlockEntity.class.getSimpleName() + ")...");
    }

    private static void registerItemsAndEntities() {
        // Register custom items (RuneOrb, RuneProgramTablet) and entities (FamiliarEntity).
        System.out.println("Registering AetherVault Items and Entities...");
    }
}