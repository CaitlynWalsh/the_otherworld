package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelOtherlyMinion;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OtherlyMinionRenderer<T extends OtherlyMinion> extends MobRenderer<T, ModelOtherlyMinion<T>> {
    public static final ResourceLocation MALE_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/bosses/otherly_minion_male.png");
    public static final ResourceLocation FEMALE_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/bosses/otherly_minion_female.png");
    public static final ResourceLocation MALE_GLOW_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/glow/otherly_minion_male.png");
    public static final ResourceLocation FEMALE_GLOW_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/glow/otherly_minion_female.png");

    public OtherlyMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelOtherlyMinion<>(context.bakeLayer(ModelOtherlyMinion.LAYER_LOCATION)), 0.5f);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, T minion, float v, float v1, float v2, float v3, float v4, float v5) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(getTexture(minion));
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            @Override
            public RenderType renderType() {
                return RenderType.eyes(MALE_GLOW_TEXTURE);
            }

            private RenderType getTexture(T minion) {
                return minion.isMale() ? RenderType.eyes(MALE_GLOW_TEXTURE) : RenderType.eyes(FEMALE_GLOW_TEXTURE);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(T minion) {
        return minion.isMale() ? MALE_TEXTURE : FEMALE_TEXTURE;
    }
}
