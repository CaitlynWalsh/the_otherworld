package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.AIBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.HumanBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.HumanChildBrain;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Human extends AbstractPerson implements MonsterTargetable {

    public Human(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
    }

    @Override
    public Race getRace() {
        return Race.HUMAN;
    }

    @Override
    public VillagerType getJobType() {
        return VillagerType.UNEMPLOYED;
    }

    @Override
    protected AIBrain chooseAIBrain(Level level) {
        return this.isBaby() ? new HumanChildBrain(this, level.getProfilerSupplier()) : new HumanBrain(this, level.getProfilerSupplier());
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.HUMAN;
    }

    @Override
    public Item getCurrency() {
        return Items.EMERALD;
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.DIAMOND_RING.get());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(Items.COOKED_BEEF, random.nextInt(8, 16)));
        if (!this.isBaby()) {
            if (!this.getInventory().hasAnyMatching(this::isMeleeWeapon)) this.getInventory().addItem(new ItemStack(random.nextBoolean() ? Items.DIAMOND_SWORD : Items.IRON_SWORD));
            this.setItemSlot(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultInstance());
            if (random.nextBoolean()) this.setItemSlot(EquipmentSlot.HEAD, random.nextBoolean() ? Items.IRON_HELMET.getDefaultInstance() : Items.LEATHER_HELMET.getDefaultInstance());
            if (random.nextBoolean()) this.setItemSlot(EquipmentSlot.CHEST, random.nextBoolean() ? Items.CHAINMAIL_CHESTPLATE.getDefaultInstance() : Items.LEATHER_CHESTPLATE.getDefaultInstance());
            if (random.nextBoolean()) this.setItemSlot(EquipmentSlot.LEGS, random.nextBoolean() ? Items.CHAINMAIL_LEGGINGS.getDefaultInstance() : Items.LEATHER_LEGGINGS.getDefaultInstance());
            if (random.nextBoolean()) this.setItemSlot(EquipmentSlot.FEET, random.nextBoolean() ? Items.IRON_BOOTS.getDefaultInstance() : Items.LEATHER_BOOTS.getDefaultInstance());
        }
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby()) this.addItemsOnSpawn(this.getSpawnType());
    }

    @Override
    public <T extends AbstractPerson> void spawnSoldierIfPossible() {
    }

    @Override
    public boolean canTarget(Mob mob) {
        return true;
    }
}
