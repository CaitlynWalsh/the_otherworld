package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.layers.BetaFishFinLayer;
import net.kitcaitie.otherworld.client.model.ModelBetaFish;
import net.kitcaitie.otherworld.common.entity.BetaFish;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BetaFishRenderer<T extends BetaFish> extends MobRenderer<T, ModelBetaFish<T>> {
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/beta_fish.png");

    public BetaFishRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelBetaFish<>(context.bakeLayer(ModelBetaFish.LAYER_LOCATION)), 0.4F);
        this.addLayer(new BetaFishFinLayer<>(this, context.getModelSet()));
    }

    @Override
    public void render(T entity, float v, float v1, PoseStack stack, MultiBufferSource source, int i) {
        float[] afloat = entity.getBaseColor().getTextureDiffuseColors();
        this.model.setColor(afloat[0], afloat[1], afloat[2]);
        super.render(entity, v, v1, stack, source, i);
    }

    protected void setupRotations(T entity, PoseStack stack, float v, float v1, float v2) {
        super.setupRotations(entity, stack, v, v1, v2);
        float f = 4.3F * Mth.sin(0.6F * v);
        stack.mulPose(Axis.YP.rotationDegrees(f));
        if (!entity.isInWater()) {
            stack.translate(0.2F, 0.1F, 0.0F);
            stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return BASE_TEXTURE;
    }
}
