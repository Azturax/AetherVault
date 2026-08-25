package com.aethervault.fabric;

import com.aethervault.client.model.FamiliarModel;
import com.aethervault.client.render.FamiliarRenderer;
import com.aethervault.core.RuneProgramTabletItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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

        EntityModelLayerRegistry.registerModelLayer(FamiliarModel.LAYER, FamiliarModel::createBodyLayer);
        EntityRendererRegistry.register(ModRegistry.FAMILIAR, FamiliarRenderer::new);
    }
}

