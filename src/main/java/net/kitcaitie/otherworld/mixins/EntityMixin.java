package net.kitcaitie.otherworld.mixins;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "setSecondsOnFire", at=@At("HEAD"), cancellable = true)
    protected void otherworld_setSecondsOnFire(int seconds, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!entity.isInLava() && seconds > 0) {
            if (entity instanceof Player player) {
                if (PowerUtils.accessPlayerCharacter(player).isIceian() && player.isFullyFrozen()) {
                    ci.cancel();
                }
            } else if (entity instanceof IRaces races) {
                if (races.isIceian() && entity.isFullyFrozen()) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method="setTicksFrozen", at=@At("TAIL"))
    protected void otherworld_setTicksFrozen(int ticks, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.isOnFire() && ticks > 0) {
            if (entity instanceof Player player) {
                if (PowerUtils.accessPlayerCharacter(player).isIceian()) {
                    player.clearFire();
                }
            } else if (entity instanceof IRaces races) {
                if (races.isIceian()) entity.clearFire();
            }
        }
    }

}
