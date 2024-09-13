package net.kitcaitie.otherworld.client.model;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.BetaFishAnimation;
import net.kitcaitie.otherworld.common.entity.BetaFish;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBetaFish<T extends BetaFish> extends ColorableHierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "betafish"), "main");

    private final ModelPart root;
    private final ModelPart left_fin;
    private final ModelPart right_fin;
    private final ModelPart tail;

    public ModelBetaFish(ModelPart root) {
        this.root = root.getChild("root");
        this.left_fin = this.root.getChild("fin_left");
        this.right_fin = this.root.getChild("fin_right");
        this.tail = this.root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(7, 8).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition fin_top = root.addOrReplaceChild("fin_top", CubeListBuilder.create().texOffs(13, 0).addBox(0.0F, -2.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 1.0F));

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.5F, 0.0F, 0.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 3.0F));

        PartDefinition fin_left = root.addOrReplaceChild("fin_left", CubeListBuilder.create().texOffs(7, 0).addBox(-0.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -1.5F, 0.0F));

        PartDefinition fin_right = root.addOrReplaceChild("fin_right", CubeListBuilder.create().texOffs(18, 0).addBox(-2.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -1.5F, 0.0F));

        PartDefinition right_gill = root.addOrReplaceChild("right_gill", CubeListBuilder.create().texOffs(0, 14).addBox(-2.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -1.5F, -2.0F));

        PartDefinition left_gill = root.addOrReplaceChild("left_gill", CubeListBuilder.create().texOffs(7, 18).addBox(-0.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -1.5F, -2.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.IDLE, BetaFishAnimation.BETAFISH_IDLE, ageInTicks);
        this.animate(entity.AGGRO, BetaFishAnimation.BETAFISH_AGGRO, ageInTicks);
        this.animate(entity.EXPAND_GILLS, BetaFishAnimation.BETAFISH_EXPAND_GILLS, ageInTicks);

        float f = 1.0F;
        if (!entity.isInWater()) {
            f = 1.5F;
        }

        this.tail.yRot = -f * 0.45F * Mth.sin(0.6F * ageInTicks);
        this.right_fin.yRot = -(-0.2F + 0.4F * Mth.sin(ageInTicks * 0.2F));
        this.left_fin.yRot = -(0.2F - 0.4F * Mth.sin(ageInTicks * 0.2F));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
