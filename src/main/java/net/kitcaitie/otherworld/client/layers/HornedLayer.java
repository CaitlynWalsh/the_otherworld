package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.layers.OniHorns;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HornedLayer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation HORNED_LOCATION = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/oni_horns.png");
    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/blank32.png");
    public OniHorns<T> hornsModel;
    public HornedLayer(RenderLayerParent<T, M> renderParent, EntityModelSet modelSet) {
        super(renderParent);
        this.hornsModel = new OniHorns<>(modelSet.bakeLayer(OniHorns.LAYER_LOCATION));
    }

    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof AbstractClientPlayer player) {
            if (PowerUtils.accessPlayerCharacter(player).hasHorns()) {
                return HORNED_LOCATION;
            }
        }
        else if (entity instanceof IRaces races && !entity.isBaby()) {
            if (races.hasHorns()) {
                return HORNED_LOCATION;
            }
        }
        return DEFAULT_LOCATION;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (entity instanceof Player && !OtherworldConfigs.CLIENT.renderLayersOnPlayer.get()) return;
        if (!entity.isInvisible()) {
            poseStack.pushPose();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(getTextureLocation(entity)));
            getParentModel().copyPropertiesTo(this.hornsModel);
            getParentModel().getHead().translateAndRotate(poseStack);
            hornsModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}
