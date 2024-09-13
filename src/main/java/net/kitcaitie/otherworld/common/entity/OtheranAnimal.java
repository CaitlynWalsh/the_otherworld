package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public abstract class OtheranAnimal extends Animal {

    public OtheranAnimal(EntityType<? extends OtheranAnimal> type, Level level) {
        super(type, level);
    }

    public static boolean checkSpawnRules(EntityType<? extends Animal> animal, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        return levelAccessor.getBlockState(blockPos.below()).is(OtherworldTags.OTHERAN_SPAWNABLE_ON) && Animal.isBrightEnoughToSpawn(levelAccessor, blockPos);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType type, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if (type == MobSpawnType.NATURAL || type == MobSpawnType.CHUNK_GENERATION) this.setBaby(random.nextInt(10) == 0);
        return super.finalizeSpawn(p_146746_, p_146747_, type, p_146749_, p_146750_);
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            handleAnimations();
        }
        super.tick();
    }

    protected abstract void handleAnimations();

}
