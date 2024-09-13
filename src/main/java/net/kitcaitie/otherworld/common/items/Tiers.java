package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class Tiers {

    public static final ForgeTier ROSE_QUARTZ = new ForgeTier(0, 250, 10.0F, 2.0F, 22,
           null, () -> Ingredient.of(OtherworldItems.ROSE_QUARTZ_SHARD.get()));

    public static final ForgeTier OPAL = new ForgeTier(2, 250, 8.0F, 2.0F, 30,
            null, () -> Ingredient.of(OtherworldItems.OPAL.get()));

    public static final ForgeTier SAPPHIRE = new ForgeTier(2, 561, 8.0F, 2.0F, 10,
            null, () -> Ingredient.of(OtherworldItems.SAPPHIRE.get()));

    public static final ForgeTier JADE = new ForgeTier(3, 32, 8.0F, 2.0F, 10,
            null, () -> Ingredient.of(OtherworldItems.JADE.get()));

    public static final ForgeTier TOPAZ = new ForgeTier(2, 561, 8.0F, 2.0F, 10,
            null, () -> Ingredient.of(OtherworldItems.TOPAZ.get()));

}
