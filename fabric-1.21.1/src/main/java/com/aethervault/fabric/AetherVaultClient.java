package com.aethervault.fabric;

import com.aethervault.client.RuneProgramScreen;
import com.aethervault.core.RuneProgramTabletItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * Fabric client entry point: wires client-only hooks for AetherVault.
 */
@Environment(EnvType.CLIENT)
public final class AetherVaultClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RuneProgramTabletItem.setScreenOpener((player, stack) ->
                Minecraft.getInstance().setScreen(new RuneProgramScreen()));
    }
}
