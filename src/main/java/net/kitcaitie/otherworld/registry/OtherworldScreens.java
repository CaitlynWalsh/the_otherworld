package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.client.gui.overlay.SunkenSoulsOverlay;
import net.kitcaitie.otherworld.client.gui.screen.FakeInventoryScreen;
import net.kitcaitie.otherworld.client.gui.screen.LoadingScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class OtherworldScreens {

    public static void register(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(OtherworldMenus.LOADING_SCREEN.get(), LoadingScreen::new);
            MenuScreens.register(OtherworldMenus.FAKE_INVENTORY.get(), FakeInventoryScreen::new);
        });
    }

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.HELMET.id(), "sunken_souls_overlay", new SunkenSoulsOverlay());
    }

}
