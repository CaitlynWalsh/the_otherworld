package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class OtherworldTab {

    public static CreativeModeTab OTHERWORLD_TAB;

    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
       OTHERWORLD_TAB = event.registerCreativeModeTab(new ResourceLocation(Otherworld.MODID, "otherworld_tab"), builder -> builder
                .title(Component.translatable("itemGroup.otherworld.otherworld_tab"))
                .icon(() -> new ItemStack(OtherworldItems.OTHERWORLD_TOTEM.get()))
                .build());
    }

}
