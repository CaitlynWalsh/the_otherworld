package net.kitcaitie.otherworld.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.RosadilloAnimation;
import net.kitcaitie.otherworld.common.entity.Rosadillo;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRosadillo<T extends Rosadillo> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "rosadillo"), "main");
	private final ModelPart root;

	public ModelRosadillo(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition shell = root.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-7.5F, -3.5F, -10.0F, 15.0F, 8.0F, 21.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(-9.5F, -6.5F, -8.0F, 19.0F, 12.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -9.7245F, 2.4979F, -0.4363F, 0.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(33, 58).addBox(-3.5747F, -3.4018F, -7.167F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5747F, -6.5982F, -4.833F));

		PartDefinition jaw0 = head.addOrReplaceChild("jaw0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0747F, 0.0982F, -6.167F));

		PartDefinition jaw1 = head.addOrReplaceChild("jaw1", CubeListBuilder.create().texOffs(59, 71).addBox(-1.5F, -0.5F, -3.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0747F, 2.0982F, -5.667F));

		PartDefinition ear0 = head.addOrReplaceChild("ear0", CubeListBuilder.create(), PartPose.offset(-3.0F, -1.0F, -4.0F));

		PartDefinition head_r1 = ear0.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(13, 10).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0081F, 0.3258F, 0.3594F, -0.3894F, -0.9109F, -1.6312F));

		PartDefinition ear1 = head.addOrReplaceChild("ear1", CubeListBuilder.create(), PartPose.offset(3.0F, -1.0F, -4.0F));

		PartDefinition head_r2 = ear1.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(52, 0).addBox(-1.0372F, -0.3437F, -1.2482F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6732F, -0.7652F, -0.1255F, 0.5429F, -1.0334F, -1.5957F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -6.9273F, -3.5548F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, -4.5F, -4.0F, 9.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.2679F, 8.9952F, 0.1745F, 0.0F, 0.0F));

		PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 0).addBox(-3.0F, -10.0F, -1.0F, 11.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 4.2321F, -0.0048F, 0.1745F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0727F, 11.5548F));

		PartDefinition tail0 = tail.addOrReplaceChild("tail0", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0112F, -0.3306F));

		PartDefinition cube_r3 = tail0.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(54, 30).addBox(-2.5F, -0.5F, -6.5F, 5.0F, 1.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 4.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5206F, 9.7003F));

		PartDefinition cube_r4 = tail1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(62, 51).addBox(-2.0F, -0.5F, -3.5F, 4.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(54, 30).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -4.5F, -3.5F));

		PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 30).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -4.5F, -3.5F));

		PartDefinition leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(64, 60).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.5F, 4.5F));

		PartDefinition leg3 = root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -3.5F, 4.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.CURL, RosadilloAnimation.ROSADILLO_CURL, ageInTicks);
		this.animate(entity.IN_SHELL, RosadilloAnimation.ROSADILLO_IN_SHELL, ageInTicks);
		this.animate(entity.UNCURL, RosadilloAnimation.ROSADILLO_UNCURL, ageInTicks);
		this.animate(entity.IDLE, RosadilloAnimation.ROSADILLO_IDLE, ageInTicks);
		this.animate(entity.WALK, RosadilloAnimation.ROSADILLO_WALK, ageInTicks);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}