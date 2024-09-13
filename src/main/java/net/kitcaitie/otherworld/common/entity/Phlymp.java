package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

public class Phlymp extends OtheranAnimal implements FlyingAnimal, PlayerRideableJumping, Saddleable {
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(Phlymp.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState JUMPING = new AnimationState();
    public final AnimationState FLOATING = new AnimationState();
    private float playerJumpPendingScale = 0.0F;

    @Override
    public boolean removeWhenFarAway(double dist) {
        return !this.isPersistenceRequired();
    }

    public Phlymp(EntityType<? extends Phlymp> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.FLOATING.startIfStopped(this.tickCount);
    }

    @Override
    public float getWalkTargetValue(BlockPos p_27788_, LevelReader p_27789_) {
        return p_27789_.getBlockState(p_27788_).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected PathNavigation createNavigation(Level p_21480_) {
        FlyingPathNavigation navigation1 = new FlyingPathNavigation(this, p_21480_);
        navigation1.setCanFloat(true);
        navigation1.setCanOpenDoors(false);
        navigation1.setCanPassDoors(false);
        return navigation1;
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.FLYING_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 30.0D).build();
    }

    public static boolean checkPhlympSpawnRules(EntityType<? extends Phlymp> type, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource source) {
        return source.nextInt(40) == 0;
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.AMBIENT;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BreedGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !Phlymp.this.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Phlymp.this.isVehicle() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(1, new WaterAvoidingRandomFlyingGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !Phlymp.this.isVehicle() && Phlymp.this.getNavigation().isDone() && Phlymp.this.random.nextInt(16) == 0;
            }

            @Override
            public boolean canContinueToUse() {
                return !Phlymp.this.isVehicle() && !Phlymp.this.jumping && !Phlymp.this.navigation.isInProgress();
            }

            @Override
            public void start() {
                if (!Phlymp.this.jumping) {
                    if (random.nextInt(32) == 0) {
                        Phlymp.this.onRiderJump(0.45F, Phlymp.this.getDeltaMovement());
                        return;
                    }
                    Vec3 vec3 = this.getPosition();
                    if (vec3 != null) {
                        Phlymp.this.navigation.moveTo(Phlymp.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0D);
                    }
                }
            }
        });
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !Phlymp.this.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Phlymp.this.isVehicle() && super.canContinueToUse();
            }
        });
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if (p_146748_ == MobSpawnType.NATURAL || p_146748_ == MobSpawnType.CHUNK_GENERATION) {
            this.moveTo(this.getX(), random.nextInt(130, 160), this.getZ());
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.8D;
    }

    protected void updateJump(boolean jumping) {
        if (jumping) {
            if (!hasPose(Pose.LONG_JUMPING)) this.setPose(Pose.LONG_JUMPING);
        }
        else {
            if (!hasPose(Pose.STANDING)) {
                this.setPose(Pose.STANDING);
            }
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            this.setYRot(this.getYRot());
            this.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.5F;
    }

    @Override
    protected void usePlayerItem(Player p_148715_, InteractionHand p_148716_, ItemStack p_148717_) {
        super.usePlayerItem(p_148715_, p_148716_, p_148717_);
        this.setPersistenceRequired();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.PHLYMP.get().create(level);
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }

    @Override
    public void tick() {
        super.tick();

        if (fallDistance > 0.0F) {
            this.jumping = --this.fallDistance > 0;
        }

        this.updateJump(this.jumping);
    }

    @Override
    protected Vec3 getRiddenInput(LivingEntity entity, Vec3 vec3) {
        float d0 = entity.getViewXRot(1.0F);
        double d1 = d0 < -20.0F ? 0.5D : (d0 > 30.0F ? -0.5D : 0.0D);
        return new Vec3(0.0D, d1, 1.0D);
    }


    @Override
    protected float getRiddenSpeed(LivingEntity p_250911_) {
        return (float)(this.getAttributeValue(Attributes.FLYING_SPEED) * 1.8);
    }

    @Override
    protected float getBlockJumpFactor() {
        return 1.0F;
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_POSE)) {
            if (this.getPose() == Pose.LONG_JUMPING) {
                if (level.isClientSide()) {
                    this.FLOATING.stop();
                    this.JUMPING.startIfStopped(this.tickCount);
                }
            } else {
                if (level.isClientSide()) {
                    this.JUMPING.stop();
                    this.FLOATING.startIfStopped(this.tickCount);
                }
            }
        }
    }

    @Override
    protected void handleAnimations() {
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void tickRidden(LivingEntity rider, Vec3 vec3) {
        this.setRot(rider.getYRot(), rider.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        super.tickRidden(rider, vec3);
        if (this.isControlledByLocalInstance()) {
            if (this.playerJumpPendingScale > 0.0F && !this.jumping) {
                this.onRiderJump(this.playerJumpPendingScale, this.getDeltaMovement());
            }
        }
    }

    protected void onRiderJump(float playerJumpPendingScale, Vec3 vec3) {
        double d0 = 0.5D * (double)playerJumpPendingScale * (double)this.getBlockJumpFactor();
        double d1 = d0 + this.getJumpBoostPower();
        this.setDeltaMovement(vec3.x, d1, vec3.z);
        this.hasImpulse = true;
        this.jumping = true;
        this.updateJump(true);
        this.fallDistance = 50.0F;
        ForgeHooks.onLivingJump(this);
        this.playerJumpPendingScale = 0.0F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddled", isSaddled());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(SADDLED, tag.getBoolean("Saddled"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
    }

    @Override
    public void onPlayerJump(int i) {
        if (this.isSaddled()) {
            if (i < 0) {
                i = 0;
            }

            if (i >= 90) {
                this.playerJumpPendingScale = 1.0F;
            } else {
                this.playerJumpPendingScale = 0.4F + 0.4F * (float)i / 90.0F;
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(OtherworldItems.WHISP.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SADDLE) && !this.isVehicle() && !this.isSaddled()) {
            stack.shrink(1);
            this.equipSaddle(SoundSource.NEUTRAL);
            this.setPersistenceRequired();
            return InteractionResult.CONSUME;
        }
        else if (stack.isEmpty() && !this.isVehicle() && this.isSaddled()) {
            this.doPlayerRide(player);
            return InteractionResult.SUCCESS;
        }
        else return super.mobInteract(player, hand);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE.getDefaultInstance());
        }
    }

    @Override
    public boolean canJump() {
        return this.fallDistance <= 0.0F;
    }

    @Override
    public void handleStartJump(int i) {

    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }

    @Override
    public boolean isSaddleable() {
        return !this.isBaby() && !this.isSaddled();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource source) {
        this.entityData.set(SADDLED, true);
        if (source != null) {
            this.level.playSound(null, this, SoundEvents.STRIDER_SADDLE, source, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

}
