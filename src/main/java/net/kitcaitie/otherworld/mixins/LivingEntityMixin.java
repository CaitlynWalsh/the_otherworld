package net.kitcaitie.otherworld.mixins;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void indicateDamage(double p_270514_, double p_270826_);

    @Shadow protected abstract int increaseAirSupply(int p_21307_);

    @Inject(method = "tryAddFrost", at = @At("HEAD"), cancellable = true)
    protected void otherworld_tryAddFrost(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (PowerUtils.accessPlayerCharacter(player).hasFreezeResistance()) {
                ci.cancel();
            }
        }
        else if (entity instanceof IRaces races && races.hasFreezeResistance()) ci.cancel();
    }

    @Inject(method = "hurt", at= @At("HEAD"), cancellable = true)
    protected void otherworld_hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level.isClientSide()) {
            if (entity instanceof Player player) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                if (character.healsFrom(source)) {
                    player.heal(amount);
                    cir.setReturnValue(false);
                    return;
                }
                if (PowerUtils.handleDamageTrigger(character, source, player)) {
                    cir.setReturnValue(false);
                }
            } else if (entity instanceof IRaces races) {
                if (races.healsFrom(source)) {
                    entity.heal(amount);
                    cir.setReturnValue(false);
                    return;
                }
                if (PowerUtils.handleDamageTrigger(races, source, entity)) {
                    cir.setReturnValue(false);
                }
            }
            else if (entity instanceof Mob) {
                Entity entity1 = source.getEntity();
                if (entity1 instanceof Player player) {
                    PowerUtils.handleDamageTrigger(PowerUtils.accessPlayerCharacter(player), source, entity);
                }
                else if (entity1 instanceof IRaces races) {
                    PowerUtils.handleDamageTrigger(races, source, entity);
                }
            }
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at= @At("RETURN"), cancellable = true)
    protected void otherworld_canAttack(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living.getMobType() == MobType.UNDEAD && entity instanceof Player player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (character.isGhoul()) {
                cir.setReturnValue(cir.getReturnValue() && living.getLastHurtByMob() == entity);
            }
        }
    }

    @Inject(method = "getMobType", at=@At("RETURN"), cancellable = true)
    protected void otherworld_getMobType(CallbackInfoReturnable<MobType> cir) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living instanceof Player player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (character.isGhoul()) {
                cir.setReturnValue(MobType.UNDEAD);
            }
        }
    }

    @Inject(method = "getDimensions", at=@At("RETURN"), cancellable = true)
    protected void otherworld_getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (living instanceof Player player) {
            PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
            if (chr.isTransformed()) {
                if (chr.isIceian()) {
                    cir.setReturnValue(cir.getReturnValue().scale(2.0F));
                }
            }
        }
        else if (living instanceof IRaces races) {
            if (races.isTransformed()) {
                if (races.isIceian()) {
                    cir.setReturnValue(cir.getReturnValue().scale(2.0F));
                }
            }
        }
    }

}
