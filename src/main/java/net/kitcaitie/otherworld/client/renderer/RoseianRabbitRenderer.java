package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelRoseianRabbit;
import net.kitcaitie.otherworld.common.entity.RoseianRabbit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RoseianRabbitRenderer<T extends RoseianRabbit> extends MobRenderer<T, ModelRoseianRabbit<T>> {
    public static final ResourceLocation SHEARED_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/roseian_rabbit_sheared.png");
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/roseian_rabbit.png");

    public RoseianRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelRoseianRabbit<>(context.bakeLayer(ModelRoseianRabbit.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(T rabbit) {
        if (rabbit.isSheared()) {
            return SHEARED_TEXTURE;
        }
        return DEFAULT_TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float v) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
    }
}
