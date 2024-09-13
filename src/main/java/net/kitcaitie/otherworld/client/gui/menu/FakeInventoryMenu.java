package net.kitcaitie.otherworld.client.gui.menu;

import net.kitcaitie.otherworld.common.player.FakeInventory;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public class FakeInventoryMenu extends ChestMenu {
    private final FakeInventory fakeInventory;
    public FakeInventoryMenu(int id, Inventory inventory, FriendlyByteBuf byteBuf) {
        super(OtherworldMenus.FAKE_INVENTORY.get(), id, inventory, new FakeInventory(), 5);
        this.fakeInventory = (FakeInventory) this.getContainer();
        if (byteBuf != null) this.fakeInventory.load(byteBuf.readNbt().getList("i", 10));
    }

    @Override
    public ItemStack quickMoveStack(Player p_39253_, int p_39254_) {

        return super.quickMoveStack(p_39253_, p_39254_);
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int i, int i1, boolean bool) {
        if (getSlot(i).container instanceof FakeInventory) return false;
        return super.moveItemStackTo(stack, i, i1, bool);
    }

    @Override
    public void removed(Player p_39251_) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(p_39251_);
        character.fakeInventoryTag.clear();
        this.fakeInventory.save(character.fakeInventoryTag);
        character.sendPacket(p_39251_);
        super.removed(p_39251_);
    }
}
