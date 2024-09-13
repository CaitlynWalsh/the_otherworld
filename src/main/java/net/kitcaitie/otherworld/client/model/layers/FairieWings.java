package net.kitcaitie.otherworld.client.model.layers;

import com.google.common.collect.ImmutableList;
import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairieWings<T extends LivingEntity> extends AgeableListModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "fairiewings"), "main");
    private final ModelPart wings;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public FairieWings(ModelPart root) {
        this.wings = root.getChild("wings");
        this.rightWing = wings.getChild("wing1");
        this.leftWing = wings.getChild("wing0");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wings = partdefinition.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition wing0 = wings.addOrReplaceChild("wing0", CubeListBuilder.create().texOffs(16, 8).addBox(-0.5F, -3.5F, 0.0F, 1.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -19.5F, 2.0F, 0.0F, 0.7854F, 0.3927F));

        PartDefinition wing1 = wings.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -3.5F, 0.0F, 1.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -19.5F, 2.0F, 0.0F, -0.7854F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isOnGround() || entity.isPassenger()) {
            this.leftWing.yRot = (1.0995574F + Mth.cos(ageInTicks * 10.836624F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 16.2F) / 2.0F;
        }
        else {
            this.leftWing.yRot = (1.0995574F + Mth.cos(ageInTicks * 120.32113F * ((float)Math.PI / 180F)) * ((float)Math.PI / 180F) * 32.2F) / 2.5F;
        }
        this.rightWing.yRot = -this.leftWing.yRot;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.wings);
    }

}
