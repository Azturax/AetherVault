package com.aethervault.neoforge;

import com.aethervault.client.model.FamiliarModel;
import com.aethervault.client.render.FamiliarRenderer;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-only registrations for the NeoForge loader.
 */
public final class ModClient {

    private ModClient() {
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FamiliarModel.LAYER, FamiliarModel::createBodyLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.FAMILIAR.get(), FamiliarRenderer::new);
    }
}
