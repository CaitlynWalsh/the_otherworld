package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Pyroboar extends OtheranAnimal implements RangedAttackMob {
    protected static final Predicate<LivingEntity> RANGE_ATTACK_TARGET = (entity) -> {
        if (entity instanceof AbstractPerson person) {
            return !person.isEmberian();
        }
        else if (entity instanceof Player player) {
            return !PowerUtils.accessPlayerCharacter(player).isEmberian();
        }
        return true;
    };
    public AnimationState IDLE = new AnimationState();
    public AnimationState WALK = new AnimationState();

    public Pyroboar(EntityType<? extends Pyroboar> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.9D, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !RANGE_ATTACK_TARGET.test(Pyroboar.this.getTarget());
            }
        });
        this.goalSelector.addGoal(0, new RangedAttackGoal(this, 1.25D, 40, 20.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && RANGE_ATTACK_TARGET.test(Pyroboar.this.getTarget());
            }
        });
        this.goalSelector.addGoal(1, new BreedGoal(this, 0.9D));
        this.goalSelector.addGoal(1, new FollowParentGoal(this, 0.9D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null;
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float v) {
        shootFireball(entity);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOGLIN_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SoundEvents.HOGLIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOGLIN_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() + 0.35F;
    }

    private void shootFireball(LivingEntity target) {
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3D) - this.getY();
        double d2 = target.getZ() - this.getZ();
        SmallFireball fireball = new SmallFireball(this.level, this, d0, d1, d2);
        fireball.setPos(this.getX() - (double)(this.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(this.yBodyRot * ((float)Math.PI / 180F)), this.getEyeY() - (double)0.1F, this.getZ() + (double)(this.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(this.yBodyRot * ((float)Math.PI / 180F)));
        if (!this.isSilent()) {
            this.playSound(SoundEvents.BLAZE_SHOOT, this.getSoundVolume(), this.getVoicePitch());
        }
        level.addFreshEntity(fireball);
    }

    @Override
    protected void handleAnimations() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.IDLE.stop();
            this.WALK.startIfStopped(this.tickCount);
        }
        else {
            this.WALK.stop();
            this.IDLE.startIfStopped(this.tickCount);
        }
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.4D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .build();
    }
}
