package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Roseian extends AbstractPerson {

    public Roseian(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
    }

    public Race getRace() {
        return Race.ROSEIAN;
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.ROSEIAN;
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(Items.COOKED_RABBIT, random.nextInt(6, 12)));
        if (this.isSoldier()) {
            if (spawnType == MobSpawnType.EVENT || random.nextBoolean()) {
                this.setItemSlot(EquipmentSlot.HEAD, OtherworldItems.ROSEGOLD_HELMET.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.CHEST, OtherworldItems.ROSEGOLD_CHESTPLATE.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.LEGS, OtherworldItems.ROSEGOLD_LEGGINGS.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.FEET, OtherworldItems.ROSEGOLD_BOOTS.get().getDefaultInstance());
            }
            if (spawnType == MobSpawnType.NATURAL) {
                if (random.nextBoolean())
                    this.getInventory().addItem(OtherworldItems.ROSEGOLD_KEY.get().getDefaultInstance());
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        if (!this.isShiftKeyDown()) {
            if (shouldRegen() && !this.hasEffect(MobEffects.REGENERATION)) this.setSharedFlag(1, true);
        }
        else if (!shouldRegen() && this.hasEffect(MobEffects.REGENERATION)) this.setSharedFlag(1, false);
        super.customServerAiStep();
    }

    public boolean shouldRegen() {
        return this.getHealth() <= 16.0F && (this.getLastHurtByMob() == null || this.isBlocking());
    }

    @Override
    public boolean isCrouching() {
        return this.isShiftKeyDown();
    }

    @Override
    public Item getCurrency() {
        return OtherworldItems.ROSEGOLD_COIN.get();
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.ROSE_QUARTZ_RING.get());
    }

    @Override
    public ItemStack getSoldierWeapon() {
        return OtherworldItems.ROSE_QUARTZ_SWORD.get().getDefaultInstance();
    }
}
