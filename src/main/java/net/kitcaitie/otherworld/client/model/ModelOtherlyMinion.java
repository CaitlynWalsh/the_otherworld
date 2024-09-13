package net.kitcaitie.otherworld.client.model;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelOtherlyMinion<T extends OtherlyMinion> extends HumanoidModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "minion"), "main");

	public ModelOtherlyMinion(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(18, 53).addBox(-5.0F, -14.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(10, 51).addBox(3.0F, -14.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(48, 39).addBox(-6.0F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -5.5F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(48, 22).addBox(0.0F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -5.5F, 0.0F, 0.0F, 0.0F, 0.4363F));

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition Body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(24, 26).addBox(-4.5F, 8.0F, -2.5F, 9.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(24, 16).addBox(-4.5F, 1.0F, -2.5F, 9.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(48, 10).addBox(-4.5F, 13.0F, -2.5F, 9.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(48, 0).addBox(-4.5F, 13.0F, 2.5F, 9.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 47).addBox(-4.5F, 13.0F, -2.5F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 44).addBox(4.5F, 13.0F, -2.5F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 36).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(42, 46).addBox(-3.5F, 7.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -22.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(46, 30).addBox(-1.5F, 7.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -22.0F, 0.0F));

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T p_102928_, float p_102929_, float p_102930_, float p_102931_, float p_102932_, float p_102933_) {
		//resetPose();
		super.setupAnim(p_102928_, p_102929_, p_102930_, p_102931_, p_102932_, p_102933_);
		OtherlyMinion.OtherlyMinionArmPose armPose = p_102928_.getArmPose();
		if (armPose == OtherlyMinion.OtherlyMinionArmPose.SPELLCASTING) {
			this.rightArm.z = 0.0F;
			this.rightArm.x = -5.0F;
			this.leftArm.z = 0.0F;
			this.leftArm.x = 5.0F;
			this.rightArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.25F;
			this.leftArm.xRot = Mth.cos(p_102931_ * 0.6662F) * 0.25F;
			this.rightArm.zRot = 2.3561945F;
			this.leftArm.zRot = -2.3561945F;
			this.rightArm.yRot = 0.0F;
			this.leftArm.yRot = 0.0F;
		}
		else if (armPose == OtherlyMinion.OtherlyMinionArmPose.RANGED_ATTACKING) {
			AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, p_102931_);
		}
		else if (armPose == OtherlyMinion.OtherlyMinionArmPose.MELEE_ATTACKING) {
			AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, p_102928_, this.attackTime, p_102931_);
		}
	}

	private void resetPose() {
		this.headParts().forEach(ModelPart::resetPose);
		this.bodyParts().forEach(ModelPart::resetPose);
		this.leftArm.resetPose();
		this.rightArm.resetPose();
		this.leftLeg.resetPose();
		this.rightLeg.resetPose();
	}
}