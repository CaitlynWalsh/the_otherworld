package net.kitcaitie.otherworld.common.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class FakeInventory extends SimpleContainer {
    public FakeInventory() {
        super(45);
    }

    public void load(ListTag p_36036_) {
        this.clearContent();
        for(int i = 0; i < p_36036_.size(); ++i) {
            CompoundTag compoundtag = p_36036_.getCompound(i);
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
               this.addItem(itemstack);
            }
        }
    }

    public ListTag save(ListTag p_36027_) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            if (!this.getItem(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                this.getItem(i).save(compoundtag);
                p_36027_.add(compoundtag);
            }
        }
        return p_36027_;
    }

    public static void saveAndLoadPrevious(Inventory inventory, ListTag previousInventory) {
        ListTag listTag = new ListTag();
        FakeInventory fakeInventory = new FakeInventory();

        inventory.save(listTag);

        fakeInventory.load(listTag);

        listTag.clear();

        fakeInventory.save(listTag);

        for (int i=0; i<listTag.size(); i++) {
            if (previousInventory.size() < 45) {
                previousInventory.add(listTag.get(i));
            }
        }
    }

}
