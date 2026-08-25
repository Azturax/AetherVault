package com.aethervault.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import com.aethervault.entities.FamiliarEntity;

/**
 * Entity model for the Familiar Carrier construct: bronze chassis, glass dome,
 * glowing core plate, teal wings, and feet. UVs map into the 32x32 body texture.
 */
public class FamiliarModel extends HierarchicalModel<FamiliarEntity> {

    /** Layer key registered by each platform's client setup. */
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("aethervault", "familiar"),
                    "main");

    private final ModelPart root;
    private final ModelPart chassis;
    private final ModelPart dome;
    private final ModelPart wingLeft;
    private final ModelPart wingRight;

    public FamiliarModel(ModelPart root) {
        this.root = root;
        this.chassis = root.getChild("chassis");
        this.dome = chassis.getChild("dome");
        this.wingLeft = chassis.getChild("wing_left");
        this.wingRight = chassis.getChild("wing_right");
    }

    /**
     * Layer geometry shared by both loaders' layer-registration APIs.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Chassis: 8x8x8 body sitting above ground.
        PartDefinition chassis = root.addOrReplaceChild("chassis",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // Glass dome head on top of the chassis.
        chassis.addOrReplaceChild("dome",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO);

        // Glowing core plate on the front face (thin box).
        chassis.addOrReplaceChild("core",
                CubeListBuilder.create()
                        .texOffs(24, 22)
                        .addBox(-1.0F, -5.0F, -4.5F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        // Teal wings pivot at the shoulders and flap around Z.
        chassis.addOrReplaceChild("wing_left",
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(-5.0F, -7.0F, -0.5F, 5.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.2F));

        chassis.addOrReplaceChild("wing_right",
                CubeListBuilder.create()
                        .texOffs(14, 28)
                        .addBox(0.0F, -7.0F, -0.5F, 5.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(4.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.2F));

        // Feet.
        chassis.addOrReplaceChild("feet",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-3.0F, 0.0F, -3.0F, 2.0F, 1.0F, 3.0F)
                        .texOffs(10, 24)
                        .addBox(1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 3.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    /**
     * Idle animation: wing flap oscillation + gentle dome hover-bob.
     */
    @Override
    public void setupAnim(FamiliarEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float flap = Mth.sin(ageInTicks * 0.35F) * 0.45F;
        this.wingLeft.zRot = 0.2F + flap;
        this.wingRight.zRot = -0.2F - flap;
        this.dome.y = -Mth.cos(ageInTicks * 0.12F) * 0.6F;
    }
}

