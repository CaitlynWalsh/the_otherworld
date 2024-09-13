package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelWhisp;
import net.kitcaitie.otherworld.common.entity.Whisp;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WhispRenderer<T extends Whisp> extends MobRenderer<T, ModelWhisp<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/animal/whisp.png");
    public WhispRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelWhisp<>(context.bakeLayer(ModelWhisp.LAYER_LOCATION)), 0.0F);
    }

    @Override
    protected int getBlockLightLevel(T p_114496_, BlockPos p_114497_) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return TEXTURE;
    }
}
