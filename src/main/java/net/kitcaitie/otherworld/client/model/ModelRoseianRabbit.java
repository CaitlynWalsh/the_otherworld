package net.kitcaitie.otherworld.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.animation.RoseianRabbitAnimation;
import net.kitcaitie.otherworld.common.entity.RoseianRabbit;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRoseianRabbit<T extends RoseianRabbit> extends HierarchicalModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Otherworld.MODID, "model_roseian_rabbit"),
			"main");
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart backleg0;
	public final ModelPart backleg1;
	public final ModelPart leg0;
	public final ModelPart leg1;
	public final ModelPart root;
	private float headXRot;
	private float jumpRotation;

	public ModelRoseianRabbit(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.body = this.root.getChild("body");
		this.backleg0 = this.root.getChild("backleg0");
		this.backleg1 = this.root.getChild("backleg1");
		this.leg0 = this.root.getChild("leg0");
		this.leg1 = this.root.getChild("leg1");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(23, 17).addBox(-2.4983F, -0.3878F, -3.7415F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 22).addBox(-1.4983F, 1.6122F, -4.2415F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0017F, -5.6122F, -3.2585F));

		PartDefinition woollayer1 = head.addOrReplaceChild("woollayer1", CubeListBuilder.create().texOffs(28, 28).addBox(-3.5F, -1.0F, -2.0F, 7.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0017F, -0.3878F, -0.7415F));

		PartDefinition ear0 = head.addOrReplaceChild("ear0", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.3684F, 0.8821F, -1.2415F, 0.0F, 0.3491F, 0.0F));

		PartDefinition cube_r1 = ear0.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 38).addBox(0.5F, -4.5F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1902F, -0.7699F, 0.842F, 0.0F, 0.0F, -0.1745F));

		PartDefinition ear1 = head.addOrReplaceChild("ear1", CubeListBuilder.create(), PartPose.offsetAndRotation(1.365F, 0.8935F, -1.2755F, 0.0F, -0.3491F, 0.0F));

		PartDefinition cube_r2 = ear1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(6, 38).addBox(-2.5F, -4.5F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.197F, -0.7812F, 0.876F, 0.0203F, 0.0043F, 0.1733F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-3.5F, -1.25F, -3.0F, 7.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.75F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.5F, 0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.25F, 5.5F));

		PartDefinition woollayer2 = body.addOrReplaceChild("woollayer2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.5F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.55F, 1.5F));

		PartDefinition backleg0 = root.addOrReplaceChild("backleg0", CubeListBuilder.create().texOffs(14, 32).addBox(-1.0F, 1.75F, -0.75F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-1.0F, 5.75F, -1.75F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -6.75F, 1.75F));

		PartDefinition backleg1 = root.addOrReplaceChild("backleg1", CubeListBuilder.create().texOffs(37, 13).addBox(-1.0F, 1.75F, -0.75F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(30, 0).addBox(-1.0F, 5.75F, -1.75F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -6.75F, 1.75F));

		PartDefinition leg0 = root.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 2.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -5.5F, -1.5F, 0.0436F, 0.0F, 0.0F));

		PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, 2.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -5.5F, -1.5F, 0.0436F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return root;
	}

	public void setupAnim(T rabbit, float v, float v1, float v2, float v3, float v4) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(rabbit.IDLE, RoseianRabbitAnimation.ROSEIAN_RABBIT_IDLE, v2);
		this.animate(rabbit.TRANSITION_IDLE, RoseianRabbitAnimation.ROSEIAN_RABBIT_TRANSITION_IDLE, v2);
		this.animate(rabbit.TRANSITION_STAND, RoseianRabbitAnimation.ROSEIAN_RABBIT_TRANSITION_STAND, v2);
		this.animate(rabbit.STAND, RoseianRabbitAnimation.ROSEIAN_RABBIT_STAND, v2);
		this.animate(rabbit.JUMP, RoseianRabbitAnimation.ROSEIAN_RABBIT_JUMP, v2);
		this.animate(rabbit.EAT, RoseianRabbitAnimation.ROSEIAN_RABBIT_EAT, v2);
	}

}
