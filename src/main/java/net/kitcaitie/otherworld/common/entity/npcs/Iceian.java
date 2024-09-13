package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Iceian extends AbstractPerson {

    public Iceian(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
    }

    public Race getRace() {
        return Race.ICEIAN;
    }

    @Override
    public ItemStack getSoldierWeapon() {
        return OtherworldItems.SAPPHIRE_SWORD.get().getDefaultInstance();
    }

    @Override
    public Item getCurrency() {
        return OtherworldItems.SAPPHIRE_COIN.get();
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.SAPPHIRE_RING.get());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(Items.COOKED_BEEF, random.nextInt(4, 8)));
        this.getInventory().addItem(new ItemStack(OtherworldItems.ICEBRUSSEL.get(), random.nextInt(4, 12)));
        if (this.isSoldier()) {
            if (spawnType == MobSpawnType.EVENT || random.nextInt(6) == 0) {
                this.setItemSlot(EquipmentSlot.HEAD, OtherworldItems.SAPPHIRE_HELMET.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.CHEST, OtherworldItems.SAPPHIRE_CHESTPLATE.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.LEGS, OtherworldItems.SAPPHIRE_LEGGINGS.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.FEET, OtherworldItems.SAPPHIRE_BOOTS.get().getDefaultInstance());
            }
            if (spawnType == MobSpawnType.NATURAL && random.nextBoolean()) {
                this.getInventory().addItem(OtherworldItems.SAPPHIRE_KEY.get().getDefaultInstance());
            }
        }
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.ICEIAN;
    }

}
