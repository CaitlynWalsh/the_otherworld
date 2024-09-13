package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Snowpaka extends AbstractChestedHorse {
    public static final Ingredient FOOD = Ingredient.of(OtherworldItems.ICEBRUSSEL.get());

    public final AnimationState IDLE = new AnimationState();
    public final AnimationState WALK = new AnimationState();
    public final AnimationState SPRINT = new AnimationState();
    public final AnimationState JUMP = new AnimationState();
    public final AnimationState FINISH_JUMP = new AnimationState();
    private boolean wasJumping;
    public Snowpaka(EntityType<? extends Snowpaka> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected int getInventorySize() {
        return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30555_, DifficultyInstance p_30556_, MobSpawnType type, @Nullable SpawnGroupData p_30558_, @Nullable CompoundTag p_30559_) {
        if (type == MobSpawnType.NATURAL || type == MobSpawnType.CHUNK_GENERATION) this.setBaby(random.nextInt(10) == 0);
        return super.finalizeSpawn(p_30555_, p_30556_, type, p_30558_, p_30559_);
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public boolean isSaddled() {
        return this.isTamed();
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.45D;
    }

    protected boolean handleEating(Player p_30796_, ItemStack p_30797_) {
        int i = 0;
        int j = 0;
        float f = 0.0F;
        boolean flag = false;
        if (FOOD.test(p_30797_)) {
            i = 10;
            j = 3;
            f = 2.0F;
            if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
                flag = true;
                this.setInLove(p_30796_);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && i > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.ageUp(i);
            }

            flag = true;
        }

        if (j > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            flag = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(j);
            }
        }

        if (flag && !this.isSilent()) {
            SoundEvent soundevent = this.getEatingSound();
            if (soundevent != null) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

        return flag;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD.test(stack);
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.LLAMA_EAT;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.25F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 0.8F);
    }

    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.8F);
    }

    @Nullable
    @Override
    public Snowpaka getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.SNOWPAKA.get().create(level);
    }

    @Override
    public boolean canMate(Animal animal) {
        return animal != this && animal instanceof Snowpaka && this.isInLove() && animal.isInLove() && this.getAge() == 0 && animal.getAge() == 0;
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            this.handleAnimations();
        }
        super.tick();
    }

    private void handleAnimations() {
        if (this.getControllingPassenger() != null) {
            if (this.isJumping()) {
                this.IDLE.stop();
                this.WALK.stop();
                this.SPRINT.stop();
                this.FINISH_JUMP.stop();
                this.JUMP.startIfStopped(this.tickCount);
                this.wasJumping = true;
            }
            else if (this.wasJumping) {
                this.IDLE.stop();
                this.WALK.stop();
                this.SPRINT.stop();
                this.JUMP.stop();
                this.FINISH_JUMP.startIfStopped(this.tickCount);
                this.wasJumping = false;
            }
            else {
                this.handleIdleAndWalkingAnim(true);
            }
        }
        else {
            this.handleIdleAndWalkingAnim(false);
        }
    }

    private void handleIdleAndWalkingAnim(boolean sprinting) {
        this.JUMP.stop();
        this.FINISH_JUMP.stop();
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.IDLE.stop();
            if (sprinting) {
                this.WALK.stop();
                this.SPRINT.startIfStopped(this.tickCount);
            }
            else {
                this.SPRINT.stop();
                this.WALK.startIfStopped(this.tickCount);
            }
        }
        else {
            this.WALK.stop();
            this.SPRINT.stop();
            this.IDLE.startIfStopped(this.tickCount);
        }
    }
}
