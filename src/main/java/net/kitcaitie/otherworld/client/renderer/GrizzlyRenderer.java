package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelGrizzly;
import net.kitcaitie.otherworld.common.entity.Grizzly;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrizzlyRenderer<T extends Grizzly> extends MobRenderer<T, ModelGrizzly<T>> {
    public static final ResourceLocation GRIZZLY_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/grizzly.png");
    public static final ResourceLocation CUB_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/grizzly_cub.png");
    public GrizzlyRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelGrizzly<>(context.bakeLayer(ModelGrizzly.LAYER_LOCATION)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isBaby() ? CUB_TEXTURE : GRIZZLY_TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack stack, float f) {
        stack.scale(1.5F, 1.5F, 1.5F);
        if (entity.isBaby()) {
            stack.scale(0.45F, 0.45F, 0.45F);
        }
    }
}
