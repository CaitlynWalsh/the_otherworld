package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelGoateer;
import net.kitcaitie.otherworld.common.entity.Goateer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoateerRenderer<T extends Goateer> extends MobRenderer<T, ModelGoateer<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/goateer.png");
    public static final ResourceLocation BABY_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/goateer_baby.png");

    public GoateerRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelGoateer<>(context.bakeLayer(ModelGoateer.LAYER_LOCATION)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isBaby() ? BABY_TEXTURE : TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack stack, float f) {
        if (entity.isBaby()) {
            stack.scale(0.5F, 0.5F, 0.5F);
        }
    }
}
