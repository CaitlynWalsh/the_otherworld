package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.common.entity.npcs.Iceian;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceianRenderer extends PersonEntityRenderer<Iceian> {
    public IceianRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
