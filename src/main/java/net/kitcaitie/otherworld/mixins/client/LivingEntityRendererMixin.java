package net.kitcaitie.otherworld.mixins.client;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "isShaking", at = @At(value = "RETURN"), cancellable = true)
    protected void injectIsShaking(T entity, CallbackInfoReturnable<Boolean> cir) {
        boolean flag = entity.isFullyFrozen();
        if (entity instanceof AbstractClientPlayer player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (character.isIceian()) flag = false;
            if (character.isFairie()) flag = flag || player.hasEffect(MobEffects.WITHER);
        }
        else if (entity instanceof IRaces races) {
            if (races.isIceian()) flag = false;
            if (races.isFairie()) flag = flag || entity.hasEffect(MobEffects.WITHER);
        }
        cir.setReturnValue(flag);
    }

}
