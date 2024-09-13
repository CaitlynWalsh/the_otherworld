package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.client.layers.EmberianGlowLayer;
import net.kitcaitie.otherworld.common.entity.npcs.Emberian;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmberianRenderer extends PersonEntityRenderer<Emberian> {

    public EmberianRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new EmberianGlowLayer<>(this));
    }


}
