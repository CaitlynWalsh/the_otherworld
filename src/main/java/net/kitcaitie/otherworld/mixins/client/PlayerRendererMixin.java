package net.kitcaitie.otherworld.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Shadow protected abstract void setModelProperties(AbstractClientPlayer p_117819_);

    @Shadow public abstract ResourceLocation getTextureLocation(AbstractClientPlayer p_117783_);

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    protected void otherworld_getPlayerTextureLocation(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation loc = otherworld_getCharacterTexture(player);
        if (loc != null) {
            cir.setReturnValue(loc);
        }
    }

    @Inject(method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("RETURN"))
    protected void otherworld_scale(AbstractClientPlayer player, PoseStack stack, float f, CallbackInfo ci) {
        if (!PowerUtils.accessPlayerCharacter(player).isMale()) {
            stack.scale(0.95F, 0.95F, 0.95F);
        }
        if (PowerUtils.accessPlayerCharacter(player).isOni()) {
            stack.scale(1.05F, 1.05F, 1.05F);
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    protected void otherworld_renderHand(PoseStack stack, MultiBufferSource source, int i, AbstractClientPlayer player, ModelPart part, ModelPart part1, CallbackInfo ci) {
        ci.cancel();
        PlayerModel<AbstractClientPlayer> playermodel =  ((PlayerRenderer)(Object)this).getModel();
        setModelProperties(player);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        part.xRot = 0.0F;
        part.render(stack, source.getBuffer(RenderType.entitySolid(getTextureLocation(player))), i, OverlayTexture.NO_OVERLAY);
        part1.xRot = 0.0F;
        part1.render(stack, source.getBuffer(RenderType.entityTranslucent(getTextureLocation(player))), i, OverlayTexture.NO_OVERLAY);
    }

    @Unique
    private @Nullable ResourceLocation otherworld_getCharacterTexture(AbstractClientPlayer player) {
        PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
        if (!chr.isHuman() && OtherworldConfigs.CLIENT.useCharacterTextures.get()) {
            String slim = player.getModelName().equals("slim") ? "_slim" : "";
            String male = chr.isMale() ? "_male" : "_female";
            return new ResourceLocation(Otherworld.MODID, "textures/entity/player/" + chr.getRace().name().toLowerCase() + chr.getTextureId() + male + slim + ".png");
        }
        return null;
    }

}
