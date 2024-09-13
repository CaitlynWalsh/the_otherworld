package net.kitcaitie.otherworld.mixins;

import net.kitcaitie.otherworld.common.entity.npcs.Human;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerMixin {

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void otherworld_finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, SpawnGroupData data, CompoundTag tag, CallbackInfoReturnable<SpawnGroupData> cir) {
        ServerLevel level = accessor.getLevel();
        if (type == MobSpawnType.STRUCTURE && level.getRandom().nextInt(5) == 0) {
            Human human = OtherworldEntities.HUMAN.get().create(level);
            human.moveTo(((Villager) (Object) this).position());
            human.finalizeSpawn(accessor, difficulty, type, null, null);
            level.addFreshEntity(human);
            ((Villager) (Object) this).discard();
        }
    }

}
