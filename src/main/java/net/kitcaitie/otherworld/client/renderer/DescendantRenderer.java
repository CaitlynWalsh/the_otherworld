package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.layers.EmberianGlowLayer;
import net.kitcaitie.otherworld.client.layers.FairieWingsLayer;
import net.kitcaitie.otherworld.client.layers.HornedLayer;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.Descendant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DescendantRenderer extends PersonEntityRenderer<Descendant> {
    private static final String location = "textures/entity/descendant/";
    public DescendantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new EmberianGlowLayer<>(this));
        this.addLayer(new HornedLayer<>(this, context.getModelSet()));
        this.addLayer(new FairieWingsLayer<>(this, context.getModelSet()));
    }

    @Override
    public void render(Descendant entity, float f, float f1, PoseStack stack, MultiBufferSource source, int i) {
        super.render(entity, f, f1, stack, source, i);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPerson person) {
        String male = person.isMale() ? "male.png" : "female.png";
        if (person.isMixedRace()) {
            //TODO: MAKE BABY TEXTURES FOR MIXED RACES
            if (person.isBaby() && (person.getRace() == IRaces.Race.FAIRIAN || person.isMale())) {
                return new ResourceLocation(Otherworld.MODID, location + person.getRace().name().toLowerCase() + "_baby_" + male);
            }
            return new ResourceLocation(Otherworld.MODID, location + person.getRace().name().toLowerCase() + "_" + male);
        }
        else if (person.canHaveVariant()) {
            return new ResourceLocation(Otherworld.MODID, location + person.getRace().name().toLowerCase() + ((Descendant)person).getVariant() + "_" + (person.isBaby() ? "baby_" : "") + male);
        }
        return super.getTextureLocation(person);
    }

}
