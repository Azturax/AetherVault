package com.aethervault.neoforge;

import com.aethervault.AetherVault;
import com.aethervault.client.RuneProgramScreen;
import com.aethervault.core.RuneProgramTabletItem;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * NeoForge entry point for AetherVault (Minecraft 1.21.1).
 */
@Mod(AetherVault.MOD_ID)
public final class AetherVaultNeoForge {

    public AetherVaultNeoForge(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        if (FMLEnvironment.dist.isClient()) {
            // Referenced lazily inside the lambda so dedicated servers never load client classes.
            RuneProgramTabletItem.setScreenOpener((player, stack) ->
                    Minecraft.getInstance().setScreen(new RuneProgramScreen()));
            modEventBus.addListener(ModClient::registerLayers);
            modEventBus.addListener(ModClient::registerRenderers);
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        AetherVault.init();
    }
}
