package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class Crystling extends OtheranAnimal {
    public static final Ingredient TEMPT_ITEM = Ingredient.of(OtherworldItems.ROSERYE_SEEDS.get());
    public AnimationState SWAY = new AnimationState();
    public AnimationState WALK = new AnimationState();

    public Crystling(EntityType<? extends OtheranAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreedGoal(this, 1));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1, TEMPT_ITEM, false));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 0.9));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return TEMPT_ITEM.test(stack);
    }

    @Override
    protected void handleAnimations() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.SWAY.stop();
            this.WALK.startIfStopped(this.tickCount);
        }
        else {
            this.WALK.stop();
            this.SWAY.startIfStopped(this.tickCount);
        }
    }

    public SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_ITEM_GIVEN;
    }

    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return OtherworldEntities.CRYSTLING.get().create(level);
    }

}
