package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
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
public class ClothesLayer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {
    private static final String location = "textures/entity/layers/clothes/";
    private static final ResourceLocation BLANK = new ResourceLocation(Otherworld.MODID, "textures/entity/layers/blank64.png");
    public ClothesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource source, int i, T entity, float f, float f1, float f2, float f3, float f4, float f5) {
        if (entity instanceof Player && !OtherworldConfigs.CLIENT.renderClothesOnPlayer.get()) return;
        if (!entity.isInvisible()) {
            VertexConsumer vertexConsumer = source.getBuffer(RenderType.entityTranslucent(getClothingLocation(entity)));
            this.getParentModel().renderToBuffer(stack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public ResourceLocation getClothingLocation(T entity) {
        if (entity instanceof AbstractClientPlayer player) {
            PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
            ResourceLocation resourceLocation = new ResourceLocation(Otherworld.MODID, location + chr.getRace().name().toLowerCase() + "_" + chr.getOccupation().name().toLowerCase() + ".png");
            return Minecraft.getInstance().getResourceManager().getResource(resourceLocation).isPresent() ? resourceLocation : BLANK;
        }
        return BLANK;
    }
}
