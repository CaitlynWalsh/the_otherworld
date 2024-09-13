package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class Fairie extends AbstractPerson {

    public Fairie(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
    }

    @Override
    public Race getRace() {
        return Race.FAIRIE;
    }

    @Override
    public Item getCurrency() {
        return OtherworldItems.OPAL.get();
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.OPAL_RING.get());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(OtherworldItems.PLUM.get(), random.nextInt(8, 12)));
        this.getInventory().addItem(new ItemStack(OtherworldItems.MULBERRY.get(), random.nextInt(16, 32)));
        if (this.isSoldier() && spawnType == MobSpawnType.NATURAL) {
            if (random.nextBoolean()) {
                this.getInventory().addItem(OtherworldItems.OPAL_KEY.get().getDefaultInstance());
            }
        }
    }

    @Override
    public ItemStack getSoldierWeapon() {
        return EnchantmentHelper.enchantItem(this.random, OtherworldItems.OPAL_SWORD.get().getDefaultInstance(), random.nextInt(20, 30), true);
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.FAIRIE;
    }

}
