package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.gui.menu.FakeInventoryMenu;
import net.kitcaitie.otherworld.client.gui.menu.LoadingScreenMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Otherworld.MODID);

    public static RegistryObject<MenuType<LoadingScreenMenu>> LOADING_SCREEN = registerMenu("loading_screen_menu", LoadingScreenMenu::new);
    public static RegistryObject<MenuType<FakeInventoryMenu>> FAKE_INVENTORY = registerMenu("fake_inventory_menu", FakeInventoryMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, IContainerFactory<T> containerFactory) {
        MenuType<T> menuType = new MenuType<>(containerFactory, FeatureFlagSet.of());
        return MENU_TYPES.register(name, () -> menuType);
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
