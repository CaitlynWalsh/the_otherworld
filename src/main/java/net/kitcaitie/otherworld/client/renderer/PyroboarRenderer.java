package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelPyroboar;
import net.kitcaitie.otherworld.common.entity.Pyroboar;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PyroboarRenderer<T extends Pyroboar> extends MobRenderer<T, ModelPyroboar<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/pyroboar.png");
    public static final ResourceLocation GLOW = new ResourceLocation(Otherworld.MODID, "textures/entity/glow/pyroboar.png");

    public PyroboarRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelPyroboar<>(context.bakeLayer(ModelPyroboar.LAYER_LOCATION)), 0.5F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float f) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }
}
