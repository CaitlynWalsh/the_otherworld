package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.common.blocks.EntityNestBlock;
import net.kitcaitie.otherworld.common.blocks.entity.EntityNestBlockEntity;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Fairling extends Animal implements EntityNestBlock.NestInhabitor {
    private static final Map<Item, Integer> WANTED_ITEMS = Map.of(
            OtherworldItems.MULBERRY.get(), 0
    );
    public static final List<Item> SPAWN_ITEMS = List.of(
            OtherworldItems.MULBERRY.get(),
            OtherworldItems.MULBERRY_SEEDS.get(),
            OtherworldItems.PLUM.get(),
            OtherworldBlocks.MAJIA_POPPY.get().asItem(),
            OtherworldItems.OPAL.get(),
            OtherworldBlocks.MYSTWOOD_SAPLING.get().asItem(),
            OtherworldItems.ROSERYE_SEEDS.get(),
            OtherworldItems.ROSE_QUARTZ_SHARD.get(),
            OtherworldItems.ROSEGOLD_COIN.get(),
            OtherworldBlocks.BLUSHING_MUMS.get().asItem(),
            OtherworldItems.WHISP.get(),
            Items.IRON_INGOT,
            Items.WHEAT_SEEDS,
            Items.LEATHER,
            Items.SLIME_BALL,
            Items.BROWN_WOOL,
            OtherworldBlocks.REDSPRUCE_SAPLING.get().asItem(),
            Items.CHARCOAL,
            OtherworldItems.THORNBERRY.get(),
            OtherworldItems.SPICEROOT.get(),
            OtherworldItems.TOPAZ_COIN.get(),
            OtherworldBlocks.FIRE_LILY.get().asItem(),
            OtherworldItems.ICEBRUSSEL.get(),
            OtherworldItems.SAPPHIRE_COIN.get(),
            OtherworldBlocks.FROSTED_BELLFLOWER.get().asItem()
    );
    private static final EntityDataAccessor<Boolean> DATA_HAPPY = SynchedEntityData.defineId(Fairling.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PANIC = SynchedEntityData.defineId(Fairling.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CAN_HAVE_BABY = SynchedEntityData.defineId(Fairling.class, EntityDataSerializers.BOOLEAN);
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float spinningAnimationCooldownTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;
    private int eatTimer = 20;
    private int panicTimer = 0;
    private BlockPos homePos = BlockPos.ZERO;

    public Fairling(EntityType<? extends Fairling> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier createAttributes() {
        return Allay.createAttributes().build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.6D) {
            @Override
            protected boolean shouldPanic() {
                return Fairling.this.isPanicking();
            }
        });
        this.goalSelector.addGoal(0, new FairlingGoToHomeGoal(this, 1.1D, 30));
        this.goalSelector.addGoal(1, new FairlingGoToItemGoal(this, 1.0D, 12.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(OtherworldItems.MULBERRY.get()), false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, AbstractPerson.class, 10.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, AbstractVillager.class, 10.0F));
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    public boolean isPanicking() {
        return level.isClientSide() ? this.entityData.get(DATA_PANIC) : this.panicTimer > 0;
    }

    public void setPanicTime(int time) {
        this.entityData.set(DATA_PANIC, time > 0);
        this.panicTimer = time;
    }

    @Override
    public void completeUsingItem() {
        if (this.getUseItem().is(OtherworldItems.MULBERRY.get())) {
            this.getUseItem().shrink(1);
            this.heal(4.0F);
            if (!this.level.isClientSide()) {
                if (this.isBaby()) {
                    this.ageUp(10, true);
                }
                else if (this.isHappy() && this.canReproduce() && getAge() == 0) {
                    this.haveBaby((ServerLevel) this.level);
                }
            }
        }
        super.completeUsingItem();
    }

    public void eatFood() {
        if (this.getHealth() < this.getMaxHealth() || this.isBaby() || this.wantsToHaveBaby()) {
            ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
            if (!this.isUsingItem() && stack.is(OtherworldItems.MULBERRY.get())) {
                this.useItem(stack, InteractionHand.MAIN_HAND);
                this.eatTimer = 100;
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_CELEBRATE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 2.0F;
    }

    public void haveBaby(ServerLevel level) {
        Fairling baby = this.getBreedOffspring(level, this);
        baby.moveTo(this.position());
        baby.setBaby(true);
        baby.finalizeSpawn(level, level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.BREEDING, null, null);
        level.addFreshEntity(baby);
        this.setAge(6000);
    }

    public boolean wantsToHaveBaby() {
        return this.canReproduce() && this.getAge() == 0 && this.isHappy() && this.getItemInHand(InteractionHand.MAIN_HAND).is(OtherworldItems.MULBERRY.get());
    }

    public void useItem(ItemStack itemStack, InteractionHand hand) {
        this.stopUsingItem();
        this.setItemInHand(hand, itemStack);
        this.startUsingItem(hand);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public void travel(Vec3 p_218382_) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            if (this.hasItemInHand()) {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
            }

            if (this.isHappy()) {
                ++this.spinningAnimationCooldownTicks;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                if (this.isSpinning()) {
                    ++this.spinningAnimationTicks;
                } else {
                    --this.spinningAnimationTicks;
                }

                this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
            } else {
                this.spinningAnimationCooldownTicks = 0.0F;
                this.spinningAnimationTicks = 0.0F;
                this.spinningAnimationTicks0 = 0.0F;
            }
        } else {
            if (this.getHealth() < this.getMaxHealth() || this.getLastHurtByMob() != null) {
                this.setHappy(false);
            }
            else if (this.getItemInHand(InteractionHand.MAIN_HAND).is(OtherworldItems.MULBERRY.get())) {
                if (!this.isHappy()) this.setHappy(true);
            }
            else if (this.isHappy()) {
                this.setHappy(false);
            }

            if (panicTimer > 0) {
                --panicTimer;
                this.setPanicTime(panicTimer);
                return;
            }

            if (eatTimer > 0) --eatTimer;
            else {
                this.eatFood();
            }
        }
    }

    @Override
    public void afterLeaveNest(BlockPos blockPos, EntityNestBlockEntity entity, @Nullable Player player, boolean emergency) {
        if (emergency) {
            this.setPanicTime(300);
        }
        else if (this.getHealth() < this.getMaxHealth()) {
            if (!entity.getContents().isEmpty()) {
                this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(entity.getContents().getItem()));
                entity.getContents().shrink(1);
            }
        }
    }

    public boolean canAddItemToNest() {
        return !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.getItemInHand(InteractionHand.MAIN_HAND).is(OtherworldItems.MULBERRY.get());
    }

    @Override
    public void onEnterNest(Entity entity, BlockPos pos, BlockState state, EntityNestBlockEntity nestBlock) {
        if (!level.isClientSide() && this.canAddItemToNest()) {
            if (nestBlock.addItemToNest(this.getItemInHand(InteractionHand.MAIN_HAND).copy())) {
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float v) {
        super.actuallyHurt(source, v);
        this.setPanicTime(300);
    }

    public boolean canReproduce() {
        return this.entityData.get(DATA_CAN_HAVE_BABY);
    }

    public void setCanReproduce(boolean reproduce) {
        this.entityData.set(DATA_CAN_HAVE_BABY, reproduce);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HAPPY, false);
        this.entityData.define(DATA_CAN_HAVE_BABY, false);
        this.entityData.define(DATA_PANIC, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Happy", this.isHappy());
        tag.putBoolean("Reproduce", this.canReproduce());
        tag.putInt("PanicTime", this.panicTimer);
        this.saveNestData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHappy(tag.getBoolean("Happy"));
        this.setCanReproduce(tag.getBoolean("Reproduce"));
        this.setPanicTime(tag.getInt("PanicTime"));
        this.readNestData(tag);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (type != MobSpawnType.BREEDING && this.random.nextInt(7) == 0) {
            if (random.nextInt(1000) == 0) {
                this.setItemInHand(InteractionHand.MAIN_HAND, OtherworldItems.OTHERWORLD_TOTEM.get().getDefaultInstance());
            }
            else if (random.nextInt(100) == 0) {
                this.setItemInHand(InteractionHand.MAIN_HAND, OtherworldItems.OTHERAN_EYE.get().getDefaultInstance());
            }
            else this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(SPAWN_ITEMS.get(this.random.nextInt(SPAWN_ITEMS.size()))));
        }
        this.setCanReproduce(random.nextInt(4) == 0);
        return super.finalizeSpawn(accessor, instance, type, data, tag);
    }

    public static boolean checkSpawnRules(EntityType<? extends Fairling> animal, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        BlockState blockState = levelAccessor.getBlockState(blockPos.below());
        return blockState.is(OtherworldTags.OTHERAN_SPAWNABLE_ON) || blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES);
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return 2.0F;
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack stack, ItemStack stack1) {
        if (WANTED_ITEMS.containsKey(stack.getItem())) {
            if (WANTED_ITEMS.containsKey(stack1.getItem())) return WANTED_ITEMS.get(stack.getItem()) < WANTED_ITEMS.get(stack1.getItem());
            return true;
        }
        return false;
    }

    public boolean isHappy() {
        return this.entityData.get(DATA_HAPPY);
    }

    public void setHappy(boolean happy) {
        this.entityData.set(DATA_HAPPY, happy);
    }

    protected float getStandingEyeHeight(Pose p_218356_, EntityDimensions p_218357_) {
        return p_218357_.height * 0.6F;
    }

    public boolean causeFallDamage(float p_218321_, float p_218322_, DamageSource p_218323_) {
        return false;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return WANTED_ITEMS.containsKey(stack.getItem()) && this.canReplaceCurrentItem(stack, this.getItemInHand(InteractionHand.MAIN_HAND));
    }

    public boolean isSpinning() {
        float f = this.spinningAnimationCooldownTicks % 55.0F;
        return f < 15.0F;
    }

    public float getHoldingItemAnimationProgress(float flt) {
        return Mth.lerp(flt, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    public float getSpinningProgress(float flt) {
        return Mth.lerp(flt, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    @Override
    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        ItemStack itemstack = item.getItem();
        ItemStack itemstack1 = this.equipItemIfPossible(new ItemStack(itemstack.getItem()));
        if (!itemstack1.isEmpty()) {
            this.onItemPickup(item);
            this.take(item, itemstack1.getCount());
            itemstack.shrink(itemstack1.getCount());
            if (itemstack.isEmpty()) {
                item.discard();
            }
        }
    }

    public boolean isFlapping() {
        return !this.isOnGround();
    }

    public boolean canTakeItem(ItemStack stack) {
        return WANTED_ITEMS.containsKey(stack.getItem());
    }

    @Nullable
    @Override
    public Fairling getBreedOffspring(ServerLevel s, AgeableMob p_146744_) {
        return OtherworldEntities.FAIRLING.get().create(s);
    }

    @Override
    public BlockPos getNestPos() {
        return this.homePos;
    }

    @Override
    public void setNestPos(BlockPos pos) {
        this.homePos = pos;
    }
    
    static class FairlingGoToHomeGoal extends Goal {
        protected final Predicate<BlockPos> IS_VALID_HOME;
        protected final Predicate<BlockPos> CAN_ENTER_HOME;
        protected final Predicate<BlockPos> CAN_ADD_FOOD;
        private final Fairling fairling;
        private final double speed;
        private final int range;
        private BlockPos homePos;
        
        public FairlingGoToHomeGoal(Fairling fairling, double speed, int range) {
            this.fairling = fairling;
            this.speed = speed;
            this.range = range;
            this.IS_VALID_HOME = (block) -> {
                if (block == null || block == BlockPos.ZERO) return false;
                BlockEntity blockEntity = fairling.level.getBlockEntity(block);
                return blockEntity instanceof EntityNestBlockEntity entity && Objects.equals(entity.getEntityType(fairling.level, block), fairling.getType());
            };
            this.CAN_ENTER_HOME = (block) -> {
                if (block == null || block == BlockPos.ZERO) return false;
                EntityNestBlockEntity blockEntity = (EntityNestBlockEntity) fairling.level.getBlockEntity(block);
                return Objects.equals(blockEntity.getEntityType(fairling.level, block), fairling.getType()) && blockEntity.getNumberOfInhabitants() < blockEntity.getMaxOccupants();
            };
            this.CAN_ADD_FOOD = (block) -> {
                if (block == null || block == BlockPos.ZERO) return false;
                EntityNestBlockEntity blockEntity = (EntityNestBlockEntity) fairling.level.getBlockEntity(block);
                return blockEntity.getContents().isEmpty() || blockEntity.getContents().getCount() < blockEntity.getContents().getMaxStackSize();
            };
        }

        @Override
        public boolean canUse() {
            if (fairling.level.isNight() || fairling.canAddItemToNest() || fairling.level.isRaining() || fairling.getLastHurtByMob() != null) {
                if (homePos == null || homePos == BlockPos.ZERO) {
                    this.homePos = fairling.getNestPos();
                    if (homePos != BlockPos.ZERO) {
                        if (IS_VALID_HOME.test(homePos)) return CAN_ENTER_HOME.test(homePos);
                    }
                    if (fairling.level instanceof ServerLevel serverLevel) {
                        this.homePos = findHome(fairling, serverLevel);
                        return homePos != BlockPos.ZERO;
                    }
                }
                else if (!fairling.level.isNight() && !fairling.level.isRaining() && fairling.getLastHurtByMob() == null) {
                    return CAN_ADD_FOOD.test(homePos);
                }
                else return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.homePos != null && (fairling.level.isNight() || (fairling.canAddItemToNest() && CAN_ADD_FOOD.test(homePos)) || fairling.level.isRaining() || fairling.getLastHurtByMob() != null) && IS_VALID_HOME.and(CAN_ENTER_HOME).test(homePos);
        }

        public void start() {
            this.fairling.getNavigation().stop();
            super.start();
        }

        public void stop() {
            this.fairling.getNavigation().stop();
            this.fairling.getNavigation().resetMaxVisitedNodesMultiplier();
        }

        @Override
        public void tick() {
            if (!IS_VALID_HOME.test(homePos)) {
                this.homePos = null;
                fairling.setNestPos(BlockPos.ZERO);
            }
            else if (homePos.closerToCenterThan(fairling.position(), 1.3D) && CAN_ENTER_HOME.test(homePos)) {
                ((EntityNestBlockEntity) fairling.level.getBlockEntity(homePos)).addOccupant(fairling);
            }
            else {
                boolean flag = pathfindDirectlyTowards(this.homePos);
                if (!flag) {
                    this.homePos = null;
                }
            }
        }

        private boolean pathfindDirectlyTowards(BlockPos pos) {
            this.fairling.navigation.setMaxVisitedNodesMultiplier(10.0F);
            this.fairling.navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), speed);
            return this.fairling.navigation.getPath() != null && this.fairling.navigation.getPath().canReach();
        }

        private BlockPos findHome(Fairling fairling, ServerLevel serverLevel) {
            BlockPos blockPos = fairling.blockPosition();
            PoiManager poiManager = serverLevel.getPoiManager();
            Stream<PoiRecord> stream = poiManager.getInRange((poi) -> Objects.equals(OtherworldPOIs.FAIRLING_HOMES.get(), poi.value()), blockPos, range, PoiManager.Occupancy.ANY);
            return stream.map(PoiRecord::getPos).filter(IS_VALID_HOME.and(CAN_ENTER_HOME)).min(Comparator.comparingDouble(blockPos::distSqr)).orElse(BlockPos.ZERO);
        }
        
    }

    static class FairlingGoToItemGoal extends Goal {
        private final Fairling fairling;
        private final double speed;
        private final double range;
        private ItemEntity wantedItem;

        public FairlingGoToItemGoal(Fairling mob, double speed, double range) {
            this.fairling = mob;
            this.speed = speed;
            this.range = range;
        }

        @Override
        public boolean canUse() {
            List<ItemEntity> items = fairling.level.getEntitiesOfClass(ItemEntity.class, fairling.getBoundingBox().inflate(range), (item) -> fairling.wantsToPickUp(item.getItem()));
            if (!items.isEmpty()) {
                this.wantedItem = items.get(0);
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return fairling.isAlive() && !wantedItem.isRemoved();
        }

        @Override
        public void tick() {
            super.tick();
            this.fairling.getLookControl().setLookAt(wantedItem);
            this.fairling.getNavigation().moveTo(wantedItem, speed);
            if (fairling.position().closerThan(wantedItem.position(), 2.0D)) {
                this.fairling.pickUpItem(this.wantedItem);
            }
        }
    }
}
