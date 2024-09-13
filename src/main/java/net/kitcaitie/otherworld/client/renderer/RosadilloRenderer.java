package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelRosadillo;
import net.kitcaitie.otherworld.common.entity.Rosadillo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RosadilloRenderer<T extends Rosadillo> extends MobRenderer<T, ModelRosadillo<T>> {
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/rosadillo.png");

    public RosadilloRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelRosadillo<>(context.bakeLayer(ModelRosadillo.LAYER_LOCATION)), 0.6f);
    }

    public ResourceLocation getTextureLocation(T entity) {
        return DEFAULT_TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float f) {
        if (entity.isBaby()) {
            poseStack.scale(0.55f, 0.55f, 0.55f);
        }
    }
}
