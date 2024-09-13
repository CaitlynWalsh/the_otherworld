package net.kitcaitie.otherworld.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.GoateerAnimation;
import net.kitcaitie.otherworld.common.entity.Goateer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGoateer<T extends Goateer> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "goateer"), "main");
    private final ModelPart root;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart leftFrontLeg;

    public ModelGoateer(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.rightHindLeg = this.root.getChild("back_right_leg");
        this.leftHindLeg = this.root.getChild("back_left_leg");
        this.rightFrontLeg = this.root.getChild("front_right_leg");
        this.leftFrontLeg = this.root.getChild("front_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -20.0F, -9.0F));

        PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(0, 44).addBox(-2.4571F, -4.4168F, -3.625F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5429F, -8.5832F, -0.375F));

        PartDefinition head_r1 = head2.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-1.1383F, -3.2108F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 23).addBox(-1.1383F, -3.2108F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4571F, -5.4168F, 1.375F, 0.0F, 0.0F, -0.1745F));

        PartDefinition head_r2 = head2.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(44, 36).addBox(-0.7412F, -3.0341F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5429F, -5.4168F, 1.375F, 0.0F, 0.0F, 0.1745F));

        PartDefinition mouth = head2.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(56, 31).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(0.0F, 2.0F, -4.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5429F, -1.4168F, -3.625F));

        PartDefinition left_ear = head2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 44).addBox(-0.9919F, -3.9708F, -0.1593F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8706F, -2.8459F, 1.9114F, -3.0778F, -1.533F, -1.4634F));

        PartDefinition right_ear = head2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(41, 0).addBox(-1.0852F, -3.9507F, -0.0671F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9152F, -2.9938F, 1.7787F, 1.5708F, 1.5272F, -0.2182F));

        PartDefinition neck = head.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(43, 0).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -1.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 23).addBox(-5.0F, -8.0F, -6.0F, 10.0F, 9.0F, 11.0F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(-5.0F, -9.0F, -17.0F, 10.0F, 11.0F, 11.0F, new CubeDeformation(0.05F))
                .texOffs(28, 29).addBox(0.0F, -1.0F, -15.0F, 0.0F, 8.0F, 15.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, -13.0F, 6.0F));

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -21.0F, 11.0F));

        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(0, 23).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(0, 57).addBox(-3.0F, -1.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, -9.0F));

        PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(40, 53).addBox(-1.0F, -1.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, -9.0F));

        PartDefinition back_left_leg = root.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(23, 53).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, 8.0F));

        PartDefinition back_right_leg = root.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(44, 19).addBox(-1.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, 8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float p_103510_, float p_103511_, float ageInTicks, float p_103513_, float p_103514_) {
        root.getAllParts().forEach(ModelPart::resetPose);
        this.head.xRot = p_103514_ * ((float)Math.PI / 180F);
        this.head.yRot = p_103513_ * ((float)Math.PI / 180F);
        this.rightHindLeg.xRot = Mth.cos(p_103510_ * 0.6662F) * 1.4F * p_103511_;
        this.leftHindLeg.xRot = Mth.cos(p_103510_ * 0.6662F + (float)Math.PI) * 1.4F * p_103511_;
        this.rightFrontLeg.xRot = Mth.cos(p_103510_ * 0.6662F + (float)Math.PI) * 1.4F * p_103511_;
        this.leftFrontLeg.xRot = Mth.cos(p_103510_ * 0.6662F) * 1.4F * p_103511_;
        this.animate(entity.IDLE, GoateerAnimation.GOATEER_IDLE, ageInTicks);
        this.animate(entity.WALK, GoateerAnimation.GOATEER_WALK, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
