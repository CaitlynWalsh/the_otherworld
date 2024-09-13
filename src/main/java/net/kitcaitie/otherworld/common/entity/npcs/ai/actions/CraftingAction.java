package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.inv.CraftingManager;
import net.kitcaitie.otherworld.common.entity.npcs.inv.PersonInventory;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CraftingAction extends Action {
    protected final PersonInventory inventory;
    protected @Nullable Item itemToCraft = null;

    public CraftingAction(AbstractPerson person) {
        super(person);
        this.inventory = person.getInventory();
        this.setFlags(EnumSet.of(Flags.CRAFTING));
    }

    @Override
    public boolean canStart() {
        return person.canCraft() && !person.isSleeping() && !person.isTrading();
    }

    @Override
    public void tick() {
        if (person.tickCount % 40 == 1) {
            if (this.itemToCraft == null) {
                this.checkForItemsToCraft();
            }
            else if (inventory.getCraftingItem() == null) this.itemToCraft = null;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P1;
    }

    public void overrideCraftItem(@Nullable Item item) {
        this.itemToCraft = item;
        if (this.itemToCraft != null) {
            inventory.setCraftingItem(itemToCraft);
        }
    }

    protected void checkForItemsToCraft() {
        if (this.inventory.getCraftingItem() == null) {
            IOccupation.VillagerType villagerType = person.getJobType();
            CraftingManager.CRAFTING_MAP.get(person.getRace()).stream().filter((sup) -> {
                Item i = sup.get();
                ItemStack stack;
                if (villagerType == IOccupation.VillagerType.FARMER) {
                    if (i == OtherworldItems.MULBERRY_SEEDS.get()) {
                        stack = person.getItemInInventory((s) -> s.is(OtherworldItems.MULBERRY_SEEDS.get()));
                        if (stack.isEmpty()) {
                            return true;
                        }
                    }
                    if (i == Items.BONE_MEAL) {
                        stack = person.getItemInInventory((s) -> s.is(OtherworldTags.CRAFT_TO_BONEMEAL));
                        if (!stack.isEmpty() && stack.getCount() > 9) {
                            return true;
                        }
                    }
                }
                if (villagerType == IOccupation.VillagerType.COOK) {
                    stack = person.getItemInInventory(person::isEdibleItem);
                    if (stack.isEmpty() || stack.getCount() < 16) {
                        return person.isEdibleItem(i.getDefaultInstance());
                    }
                }
                return false;
            }).findFirst().ifPresent(item -> this.overrideCraftItem(item.get()));
        }
    }
}
