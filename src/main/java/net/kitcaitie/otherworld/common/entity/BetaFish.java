package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BetaFish extends AbstractSchoolingFish {
    private static final Predicate<LivingEntity> ATTACKABLES = (entity) -> {
        if (entity instanceof BetaFish) return false;
        return entity instanceof AbstractSchoolingFish || (entity instanceof Slime slime && slime.getSize() <= 1);
    };
    protected static final EntityDataAccessor<String> BASE_COLOR = SynchedEntityData.defineId(BetaFish.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> FIN_COLOR = SynchedEntityData.defineId(BetaFish.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(BetaFish.class, EntityDataSerializers.LONG);

    public AnimationState IDLE = new AnimationState();
    public AnimationState AGGRO = new AnimationState();
    public AnimationState EXPAND_GILLS = new AnimationState();

    public BetaFish(EntityType<? extends BetaFish> type, Level level) {
        super(type, level);
    }

    public DyeColor getBaseColor() {
        return DyeColor.byName(this.entityData.get(BASE_COLOR), DyeColor.WHITE);
    }

    public DyeColor getFinColor() {
        return DyeColor.byName(this.entityData.get(FIN_COLOR), DyeColor.WHITE);
    }

    public void setBaseColor(DyeColor dyeColor) {
        this.entityData.set(BASE_COLOR, dyeColor.getName());
    }

    public void setFinColor(DyeColor dyeColor) {
        this.entityData.set(FIN_COLOR, dyeColor.getName());
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, ATTACKABLES));
        this.goalSelector.addGoal(0, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 40));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        DyeColor[] colors = DyeColor.values();
        this.setBaseColor(colors[this.random.nextInt(colors.length)]);
        this.setFinColor(colors[this.random.nextInt(colors.length)]);
        this.entityData.set(LAST_POSE_CHANGE_TICK, this.level.getGameTime() - 32L);
        return super.finalizeSpawn(accessor, instance, spawnType, data, tag);
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return OtherworldItems.FIGHTING_FISH_BUCKET.get().getDefaultInstance();
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("BaseColorTag", getBaseColor().getName());
        tag.putString("FinColorTag", getFinColor().getName());
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        super.loadFromBucketTag(tag);
        if (tag.contains("BaseColorTag")) {
            this.entityData.set(BASE_COLOR, tag.getString("BaseColorTag"));
            this.entityData.set(FIN_COLOR, tag.getString("FinColorTag"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("BaseColor", this.entityData.get(BASE_COLOR));
        tag.putString("FinColor", this.entityData.get(FIN_COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(BASE_COLOR, tag.getString("BaseColor"));
        this.entityData.set(FIN_COLOR, tag.getString("FinColor"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BASE_COLOR, DyeColor.WHITE.getName());
        this.entityData.define(FIN_COLOR, DyeColor.WHITE.getName());
        this.entityData.define(LAST_POSE_CHANGE_TICK, -30L);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            this.handleAnimations();
        }
        super.tick();
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isInflatingGills() && this.isInWaterOrBubble()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 0.0D, 0.0D));
            vec3 = vec3.multiply(0.0D, 0.0D, 0.0D);
        }
        super.travel(vec3);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    public void setAggressive(boolean aggro) {
        if (aggro && isInWaterOrBubble()) {
            this.inflateGills();
        }
        super.setAggressive(aggro);
    }

    public static AttributeSupplier setAttributes() {
        return TropicalFish.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.5F)
                .build();
    }

    public static boolean checkBetaFishSpawnRules(EntityType<? extends BetaFish> type, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        return levelAccessor.getFluidState(blockPos).is(FluidTags.WATER) && (levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) || levelAccessor.getBlockState(blockPos.below()).is(BlockTags.DIRT));
    }

    @Override
    public boolean canBeFollowed() {
        return false;
    }

    @Override
    protected boolean canRandomSwim() {
        return true;
    }

    public boolean isInflatingGills() {
        return (this.level.getGameTime() - this.entityData.get(LAST_POSE_CHANGE_TICK)) < 30L;
    }

    protected void inflateGills() {
        if (!this.isInflatingGills()) {
            this.entityData.set(LAST_POSE_CHANGE_TICK, this.level.getGameTime());
        }
    }

    protected void handleAnimations() {
        if (this.isInWaterOrBubble()) {
            if (this.isInflatingGills()) {
                this.IDLE.stop();
                this.AGGRO.stop();
                this.EXPAND_GILLS.startIfStopped(this.tickCount);
                return;
            }
            else if (this.isAggressive()) {
                this.IDLE.stop();
                this.EXPAND_GILLS.stop();
                this.AGGRO.startIfStopped(this.tickCount);
                return;
            }
        }
        this.AGGRO.stop();
        this.EXPAND_GILLS.stop();
        this.IDLE.startIfStopped(this.tickCount);
    }
}
