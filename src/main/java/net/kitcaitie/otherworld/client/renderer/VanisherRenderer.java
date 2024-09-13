package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Vanisher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VanisherRenderer extends PersonEntityRenderer<Vanisher> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/ghoul_villager_male.png");
    public VanisherRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPerson person) {
        return TEXTURE;
    }

}
