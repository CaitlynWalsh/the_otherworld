package net.kitcaitie.otherworld.common.entity.npcs;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.entity.npcs.ai.PersonLookControl;
import net.kitcaitie.otherworld.common.entity.npcs.ai.PersonMoveControl;
import net.kitcaitie.otherworld.common.entity.npcs.ai.PersonNavigation;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.ActionUtils;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.AIBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.ChildBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.SoldierBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.VillagerBrain;
import net.kitcaitie.otherworld.common.entity.npcs.data.DeathData;
import net.kitcaitie.otherworld.common.entity.npcs.data.MerchantData;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.kitcaitie.otherworld.common.entity.npcs.inv.PersonInventory;
import net.kitcaitie.otherworld.common.items.ElementalFoodItem;
import net.kitcaitie.otherworld.common.items.MarriageItem;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.EventHandler;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.NameGenerator;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.common.world.OtherworldServerLevel;
import net.kitcaitie.otherworld.network.NetworkMessages;
import net.kitcaitie.otherworld.network.s2c.DialogueSayS2CPacket;
import net.kitcaitie.otherworld.network.s2c.OpenBountyScreenS2CPacket;
import net.kitcaitie.otherworld.network.s2c.OpenOccupationScreenS2CPacket;
import net.kitcaitie.otherworld.network.s2c.OpenQuestScreenS2CPacket;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.kitcaitie.otherworld.registry.OtherworldTrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractPerson extends AgeableMob implements InventoryCarrier, IRaces, IOccupation, Merchant, Npc, RangedAttackMob {
    protected static final EntityDataAccessor<Boolean> DATA_MALE = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<String> DATA_OCCUPATION = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Integer> DATA_OCCUPATION_STATUS = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<CompoundTag> TAG_PERSON_DATA = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.COMPOUND_TAG);
    protected static final EntityDataAccessor<CompoundTag> TAG_MERCHANT_DATA = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.COMPOUND_TAG);
    protected static final EntityDataAccessor<BlockPos> DATA_HOME_POS = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<BlockPos> DATA_WORK_POS = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Integer> DATA_RIDE_COOLDOWN = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(AbstractPerson.class, EntityDataSerializers.BYTE);
    public static final NameGenerator nameGen = new NameGenerator();
    protected AIBrain aiBrain;
    public final List<LivingEntity> passiveTargets = new ArrayList<>();
    protected PersonData personData = new PersonData();
    protected PersonData clientPersonData;
    protected MerchantData merchantData = new MerchantData(VillagerType.UNEMPLOYED, 1);
    protected MerchantData clientMerchantData = new MerchantData(VillagerType.UNEMPLOYED, 1);
    protected PersonInventory inventory = new PersonInventory(this, 27);
    protected WarEvent involvedWar;
    protected int despawnTimer;
    protected Player tradingPlayer;
    protected Player lastInteractPlayer;
    protected @Nullable MerchantOffers tradingOffers;
    private int updateMerchantTimer;
    private boolean increaseProfessionLevelOnUpdate;
    protected int villagerXP;
    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private int foodLevel = 20;
    private int lastFoodLevel = 20;
    private float saturationLevel = 5.0F;
    private float exhaustionLevel;
    private int foodTickTimer;
    private int sleepTimer;
    public boolean hasVillagerDiedOrLeftRecently;


    protected AbstractPerson(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
        this.moveControl = new PersonMoveControl(this);
        this.lookControl = new PersonLookControl(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected PersonNavigation createNavigation(Level level) {
        return new PersonNavigation(this, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .build();
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false;
    }

    @Nullable
    public AbstractPerson getBaby(ServerLevel level, LivingEntity partner) {
        if (this.isMixedRace()) return null;
        if (this.canBreedWith(partner)) {
            if (partner instanceof Player player) {
                return Descendant.create(level, this, PowerUtils.accessPlayerCharacter(player));
            }
            else if (partner instanceof AbstractPerson person && person.getRace() == this.getRace()) {
                return this.create(level, partner);
            }
        }
        return null;
    }

    public boolean isBlind() {
        if (level.isClientSide()) return (this.entityData.get(DATA_FLAGS) & 1) > 0;
        return hasEffect(MobEffects.BLINDNESS) || hasEffect(MobEffects.DARKNESS);
    }

    public void setDataFlag(int data, boolean bool) {
        int i = this.entityData.get(DATA_FLAGS);

        if (bool) i |= data;
        else i &= ~data;

        this.entityData.set(DATA_FLAGS, (byte)i);
    }

    protected int getRideCooldown() {
        return this.entityData.get(DATA_RIDE_COOLDOWN);
    }

    protected void setRideCooldown(int cooldown) {
        this.entityData.set(DATA_RIDE_COOLDOWN, cooldown);
    }

    public AbstractPerson create(ServerLevel level, LivingEntity partner) {
        return (AbstractPerson) this.getType().create(level);
    }

    public boolean canBreedWith(LivingEntity entity) {
        if (entity instanceof AbstractPerson && !OtherworldConfigs.SERVER.doNaturalBreeding.get()) return false;
        if (isOppositeGender(entity)) {
            if (entity instanceof Player player) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                return Race.getRaceFromBreeding(this.getRace(), character.getRace()) != null;
            } else if (entity instanceof AbstractPerson person) {
                return this.getRace() == person.getRace();
            }
        }
        return false;
    }

    public void breedWith(ServerLevel level, LivingEntity entity, @Nullable PlayerCharacter character) {
        if (this.canBreedWith(entity)) {
            AbstractPerson baby = this.getBaby(level, entity);
            if (baby != null) {
                PersonData thisData = this.getPersonData();
                PersonData babyData = baby.getPersonData();

                baby.setBaby(true);
                baby.finalizeSpawn(level, level.getCurrentDifficultyAt(baby.blockPosition()), MobSpawnType.BREEDING, null, null);
                if (!this.isMale()) baby.setPos(this.position());
                else baby.setPos(entity.position());

                thisData.addChild(baby);

                if (entity instanceof AbstractPerson person) {
                    PersonData parentData = person.getPersonData();
                    parentData.addChild(baby);
                    person.setPersonData(parentData);
                }

                babyData.setParents(baby, this, entity, character);

                if (entity instanceof Player && character != null) {
                    if (this instanceof Oni oni && character.isOni()) {
                        ((Descendant)baby).setVariant(baby.random.nextBoolean() ? character.getTextureId() : oni.getVariant());
                    }
                }

                level.addFreshEntity(baby);
                this.setAge(OtherworldConfigs.SERVER.breedingCooldownAge.get());
                if (entity instanceof AbstractPerson person) person.setAge(OtherworldConfigs.SERVER.breedingCooldownAge.get());
                this.handleEntityEvent((byte) 18);
                baby.handleEntityEvent((byte) 18);

                this.setPersonData(thisData);
                baby.setPersonData(babyData);

                if (entity instanceof Player player && character != null) character.sendPacket(player);
            }
        }
    }

    public boolean isOppositeGender(LivingEntity entity) {
        boolean flag = false;
        if (entity instanceof Player player) {
            flag = PowerUtils.accessPlayerCharacter(player).isMale() != this.isMale();
        }
        else if (entity instanceof AbstractPerson person) {
            flag = person.isMale() != this.isMale();
        }
        return flag;
    }

    public void useItem(ItemStack itemStack, InteractionHand hand) {
        this.stopUsingItem();
        this.setItemInHand(hand, itemStack);
        this.startUsingItem(hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return getBaby(level, mob);
    }

    @Override
    public void handleEntityEvent(byte b) {
        switch (b) {
            case 17 -> this.addParticles(ParticleTypes.SPLASH, this.level);
            case 18 -> this.addParticles(ParticleTypes.HEART, this.level);
            case 19 -> this.addSingleParticle(ParticleTypes.HEART, this.level);
            case 20 -> this.addSingleParticle(ParticleTypes.ANGRY_VILLAGER, this.level);
            case 21 -> this.addParticles(ParticleTypes.ANGRY_VILLAGER, this.level);
            default -> super.handleEntityEvent(b);
        }
    }

    public void joinWarEvent(WarEvent warEvent) {
        this.involvedWar = warEvent;
        if (this.isSoldier() || this.isMale()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, getMeleeWeaponInInventory());
        }
        if (this.getAi() != null) {
            this.getAi().switchStates(AIBrain.AIStates.WAR);
        }
    }

    public WarEvent getInvolvedWar() {
        return this.involvedWar;
    }

    public ItemStack getMeleeWeaponInInventory() {
        ItemStack swords = getItemInInventory((item) -> item.getItem() instanceof SwordItem);
        if (!swords.isEmpty()) {
            return swords;
        }
        ItemStack axes = getItemInInventory((item) -> item.getItem() instanceof AxeItem);
        if (!axes.isEmpty()) {
            return axes;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getItemInInventory(Predicate<ItemStack> predicate) {
        for (int i=0; i<getInventory().getContainerSize(); i++) {
            ItemStack item = getInventory().getItem(i);
            if (predicate.test(item)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    public abstract Race getRace();

    public abstract Dialogue.Type getDialogueType();

    public boolean isHurt() {
        return this.getHealth() < this.getMaxHealth();
    }

    public boolean isVeryHurt() {
        return this.getHealth() <= 12.0F;
    }

    public Occupation getOccupation() {
        if (this.isBaby()) {
            return Occupation.VILLAGER;
        }
        return Occupation.valueOf(this.entityData.get(DATA_OCCUPATION));
    }

    protected AIBrain chooseAIBrain(Level level) {
        if (this.isBaby()) {
            return new ChildBrain(this, level.getProfilerSupplier());
        }
        return this.isSoldier() ? new SoldierBrain(this, level.getProfilerSupplier()) : new VillagerBrain(this, level.getProfilerSupplier());
    }

    public AIBrain getAi() {
        return aiBrain;
    }

    @Override
    protected void ageBoundaryReached() {
        this.aiBrain = chooseAIBrain(this.level);
    }

    public void setOccupation(Occupation occupation, int status) {
        if (!this.isBaby()) {
            this.entityData.set(DATA_OCCUPATION, occupation.name());
            this.entityData.set(DATA_OCCUPATION_STATUS, status);
            this.aiBrain = chooseAIBrain(level);
        }
    }

    public void setOccupation(Occupation occupation) {
        this.setOccupation(occupation, 0);
    }

    @Override
    public int getOccupationStatus() {
        return this.entityData.get(DATA_OCCUPATION_STATUS);
    }

    public void addOccupationStatus() {
        this.setOccupation(this.getOccupation(), Math.min(getOccupationStatus() + 1, this.getOccupation().getMaxStatus()));
    }

    public MerchantData getMerchantData() {
        if (level.isClientSide()) {
            if (this.clientMerchantData == null || this.getMerchantDataTag().contains("Dirty")) this.clientMerchantData = MerchantData.readNbt(getMerchantDataTag());
            return this.clientMerchantData;
        }
        return this.merchantData;
    }

    public CompoundTag getMerchantDataTag() {
        return this.entityData.get(TAG_MERCHANT_DATA);
    }

    public void setMerchantData(MerchantData data) {
        this.entityData.set(TAG_MERCHANT_DATA, data.writeNbt());
        this.merchantData = data;
        if (level.isClientSide()) {
            this.clientMerchantData = MerchantData.readNbt(getMerchantDataTag());
        }
    }

    public VillagerType getJobType() {
        return this.merchantData.getVillagerType();
    }

    public void setJobType(VillagerType type) {
        if (this.canChangeProfession()) {
            this.setMerchantData(MerchantData.readNbt(this.getMerchantDataTag()).setVillagerType(type));
            this.overrideOffers(null);
            this.updateTrades();
        }
    }

    public BlockPos getWorkPos() {
        return this.entityData.get(DATA_WORK_POS);
    }

    public void setWorkPos(BlockPos blockPos) {
        if (this.canChangeProfession()) {
            this.entityData.set(DATA_WORK_POS, blockPos);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAggressive()) return InteractionResult.PASS;

        this.setLastInteractPlayer(player);

        if (this.isSleeping()) {
            if (!this.level.isClientSide()) {
                this.stopSleeping();
            }
            return InteractionResult.SUCCESS;
        }

        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTrading()) {
            if (this.isTradingItem(stack) && !this.getPersonData().isMarriedTo(player)) {
                this.startTrading(player);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else {
                InteractionResult family = handleFamilyInteractions(player, character, stack);
                if (family != InteractionResult.FAIL) {
                    if (!level.isClientSide()) {
                        this.setDirtyPersonData();
                    }
                    return family;
                } else {
                    InteractionResult questResult = handleQuests(player, character, stack);
                    if (questResult != InteractionResult.FAIL) {
                        if (!level.isClientSide()) {
                            this.setDirtyPersonData();
                            character.sendPacket(player);
                        }
                        return questResult;
                    }
                }
            }
            this.sayTo(player, null);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void setDirtyPersonData() {
        PersonData personData1 = this.getPersonData();
        personData1.setDirty();
        this.setPersonData(personData1);
    }

    @Override
    public void rideTick() {
        final Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        }
        else if (entity instanceof Player || entity instanceof AbstractPerson) {
            this.setDeltaMovement(Vec3.ZERO);
            this.tick();
            if (this.isPassenger() && this.getVehicle() instanceof final LivingEntity vehicle) {
                this.yBodyRot = vehicle.yBodyRot;
                this.setYRot(vehicle.getYRot());
                final float radius = 0.3F;
                final float angle = (0.0174532925F * (vehicle.yBodyRot - 180F));
                final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                final double extraZ = radius * Mth.cos(angle);
                this.setPos(vehicle.getX() + extraX, Math.max(vehicle.getY() + vehicle.getBbHeight() - 0.8, vehicle.getY()), vehicle.getZ() + extraZ);
                if (this.shouldDismountVeichle(vehicle)) {
                    this.removeVehicle();
                }
            }
        }
        else super.rideTick();
    }



    protected boolean shouldDismountVeichle(Entity vehicle) {
        if (!vehicle.isAlive() || vehicle.isRemoved()) return true;
        if (vehicle instanceof LivingEntity entity) {
            if (this.getRideCooldown() <= 0 && entity.isShiftKeyDown()) return true;
            if (entity instanceof Player || entity instanceof AbstractPerson) {
                if (!isBaby()) return true;
            }
            return entity.isFallFlying() || entity.isVisuallySwimming() || entity.isUnderWater();
        }
        return false;
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        if (!this.isSleeping() && !this.level.isClientSide()) {
            this.sleepTimer = 200;
        }
    }

    public boolean isTradingItem(ItemStack stack) {
        if (getOffers() != null) {
            for (MerchantOffer offer : getOffers()) {
                if (offer.getBaseCostA().is(stack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canHaveChildWith(Player player) {
        return this.getAge() == 0 && (OtherworldConfigs.SERVER.childLimit.get() == -1 || OtherworldConfigs.SERVER.childLimit.get() > PowerUtils.accessPlayerCharacter(player).getChildren().size()) && canBreedWith(player);
    }

    @Override
    public boolean startRiding(Entity p_21396_, boolean p_21397_) {
        this.setRideCooldown(20);
        return super.startRiding(p_21396_, p_21397_);
    }

    public InteractionResult handleFamilyInteractions(Player player, PlayerCharacter character, ItemStack stack) {
        if (this.isBaby() && player.isShiftKeyDown() && player.getPassengers().isEmpty() && PersonData.readData(getPersonDataTag()).isChildOf(player)) {
            this.setPose(Pose.SITTING);
            this.startRiding(player, true);
            return InteractionResult.SUCCESS;
        }
        PersonData data = this.getPersonData();
        if (isWooingItem(stack) || isMarriageItem(stack)) {
            if (data.isChildOf(player)) {
                return InteractionResult.FAIL;
            }
            else if (data.isMarriedTo(player)) {
                if (isWooingItem(stack)) {
                    stack.shrink(1);
                    if (canHaveChildWith(player)) {
                        if (!level.isClientSide()) {
                            this.breedWith((ServerLevel) level, player, character);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    this.handleEntityEvent((byte) 19);
                    this.sayTo(player, DialogueEvent.WOOING.getString());
                    return InteractionResult.SUCCESS;
                }
            } else if (data.isLoverOf(player)) {
                if (!this.canGetMarriedTo(player)) {
                    if (!level.isClientSide()) {
                        this.handleEntityEvent((byte) 17);
                    }
                    data.modifyRelationship(this, player, PersonData.RelationshipData.Status.NEUTRAL, null);
                    this.setPersonData(data);
                    this.sayTo(player, DialogueEvent.BREAK_UP.getString());
                    return InteractionResult.SUCCESS;
                }
                else if (isMarriageItem(stack) && stack.getCount() == 2) {
                    if (!level.isClientSide()) {
                        data.setSpouse(this, player);
                        if (stack.getItem() instanceof MarriageItem) {
                            MarriageItem.marry(stack, player, this);
                        }
                        else stack.setCount(1);
                        this.handleEntityEvent((byte) 18);
                        this.setPersonData(data);
                    }
                    this.sayTo(player, DialogueEvent.PROPOSAL.getString());
                    return InteractionResult.SUCCESS;
                } else if (isWooingItem(stack)) {
                    if (!level.isClientSide()) {
                        stack.shrink(1);
                        this.handleEntityEvent((byte) 19);
                    }
                    this.sayTo(player, DialogueEvent.WOOING.getString());
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (this.canGetMarriedTo(player)) {
                    if (data.isFriendlyTowards(player) && isWooingItem(stack)) {
                        if (!level.isClientSide()) {
                            stack.shrink(1);
                            this.handleEntityEvent((byte) 19);
                        }
                        data.modifyRelationship(this, player, PersonData.RelationshipData.Status.FRIENDLY, PersonData.RelationshipData.Family.LOVER);
                        this.setPersonData(data);
                    } else if (data.isHostileTowards(player) && !level.isClientSide()) {
                        this.handleEntityEvent((byte) 20);
                    }
                    this.sayTo(player, isWooingItem(stack) ? DialogueEvent.WOOING.getString() : DialogueEvent.PROPOSAL.getString());
                    return InteractionResult.SUCCESS;
                }
                else {
                    if (data.isHostileTowards(player) && !level.isClientSide()) {
                        this.handleEntityEvent((byte) 20);
                    }
                    this.sayTo(player, isWooingItem(stack) ? DialogueEvent.WOOING_FAIL.getString() : DialogueEvent.PROPOSAL_FAIL.getString());
                }

            }
        } else if (data.isFamilyWith(player) && isGift(stack)) {
            ItemStack stack1 = stack.getItem().getDefaultInstance();
            if (!this.getInventory().canAddItem(stack1)) {
                this.sayTo(player, DialogueEvent.INV_FULL.getString());
            } else {
                if (!level.isClientSide()) {
                    this.getInventory().addItem(stack1);
                    stack.shrink(1);
                }
                data.addRelationPoints(this, player, getValueOfItem(stack1));
                this.setPersonData(data);
                this.sayTo(player, DialogueEvent.GIFT.getString());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public boolean isGift(ItemStack stack) {
        return getValueOfItem(stack) > 0;
    }

    protected int getValueOfItem(ItemStack item) {
        if (item.is(OtherworldTags.VALUABLES)) return 4 * item.getCount();
        else if (isEdibleItem(item)) return item.getCount();
        else if (item.is(getCurrency())
                || item.is(OtherworldItems.TOPAZ.get())
                || item.is(OtherworldItems.SAPPHIRE.get())
                || item.is(OtherworldItems.ROSEGOLD_COIN.get())
                || item.is(OtherworldItems.ROSEGOLD_INGOT.get())) return 2 * item.getCount();
        else if (item.is(Items.IRON_INGOT)) return 3 * item.getCount();
        return 0;
    }

    public boolean isEdibleItem(ItemStack item) {
        FoodProperties foodProperties = item.getFoodProperties(this);
        if (foodProperties != null && item.isEdible()) {
            if (item.getItem() instanceof ElementalFoodItem elementalFoodItem) {
                return elementalFoodItem.getWorldType().equals(this.getRace().getHomeWorld());
            }
            else return foodProperties.getEffects().stream().noneMatch((pair) -> {
                MobEffect mobEffect = pair.getFirst().getEffect();
                return mobEffect.getCategory() == MobEffectCategory.HARMFUL && !this.isImmuneTo(mobEffect);
            });
        }
        return false;
    }

    public boolean isEdibleFor(IRaces race, ItemStack item) {
        FoodProperties foodProperties = item.getFoodProperties(this);
        if (foodProperties != null && item.isEdible()) {
            if (item.getItem() instanceof ElementalFoodItem elementalFoodItem) {
                return elementalFoodItem.getWorldType().equals(race.getRace().getHomeWorld());
            }
            else return foodProperties.getEffects().stream().noneMatch((pair) -> {
                MobEffect mobEffect = pair.getFirst().getEffect();
                return mobEffect.getCategory() == MobEffectCategory.HARMFUL && !race.isImmuneTo(mobEffect);
            });
        }
        return false;
    }

    public boolean isWooingItem(ItemStack stack) {
        return stack.is(ItemTags.FLOWERS);
    }

    public boolean isMarriageItem(ItemStack stack) {
        return false;
    }

    public void addSingleParticle(ParticleOptions particle, Level level) {
        if (level.isClientSide()) {
            level.addParticle(particle, this.getX(), this.getEyeY(), this.getZ(), 0.0D, 2.5D, 0.0D);
        }
        else if (level instanceof ServerLevel slevel) {
            slevel.sendParticles(particle, this.getX(), this.getEyeY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }
    }

    public void addParticles(ParticleOptions particle, Level level) {
        if (level instanceof ServerLevel slevel) {
            for (int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                slevel.sendParticles(particle, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 1, d0, d1, d2, 1.0D);
            }
        }
    }

    public InteractionResult handleQuests(Player player, PlayerCharacter character, ItemStack stack) {
        if (this.canAssignQuests(player)) {
            Quest.Context context = getQuestContext(player);
            if (context != null) {
                Quest assigned = character.assignedQuest(this, player, context);
                if (assigned != null) {
                    if (!level.isClientSide()) {
                        if (!stack.isEmpty() && EventHandler.giveQuestItems(player, this, stack)) {
                            return InteractionResult.SUCCESS;
                        } else if (EventHandler.completeQuest(player, this)) {
                            return InteractionResult.SUCCESS;
                        } else {
                            this.sayTo(player, assigned.getID());
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                else if (!character.areQuestsFull()) {
                    if (!character.canHaveQuest(context)) {
                        this.sayTo(player, DialogueEvent.QUEST_DENY.getString());
                        return InteractionResult.sidedSuccess(player.level.isClientSide());
                    }
                    Quest quest = EventHandler.chooseQuest(player, context);
                    if (quest != null) {
                        this.sayTo(player, DialogueEvent.QUEST_PROMPT.getString());
                        if (!level.isClientSide()) {
                          NetworkMessages.sendToPlayer(new OpenQuestScreenS2CPacket(this.getId(), quest.getID()), (ServerPlayer) player);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    else if (this.canChangePlayerProfession(character)) {
                        this.sayTo(player, DialogueEvent.JOB_PROMPT.getString());
                        if (!level.isClientSide()) {
                            NetworkMessages.sendToPlayer(new OpenOccupationScreenS2CPacket(this.getId(), Occupation.SOLDIER.name()), (ServerPlayer) player);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    if (!level.isClientSide() && this.canAssignBounties(player) && character.getCurrentBounty() == null) {
                        Bounty bounty = this.chooseBounty(player);
                        if (bounty != null) {
                            NetworkMessages.sendToPlayer(new OpenBountyScreenS2CPacket(bounty), (ServerPlayer) player);
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else {
                    if (!level.isClientSide() && this.canAssignBounties(player) && character.getCurrentBounty() == null) {
                        Bounty bounty = this.chooseBounty(player);
                        if (bounty != null) {
                            NetworkMessages.sendToPlayer(new OpenBountyScreenS2CPacket(bounty), (ServerPlayer) player);
                            return InteractionResult.SUCCESS;
                        }
                    }
                    this.sayTo(player, DialogueEvent.QUEST_FULL.getString());
                    return InteractionResult.SUCCESS;
                }
            }
            if (!level.isClientSide() && this.canAssignBounties(player) && character.getCurrentBounty() == null) {
                Bounty bounty = this.chooseBounty(player);
                if (bounty != null) {
                    NetworkMessages.sendToPlayer(new OpenBountyScreenS2CPacket(bounty), (ServerPlayer) player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    public boolean canAssignBounties(Player player) {
        return this.getRace().getHomeWorld() != null && this.isSoldier() && this.canAssignQuests(player);
    }

    @Nullable
    private Bounty chooseBounty(Player player) {
        if (!level.isClientSide()) {
            Story story = Otherworld.getStoryline((ServerLevel) this.getLevel()).getStory();
            if (this.isAlliedTo(player)) {
                List<Bounty> bounties = story.getBounties(this.getRace().getHomeWorld());
                if (!bounties.isEmpty()) {
                    return bounties.stream().filter((b) -> b.getCriminalUuid() == null || !b.getCriminalUuid().equals(player.getUUID())).findAny().orElse(null);
                }
            }
        }
        return null;
    }

    public static boolean checkPersonSpawning(EntityType<? extends Mob> type, LevelAccessor accessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        return accessor.getBlockState(blockPos.below()).is(OtherworldTags.OTHERAN_SPAWNABLE_ON) || (type.getClass().isInstance(Fairie.class) && accessor.getBlockState(blockPos.below()).is(BlockTags.LEAVES));
    }

    protected boolean canChangePlayerProfession(PlayerCharacter character) {
        if (this.isSoldier()) {
            if (character.getRace() == this.getRace()) {
                if (character.isCriminal() || character.isSoldier()) return false;
                if (this.getType() == OtherworldEntities.EMBERIAN.get() || (!isAggressiveRace() && !isFairie())) {
                    return character.unlockedMaleQuests();
                }
                return true;
            }
        }
        return false;
    }

    public void sayTo(Player player, @Nullable String event) {
        if (!level.isClientSide()) {
            NetworkMessages.sendToPlayer(new DialogueSayS2CPacket(this.getId(), player.getUUID(), event), (ServerPlayer) player);
        }
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (this.getPersonData().isFriendlyTowards(living)) return true;
            if (this.getPersonData().isFamilyWith(living)) return true;
            if (this.getTarget() != null && this.getTarget().is(living)) return false;
            if (living instanceof Player player) {
                if (!player.level.isClientSide()) {
                    Story story = Otherworld.getStoryline((ServerLevel) player.level).getStory();
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                    if (story.areRacesAtWar(character.getRace(), this.getRace())) {
                        return character.isSpy() && !character.isCaughtSpying();
                    } else if (this.getRace() == character.getRace() || story.areRacesAllied(character.getRace(), this.getRace())) {
                        return !this.isHuman() && !character.isCriminal();
                    }
                }
            }
        }
        return super.isAlliedTo(entity);
    }

    @Nullable
    protected Quest.Context getQuestContext(Player player) {
        Quest.Context context = Quest.Context.context(player, this, this.randomizedQuestGenderContext(player));
        if (level instanceof ServerLevel) {
            WarEvent warEvent = Otherworld.getStoryline((ServerLevel) level).getStory().getWarEvents().stream().filter((we) -> Arrays.asList(we.invader).contains(this.getRace())).findFirst().orElse(null);
            if (warEvent != null) return context.war(warEvent);
        }
        return context;
    }

    public int randomizedQuestGenderContext(Player player) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        if (this.isSoldier()) {
            if (this.isOni() || this.isGhoul() || this.isFairie()) {
                return -1;
            }
            if (!character.isSoldier() && !character.unlockedMaleQuests()) {
                return random.nextFloat() < 0.4F ? 0 : 1;
            }
            boolean male = character.unlockedMaleQuests() && random.nextFloat() < 0.4F;
            boolean female = !character.unlockedMaleQuests() && random.nextFloat() < 0.4F;
            return male ? 1 : (female ? 0 : -1);
        }
        return -1;
    }

    public boolean canAssignQuests(Player player) {
        return !this.isBaby() && !this.getPersonData().isFamilyWith(player) && this.getSpawnType() != MobSpawnType.EVENT;
    }

    public boolean canAssignQuest(Quest.Context context, Player player, boolean ignoreGender) {
        return Quest.Context.context(player, this, randomizedQuestGenderContext(player)).equals(context, ignoreGender);
    }

    public boolean canGetMarriedTo(LivingEntity entity) {
        boolean flag = !this.isBaby() && !this.getPersonData().isMarried() && !this.getPersonData().isChildOf(entity) && this.getSpawnType() != MobSpawnType.EVENT && !this.isLeader();
        return flag && ((entity instanceof Player player && (!PowerUtils.accessPlayerCharacter(player).isMarried() && (!this.getPersonData().isInLove() || this.getPersonData().isLoverOf(player))) || (entity instanceof AbstractPerson person && !this.getPersonData().isInLove() && !person.getPersonData().isInLove() && !person.isBaby() && !person.getPersonData().isMarried() && !person.getPersonData().isChildOf(entity) && person.getSpawnType() != MobSpawnType.EVENT && !person.isLeader())));
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!isBlocking()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public boolean isDamageSourceBlocked(DamageSource source) {
        if (super.isDamageSourceBlocked(source)) {
            this.handleEntityEvent((byte) 29);
            return true;
        }
        return false;
    }

    @Override
    protected void hurtCurrentlyUsedShield(float amount) {
        if (this.isBlocking()) {
            if (this.useItem.canPerformAction(ToolActions.SHIELD_BLOCK)) {
                if (amount > 3.0F) {
                    int i = 1 + Mth.floor(amount);
                    InteractionHand hand = this.getUsedItemHand();
                    this.useItem.hurtAndBreak(i, this, (person) -> {
                        person.broadcastBreakEvent(hand);
                    });
                    if (this.useItem.isEmpty()) {
                        if (hand == InteractionHand.MAIN_HAND) {
                            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else {
                            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        }
                        this.useItem = ItemStack.EMPTY;
                        this.handleEntityEvent((byte) 30);
                    }
                }
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float v) {
        super.actuallyHurt(source, v);
        this.setTradingPlayer(null);
        Entity entity = source.getEntity();
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            if (this.getInvolvedWar() == null) {
                if (this.isVeryHurt()) this.notifyOthersThatIAmInTrouble(living);
                if (living instanceof Player player && this.getHealth() > 0) {
                    PersonData data = this.getPersonData();
                    data.removeRelationPoints(this, player, (int) v * 2);
                    this.setPersonData(data);
                    this.handleEntityEvent((byte) 21);
                    this.sayTo(player, DialogueEvent.HURT.getString());
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            PowerUtils.spawnParticles(this, this, this.level);
        }

        super.tick();

        if (!this.level.isClientSide()) {
            PowerUtils.handleBasePowersAndWeaknesses(this, this, (ServerLevel) this.level);

            if (this.level.isDay() && this.isSleeping()) {
                this.stopSleeping();
            }

            if (this.getRideCooldown() > 0) this.setRideCooldown(this.getRideCooldown() - 1);

            this.checkDataFlags();
            this.handleHunger();
        }
    }

    protected void checkDataFlags() {
        if (this.hasEffect(MobEffects.BLINDNESS) || this.hasEffect(MobEffects.DARKNESS)) {
            if ((this.entityData.get(DATA_FLAGS) & 1) <= 0) {
                setDataFlag(1, true);
            }
        }
        else if ((this.entityData.get(DATA_FLAGS) & 1) > 0) {
            setDataFlag(1, false);
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        double x1 = this.getX();
        double y1 = this.getY();
        double z1 = this.getZ();
        super.travel(vec3);
        //TODO: MAYBE IMPLEMENT NATURAL NPC HUNGER IN THE FUTURE
        /*
        if (!this.isPassenger() && this.isAlive()) {
                    double x = this.getX() - x1;
                    double y = this.getY() - y1;
                    double z = this.getZ() - z1;
                    if (this.isInWater()) {
                        int k = Math.round((float) Math.sqrt(x * x + z * z) * 100.0F);
                        if (k > 0) {
                    this.addExhaustion(0.05F * (float) k * 0.01F);
                }
            } else if (this.isOnGround()) {
                int l = Math.round((float) Math.sqrt(x * x + z * z) * 100.0F);
                if (l > 0) {
                    this.addExhaustion(0.05F * (float) l * 0.01F);
                }
            }
        }
         */
    }

    private void handleHunger() {
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if (this.saturationLevel > 0.0F && this.isHurt() && this.foodLevel >= 20) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                this.heal(f / 6.0F);
                this.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel >= 18 && this.isHurt()) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                this.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (this.getHealth() > 1.0F || this.level.getDifficulty() == Difficulty.HARD) {
                    this.hurt(this.level.damageSources().starve(), 1.0F);
                }
                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
    }

    public int getHungerLevel() {
        return this.foodLevel;
    }

    public int getLastHungerLevel() {
        return this.lastFoodLevel;
    }

    public float getSaturation() {
        return this.saturationLevel;
    }

    public float getExhaustion() {
        return this.exhaustionLevel;
    }

    public boolean isHungry() {
        return this.foodLevel <= 16;
    }

    public boolean isStarving() {
        return this.foodLevel <= 6;
    }

    public void setHungerLevel(int i) {
        this.foodLevel = i;
    }

    public void setLastHungerLevel(int i) {
        this.lastFoodLevel = i;
    }

    public void setSaturationLevel(float f) {
        this.saturationLevel = f;
    }

    public void setExhaustionLevel(float f) {
        this.exhaustionLevel = f;
    }

    public void addExhaustion(float f) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + f, 40.0F);
    }

    public void eatFood(int i, float f) {
        this.foodLevel = Math.min(i + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + (float) i * f * 2.0F, (float) this.foodLevel);
    }

    @Override
    public void die(DamageSource source) {
        if (level instanceof ServerLevel slevel) {
            DeathData data = new DeathData(this, source);

            if (source.getEntity() instanceof LivingEntity entity) {
                if (this.getInvolvedWar() == null) this.notifyOthersThatIAmInTrouble(entity);
                if (entity instanceof ServerPlayer player) {
                    data.onDeathByPlayer(player);
                    this.sayTo(player, DialogueEvent.DEATH.getString());
                }
            }

            this.getPersonData().clearAllRelations(this, slevel, true);
        }
        this.setTradingPlayer(null);
        super.die(source);
    }

    protected void notifyOthersThatIAmInTrouble(LivingEntity entity) {
        if (level instanceof ServerLevel) {
            ActionUtils.findGroupOfPeople(this, (person) -> person.isSoldier() && !person.isAggressive() && (person.getRace() == this.getRace() || Otherworld.getStoryline((ServerLevel) level).getStory().areRacesAllied(person.getRace(), this.getRace())), 20.0D)
                    .forEach((person -> person.setTarget(entity)));
        }
    }


    @Nullable
    @Override
    public Entity changeDimension(ServerLevel p_20118_, ITeleporter teleporter) {
        this.setTradingPlayer(null);
        return super.changeDimension(p_20118_, teleporter);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.firstTick) {
            if (!this.isTrading() && this.updateMerchantTimer > 0) {
                --this.updateMerchantTimer;
                if (this.updateMerchantTimer <= 0) {
                    if (this.increaseProfessionLevelOnUpdate) {
                        this.increaseMerchantCareer();
                        this.increaseProfessionLevelOnUpdate = false;
                    }

                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                }
            }

            if (!this.level.hasNearbyAlivePlayer(getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D, 40.0D)) return;

            int i = this.level.getServer().getTickCount() + this.getId();
            if (i % 2 != 0 && this.tickCount > 1) {
                if (getAi() != null) {
                    if (this.involvedWar != null) {
                        if (!this.involvedWar.isWarFinished()) {
                            if (this.isTrading()) this.setTradingPlayer(null);
                            getAi().switchStates(AIBrain.AIStates.WAR);
                            this.stopSleeping();
                        } else {
                            getAi().switchStates(AIBrain.AIStates.CORE);
                            this.involvedWar = null;
                        }
                    }
                    if (getAi().actionMap.isEmpty()) {
                        getAi().initAI();
                    }
                    getAi().tickAI();
                } else this.aiBrain = chooseAIBrain(this.level);

                if (this.tickCount % 20 == 1) {
                    if (this.involvedWar == null && this.getAi() != null && this.level.getServer() != null) {
                        WarEvent warEvent = ((OtherworldServerLevel) level).getStoryline().getStory().getNearbyActiveWar(this.blockPosition(), 1152, this.getRace(), null);
                        if (warEvent != null && !warEvent.isWarFinished()) this.joinWarEvent(warEvent);
                    }
                }

                if (getSpawnType() == MobSpawnType.EVENT) {
                    if (despawnTimer <= 0) {
                        if (!this.level.hasNearbyAlivePlayer(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ(), 40.0D)) {
                            this.discard();
                        }
                    } else {
                        despawnTimer--;
                    }
                }

                if (!(this instanceof Descendant) && this.random.nextFloat() < 0.001) {
                    this.spawnSoldierIfPossible();
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        PowerUtils.powerTick(this, this);
        this.updateSwingTime();
        if (!this.level.isClientSide()) {
            this.inventory.tick();
            if (this.isTrading()) {
                this.getLookControl().setLookAt(this.tradingPlayer);
                if (getNavigation().isInProgress()) this.getNavigation().stop();
            }
            if (this.sleepTimer > 0) {
                --this.sleepTimer;
            }
        }
    }

    public <T extends AbstractPerson> void spawnSoldierIfPossible() {
        if (this.isVillager() && this.getInvolvedWar() == null && this.level instanceof ServerLevel slevel) {
            if (this.hasValidHomePos() && this.getHomePos().distToCenterSqr(this.position()) < 20.0D) {
                List<AbstractPerson> people = ActionUtils.findGroupOfPeople(this, (p) -> p.getRace() == this.getRace() && !p.is(this) && (!(p instanceof Descendant)), 80.0D);
                List<AbstractPerson> soldiers = people.stream().filter(IOccupation::isSoldier).toList();
                people.removeAll(soldiers);
                if (!people.isEmpty() && (soldiers.isEmpty() || soldiers.size() < (people.size() / 3))) {
                    T person = SpawnUtil.trySpawnMob((EntityType<T>) this.getType(), MobSpawnType.MOB_SUMMONED, slevel, this.blockPosition(), 15, 10, 8, (p_216416_, p_216417_, p_216418_, p_216419_, p_216420_) -> {
                        if (SpawnUtil.Strategy.ON_TOP_OF_COLLIDER.canSpawnOn(p_216416_, p_216417_, p_216418_, p_216419_, p_216420_)) {
                            BlockState state = p_216416_.getBlockState(p_216417_);
                            switch (getRace()) {
                                case FAIRIE -> {
                                    return state.is(BlockTags.LEAVES) || state.is(OtherworldTags.OTHERAN_SPAWNABLE_ON);
                                }
                                case EMBERIAN -> {
                                    return state.is(Blocks.MAGMA_BLOCK) || state.is(OtherworldTags.OTHERAN_SPAWNABLE_ON);
                                }
                                default -> {
                                    return state.is(OtherworldTags.OTHERAN_SPAWNABLE_ON);
                                }
                            }
                        }
                        return false;
                    }).orElse(null);
                    if (person != null) {
                        if (person.isEmberian() || (!person.isAggressiveRace() && !person.isFairie())) {
                            person.setMale(true);
                            nameGen.createName(person);
                        }
                        person.getInventory().removeAllItems();
                        person.setOccupation(Occupation.SOLDIER);
                        person.addItemsOnSpawn(MobSpawnType.MOB_SUMMONED);
                        person.restrictTo(person.blockPosition(), 32);
                    }
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        PersonData.createPerson(this);

        if (type == MobSpawnType.STRUCTURE) {
            this.setHomePos(this.blockPosition());
            if (random.nextFloat() < 0.25F && !this.isSoldier()) {
                this.setBaby(true);
            }
        }
        if (type == MobSpawnType.EVENT) {
            this.despawnTimer = 12000;
        }
        if (type == MobSpawnType.PATROL || type == MobSpawnType.NATURAL) {
            if (this.getRace() == Race.EMBERIAN || (!this.isAggressiveRace() && !this.isFairie())) {
                this.setMale(true);
                nameGen.createName(this);
            }
            this.setOccupation(Occupation.SOLDIER);
        }
        if (type != MobSpawnType.BREEDING) {
            this.addItemsOnSpawn(type);
        }
        this.updateTrades();
        return super.finalizeSpawn(accessor, instance, type, data, tag);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        detectVillagersNearby();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.aiBrain != null) {
            this.aiBrain.stopAllActions();
        }
        super.remove(reason);
    }

    public void findSpouseToMarry(ServerLevel level) {
        if (OtherworldConfigs.SERVER.doNaturalMarriage.get()) {
            if (this.isVillager() && !this.isBaby() && !this.getPersonData().isMarried() && !this.getPersonData().isInLove()) {
                List<? extends AbstractPerson> mates = level.getEntitiesOfClass(AbstractPerson.class, this.getBoundingBox().inflate(8.0D), (p) -> p.getRace() == this.getRace() && p.isVillager() && this.canBreedWith(p) && this.canGetMarriedTo(p));
                if (!mates.isEmpty()) {
                    PersonData personData1 = this.getPersonData();
                    personData1.setSpouse(this, mates.get(0));
                    this.setPersonData(personData1);
                }
            }
        }
    }

    protected void detectVillagersNearby() {
        if (this.hasValidHomePos()) {
            ActionUtils.findGroupOfPeople(this, (person) -> {
                if (person.isVillager() && person.getRace() == person.getRace()) {
                    if (person.hasValidHomePos()) {
                        return !this.getPersonData().getRelationships().containsKey(person.getUUID());
                    }
                }
                return false;
            }, 100.0D).forEach((villager) -> {
                PersonData data = this.getPersonData();
                data.modifyRelationship(this, villager, PersonData.RelationshipData.Status.NEUTRAL);
                this.setPersonData(data);
            });
        }
    }

    public long getIdentity() {
        return this.getUUID().hashCode();
    }

    public ItemStack getSoldierWeapon() {
        if (this.isBrute()) {
            return Items.IRON_AXE.getDefaultInstance();
        }
        return Items.IRON_SWORD.getDefaultInstance();
    }

    public void writeData(CompoundTag tag) {
        tag.putBoolean("Male", isMale());
        tag.putString("Occupation", getOccupation().name());
        tag.putInt("OccupationStatus", getOccupationStatus());
        if (this.isVillager()) {
            tag.put("MerchantData", this.getMerchantData().writeNbt());
        }
        tag.putInt("HungerLevel", this.foodLevel);
        tag.putInt("HungerTick", this.foodTickTimer);
        tag.putFloat("Saturation", this.saturationLevel);
        tag.putFloat("Exhaustion", this.exhaustionLevel);
        tag.putLong("HomePos", getHomePos().asLong());
        tag.putLong("WorkPos", getWorkPos().asLong());
        tag.putInt("DespawnTime", this.despawnTimer);
        tag.put("PersonData", this.getPersonData().saveData());
        if (this.getOffers() != null) {
            tag.put("Trades", getOffers().createTag());
        }
        tag.putLong("LastRestock", this.lastRestockGameTime);
        tag.putInt("RestocksToday", this.numberOfRestocksToday);
        tag.putInt("Experience", this.getVillagerXp());
        tag.putBoolean("VillageChanged", this.hasVillagerDiedOrLeftRecently);
    }

    public void readData(CompoundTag tag) {
        if (tag.contains("Male"))
            this.setMale(tag.getBoolean("Male"));
        if (tag.contains("Occupation"))
            this.setOccupation(Occupation.valueOf(tag.getString("Occupation")), tag.getInt("OccupationStatus"));
        if (tag.contains("PersonData"))
            this.setPersonData(tag.getCompound("PersonData"));
        if (tag.contains("MerchantData"))
            this.setMerchantData(MerchantData.readNbt(tag.getCompound("MerchantData")));
        if (tag.contains("HomePos")) {
            this.setHomePos(BlockPos.of(tag.getLong("HomePos")));
        }
        if (tag.contains("WorkPos")) {
            this.setWorkPos(BlockPos.of(tag.getLong("WorkPos")));
        }
        if (tag.contains("HungerLevel")) {
            this.foodLevel = tag.getInt("HungerLevel");
            this.foodTickTimer = tag.getInt("HungerTick");
            this.saturationLevel = tag.getFloat("Saturation");
            this.exhaustionLevel = tag.getFloat("Exhaustion");
        }
        if (tag.contains("DespawnTime"))
            this.despawnTimer = tag.getInt("DespawnTime");
        if (tag.contains(("Trades")))
            this.overrideOffers(new MerchantOffers(tag.getCompound("Trades")));
        if (tag.contains("LastRestock")) this.lastRestockGameTime = tag.getLong("LastRestock");
        if (tag.contains("RestocksToday")) this.numberOfRestocksToday = tag.getInt("RestocksToday");
        if (tag.contains("Experience")) this.villagerXP = tag.getInt("Experience");
        if (tag.contains("VillageChanged")) this.hasVillagerDiedOrLeftRecently = tag.getBoolean("VillageChanged");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        writeInventoryToTag(tag);
        this.writeData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readInventoryFromTag(tag);
        this.readData(tag);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MALE, true);
        this.entityData.define(DATA_OCCUPATION, Occupation.VILLAGER.name());
        this.entityData.define(DATA_OCCUPATION_STATUS, 0);
        this.entityData.define(TAG_PERSON_DATA, new PersonData().saveData());
        this.entityData.define(TAG_MERCHANT_DATA, new MerchantData(VillagerType.UNEMPLOYED, 1).writeNbt());
        this.entityData.define(DATA_HOME_POS, BlockPos.ZERO);
        this.entityData.define(DATA_WORK_POS, BlockPos.ZERO);
        this.entityData.define(DATA_RIDE_COOLDOWN, 0);
        this.entityData.define(DATA_FLAGS, (byte)0);
    }

    public Item getCurrency() {
        return null;
    }

    public void addItemsOnSpawn(MobSpawnType spawnType) {
        if (!this.isBaby()) {
            if (this.getCurrency() != null) {
                this.getInventory().addItem(new ItemStack(getCurrency(), random.nextInt(1, 3)));
            }
            if (this.isSoldier()) {
                ItemStack weapon = this.getInventory().addItem(this.getSoldierWeapon());
                this.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                boolean flag = this.isOni() ? random.nextInt(6) == 0 : this.random.nextBoolean();
                if (flag) this.setItemSlot(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultInstance());
            }
        }
    }

    @Override
    public void stopUsingItem() {
        if (!level.isClientSide()) {
            InteractionHand hand = getUsedItemHand();
            if (hand == InteractionHand.MAIN_HAND && !(this.useItem.getItem() instanceof ShieldItem)) {
                this.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        super.stopUsingItem();
    }

    public boolean isMale() {
        return this.entityData.get(DATA_MALE);
    }

    public void setMale(boolean male) {
        this.entityData.set(DATA_MALE, male);
    }

    public BlockPos getHomePos() {
        return this.entityData.get(DATA_HOME_POS);
    }

    public void setHomePos(BlockPos pos) {
        this.entityData.set(DATA_HOME_POS, pos);
        if (pos == BlockPos.ZERO) this.clearRestriction();
        else this.restrictTo(pos, 64);
    }

    public boolean hasValidHomePos() {
        return getHomePos() != BlockPos.ZERO;
    }

    @Override
    public PersonInventory getInventory() {
        return inventory;
    }

    public PersonData getPersonData() {
        return this.level.isClientSide() ? this.getClientPersonData() : this.personData;
    }

    public CompoundTag getPersonDataTag() {
        return this.entityData.get(TAG_PERSON_DATA);
    }

    public void setPersonData(PersonData personData) {
        this.entityData.set(TAG_PERSON_DATA, personData.saveData());
        this.personData = personData;
    }

    public void setPersonData(CompoundTag tag) {
        this.entityData.set(TAG_PERSON_DATA, tag);
        this.personData = PersonData.readData(tag);
    }

    @OnlyIn(Dist.CLIENT)
    public PersonData getClientPersonData() {
        if (this.clientPersonData == null || this.getPersonDataTag().getBoolean("Dirty")) {
            this.clientPersonData = PersonData.readData(this.getPersonDataTag());
        }
        return this.clientPersonData;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int amount, boolean b) {
        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            if (this.isSoldier() && random.nextInt(3) == 0) continue;
            this.spawnAtLocation(getInventory().getItem(i));
        }
        super.dropCustomDeathLoot(source, amount, b);
    }

    @Override
    public boolean canHoldItem(ItemStack item) {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return getInventory().canAddItem(stack) && (getInventory().getNeededIngredient() != null && getInventory().getNeededIngredient().test(stack) ||
                (isGift(stack) || (((isMeleeWeapon(stack) && getItemInInventory(this::isMeleeWeapon).isEmpty()) || (stack.getItem() instanceof ShieldItem) && (!getItemInHand(InteractionHand.OFF_HAND).is(Items.SHIELD) && getItemInInventory((s) -> s.is(Items.SHIELD)).isEmpty())) && !this.isBaby()) || isTradingItem(stack)) || (getJobType() == VillagerType.FARMER && isFarmingItem(stack))) ||
                (getInvolvedWar() != null && Set.of(getInvolvedWar().invader).contains(getRace()) && stack.is(OtherworldTags.KEYS) && !getInventory().hasAnyMatching((i) -> i.is(stack.getItem())));
    }

    public boolean isFarmingItem(ItemStack stack) {
        return stack.getItem() instanceof HoeItem || stack.getItem() instanceof BoneMealItem || (stack.getItem() instanceof ItemNameBlockItem && ((ItemNameBlockItem)stack.getItem()).getBlock() instanceof CropBlock) || stack.is(Tags.Items.CROPS);
    }

    public boolean isMeleeWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }

    public void pickUp(ItemEntity item) {
        ItemStack itm = item.getItem();
        PersonData data = getPersonData();
        if (item.getOwner() instanceof Player player) {
            boolean flag = this.getTarget() != null && this.getTarget().is(player);
            if (isGift(itm)) {
                data.addRelationPoints(this, player, getValueOfItem(itm));
                this.setPersonData(data);
                if (flag) {
                    this.setTarget(null);
                    this.getNavigation().stop();
                }
            }
            else if (isTradingItem(itm)) {
                data.addRelationPoints(this, player, 2 * itm.getCount());
                this.setPersonData(data);
                if (flag) {
                    this.setTarget(null);
                    this.getNavigation().stop();
                }
            }
            else if (isMeleeWeapon(itm) && this.getTarget() != null && !this.getTarget().is(player)) {
                data.addRelationPoints(this, player, 8);
                this.setPersonData(data);
            }
        }
        InventoryCarrier.pickUpItem(this, this, item);
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
    }

    public boolean canSleep() {
        return getOccupation() == Occupation.VILLAGER && this.sleepTimer == 0 && this.getInvolvedWar() == null;
    }

    private void startTrading(Player player) {
        this.updateSpecialPrices(player);
        this.setTradingPlayer(player);
        this.openTradingScreen(player, Component.literal(this.getDisplayName().getString() + " - " + Component.translatable("villagertype.otherworld." + this.getJobType().name().toLowerCase()).getString()), this.getMerchantData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        boolean flag = this.tradingPlayer != null && player == null;
        this.tradingPlayer = player;
        if (flag) {
            this.resetSpecialPrices();
        }
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXP;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.tradingOffers = offers;
    }

    @Override
    public MerchantOffers getOffers() {
        return this.tradingOffers;
    }

    @Override
    public boolean isClientSide() {
        return this.level.isClientSide();
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public void overrideXp(int xp) {
        this.villagerXP = xp;
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    public void restock() {
        this.updateDemand();
        if (this.getOffers() != null) {
            for (MerchantOffer merchantOffer : this.getOffers()) {
                merchantOffer.resetUses();
            }
            this.lastRestockGameTime = this.level.getGameTime();
            ++this.numberOfRestocksToday;
        }
    }

    private boolean needsToRestock() {
        if (this.getOffers() != null) {
            for (MerchantOffer merchantoffer : this.getOffers()) {
                if (merchantoffer.needsRestock()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean shouldRestock() {
        long i = this.lastRestockGameTime + 12000L;
        long j = this.level.getGameTime();
        boolean flag = j > i;
        long k = this.level.getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            long l = this.lastRestockCheckDayTime / 24000L;
            long i1 = k / 24000L;
            flag |= i1 > l;
        }

        this.lastRestockCheckDayTime = k;
        if (flag) {
            this.lastRestockGameTime = j;
            this.resetNumberOfRestocks();
        }

        return this.allowedToRestock() && this.needsToRestock();
    }

    private void catchUpDemand() {
        if (this.getOffers() != null) {
            int i = 2 - this.numberOfRestocksToday;
            if (i > 0) {
                for(MerchantOffer merchantoffer : this.getOffers()) {
                    merchantoffer.resetUses();
                }
            }

            for(int j = 0; j < i; ++j) {
                this.updateDemand();
            }
        }
    }

    private void resetSpecialPrices() {
        if (this.getOffers() != null) {
            for (MerchantOffer merchantoffer : this.getOffers()) {
                merchantoffer.resetSpecialPriceDiff();
            }
        }
    }

    private void updateSpecialPrices(Player player) {
        if (this.getOffers() != null) {
            int i = getPriceForPlayer(player);
            if (i != 0) {
                for (MerchantOffer merchantoffer : this.getOffers()) {
                    merchantoffer.addToSpecialPriceDiff(-Mth.floor((float) i * merchantoffer.getPriceMultiplier()));
                }
            }
        }
    }

    private int getPriceForPlayer(Player player) {
        PersonData.RelationshipData data = PersonData.readData(this.getPersonDataTag()).getRelationship(player);
        return (data.familyType != null ? 80 : 0) + (data.relationStatus != PersonData.RelationshipData.Status.NEUTRAL ? (data.relationStatus == PersonData.RelationshipData.Status.FRIENDLY ? 20 : -20) + (data.relationStatus != PersonData.RelationshipData.Status.HOSTILE ? data.relationPoints * 4 : 0) : 0);
    }

    private void increaseMerchantCareer() {
        this.setMerchantData(this.getMerchantData().setLevel(this.getMerchantData().getLevel() + 1));
        this.updateTrades();
    }

    protected void updateTrades() {
        MerchantData data = this.getMerchantData();
        List<Map<VillagerType, Int2ObjectMap<OtherworldTrades.ItemListing[]>>> list = OtherworldTrades.TRADES.getOrDefault(this.getRace(), null);
        if (list != null && !list.isEmpty()) {
            Map<IOccupation.VillagerType, Int2ObjectMap<OtherworldTrades.ItemListing[]>> map = list.stream().filter((m) -> m.containsKey(data.getVillagerType())).findFirst().orElse(null);
            if (map != null) {
                Int2ObjectMap<OtherworldTrades.ItemListing[]> int2ObjectMap = map.getOrDefault(data.getVillagerType(), null);
                if (int2ObjectMap != null) {
                    MerchantOffers offers = this.getOffers() != null ? this.getOffers() : new MerchantOffers();
                    this.overrideOffers(this.addOffersFromItemListings(offers, int2ObjectMap.get(data.getLevel()), 2));
                    return;
                }
            }
        }
        this.overrideOffers(null);
    }

    protected MerchantOffers addOffersFromItemListings(MerchantOffers p_35278_, OtherworldTrades.ItemListing[] p_35279_, int p_35280_) {
        Set<Integer> set = Sets.newHashSet();
        if (p_35279_.length > p_35280_) {
            while(set.size() < p_35280_) {
                set.add(this.random.nextInt(p_35279_.length));
            }
        } else {
            for(int i = 0; i < p_35279_.length; ++i) {
                set.add(i);
            }
        }

        for(Integer integer : set) {
            OtherworldTrades.ItemListing itemlisting = p_35279_[integer];
            MerchantOffer merchantoffer = itemlisting.getOffer(this, this.random);
            if (merchantoffer != null) {
                p_35278_.add(merchantoffer);
            }
        }
        return p_35278_;
    }


    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }


    private void updateDemand() {
        if (this.getOffers() != null) {
            for (MerchantOffer merchantoffer : this.getOffers()) {
                merchantoffer.updateDemand();
            }
        }
    }

    public boolean canChangeProfession() {
        return this.villagerXP <= 0;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        PersonData data = getPersonData();
        data.addRelationPoints(this, this.tradingPlayer, 2);
        data.setDirty();
        this.setPersonData(data);

        offer.increaseUses();

        this.getInventory().addItem(offer.getBaseCostA().copy());

        int i = 3 + this.random.nextInt(4);
        this.villagerXP += offer.getXp();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }
        if (offer.shouldRewardExp()) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    public boolean isVillagerWorkingHours() {
        return level.getDayTime() >= 2000L && level.getDayTime() < 9000L;
    }

    private boolean shouldIncreaseLevel() {
        int i = this.getMerchantData().getLevel();
        return MerchantData.canLevelUp(i) && this.villagerXP >= MerchantData.getMaxXpPerLevel(i);
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {

    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.EXPERIENCE_ORB_PICKUP;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    public ItemStack getShootItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float v) {

    }

    public boolean canHaveName() {
        return true;
    }

    public Player getLastInteractPlayer() {
        return this.lastInteractPlayer;
    }

    public void setLastInteractPlayer(Player player) {
        this.lastInteractPlayer = player;
    }

    public boolean canCraft() {
        return !this.isBaby() && (getJobType() == VillagerType.FARMER || getJobType() == VillagerType.COOK);
    }

    public boolean wantsToBreed() {
        return this.hasVillagerDiedOrLeftRecently;
    }

    @Override
    public void removeFreeWill() {
        if (this.aiBrain != null) {
            this.aiBrain.stopAllActions();
        }
        this.aiBrain = null;
    }
}
