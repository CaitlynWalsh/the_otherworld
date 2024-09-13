package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelFairling;
import net.kitcaitie.otherworld.common.entity.Fairling;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairlingRenderer extends MobRenderer<Fairling, ModelFairling> {
    public static final ResourceLocation FAIRLING_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/fairling.png");
    public static final ResourceLocation BABY_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/fairling_baby.png");

    public FairlingRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelFairling(context.bakeLayer(ModelFairling.LAYER_LOCATION)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Fairling fairling) {
        return fairling.isBaby() ? BABY_TEXTURE : FAIRLING_TEXTURE;
    }

    @Override
    protected void scale(Fairling fairling, PoseStack stack, float v) {
        if (fairling.isBaby()) stack.scale(0.5F, 0.5F, 0.5F);
    }

}
