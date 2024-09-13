package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelSnowpaka;
import net.kitcaitie.otherworld.common.entity.Snowpaka;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowpakaRenderer<T extends Snowpaka> extends MobRenderer<T, ModelSnowpaka<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/snowpaka.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/snowpaka_baby.png");

    public SnowpakaRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelSnowpaka<>(context.bakeLayer(ModelSnowpaka.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack stack, float f) {
        stack.scale(1.05F, 1.05F, 1.05F);
        if (entity.isBaby()) {
            stack.scale(0.45F, 0.45F, 0.45F);
        }
    }
}
