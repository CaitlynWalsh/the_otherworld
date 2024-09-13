package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.layers.CrystlingCrystalLayer;
import net.kitcaitie.otherworld.client.model.ModelCrystling;
import net.kitcaitie.otherworld.common.entity.Crystling;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystlingRenderer<T extends Crystling> extends MobRenderer<T, ModelCrystling<T>> {
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/crystling.png");

    public CrystlingRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelCrystling<>(context.bakeLayer(ModelCrystling.LAYER_LOCATION)), 0.4f);
        this.addLayer(new CrystlingCrystalLayer<>(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return DEFAULT_TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float f) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
    }

}
