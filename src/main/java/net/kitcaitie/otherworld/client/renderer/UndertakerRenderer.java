package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Undertaker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UndertakerRenderer extends PersonEntityRenderer<Undertaker> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/undertaker.png");
    public UndertakerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPerson person) {
        return TEXTURE;
    }
}
