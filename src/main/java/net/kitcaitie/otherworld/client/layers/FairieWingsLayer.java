package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.model.layers.FairieWings;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairieWingsLayer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/fairie_wings.png");
    public static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/blank64.png");
    public FairieWings<T> fairieWings;

    public FairieWingsLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.fairieWings = new FairieWings<>(modelSet.bakeLayer(FairieWings.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (entity instanceof Player && !OtherworldConfigs.CLIENT.renderLayersOnPlayer.get()) return;
        if (!entity.isInvisible()) {
            poseStack.pushPose();
            getParentModel().copyPropertiesTo(this.fairieWings);
            this.fairieWings.setupAnim(entity, f, f1, f3, f4, f5);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
            this.fairieWings.renderToBuffer(poseStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof AbstractClientPlayer player) {
            if (PowerUtils.accessPlayerCharacter(player).hasWings()) {
                return TEXTURE;
            }
        }
        else if (entity instanceof IRaces races && races.hasWings()) {
            return TEXTURE;
        }
        return BLANK_TEXTURE;
    }
}
