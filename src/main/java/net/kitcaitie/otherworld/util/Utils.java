package net.kitcaitie.otherworld.util;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Utils {

    public static BlockPos vecToBpos(Vec3 vec3) {
        return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
    }

    public static List<Pair<Supplier<Item>, Integer>> createBountyRewardsForRace(IRaces.Race race) {
        RandomSource randomSource = RandomSource.create();
        List<Pair<Supplier<Item>, Integer>> list;
        switch (race) {
            case HUMAN -> list = new ArrayList<>(List.of(Pair.of(() -> Items.EMERALD, randomSource.nextInt(10, 40)), Pair.of(() -> Items.DIAMOND, randomSource.nextInt(3)), Pair.of(() -> Items.GOLD_INGOT, randomSource.nextInt(10))));
            case ONI -> list = new ArrayList<>(List.of(Pair.of(() -> Items.IRON_INGOT, randomSource.nextInt(10, 40)), Pair.of(() -> Items.COOKED_MUTTON, randomSource.nextInt(20)), Pair.of(() -> Items.LEATHER, randomSource.nextInt(20))));
            case FAIRIE -> list = new ArrayList<>(List.of(Pair.of(OtherworldItems.OPAL, randomSource.nextInt(10, 40)), Pair.of(OtherworldItems.OTHERAN_EYE, randomSource.nextInt( 1)), Pair.of(() -> Items.ENDER_PEARL, randomSource.nextInt(16))));
            case ROSEIAN -> list = new ArrayList<>(List.of(Pair.of(OtherworldItems.ROSEGOLD_COIN, randomSource.nextInt(10, 40)), Pair.of(OtherworldItems.ROSEGOLD_INGOT, randomSource.nextInt(20)), Pair.of(OtherworldItems.ROSERYE_BREAD, randomSource.nextInt(10, 32))));
            case EMBERIAN -> list = new ArrayList<>(List.of(Pair.of(OtherworldItems.TOPAZ_COIN, randomSource.nextInt(10, 40)), Pair.of(OtherworldItems.TOPAZ, randomSource.nextInt(20)), Pair.of(OtherworldItems.SPICEROOT, randomSource.nextInt(10, 64)), Pair.of(() -> Items.COOKED_PORKCHOP, randomSource.nextInt(32))));
            case ICEIAN -> list = new ArrayList<>(List.of(Pair.of(OtherworldItems.SAPPHIRE_COIN, randomSource.nextInt(10, 40)), Pair.of(OtherworldItems.SAPPHIRE, randomSource.nextInt(20)), Pair.of(OtherworldItems.ICEBRUSSEL, randomSource.nextInt(10, 64)), Pair.of(() -> Items.COOKED_BEEF, randomSource.nextInt(32))));
            default -> list = new ArrayList<>();
        }
        list.removeIf((pair) -> pair.getSecond() <= 0);
        return list;
    }

    public static void addItemsForOccupation(IOccupation.Occupation occupation, IRaces races, Player player) {
        ItemStack toAdd = ItemStack.EMPTY;

        if (occupation == IOccupation.Occupation.SOLDIER) {
            ItemStack weapon = getSoldierWeapon(races.getRace(), player.getRandom());
            if (!weapon.isEmpty() && !player.getInventory().hasAnyMatching((stack) -> ItemStack.isSame(stack, weapon))) {
                toAdd = weapon;
            }
        }

        if (!toAdd.isEmpty()) player.addItem(toAdd);
    }

    private static ItemStack getSoldierWeapon(IRaces.Race race, RandomSource source) {
        switch (race) {
            case ROSEIAN -> {
                return OtherworldItems.ROSE_QUARTZ_SWORD.get().getDefaultInstance();
            }
            case FAIRIE -> {
                return EnchantmentHelper.enchantItem(source, OtherworldItems.OPAL_SWORD.get().getDefaultInstance(), source.nextInt(20, 30), true);
            }
            case ONI -> {
                return Items.IRON_SWORD.getDefaultInstance();
            }
            case EMBERIAN -> {
                return OtherworldItems.TOPAZ_SWORD.get().getDefaultInstance();
            }
            case ICEIAN -> {
                return OtherworldItems.SAPPHIRE_SWORD.get().getDefaultInstance();
            }
        }
        return ItemStack.EMPTY;
    }
}
