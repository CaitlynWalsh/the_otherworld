package net.kitcaitie.otherworld.client.renderer;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.layers.HornedLayer;
import net.kitcaitie.otherworld.client.model.layers.OniHorns;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.Oni;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OniRenderer extends PersonEntityRenderer<Oni> {

    public OniRenderer(EntityRendererProvider.Context context) {
        super(context);
        context.bakeLayer(OniHorns.LAYER_LOCATION);
        this.addLayer(new HornedLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPerson person) {
        Oni oni = (Oni) person;
        String ismale = oni.isMale() ? "male" : "female";
        if (oni.isBaby()) {
            return new ResourceLocation(Otherworld.MODID, "textures/entity/oni" + oni.getVariant() + "_baby_" + ismale + ".png");
        }
        return new ResourceLocation(Otherworld.MODID, "textures/entity/oni" + oni.getVariant() + "_" + person.getOccupation().name().toLowerCase() + "_" + ismale + ".png");
    }
}
