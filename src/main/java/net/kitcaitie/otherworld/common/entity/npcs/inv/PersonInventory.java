package net.kitcaitie.otherworld.common.entity.npcs.inv;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class PersonInventory extends SimpleContainer {
    public final AbstractPerson person;
    protected final CraftingManager craftingManager;

    public PersonInventory(AbstractPerson person, int size) {
        super(size);
        this.person = person;
        this.craftingManager = new CraftingManager(this);
    }

    public void tick() {
        this.craftingManager.tick();

        for (int i=0; i<getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) getItem(i).inventoryTick(person.level, person, i, false);
        }
        for (ItemStack stack : person.getArmorSlots()) {
            if (!stack.isEmpty()) stack.inventoryTick(person.level, person, ((ArmorItem) stack.getItem()).getEquipmentSlot().getIndex(), false);
        }
    }

    public Ingredient getNeededIngredient() {
        return this.craftingManager.getNeededMaterial();
    }

    public Item getCraftingItem() {
        return this.craftingManager.getWantsToCraft();
    }

    public void setCraftingItem(@Nullable Item item) {
        this.craftingManager.setWantsToCraft(item);
    }
}
