package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.ActionUtils;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Goateer extends OtheranAnimal {
    public AnimationState IDLE = new AnimationState();
    public AnimationState WALK = new AnimationState();

    public Goateer(EntityType<? extends Goateer> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D) {
            @Override
            public boolean canUse() {
                return Goateer.this.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(1, new GoateerRamGoal(this, 1.8D, 4.0D));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !Goateer.this.isAggressive() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Goateer.this.isAggressive() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !Goateer.this.isAggressive() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Goateer.this.isAggressive() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !Goateer.this.isAggressive() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Goateer.this.isAggressive() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return !Goateer.this.isAggressive() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Goateer.this.isAggressive() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !Goateer.this.isAggressive() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !Goateer.this.isAggressive() && super.canContinueToUse();
            }
        });

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this) {
            @Override
            public void start() {
                super.start();
                Goateer.this.setAggressive(true);
            }
        });
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAggressive() ? null : SoundEvents.GOAT_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GOAT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GOAT_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.5F;
    }

    @Override
    public boolean canDisableShield() {
        return !this.isBaby();
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
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5F)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_KNOCKBACK, 0.6F)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .build();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.GOATEER.get().create(level);
    }

    static class GoateerRamGoal extends Goal {
        protected final Goateer goateer;
        protected final double speed;
        protected final double knockbackForce;
        protected TargetingConditions targetingConditions;

        protected LivingEntity target;
        protected int ramCooldown;
        protected int stuckTime;
        private Vec3 ramDirection;
        private boolean ramming;
        private int ramTimes;
        private Path lastPath;

        public GoateerRamGoal(Goateer goateer, double speed, double knockbackForce) {
            this.goateer = goateer;
            this.speed = speed;
            this.knockbackForce = knockbackForce;
        }

        @Override
        public boolean canUse() {
            if (goateer.isBaby() && goateer.random.nextInt(400) == 0) {
                List<Goateer> list = goateer.level.getEntitiesOfClass(Goateer.class, goateer.getBoundingBox().inflate(10.0D), AgeableMob::isBaby);
                if (list.isEmpty()) return false;
                this.target = list.get(0);
                return true;
            }
            else if (goateer.isAggressive() && goateer.getTarget() != null) {
                this.target = goateer.getTarget();
                return target.isAlive() && goateer.canAttack(target);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return goateer.isAggressive() && stuckTime < 400 && this.target != null && this.target.isAlive() && targetingConditions.test(goateer, target);
        }

        @Override
        public void start() {
            super.start();
            this.targetingConditions = (goateer.isBaby() ? TargetingConditions.forNonCombat() : TargetingConditions.forCombat()).range(35.0D).selector((ent) -> ent.is(target)).ignoreLineOfSight();
            goateer.getNavigation().stop();
            goateer.setAggressive(true);
            this.ramCooldown = 0;
            this.stuckTime = 0;
            updateDirection();
        }

        protected void updateDirection() {
            BlockPos blockpos = goateer.blockPosition();
            Vec3 vec3 = target.position();
            this.ramDirection = (new Vec3((double)blockpos.getX() - vec3.x(), 0.0D, (double)blockpos.getZ() - vec3.z())).normalize();
        }

        @Override
        public void tick() {
            if (ramCooldown <= 0) {
                List<LivingEntity> list = goateer.level.getNearbyEntities(LivingEntity.class, targetingConditions, goateer, goateer.getBoundingBox());
                if (!list.isEmpty()) {
                    boolean blocked = target.isDamageSourceBlocked(goateer.damageSources().mobAttack(goateer));
                    if (!ramming) updateDirection();
                    target.hurt(goateer.damageSources().noAggroMobAttack(goateer), (float) (goateer.isBaby() ? 0.0F : goateer.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                    if (blocked && goateer.canDisableShield() && target instanceof Player player) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        this.goateer.level.broadcastEntityEvent(player, (byte)30);
                    }
                    int i = goateer.hasEffect(MobEffects.MOVEMENT_SPEED) ? goateer.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
                    int j = goateer.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? goateer.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
                    float f = 0.25F * (float) (i - j);
                    float f1 = Mth.clamp(goateer.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
                    float f2 = blocked ? 0.5F : 1.0F;
                    target.knockback((double) (f2 * f1) * (goateer.isBaby() ? knockbackForce / 4.0D : knockbackForce), this.ramDirection.x(), this.ramDirection.z());
                    this.ramCooldown = 200;
                    this.stuckTime = 0;
                    this.ramTimes++;
                    if (ramming) {
                        goateer.playSound(SoundEvents.GOAT_RAM_IMPACT, 1.0F, goateer.getVoicePitch());
                        this.ramming = false;
                    }
                    if (goateer.isBaby() || ramTimes >= 4) {
                        goateer.setAggressive(false);
                    }
                }
                else {
                    if (!ramming) {
                        goateer.getNavigation().stop();
                        this.ramming = true;
                        goateer.playSound(SoundEvents.GOAT_PREPARE_RAM, 1.0F, goateer.getVoicePitch());
                    }

                    ++stuckTime;

                    if (stuckTime % 20 == 1) {
                        this.lastPath = null;
                    }

                    PathNavigation navigation1 = this.goateer.getNavigation();
                    if (!navigation1.isInProgress() || this.lastPath == null || !Objects.equals(navigation1.getPath(), lastPath) || navigation1.isStuck()) {
                        updateDirection();
                        this.lastPath = this.goateer.getNavigation().createPath(this.target, 0);
                        this.goateer.getNavigation().moveTo(lastPath, this.speed);
                    }
                }

                ActionUtils.lookAt(this.goateer, target.blockPosition().below(60));
            }
            else {
                if (!goateer.isBaby()) {
                    ActionUtils.lookAndTurnTo(this.goateer, this.target);
                    if (goateer.random.nextInt(60) == 0) {
                        goateer.playSound(SoundEvents.HORSE_BREATHE, 1.0F, 1.0F);
                    }
                }
                --ramCooldown;
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.goateer.setAggressive(false);
            this.goateer.setTarget(null);
            this.target = null;
            this.ramCooldown = 0;
            this.stuckTime = 0;
            this.ramDirection = null;
            this.ramTimes = 0;
            this.ramming = false;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return target != null;
        }
    }
}
