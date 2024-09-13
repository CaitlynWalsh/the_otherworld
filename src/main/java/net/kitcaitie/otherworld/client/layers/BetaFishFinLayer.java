package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.ModelBetaFish;
import net.kitcaitie.otherworld.common.entity.BetaFish;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BetaFishFinLayer<T extends BetaFish> extends RenderLayer<T, ModelBetaFish<T>> {
    private final ModelBetaFish<T> layerModel;
    private static final ResourceLocation FIN_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/beta_layer.png");

    public BetaFishFinLayer(RenderLayerParent<T, ModelBetaFish<T>> parent, EntityModelSet modelSet) {
        super(parent);
        this.layerModel = new ModelBetaFish<>(modelSet.bakeLayer(ModelBetaFish.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource source, int i, T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (!entity.isInvisible()) {
            float[] afloat = entity.getFinColor().getTextureDiffuseColors();
            this.getParentModel().copyPropertiesTo(layerModel);
            layerModel.prepareMobModel(entity, f, f1, f2);
            layerModel.setupAnim(entity, f, f1, f3, f4, f2);
            layerModel.setColor(afloat[0], afloat[1], afloat[2]);
            renderColoredCutoutModel(layerModel, FIN_TEXTURE, stack, source, i, entity, afloat[0], afloat[1], afloat[2]);
        }
    }
}
