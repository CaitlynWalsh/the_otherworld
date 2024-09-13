package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Grizzly extends OtheranAnimal implements AggressiveAnimal {
    protected static final Ingredient FOODS = Ingredient.of(OtherworldItems.THORNBERRY.get());
    protected static final Predicate<LivingEntity> ATTACKABLES = (entity) -> entity instanceof AbstractPerson || entity instanceof Player || entity instanceof AbstractVillager || entity instanceof AbstractIllager;
    public static final EntityDataAccessor<Integer> ATTACK_DELAY_TICKS = SynchedEntityData.defineId(Grizzly.class, EntityDataSerializers.INT);
    public AnimationState IDLE = new AnimationState();
    public AnimationState WALK = new AnimationState();
    public AnimationState AGGRO_WALK = new AnimationState();
    public AnimationState ATTACK = new AnimationState();

    public Grizzly(EntityType<? extends Grizzly> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 0.9D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, AbstractPerson.class, 16.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, AbstractVillager.class, 16.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, AbstractIllager.class, 16.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return !Grizzly.this.isBaby() && super.canUse();
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, ATTACKABLES) {
            @Override
            public boolean canUse() {
                return !Grizzly.this.isBaby() && super.canUse();
            }
        });
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOODS.test(stack);
    }

    @Override
    public void aiStep() {
        this.updateAttackTime();
        super.aiStep();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attack();
        return super.doHurtTarget(entity);
    }

    protected void updateAttackTime() {
        int i = this.entityData.get(ATTACK_DELAY_TICKS);
        if (i > 0) {
            this.entityData.set(ATTACK_DELAY_TICKS, --i);
            if (this.entityData.get(ATTACK_DELAY_TICKS) <= 0) {
                this.setPose(Pose.STANDING);
            }
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isAlive()) {
            if (this.isAttacking()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
                vec3 = vec3.multiply(0.0D, 1.0D, 0.0D);
            }
            super.travel(vec3);
        }
    }

    @Override
    protected void handleAnimations() {
        if (this.isAttacking()) {
            this.IDLE.stop();
            this.WALK.stop();
            this.AGGRO_WALK.stop();
            this.ATTACK.startIfStopped(this.tickCount);
        }
        else if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.IDLE.stop();
            this.ATTACK.stop();
            if (this.isAggressive()) {
                this.WALK.stop();
                this.AGGRO_WALK.startIfStopped(this.tickCount);
            }
            else {
                this.AGGRO_WALK.stop();
                this.WALK.startIfStopped(this.tickCount);
            }
        }
        else {
            this.ATTACK.stop();
            this.WALK.stop();
            this.AGGRO_WALK.stop();
            this.IDLE.startIfStopped(this.tickCount);
        }
    }

    public boolean isAttacking() {
        return this.getPose() == Pose.ROARING;
    }

    public void attack() {
        if (!isAttacking()) {
            this.setPose(Pose.ROARING);
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, this.getSoundVolume(), this.getVoicePitch());
            this.entityData.set(ATTACK_DELAY_TICKS, 40);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_DELAY_TICKS, 0);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.MAX_HEALTH,  40.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
                .build();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.GRIZZLY.get().create(level);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBaby()) {
            return SoundEvents.POLAR_BEAR_AMBIENT_BABY;
        }
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.8F, this.getVoicePitch());
    }
}
