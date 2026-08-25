package com.aethervault.client.render;

import com.aethervault.client.model.FamiliarModel;
import com.aethervault.entities.FamiliarEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renders the Familiar Carrier with its bronze/glass construct texture.
 */
public class FamiliarRenderer extends MobRenderer<FamiliarEntity, FamiliarModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("aethervault", "textures/entity/familiar_body.png");

    public FamiliarRenderer(EntityRendererProvider.Context context) {
        super(context, new FamiliarModel(context.bakeLayer(FamiliarModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FamiliarEntity entity) {
        return TEXTURE;
    }
}
