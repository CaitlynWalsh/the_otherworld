package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.AIBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.HumanBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.HumanChildBrain;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class Descendant extends AbstractPerson implements MonsterTargetable {
    protected static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Descendant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_RACE = SynchedEntityData.defineId(Descendant.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> HAS_ATTRIBUTE = SynchedEntityData.defineId(Descendant.class, EntityDataSerializers.BOOLEAN);

    public Descendant(EntityType<? extends Descendant> type, Level level) {
        super(type, level);
    }

    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        tag.putInt("Variant", this.getVariant());
        tag.putString("Race", this.getRace().name());
        tag.putBoolean("HasAttribute", this.hasSpecialAttribute());
    }

    @Override
    public Item getCurrency() {
        switch (getRace()) {
            case HUMAN -> {
                return Items.EMERALD;
            }
            case ONI -> {
                return Items.IRON_INGOT;
            }
            case ROSEIAN -> {
                return OtherworldItems.ROSEGOLD_COIN.get();
            }
            case FAIRIE -> {
                return OtherworldItems.OPAL.get();
            }
            case EMBERIAN -> {
                return OtherworldItems.TOPAZ_COIN.get();
            }
            case ICEIAN -> {
                return OtherworldItems.SAPPHIRE_COIN.get();
            }
        }
        return null;
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setHasSpecialAttribute(boolean hasSpecialAttribute) {
        this.entityData.set(HAS_ATTRIBUTE, hasSpecialAttribute);
    }

    public boolean hasSpecialAttribute() {
        return this.entityData.get(HAS_ATTRIBUTE);
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
    }

    @Override
    public boolean hasWings() {
        return isFairie() || (getRace() == Race.FAIRIAN && hasSpecialAttribute());
    }

    @Override
    public boolean hasHorns() {
        return isOni() || (getRace() == Race.ONIMAN && hasSpecialAttribute());
    }

    @Override
    protected AIBrain chooseAIBrain(Level level) {
        if (this.isHuman()) {
            return this.isBaby() ? new HumanChildBrain(this, level.getProfilerSupplier()) : new HumanBrain(this, level.getProfilerSupplier());
        }
        return super.chooseAIBrain(level);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("Variant")) this.setVariant(tag.getInt("Variant"));
        if (tag.contains("Race")) this.setRace(Race.valueOf(tag.getString("Race")));
        if (tag.contains("HasAttribute")) this.setHasSpecialAttribute(tag.getBoolean("HasAttribute"));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_RACE)) {
            this.updateRace();
        }
    }

    protected void updateRace() {
        if (!this.level.isClientSide()) {
            this.getNavigation().stop();
            this.navigation = this.createNavigation(this.level);
            this.aiBrain = chooseAIBrain(this.level);
            switch (this.getRace()) {
                case EMBERIAN -> {
                    this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
                    this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
                    this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
                }
                case GHOUL -> {
                    this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
                    this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, -1.0F);
                }
                default -> {
                    this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
                    this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
                    this.setPathfindingMalus(BlockPathTypes.WATER, 8.0F);
                    this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 8.0F);
                }
            }
        }
    }

    @Override
    public Race getRace() {
        try {
            return Race.valueOf(this.entityData.get(DATA_RACE));
        }
        catch (Exception ignored) {
            return Race.HUMAN;
        }
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.DESCENDANT;
    }

    public void setRace(Race race) {
        this.entityData.set(DATA_RACE, race.name());
        updateRace();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(DATA_RACE, Race.HUMAN.name());
        this.entityData.define(HAS_ATTRIBUTE, false);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isBaby() && !player.isShiftKeyDown()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof ArmorItem armor) {
                ItemStack itemStackCopy = itemStack.split(1);
                EquipmentSlot slot = armor.getEquipmentSlot();
                ItemStack itemStack1 = this.getItemBySlot(slot);
                if (!itemStack1.isEmpty()) {
                    ItemStack itemStack1Copy = itemStack1.split(1);
                    player.setItemInHand(hand, itemStack1Copy);
                    this.setItemSlot(slot, itemStackCopy);
                } else {
                    this.setItemSlot(slot, itemStackCopy);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else if (itemStack.isEmpty()) {
                for (ItemStack stack : this.getArmorSlots()) {
                    if (!stack.isEmpty()) {
                        player.setItemInHand(hand, stack.split(1));
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }


    public static Descendant create(ServerLevel level, IRaces parent1, IRaces parent2) {
        Descendant baby = OtherworldEntities.DESCENDANT.get().create(level);
        Race race = Race.getRaceFromBreeding(parent1.getRace(), parent2.getRace());
        baby.setRace(race == null ? Race.HUMAN : race);
        baby.setHasSpecialAttribute(baby.random.nextBoolean());
        return baby;
    }

    @Override
    public boolean canTarget(Mob mob) {
        return getRace() == Race.HUMAN || getRace() == Race.ONIMAN;
    }
}
