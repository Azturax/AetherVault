package com.aethervault.fabric;

import com.aethervault.AetherVault;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point for AetherVault (Minecraft 1.21.1).
 */
public final class AetherVaultFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModRegistry.register();
        AetherVault.init();
    }
}