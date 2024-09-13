package net.kitcaitie.otherworld.client.gui.menu;

import net.kitcaitie.otherworld.registry.OtherworldMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class LoadingScreenMenu extends EmptyMenu {

    public LoadingScreenMenu(int id, Inventory inventory, FriendlyByteBuf byteBuf) {
        super(OtherworldMenus.LOADING_SCREEN.get(), id, inventory, byteBuf);
    }

}
