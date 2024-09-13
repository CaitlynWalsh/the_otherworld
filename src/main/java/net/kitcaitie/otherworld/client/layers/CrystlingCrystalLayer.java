package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.kitcaitie.otherworld.client.model.ModelCrystling;
import net.kitcaitie.otherworld.common.entity.Crystling;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystlingCrystalLayer<T extends Crystling> extends RenderLayer<T, ModelCrystling<T>> {
    private final BlockRenderDispatcher blockRenderer;

    public CrystlingCrystalLayer(RenderLayerParent<T, ModelCrystling<T>> parent, BlockRenderDispatcher dispatcher) {
        super(parent);
        this.blockRenderer = dispatcher;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, T entity, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
        if (!entity.isBaby()) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
            if (!entity.isInvisible() || flag) {
                BlockState blockState = OtherworldBlocks.ROSE_QUARTZ_CRYSTAL.get().defaultBlockState();
                BakedModel bakedModel = this.blockRenderer.getBlockModel(blockState);
                poseStack.pushPose();
                this.getParentModel().root().translateAndRotate(poseStack);
                this.getParentModel().body().translateAndRotate(poseStack);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(0.0F, -0.34375F, 0.0F);
                poseStack.scale(0.625F, -0.625F, -0.625F);
                poseStack.translate(-0.5F, -0.45F, -0.5F);
                this.renderCrystal(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, flag, blockState, bakedModel);
                poseStack.popPose();
            }
        }
    }

    protected void renderCrystal(PoseStack poseStack, MultiBufferSource bufferSource, int i, int i1, boolean b, BlockState blockState, BakedModel model) {
        if (b) {
            this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), blockState, model, 0.0F, 0.0F, 0.0F, i, i1);
        } else {
            this.blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, i, i1);
        }
    }

}
