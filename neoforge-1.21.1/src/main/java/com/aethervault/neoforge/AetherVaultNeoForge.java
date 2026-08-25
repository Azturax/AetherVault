package com.aethervault.neoforge;

import com.aethervault.AetherVault;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * NeoForge entry point for AetherVault (Minecraft 1.21.1).
 */
@Mod(AetherVault.MOD_ID)
public final class AetherVaultNeoForge {

    public AetherVaultNeoForge(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        AetherVault.init();
    }
}