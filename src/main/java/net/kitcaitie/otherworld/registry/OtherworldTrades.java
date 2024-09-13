package net.kitcaitie.otherworld.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class OtherworldTrades {
    private static final int DEFAULT_SUPPLY = 12;
    private static final int COMMON_ITEMS_SUPPLY = 16;
    private static final int UNCOMMON_ITEMS_SUPPLY = 3;
    private static final int XP_LEVEL_1_SELL = 1;
    private static final int XP_LEVEL_1_BUY = 2;
    private static final int XP_LEVEL_2_SELL = 5;
    private static final int XP_LEVEL_2_BUY = 10;
    private static final int XP_LEVEL_3_SELL = 10;
    private static final int XP_LEVEL_3_BUY = 20;
    private static final int XP_LEVEL_4_SELL = 15;
    private static final int XP_LEVEL_4_BUY = 30;
    private static final int XP_LEVEL_5_TRADE = 30;
    private static final float LOW_TIER_PRICE_MULTIPLIER = 0.05F;
    private static final float HIGH_TIER_PRICE_MULTIPLIER = 0.2F;
    private static final Map<IRaces.Race, Supplier<Item>> CURRENCY = Map.of(
            IRaces.Race.HUMAN, () -> Items.EMERALD,
            IRaces.Race.ONI, () -> Items.IRON_INGOT,
            IRaces.Race.ROSEIAN, OtherworldItems.ROSEGOLD_COIN,
            IRaces.Race.FAIRIE, OtherworldItems.OPAL,
            IRaces.Race.EMBERIAN, OtherworldItems.TOPAZ_COIN,
            IRaces.Race.ICEIAN, OtherworldItems.SAPPHIRE_COIN
    );

    public static final Map<IRaces.Race, List<Map<IOccupation.VillagerType, Int2ObjectMap<ItemListing[]>>>> TRADES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(IRaces.Race.ONI, Lists.newArrayList(
                toMap(IOccupation.VillagerType.FARMER, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.WHEAT, 1, 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new CurrencyForItems(IRaces.Race.ONI, Items.WHEAT_SEEDS, 64, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.PUMPKIN, 1, 3, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY), new CurrencyForItems(IRaces.Race.ONI, OtherworldItems.THORNBERRY::get, 32, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL)},
                        3, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, OtherworldBlocks.THORNED_TULIP.get().asItem(), 1, 4, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY), new CurrencyForItems(IRaces.Race.ONI, OtherworldBlocks.REDSPRUCE_SAPLING.get().asItem(), 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_3_SELL)},
                        4, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.WATER_BUCKET, 1, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_4_BUY), new ItemsForCurrency(IRaces.Race.ONI, Items.BONE_MEAL, 1, 9, COMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY)},
                        5, new ItemListing[]{new CurrencyForItems(IRaces.Race.ONI, OtherworldItems.ROSERYE::get, 8, COMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE), new CurrencyForItems(IRaces.Race.ONI, OtherworldItems.ROSE_QUARTZ_SHARD::get, 20, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE)}
                )),
                toMap(IOccupation.VillagerType.COOK, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.BREAD, 1, 8, 4, XP_LEVEL_1_BUY), new ItemsForCurrency(IRaces.Race.ONI, Items.COOKED_MUTTON, 1, 6, 4, XP_LEVEL_1_BUY)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.PUMPKIN_PIE, 2, 1, 4, XP_LEVEL_2_BUY), new CurrencyForItems(IRaces.Race.ONI, Items.SUGAR, 6, DEFAULT_SUPPLY, XP_LEVEL_2_SELL)},
                        3, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, Items.COOKED_SALMON, 1, 6, DEFAULT_SUPPLY, XP_LEVEL_3_BUY), new ItemsForCurrency(IRaces.Race.ONI, Items.MUSHROOM_STEW, 1, 1, DEFAULT_SUPPLY, XP_LEVEL_3_BUY)},
                        4, new ItemListing[]{new CurrencyForItems(IRaces.Race.ONI, Items.RABBIT, 8, DEFAULT_SUPPLY, XP_LEVEL_4_SELL), new ItemsForCurrency(IRaces.Race.ONI, Items.RABBIT_STEW, 1, 1, 4, XP_LEVEL_4_BUY)},
                        5, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ONI, OtherworldItems.ROSERYE_BREAD.get(), 1, 6, 4, XP_LEVEL_5_TRADE), new ItemsForCurrency(IRaces.Race.ONI, Items.HONEY_BOTTLE, 2, 1, 4, XP_LEVEL_5_TRADE)}
                ))
        ));
        map.put(IRaces.Race.ROSEIAN, Lists.newArrayList(
                toMap(IOccupation.VillagerType.FARMER, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ROSEIAN, OtherworldItems.ROSERYE.get(), 1, 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new CurrencyForItems(IRaces.Race.ROSEIAN, OtherworldItems.ROSERYE_SEEDS.get(), 64, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL)},
                        2, new ItemListing[]{new CurrencyForItems(IRaces.Race.ROSEIAN, OtherworldItems.MULBERRY.get(), 32, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL), new ItemsForCurrency(IRaces.Race.ROSEIAN, OtherworldBlocks.BLUSHING_MUMS.get().asItem(), 1, 4, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL)},
                        3, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.WATER_BUCKET, 3, 1, 4, XP_LEVEL_3_BUY), new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.BONE_MEAL, 1, 6, COMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY)},
                        4, new ItemListing[]{new CurrencyForItems(IRaces.Race.ROSEIAN, Items.SUGAR_CANE, 6, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_4_SELL), new CurrencyForItems(IRaces.Race.ROSEIAN, Items.PUMPKIN, 3, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_4_SELL)},
                        5, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ROSEIAN, OtherworldBlocks.ROSE_QUARTZ_CRYSTAL.get().asItem(), 6, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE)}
                )),
                toMap(IOccupation.VillagerType.COOK, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ROSEIAN, OtherworldItems.ROSERYE_BREAD.get(), 1, 8, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.COOKED_RABBIT, 1, 4, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY)},
                        2, new ItemListing[]{new CurrencyForItems(IRaces.Race.ROSEIAN, Items.SUGAR, 6, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL), new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.RABBIT_STEW, 1, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY)},
                        3, new ItemListing[]{new CurrencyForItems(IRaces.Race.ROSEIAN, OtherworldItems.THORNBERRY.get(), 18, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_SELL), new CurrencyForItems(IRaces.Race.ROSEIAN, OtherworldItems.PLUM.get(), 18, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_SELL)},
                        4, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.HONEY_BOTTLE, 3, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE), new ItemsForCurrency(IRaces.Race.ROSEIAN, Items.CAKE, 6, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_4_BUY)},
                        5, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.ROSEIAN, Items.POTION, MobEffects.HEAL, 8, 1, 4, XP_LEVEL_5_TRADE)}
                ))
        ));
        map.put(IRaces.Race.FAIRIE, Lists.newArrayList(
                toMap(IOccupation.VillagerType.FARMER, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.FAIRIE, OtherworldItems.MULBERRY.get(), 1, 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new ItemsForCurrency(IRaces.Race.FAIRIE, OtherworldItems.PLUM.get(), 1, 8, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new CurrencyForItems(IRaces.Race.FAIRIE, OtherworldItems.MULBERRY_SEEDS.get(), 64, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL)},
                        2, new ItemListing[]{new CurrencyForItems(IRaces.Race.FAIRIE, OtherworldItems.ROSERYE.get(), 18, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL), new CurrencyForItems(IRaces.Race.FAIRIE, OtherworldItems.THORNBERRY.get(), 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL)},
                        3, new ItemListing[]{new ItemsForCurrency(IRaces.Race.FAIRIE, Items.WATER_BUCKET, 3, 1, 4, XP_LEVEL_3_BUY), new ItemsForCurrency(IRaces.Race.FAIRIE, Items.BONE_MEAL, 1, 6, COMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY)},
                        4, new ItemListing[]{new CurrencyForItems(IRaces.Race.FAIRIE, OtherworldItems.SPICEROOT.get(), 6, COMMON_ITEMS_SUPPLY, XP_LEVEL_4_SELL), new CurrencyForItems(IRaces.Race.FAIRIE, OtherworldItems.ICEBRUSSEL.get(), 6, COMMON_ITEMS_SUPPLY, XP_LEVEL_4_SELL)},
                        5, new ItemListing[]{new ItemsForCurrency(IRaces.Race.FAIRIE, OtherworldItems.WHISP.get(), 4, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE)}

                )),
                toMap(IOccupation.VillagerType.COOK, ImmutableMap.of(
                        1, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.SPLASH_POTION, MobEffects.WEAKNESS, 6, 1, 6, XP_LEVEL_1_BUY), new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.SPLASH_POTION, MobEffects.MOVEMENT_SLOWDOWN, 6, 1, 6, XP_LEVEL_1_BUY)},
                        2, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.MOVEMENT_SPEED, 8, 1, 6, XP_LEVEL_2_BUY), new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.JUMP, 6, 1, 6, XP_LEVEL_2_BUY)},
                        3, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.FIRE_RESISTANCE, 6, 1, 6, XP_LEVEL_3_BUY), new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.WATER_BREATHING, 6, 1, 6, XP_LEVEL_3_BUY)},
                        4, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.INVISIBILITY, 10, 1, 6, XP_LEVEL_4_BUY), new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.POTION, MobEffects.NIGHT_VISION, 8, 1, 6, XP_LEVEL_4_BUY)},
                        5, new ItemListing[]{new PotionItemsForCurrency(IRaces.Race.FAIRIE, Items.SPLASH_POTION, MobEffects.HARM, 12, 1, 6, XP_LEVEL_5_TRADE)}
                ))
        ));
        map.put(IRaces.Race.EMBERIAN, Lists.newArrayList(
                toMap(IOccupation.VillagerType.FARMER, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.EMBERIAN, OtherworldItems.SPICEROOT.get(), 1, 16, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new CurrencyForItems(IRaces.Race.EMBERIAN, OtherworldItems.SPICEROOT_SEEDS.get(), 64, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.EMBERIAN, Items.BONE_MEAL, 1, 9, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY), new ItemsForCurrency(IRaces.Race.EMBERIAN, Items.LAVA_BUCKET, 4,1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY)},
                        3, new ItemListing[]{},
                        4, new ItemListing[]{},
                        5, new ItemListing[]{}
                )),
                toMap(IOccupation.VillagerType.COOK, ImmutableMap.of(
                        1, new ItemListing[]{new CurrencyForItems(IRaces.Race.EMBERIAN, Items.COOKED_PORKCHOP, 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL), new ItemsForCurrency(IRaces.Race.EMBERIAN, OtherworldItems.SPICEROOT_JUICE.get(), 1, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.EMBERIAN, OtherworldItems.SPICEROOT_STEW.get(), 2, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY)},
                        3, new ItemListing[]{},
                        4, new ItemListing[]{},
                        5, new ItemListing[]{}
                ))
        ));
        map.put(IRaces.Race.ICEIAN, Lists.newArrayList(
                toMap(IOccupation.VillagerType.FARMER, ImmutableMap.of(
                        1, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ICEIAN, OtherworldItems.ICEBRUSSEL.get(), 1, 16, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY), new CurrencyForItems(IRaces.Race.ICEIAN, OtherworldItems.ICEBRUSSEL_SEEDS.get(), 64, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ICEIAN, Items.BONE_MEAL, 1, 9, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY), new ItemsForCurrency(IRaces.Race.ICEIAN, Items.POWDER_SNOW_BUCKET, 4,1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_BUY)},
                        3, new ItemListing[]{},
                        4, new ItemListing[]{},
                        5, new ItemListing[]{}
                )),
                toMap(IOccupation.VillagerType.COOK, ImmutableMap.of(
                        1, new ItemListing[]{new CurrencyForItems(IRaces.Race.ICEIAN, OtherworldItems.FROZEN_BEEF.get(), 12, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_SELL), new ItemsForCurrency(IRaces.Race.ICEIAN, OtherworldItems.ICEBRUSSEL_SYRUP.get(), 1, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY)},
                        2, new ItemListing[]{new ItemsForCurrency(IRaces.Race.ICEIAN, OtherworldItems.ICEBRUSSEL_CREAM.get(), 2, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY)},
                        3, new ItemListing[]{},
                        4, new ItemListing[]{},
                        5, new ItemListing[]{}
                ))
        ));
    });

    private static Map<IOccupation.VillagerType, Int2ObjectMap<ItemListing[]>> toMap(IOccupation.VillagerType occupation, ImmutableMap<Integer, ItemListing[]> map) {
        return Maps.newHashMap(Map.of(occupation, new Int2ObjectOpenHashMap<>(map)));
    }

    public interface ItemListing {
        @Nullable
        MerchantOffer getOffer(Entity p_219693_, RandomSource p_219694_);
    }

    static class CurrencyForItems implements ItemListing {
        private final IRaces.Race race;
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public CurrencyForItems(IRaces.Race race, Block p_35657_, int p_35658_, int p_35659_, int p_35660_) {
            this(race, p_35657_.asItem(), p_35658_, p_35659_, p_35660_);
        }

        public CurrencyForItems(IRaces.Race race, ItemLike p_35657_, int p_35658_, int p_35659_, int p_35660_) {
            this.race = race;
            this.item = p_35657_.asItem();
            this.cost = p_35658_;
            this.maxUses = p_35659_;
            this.villagerXp = p_35660_;
            this.priceMultiplier = LOW_TIER_PRICE_MULTIPLIER;
        }

        public MerchantOffer getOffer(Entity p_219682_, RandomSource p_219683_) {
            ItemStack itemstack = new ItemStack(this.item, this.cost);
            return new MerchantOffer(itemstack, new ItemStack(CURRENCY.get(race).get()), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsForCurrency implements ItemListing {
        protected final IRaces.Race race;
        protected ItemStack itemStack;
        protected final int emeraldCost;
        protected final int numberOfItems;
        protected final int maxUses;
        protected final int villagerXp;
        protected final float priceMultiplier;

        public ItemsForCurrency(IRaces.Race race, Block p_35765_, int p_35766_, int p_35767_, int p_35768_, int p_35769_) {
            this(race, new ItemStack(p_35765_), p_35766_, p_35767_, p_35768_, p_35769_);
        }

        public ItemsForCurrency(IRaces.Race race, Item p_35741_, int p_35742_, int p_35743_, int p_35744_) {
            this(race, new ItemStack(p_35741_), p_35742_, p_35743_, DEFAULT_SUPPLY, p_35744_);
        }

        public ItemsForCurrency(IRaces.Race race, Item p_35746_, int p_35747_, int p_35748_, int p_35749_, int p_35750_) {
            this(race, new ItemStack(p_35746_), p_35747_, p_35748_, p_35749_, p_35750_);
        }

        public ItemsForCurrency(IRaces.Race race, ItemStack p_35752_, int p_35753_, int p_35754_, int p_35755_, int p_35756_) {
            this(race, p_35752_, p_35753_, p_35754_, p_35755_, p_35756_, LOW_TIER_PRICE_MULTIPLIER);
        }

        public ItemsForCurrency(IRaces.Race race, ItemStack p_35758_, int p_35759_, int p_35760_, int p_35761_, int p_35762_, float p_35763_) {
            this.race = race;
            this.itemStack = p_35758_;
            this.emeraldCost = p_35759_;
            this.numberOfItems = p_35760_;
            this.maxUses = p_35761_;
            this.villagerXp = p_35762_;
            this.priceMultiplier = p_35763_;
        }

        public MerchantOffer getOffer(Entity p_219699_, RandomSource p_219700_) {
            return new MerchantOffer(new ItemStack(CURRENCY.get(race).get(), this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsAndCurrencyToItems implements ItemListing {
        private final IRaces.Race race;
        private final ItemStack fromItem;
        private final int fromCount;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int toCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsAndCurrencyToItems(IRaces.Race race, ItemLike p_35725_, int p_35726_, Item p_35727_, int p_35728_, int p_35729_, int p_35730_) {
            this(race, p_35725_, p_35726_, 1, p_35727_, p_35728_, p_35729_, p_35730_);
        }

        public ItemsAndCurrencyToItems(IRaces.Race race, ItemLike p_35717_, int p_35718_, int p_35719_, Item p_35720_, int p_35721_, int p_35722_, int p_35723_) {
            this.race = race;
            this.fromItem = new ItemStack(p_35717_);
            this.fromCount = p_35718_;
            this.emeraldCost = p_35719_;
            this.toItem = new ItemStack(p_35720_);
            this.toCount = p_35721_;
            this.maxUses = p_35722_;
            this.villagerXp = p_35723_;
            this.priceMultiplier = LOW_TIER_PRICE_MULTIPLIER;
        }

        @Nullable
        public MerchantOffer getOffer(Entity p_219696_, RandomSource p_219697_) {
            return new MerchantOffer(new ItemStack(CURRENCY.get(race).get(), this.emeraldCost), new ItemStack(this.fromItem.getItem(), this.fromCount), new ItemStack(this.toItem.getItem(), this.toCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class PotionItemsForCurrency extends ItemsForCurrency {
        private final MobEffect potionKey;
        public PotionItemsForCurrency(IRaces.Race race, Item item, MobEffect potionKey, int itemAmount, int emeraldCost, int maxUses, int priceMultiplier) {
            super(race, item, itemAmount, emeraldCost, maxUses, priceMultiplier);
            this.potionKey = potionKey;
        }

        @Override
        public MerchantOffer getOffer(Entity p_219699_, RandomSource p_219700_) {
            Potion potion = BuiltInRegistries.POTION.stream().filter((p) -> p.getEffects().stream().anyMatch((m) -> m.getEffect().equals(potionKey))).findFirst().orElse(null);
            if (potion == null) {
                Otherworld.LOGGER.error("Failed to load potion trade for effect: " + potionKey.getDescriptionId() + " | Potion was not found in registry.");
                return null;
            }
            return new MerchantOffer(new ItemStack(CURRENCY.get(race).get(), this.emeraldCost), PotionUtils.setPotion(new ItemStack(itemStack.getItem(), numberOfItems), potion), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

}
