package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelPhlymp;
import net.kitcaitie.otherworld.common.entity.Phlymp;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhlympRenderer<T extends Phlymp> extends MobRenderer<T, ModelPhlymp<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/phlymp.png");
    protected static final ResourceLocation SADDLE = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/phlymp_saddle.png");
    public PhlympRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelPhlymp<>(context.bakeLayer(ModelPhlymp.LAYER_LOCATION)), 0.8F);
        this.addLayer(new SaddleLayer<>(this, getModel(), SADDLE));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(T t, PoseStack stack, float v) {
        if (t.isBaby()) {
            stack.scale(0.3F, 0.3F, 0.3F);
        }
        stack.scale(1.5F, 1.5F, 1.5F);
    }
}
