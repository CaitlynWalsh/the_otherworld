package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoseianRabbit extends OtheranAnimal implements IForgeShearable {
    public static final Ingredient TEMPT_ITEM = Ingredient.of(OtherworldItems.ROSERYE.get());
    protected static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(RoseianRabbit.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_EATING = SynchedEntityData.defineId(RoseianRabbit.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(RoseianRabbit.class, EntityDataSerializers.LONG);
    protected int eatAnimationTick;
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;
    private boolean interested;

    public AnimationState IDLE = new AnimationState();
    public AnimationState JUMP = new AnimationState();
    public AnimationState EAT = new AnimationState();
    public AnimationState STAND = new AnimationState();
    public AnimationState TRANSITION_STAND = new AnimationState();
    public AnimationState TRANSITION_IDLE = new AnimationState();

    public RoseianRabbit(EntityType<? extends OtheranAnimal> type, Level level) {
        super(type, level);
        this.jumpControl = new RoseianRabbitJumpControl(this);
        this.moveControl = new RoseianRabbitMoveControl(this);
        this.setSpeedModifier(0.0D);
        this.setPose(Pose.SITTING);
    }

    public static AttributeSupplier createAttributes() {
        return Rabbit.createAttributes().build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BreedGoal(this, 1));
        this.goalSelector.addGoal(1, new RoseianRabbitTemptGoal(this, 1, TEMPT_ITEM, false));
        this.goalSelector.addGoal(1, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(2, new EatRosegrassGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return TEMPT_ITEM.test(stack);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean b) {
        this.entityData.set(SHEARED, b);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sheared", this.isSheared());
    }

    public boolean isShearable(ItemStack item, Level level, BlockPos blockPos) {
        return this.readyForShearing();
    }

    public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level level, BlockPos pos, int fortune) {
        this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, player != null ? SoundSource.PLAYERS : SoundSource.BLOCKS, 1.0F, 1.0F);
        this.setSheared(true);
        return List.of(new ItemStack(Items.PINK_WOOL));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHEARED, false);
        this.entityData.define(DATA_EATING, false);
        this.entityData.define(LAST_POSE_CHANGE_TICK, -15L);
    }

    protected void customServerAiStep() {
        if (this.jumpDelayTicks > 0) {
            --this.jumpDelayTicks;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            RoseianRabbit.RoseianRabbitJumpControl rabbit$rabbitjumpcontrol = (RoseianRabbit.RoseianRabbitJumpControl)this.jumpControl;
            if (!rabbit$rabbitjumpcontrol.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    Path path = this.navigation.getPath();
                    Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) {
                        vec3 = path.getNextEntityPos(this);
                    }

                    this.facePoint(vec3.x, vec3.z);
                    this.startJumping();
                }
            } else if (!rabbit$rabbitjumpcontrol.canJump()) {
                this.enableJumpControl();
            }
        }
        this.wasOnGround = this.onGround;
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
        super.aiStep();
    }

    public boolean isEating() {
        return this.entityData.get(DATA_EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(DATA_EATING, eating);
    }

    public boolean isInterested() {
        return this.interested;
    }

    public void setInterested(boolean b) {
        this.interested = b;
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return false;
    }

    private void facePoint(double p_29687_, double p_29688_) {
        this.setYRot((float)(Mth.atan2(p_29688_ - this.getZ(), p_29687_ - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
    }

    private void enableJumpControl() {
        ((RoseianRabbit.RoseianRabbitJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((RoseianRabbit.RoseianRabbitJumpControl)this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        if (this.moveControl.getSpeedModifier() < 2.2D) {
            this.jumpDelayTicks = 10;
        } else {
            this.jumpDelayTicks = 1;
        }

    }

    @Override
    public void tick() {
        if (this.isInPoseTransition()) {
            this.getNavigation().stop();
        }
        super.tick();
    }

    public boolean isInPoseTransition() {
        return (this.level.getGameTime() - this.entityData.get(LAST_POSE_CHANGE_TICK)) < 15L;
    }

    @Override
    protected void handleAnimations() {
        if (!this.isOnGround()) {
            this.IDLE.stop();
            this.EAT.stop();
            this.STAND.stop();
            this.TRANSITION_STAND.stop();
            this.TRANSITION_IDLE.stop();
            this.JUMP.startIfStopped(this.tickCount);
        } else {
            this.JUMP.stop();
        }
        Pose pose = this.getPose();
        if (pose == Pose.STANDING) {
            this.IDLE.stop();
            this.EAT.stop();
            this.TRANSITION_IDLE.stop();
            this.TRANSITION_STAND.animateWhen(this.isInPoseTransition(), this.tickCount);
            this.STAND.animateWhen(!this.isInPoseTransition(), this.tickCount);
        }
        else if (pose == Pose.SITTING) {
            this.TRANSITION_STAND.stop();
            this.STAND.stop();
            this.TRANSITION_IDLE.animateWhen(this.isInPoseTransition(), this.tickCount);
            this.IDLE.animateWhen(!this.isInPoseTransition() && !this.isEating(), this.tickCount);
            this.EAT.animateWhen(!this.isInPoseTransition() && this.isEating(), this.tickCount);
        }
    }

    public void standUp() {
        if (!this.hasPose(Pose.STANDING)) {
            this.setPose(Pose.STANDING);
            this.entityData.set(LAST_POSE_CHANGE_TICK, this.level.getGameTime());
        }
    }

    public void idle() {
        if (!this.hasPose(Pose.SITTING)) {
            this.setPose(Pose.SITTING);
            this.entityData.set(LAST_POSE_CHANGE_TICK, this.level.getGameTime());
        }
    }

    @Override
    public void die(DamageSource source) {
        this.IDLE.stop();
        this.STAND.stop();
        this.JUMP.stop();
        this.EAT.stop();
        this.TRANSITION_STAND.stop();
        this.TRANSITION_IDLE.stop();
        super.die(source);
    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Sheared")) {
            this.setSheared(tag.getBoolean("Sheared"));
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 10) {
            this.eatAnimationTick = 40;
        }
        else if (b == 1) {
            this.spawnSprintParticle();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        }
        else {
            super.handleEntityEvent(b);
        }
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
    }

    @Override
    public RoseianRabbit getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return OtherworldEntities.ROSEIAN_RABBIT.get().create(serverLevel);
    }

    @Override
    public void ate() {
        this.setSheared(false);
        if (isBaby()) {
            this.ageUp(30);
        }
    }

    public ResourceLocation getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        }
        return OtherworldLootTables.ROSEIAN_RABBIT_WOOL;
    }

    public boolean readyForShearing() {
        return !this.isSheared() && !this.isBaby() && !this.isDeadOrDying();
    }

    public void setSpeedModifier(double p_29726_) {
        this.getNavigation().setSpeedModifier(p_29726_);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), p_29726_);
    }


    protected float getJumpPower() {
        if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.5D))) {
            Path path = this.navigation.getPath();
            if (path != null && !path.isDone()) {
                Vec3 vec3 = path.getNextEntityPos(this);
                if (vec3.y > this.getY() + 0.5D) {
                    return 0.5F;
                }
            }

            return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.5F;
        }
    }

    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0D) {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d1 < 0.01D) {
                this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
            }
        }
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)1);
        }
    }


    public void setJumping(boolean p_29732_) {
        super.setJumping(p_29732_);
        if (p_29732_) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.rabbit.ambient"));
    }

    public SoundEvent getJumpSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.rabbit.jump"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.rabbit.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.rabbit.death"));
    }

    public class RoseianRabbitJumpControl extends JumpControl {
        private final RoseianRabbit rabbit;
        private boolean canJump;

        public RoseianRabbitJumpControl(RoseianRabbit p_29757_) {
            super(p_29757_);
            this.rabbit = p_29757_;
        }

        public boolean wantJump() {
            return this.jump;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean p_29759_) {
            this.canJump = p_29759_;
        }

        public void tick() {
            if (this.jump) {
                this.rabbit.startJumping();
                this.jump = false;
            }
        }

    }

    static class RoseianRabbitMoveControl extends MoveControl {
        private final RoseianRabbit rabbit;
        private double nextJumpSpeed;

        public RoseianRabbitMoveControl(RoseianRabbit p_29766_) {
            super(p_29766_);
            this.rabbit = p_29766_;
        }

        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((RoseianRabbit.RoseianRabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
                this.rabbit.setSpeedModifier(0.0D);
            } else if (this.hasWanted()) {
                this.rabbit.setSpeedModifier(this.nextJumpSpeed);
            }

            super.tick();
        }

        public void setWantedPosition(double p_29769_, double p_29770_, double p_29771_, double p_29772_) {
            if (this.rabbit.isInWater()) {
                p_29772_ = 1.5D;
            }

            super.setWantedPosition(p_29769_, p_29770_, p_29771_, p_29772_);
            if (p_29772_ > 0.0D) {
                this.nextJumpSpeed = p_29772_;
            }

        }
    }

    class EatRosegrassGoal extends EatBlockGoal {
        protected final RoseianRabbit roseianRabbit;

        public EatRosegrassGoal(RoseianRabbit roseianRabbit) {
            super(roseianRabbit);
            this.roseianRabbit = roseianRabbit;
        }

        @Override
        public boolean canUse() {
            if (roseianRabbit.getRandom().nextInt(roseianRabbit.isBaby() ? 50 : 800) != 0)
                return false;
            return roseianRabbit.getBlockStateOn().is(OtherworldBlocks.ROSEGRASS_BLOCK.get()) && roseianRabbit.isOnGround() && !roseianRabbit.isInterested();
        }

        @Override
        public void start() {
            roseianRabbit.eatAnimationTick = this.adjustedTickDelay(40);
            roseianRabbit.setEating(true);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            return roseianRabbit.eatAnimationTick > 0;
        }

        @Override
        public void stop() {
            roseianRabbit.eatAnimationTick = 0;
            roseianRabbit.setEating(false);
            super.stop();
        }

        @Override
        public void tick() {
            roseianRabbit.getNavigation().stop();
            roseianRabbit.eatAnimationTick = Math.max(0, roseianRabbit.eatAnimationTick - 1);
            if (roseianRabbit.eatAnimationTick == this.adjustedTickDelay(4)) {
                if (roseianRabbit.getBlockStateOn().is(OtherworldBlocks.ROSEGRASS_BLOCK.get())) {
                    roseianRabbit.ate();
                }
            }
        }
    }

    public class RoseianRabbitTemptGoal extends TemptGoal {
        private final RoseianRabbit roseianRabbit;

        public RoseianRabbitTemptGoal(RoseianRabbit p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_) {
            super(p_25939_, p_25940_, p_25941_, p_25942_);
            this.roseianRabbit = p_25939_;
        }

        @Override
        public void start() {
            roseianRabbit.setInterested(true);
            super.start();
        }

        @Override
        public void tick() {
            if (this.player != null && roseianRabbit.distanceToSqr(this.player) < 6.25D) {
                roseianRabbit.standUp();
            }
            else {
                roseianRabbit.idle();
            }
            super.tick();
        }

        @Override
        public void stop() {
            roseianRabbit.setInterested(false);
            roseianRabbit.idle();
            super.stop();
        }
    }

}
