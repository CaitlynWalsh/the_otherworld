package net.kitcaitie.otherworld.client.model;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.PhlympAnimation;
import net.kitcaitie.otherworld.common.entity.Phlymp;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPhlymp<T extends Phlymp> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "phlymp"), "main");
    private final ModelPart root;

    public ModelPhlymp(ModelPart root) {
        this.root = root.getChild("root");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -6.0F, -10.0F, 18.0F, 16.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(1, 35).addBox(-5.0F, -11.0F, -6.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition ear0 = root.addOrReplaceChild("ear0", CubeListBuilder.create().texOffs(42, 34).addBox(0.0F, -5.0F, 0.0F, 18.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -23.0F, -1.0F));

        PartDefinition ear1 = root.addOrReplaceChild("ear1", CubeListBuilder.create().texOffs(42, 34).mirror().addBox(-18.0F, -5.0F, 0.0F, 18.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, -23.0F, -1.0F));

        PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(31, 34).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -11.0F, -3.0F));

        PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, 0.5F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -11.5F, -3.0F));

        PartDefinition leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -11.0F, 1.0F));

        PartDefinition leg3 = root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -11.0F, 1.0F));

        PartDefinition leg4 = root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -10.5F, 4.0F));

        PartDefinition leg5 = root.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(31, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -11.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        this.root.xRot = 0.0F;
        this.root.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.animate(entity.JUMPING, PhlympAnimation.JUMP, ageInTicks);
        this.animate(entity.FLOATING, PhlympAnimation.FLOATING, ageInTicks);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
