package net.kitcaitie.otherworld.client.model;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.SnowpakaAnimation;
import net.kitcaitie.otherworld.common.entity.Snowpaka;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSnowpaka<T extends Snowpaka> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "snowpaka"), "main");
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart rightChest;
    private final ModelPart leftChest;

    public ModelSnowpaka(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.rightHindLeg = this.root.getChild("leg1");
        this.leftHindLeg = this.root.getChild("leg2");
        this.rightFrontLeg = this.root.getChild("leg3");
        this.leftFrontLeg = this.root.getChild("leg4");
        this.tail = this.root.getChild("tail");
        this.leftChest = this.root.getChild("chest_left");
        this.rightChest = this.root.getChild("chest_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition SnowCamel = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = SnowCamel.addOrReplaceChild("head", CubeListBuilder.create().texOffs(66, 31).addBox(-3.0F, -14.0F, -13.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 29).addBox(0.0F, -9.0F, -12.0F, 0.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(38, 35).addBox(-5.0F, -17.0F, -7.0F, 10.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, -6.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(25, 35).addBox(0.0F, -0.5F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -14.5F, -3.0F, 0.0F, 0.0F, 0.5672F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.5F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -14.5F, -3.0F, 0.0F, 0.0F, -0.5672F));

        PartDefinition body = SnowCamel.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -10.0F, -9.0F, 12.0F, 23.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(20, 64).addBox(0.0F, -7.0F, -15.0F, 0.0F, 16.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-5.0F, -9.0F, 3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(48, 18).addBox(-5.0F, 3.0F, 3.0F, 10.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition leg1 = SnowCamel.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 69).addBox(-3.0F, 0.0F, 3.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -14.0F, 6.0F));

        PartDefinition leg2 = SnowCamel.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(65, 66).addBox(-2.0F, 0.0F, 3.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -14.0F, 6.0F));

        PartDefinition leg3 = SnowCamel.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(50, 52).addBox(-3.0F, 0.0F, -2.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -14.0F, -5.0F));

        PartDefinition leg4 = SnowCamel.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(30, 52).addBox(-2.0F, 0.0F, -2.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -14.0F, -5.0F));

        PartDefinition chest_left = SnowCamel.addOrReplaceChild("chest_left", CubeListBuilder.create().texOffs(34, 71).addBox(-8.0F, 1.0F, 0.0F, 7.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -21.0F, 3.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition chest_right = SnowCamel.addOrReplaceChild("chest_right", CubeListBuilder.create().texOffs(70, 52).addBox(-8.0F, 1.0F, 0.0F, 7.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -21.0F, 3.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition tail = SnowCamel.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -19.8825F, 14.2365F, 0.2618F, 0.0F, 0.0F));

        PartDefinition body_r1 = tail.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-3.0F, -1.0F, -12.5F, 6.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2777F, 0.1958F, 1.8762F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T snowpaka, float p_103050_, float p_103051_, float ageInTicks, float p_103053_, float p_103054_) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.leftChest.visible = snowpaka.hasChest();
        this.rightChest.visible = snowpaka.hasChest();
        this.animate(snowpaka.IDLE, SnowpakaAnimation.SNOWPAKA_IDLE, ageInTicks);
        this.animate(snowpaka.WALK, SnowpakaAnimation.SNOWPAKA_WALK, ageInTicks);
        this.animate(snowpaka.SPRINT, SnowpakaAnimation.SNOWPAKA_SPRINT, ageInTicks);
        this.animate(snowpaka.JUMP, SnowpakaAnimation.SNOWPAKA_JUMP, ageInTicks);
        this.animate(snowpaka.FINISH_JUMP, SnowpakaAnimation.SNOWPAKA_FINISH_JUMP, ageInTicks);
    }

}
