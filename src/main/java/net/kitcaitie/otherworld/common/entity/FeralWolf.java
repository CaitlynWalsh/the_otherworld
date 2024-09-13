package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FeralWolf extends Wolf implements AggressiveAnimal {
    private static final EntityDataAccessor<Boolean> HOWLING = SynchedEntityData.defineId(FeralWolf.class, EntityDataSerializers.BOOLEAN);
    public static final List<Class<? extends LivingEntity>> PREY = List.of(AbstractPerson.class, Player.class, Sheep.class, Rabbit.class, Fox.class);
    private static final Predicate<LivingEntity> PREY_SELECTOR = (entity) -> PREY.contains(entity.getClass()) || PREY.contains(entity.getClass().getSuperclass());
    private int howlTime;

    public FeralWolf(EntityType<? extends FeralWolf> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ARMOR, 4.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    public boolean isHowling() {
        return this.entityData.get(HOWLING);
    }

    public void setHowling(boolean howling) {
        this.entityData.set(HOWLING, howling);
    }

    @Override
    public void customServerAiStep() {
        if (!level.isClientSide()) {
            if (howlTime > 0) {
                if (this.isHowling()) {
                    this.goalSelector.enableControlFlag(Goal.Flag.LOOK);
                    this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                    this.getNavigation().stop();
                    this.getLookControl().setLookAt(this.position().x, this.getEyeY() + 2.0F, this.position().z);
                }
                --howlTime;
                if (howlTime <= 0) {
                    this.goalSelector.disableControlFlag(Goal.Flag.LOOK);
                    this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
                }
            }

            if (level.isNight() && this.getTarget() == null && this.level.getMoonBrightness() > 0.9F) {
                if (!isHowling()) {
                    this.setHowling(true);
                }
            }
            else if (this.isHowling()) this.setHowling(false);
        }
        super.customServerAiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOWLING, false);
    }

    @Override
    public boolean isAngry() {
        return true;
    }

    @Override
    public boolean isTame() {
        return false;
    }

    @Override
    public void setTame(boolean p_30443_) {
    }

    @Override
    public boolean isInterested() {
        return false;
    }

    @Override
    public boolean isOwnedBy(LivingEntity p_21831_) {
        return false;
    }

    @Override
    public boolean canMate(Animal p_30392_) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void tame(Player p_21829_) {
    }

    @Override
    public boolean isOrderedToSit() {
        return false;
    }

    @Override
    public void setOrderedToSit(boolean p_21840_) {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, PREY_SELECTOR));
    }

    @Nullable
    @Override
    public Wolf getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isHowling() ? SoundEvents.WOLF_HOWL : SoundEvents.WOLF_GROWL;
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
        if (this.isHowling()) this.howlTime = this.getAmbientSoundInterval() / 3;
    }

    @Override
    public int getAmbientSoundInterval() {
        if (isHowling()) return 260;
        return isAggressive() ? 40 : 80;
    }

    @Override
    protected float getSoundVolume() {
        return isHowling() ? 2.0F : super.getSoundVolume();
    }
}
