package net.kitcaitie.otherworld.client.model.layers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OniHorns<T extends LivingEntity> extends AgeableListModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "player"), "horns");
	private final ModelPart horn0;
	private final ModelPart horn1;

	public OniHorns(ModelPart root) {
		this.horn0 = root.getChild("horn0");
		this.horn1 = root.getChild("horn1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition horn0 = partdefinition.addOrReplaceChild("horn0", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -2.3F, -0.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(16, 15).addBox(0.0F, -1.3F, 1.8F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(17, 6).addBox(-1.0F, -1.3F, -1.2F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(7, 19).addBox(0.0F, 1.7F, -1.2F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 19).addBox(0.0F, 1.7F, 0.8F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(0.0F, 2.7F, -0.2F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -4.7F, -0.8F));

		PartDefinition horn1 = partdefinition.addOrReplaceChild("horn1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.3F, -0.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(9, 10).addBox(-2.0F, -1.3F, 1.8F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 0).addBox(-1.0F, -1.3F, -1.2F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 11).addBox(-2.0F, 1.7F, -1.2F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 0).addBox(-2.0F, 1.7F, 0.8F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 3).addBox(-2.0F, 2.7F, -0.2F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -4.7F, -0.8F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		horn0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		horn1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.horn0, this.horn1);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of();
	}

	@Override
	public void setupAnim(T entity, float v, float v1, float v2, float v3, float v4) {
		this.horn0.yRot = v1 * ((float)Math.PI / 180F);
		this.horn0.xRot = v1 * ((float)Math.PI / 180F);
		this.horn1.yRot = this.horn0.yRot;
		this.horn1.xRot = this.horn0.xRot;
	}
}