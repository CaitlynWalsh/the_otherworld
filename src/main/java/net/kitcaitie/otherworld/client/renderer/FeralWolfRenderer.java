package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.FeralWolf;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FeralWolfRenderer<T extends FeralWolf> extends MobRenderer<T, WolfModel<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/feral_wolf.png");

    public FeralWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new WolfModel<>(context.bakeLayer(ModelLayers.WOLF)), 0.6F);
    }

    @Override
    protected void scale(T t, PoseStack stack, float size) {
        stack.scale(1.2F, 1.2F, 1.2F);
    }

    protected float getBob(T t, float p_116529_) {
        return t.getTailAngle();
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return TEXTURE;
    }
}
