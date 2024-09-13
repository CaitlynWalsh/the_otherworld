package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Rosadillo extends OtheranAnimal {
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Rosadillo.class, EntityDataSerializers.LONG);
    public static final EntityDataAccessor<Integer> UNCURL_DELAY_TICKS = SynchedEntityData.defineId(Rosadillo.class, EntityDataSerializers.INT);
    public static final int CURL_UNCURL_ANIMATION_TICKS = 36;
    public AnimationState CURL = new AnimationState();
    public AnimationState IN_SHELL = new AnimationState();
    public AnimationState UNCURL = new AnimationState();
    public AnimationState IDLE = new AnimationState();
    public AnimationState WALK = new AnimationState();


    public Rosadillo(EntityType<? extends OtheranAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LAST_POSE_CHANGE_TICK, -36L);
        this.entityData.define(UNCURL_DELAY_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsCurled", this.getPose() == Pose.SITTING);
        tag.putInt("UncurlDelay", this.entityData.get(UNCURL_DELAY_TICKS));
        tag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(UNCURL_DELAY_TICKS, tag.getInt("UncurlDelay"));
        if (tag.getBoolean("IsCurled")) {
            this.curl(getUncurlDelayTicks());
        }
        this.setLastPoseChangeTick(tag.getLong("LastPoseTick"));
    }

    public boolean refuseToMove() {
        return this.getPose() == Pose.SITTING;
    }

    public boolean isInPoseTransition() {
        return (this.level.getGameTime() - this.entityData.get(LAST_POSE_CHANGE_TICK)) < 36L;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.refuseToMove() && source.is(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }
        else if (this.refuseToMove() && source.getEntity() != null) {
            return super.hurt(source, amount / 2);
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void actuallyHurt(DamageSource source, float v) {
        super.actuallyHurt(source, v);
        if (!this.refuseToMove() && !this.isInPoseTransition()) {
            if ((source.getEntity() != null && source.getEntity() instanceof LivingEntity) || source.is(DamageTypeTags.IS_PROJECTILE)) {
                this.curl(300);
            }
        }
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide) {
            this.goalSelector.setControlFlag(Goal.Flag.LOOK, !this.refuseToMove() && !this.isInPoseTransition());
            this.goalSelector.setControlFlag(Goal.Flag.MOVE, !this.refuseToMove() && !this.isInPoseTransition());
            if (!this.level.isDay() && !this.refuseToMove() && !this.isInPoseTransition()) {
                if (this.getRandom().nextInt(100) == 0) {
                    this.curl(300);
                }
            } else {
                if (getUncurlDelayTicks() == 0) {
                    if (this.level.isDay() && this.refuseToMove() && !this.isInPoseTransition()) {
                        if (this.getRandom().nextInt(100) == 0) {
                            this.uncurl();
                        }
                    }
                }
            }
        }
        super.aiStep();
        this.updateUncurlDelay();
    }

    public void curl(int delayTicks) {
        if (!this.hasPose(Pose.SITTING)) {
            this.setPose(Pose.SITTING);
            this.setLastPoseChangeTick(this.level.getGameTime());
            this.entityData.set(UNCURL_DELAY_TICKS, delayTicks);
        }
    }

    public void uncurl() {
        if (!this.hasPose(Pose.STANDING)) {
            this.setPose(Pose.STANDING);
            this.setLastPoseChangeTick(this.level.getGameTime());
            this.entityData.set(UNCURL_DELAY_TICKS, 0);
        }
    }

    public void updateUncurlDelay() {
        if (getUncurlDelayTicks() > 0) {
            this.entityData.set(UNCURL_DELAY_TICKS, getUncurlDelayTicks() - 1);
        }
    }

    public int getUncurlDelayTicks() {
        return this.entityData.get(UNCURL_DELAY_TICKS);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isAlive()) {
            if ((this.refuseToMove() || this.isInPoseTransition()) && this.isOnGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
                vec3 = vec3.multiply(0.0D, 1.0D, 0.0D);
            }

            super.travel(vec3);
        }
    }

    @Override
    protected void handleAnimations() {
        Pose pose = this.getPose();
        if (pose == Pose.SITTING) {
            this.IDLE.stop();
            this.WALK.stop();
            this.UNCURL.stop();
            if (this.isInPoseTransition()) {
                this.IN_SHELL.stop();
                this.CURL.startIfStopped(this.tickCount);
            }
            else {
                this.CURL.stop();
                this.IN_SHELL.startIfStopped(this.tickCount);
            }
        } else if (pose == Pose.STANDING) {
            this.IN_SHELL.stop();
            this.CURL.stop();
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && !this.isInPoseTransition()) {
                this.UNCURL.stop();
                this.IDLE.stop();
                this.WALK.startIfStopped(this.tickCount);
            }
            else if (this.isInPoseTransition()) {
                this.IDLE.stop();
                this.WALK.stop();
                this.UNCURL.startIfStopped(this.tickCount);
            }
            else {
                this.UNCURL.stop();
                this.WALK.stop();
                this.IDLE.startIfStopped(this.tickCount);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        this.setPose(Pose.STANDING);
        this.setLastPoseChangeTick(this.level.getGameTime() - 32L);
        return super.finalizeSpawn(levelAccessor, instance, spawnType, groupData, tag);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ARMOR, 15.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5F)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0F)
                .build();
    }

    private void setLastPoseChangeTick(long time) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, time);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.ROSADILLO.get().create(level);
    }

}
