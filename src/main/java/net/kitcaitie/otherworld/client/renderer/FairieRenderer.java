package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.client.layers.FairieWingsLayer;
import net.kitcaitie.otherworld.client.model.layers.FairieWings;
import net.kitcaitie.otherworld.common.entity.npcs.Fairie;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairieRenderer extends PersonEntityRenderer<Fairie> {
    public FairieRenderer(EntityRendererProvider.Context context) {
        super(context);
        context.bakeLayer(FairieWings.LAYER_LOCATION);
        this.addLayer(new FairieWingsLayer<>(this, context.getModelSet()));
    }
}
