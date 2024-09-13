package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.items.Tiers;
import net.kitcaitie.otherworld.common.items.*;
import net.kitcaitie.otherworld.common.story.events.EventTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class OtherworldItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Otherworld.MODID);
    public static final List<RegistryObject<Item>> SECRET_ITEMS = new ArrayList<>();

    //TOTEMS AND MAGICAL ITEMS
    public static final RegistryObject<Item> OTHERWORLD_TOTEM = ITEMS.register("totem_of_the_otherworld", () ->
            new PortalTotemItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant(), List.of(Otherworld.OTHERWORLD, Level.OVERWORLD), () -> Blocks.CRYING_OBSIDIAN, OtherworldBlocks.OTHERWORLD_PORTAL,
                    new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
                        if (context.player.level instanceof ServerLevel sLevel) {
                            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sLevel);
                            bolt.moveTo(Vec3.atBottomCenterOf(((BlockPos)context.object)));
                            bolt.setCause((ServerPlayer) context.player);
                            bolt.setDamage(2.0F);
                            sLevel.addFreshEntity(bolt);
                        }
                    }))));
    public static final RegistryObject<Item> OTHERAN_EYE = registerItem("otheran_eye", new Item.Properties().stacksTo(16).fireResistant());

    //BLOCK ITEMS
    public static final RegistryObject<Item> ROSERYE_SEEDS = ITEMS.register("roserye_seeds", () ->
            new ItemNameBlockItem(OtherworldBlocks.ROSERYE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MULBERRY_SEEDS = ITEMS.register("mulberry_seeds", () ->
            new ItemNameBlockItem(OtherworldBlocks.MULBERRY_VINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPICEROOT_SEEDS = ITEMS.register("spiceroot_seeds", () ->
            new ItemNameBlockItem(OtherworldBlocks.SPICEROOT.get(), new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ICEBRUSSEL_SEEDS = ITEMS.register("icebrussel_seeds", () ->
            new ItemNameBlockItem(OtherworldBlocks.ICEBRUSSEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> SOULBERRY = ITEMS.register("soulberry", () ->
            new ItemNameBlockItem(OtherworldBlocks.SOULBERRY_BUSH.get(), new Item.Properties().food(Foods.SWEET_BERRIES)));
    public static final RegistryObject<Item> THORNBERRY = ITEMS.register("thornberry", () ->
            new ItemNameBlockItem(OtherworldBlocks.THORNBERRY_BUSH.get(), new Item.Properties().food(Foods.SWEET_BERRIES)));

    //GEMS AND MATERIALS
    public static final RegistryObject<Item> RAW_ROSEGOLD = registerDefaultItem("raw_rosegold");
    public static final RegistryObject<Item> ROSEGOLD_INGOT = registerDefaultItem("rosegold_ingot");
    public static final RegistryObject<Item> ROSEGOLD_NUGGET = registerDefaultItem("rosegold_nugget");
    public static final RegistryObject<Item> ROSE_QUARTZ_SHARD = registerDefaultItem("rose_quartz_shard");
    public static final RegistryObject<Item> OPAL = registerDefaultItem("opal");
    public static final RegistryObject<Item> TOPAZ = registerItem("topaz", new Item.Properties().fireResistant());
    public static final RegistryObject<Item> JADE = registerDefaultItem("jade");
    public static final RegistryObject<Item> SAPPHIRE = registerDefaultItem("sapphire");
    public static final RegistryObject<Item> ONYX = registerDefaultItem("onyx");
    public static final RegistryObject<Item> CRIMSON_DIAMOND = registerDefaultItem("crimson_diamond");
    public static final RegistryObject<Item> ROSEGOLD_COIN = registerDefaultItem("rosegold_coin");
    public static final RegistryObject<Item> TOPAZ_COIN = registerItem("topaz_coin", new Item.Properties().fireResistant());
    public static final RegistryObject<Item> SAPPHIRE_COIN = registerDefaultItem("sapphire_coin");

    // RANDOM STUFF
    public static final RegistryObject<Item> WHISP = registerItem("whisp", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> ONI_HORN = registerDefaultItem("oni_horn");
    public static final RegistryObject<Item> WITHERDUST = registerDefaultItem("witherdust");
    public static final RegistryObject<Item> ROSEGOLD_KEY = registerDefaultItem("rosegold_key");
    public static final RegistryObject<Item> IRON_KEY = registerDefaultItem("iron_key");
    public static final RegistryObject<Item> OPAL_KEY = registerDefaultItem("opal_key");
    public static final RegistryObject<Item> TOPAZ_KEY = registerItem("topaz_key", new Item.Properties().fireResistant());
    public static final RegistryObject<Item> SAPPHIRE_KEY = registerDefaultItem("sapphire_key");
    public static final RegistryObject<Item> DIAMOND_RING = ITEMS.register("diamond_ring", () -> new MarriageItem(new Item.Properties()));
    public static final RegistryObject<Item> IRON_RING = ITEMS.register("iron_ring", () -> new MarriageItem(new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_RING = ITEMS.register("rose_quartz_ring", () -> new MarriageItem(new Item.Properties()));
    public static final RegistryObject<Item> OPAL_RING = ITEMS.register("opal_ring", () -> new MarriageItem(new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_RING = ITEMS.register("topaz_ring", () -> new MarriageItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> SAPPHIRE_RING = ITEMS.register("sapphire_ring", () -> new MarriageItem(new Item.Properties()));
    public static final RegistryObject<Item> OMINOUS_BOOK = ITEMS.register("ominous_book", () -> new Item(new Item.Properties()));

    //TOOLS AND ARMOR
    //---Roseian Tools and Armor---//
    public static final RegistryObject<Item> ROSEGOLD_HELMET = ITEMS.register("rosegold_helmet", () ->
            new ArmorItem(Armors.ROSEGOLD, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> ROSEGOLD_CHESTPLATE = ITEMS.register("rosegold_chestplate", () ->
            new ArmorItem(Armors.ROSEGOLD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> ROSEGOLD_LEGGINGS = ITEMS.register("rosegold_leggings", () ->
            new ArmorItem(Armors.ROSEGOLD, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> ROSEGOLD_BOOTS = ITEMS.register("rosegold_boots", () ->
            new ArmorItem(Armors.ROSEGOLD, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_PICKAXE = ITEMS.register("rose_quartz_pickaxe", () ->
            new PickaxeItem(Tiers.ROSE_QUARTZ, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_AXE = ITEMS.register("rose_quartz_axe", () ->
            new AxeItem(Tiers.ROSE_QUARTZ, 6.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_SWORD = ITEMS.register("rose_quartz_sword", () ->
            new SwordItem(Tiers.ROSE_QUARTZ, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_SHOVEL = ITEMS.register("rose_quartz_shovel", () ->
            new ShovelItem(Tiers.ROSE_QUARTZ, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> ROSE_QUARTZ_HOE = ITEMS.register("rose_quartz_hoe", () ->
            new HoeItem(Tiers.ROSE_QUARTZ, 0, -3.0F, new Item.Properties()));

    //---Fairie Tools and Armor---//
    public static final RegistryObject<Item> OPAL_PICKAXE = ITEMS.register("opal_pickaxe", () ->
            new PickaxeItem(Tiers.OPAL, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> OPAL_AXE = ITEMS.register("opal_axe", () ->
            new AxeItem(Tiers.OPAL, 6.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> OPAL_SWORD = ITEMS.register("opal_sword", () ->
            new SwordItem(Tiers.OPAL, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> OPAL_SHOVEL = ITEMS.register("opal_shovel", () ->
            new ShovelItem(Tiers.OPAL, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> OPAL_HOE = ITEMS.register("opal_hoe", () ->
            new HoeItem(Tiers.OPAL, 0, -3.0F, new Item.Properties()));


    //---Emberian Tools and Armor---//
    public static final RegistryObject<Item> TOPAZ_HELMET = ITEMS.register("topaz_helmet", () ->
            new ElementalArmorItem(Armors.TOPAZ, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_CHESTPLATE = ITEMS.register("topaz_chestplate", () ->
            new ElementalArmorItem(Armors.TOPAZ, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_LEGGINGS = ITEMS.register("topaz_leggings", () ->
            new ElementalArmorItem(Armors.TOPAZ, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_BOOTS = ITEMS.register("topaz_boots", () ->
            new ElementalArmorItem(Armors.TOPAZ, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_PICKAXE = ITEMS.register("topaz_pickaxe", () ->
            new PickaxeItem(Tiers.TOPAZ, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_AXE = ITEMS.register("topaz_axe", () ->
            new AxeItem(Tiers.TOPAZ, 6.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_SWORD = ITEMS.register("topaz_sword", () ->
            new SwordItem(Tiers.TOPAZ, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_SHOVEL = ITEMS.register("topaz_shovel", () ->
            new ShovelItem(Tiers.TOPAZ, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> TOPAZ_HOE = ITEMS.register("topaz_hoe", () ->
            new HoeItem(Tiers.TOPAZ, 0, -3.0F, new Item.Properties()));

    //---Iceian Tools and Armor---//
    public static final RegistryObject<Item> SAPPHIRE_HELMET = ITEMS.register("sapphire_helmet", () ->
            new ElementalArmorItem(Armors.SAPPHIRE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_CHESTPLATE = ITEMS.register("sapphire_chestplate", () ->
            new ElementalArmorItem(Armors.SAPPHIRE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_LEGGINGS = ITEMS.register("sapphire_leggings", () ->
            new ElementalArmorItem(Armors.SAPPHIRE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_BOOTS = ITEMS.register("sapphire_boots", () ->
            new ElementalArmorItem(Armors.SAPPHIRE, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_PICKAXE = ITEMS.register("sapphire_pickaxe", () ->
            new PickaxeItem(Tiers.SAPPHIRE, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_AXE = ITEMS.register("sapphire_axe", () ->
            new AxeItem(Tiers.SAPPHIRE, 6.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_SWORD = ITEMS.register("sapphire_sword", () ->
            new SwordItem(Tiers.SAPPHIRE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_SHOVEL = ITEMS.register("sapphire_shovel", () ->
            new ShovelItem(Tiers.SAPPHIRE, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> SAPPHIRE_HOE = ITEMS.register("sapphire_hoe", () ->
            new HoeItem(Tiers.SAPPHIRE, 0, -3.0F, new Item.Properties()));


    public static final RegistryObject<Item> JADE_PICKAXE = ITEMS.register("jade_pickaxe", () ->
            new PickaxeItem(Tiers.JADE, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> JADE_AXE = ITEMS.register("jade_axe", () ->
            new AxeItem(Tiers.JADE, 6.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> JADE_SWORD = ITEMS.register("jade_sword", () ->
            new SwordItem(Tiers.JADE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> JADE_SHOVEL = ITEMS.register("jade_shovel", () ->
            new ShovelItem(Tiers.JADE, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> JADE_HOE = ITEMS.register("jade_hoe", () ->
            new HoeItem(Tiers.JADE, 0, -3.0F, new Item.Properties()));


    //FOOD ITEMS
    public static final RegistryObject<Item> SPICEROOT = ITEMS.register("spiceroot", () ->
            new ElementalFoodItem(new Item.Properties().fireResistant(), (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build(), IWorlds.Worlds.EMBERIA, 5));
    public static final RegistryObject<Item> SPICEROOT_JUICE = ITEMS.register("spiceroot_juice", () ->
            new ElementalFoodItem(new Item.Properties().fireResistant().stacksTo(16), (new FoodProperties.Builder()).alwaysEat().nutrition(3).saturationMod(0.3F).build(), IWorlds.Worlds.EMBERIA, 10, true, () -> Items.GLASS_BOTTLE));
    public static final RegistryObject<Item> SPICEROOT_STEW = ITEMS.register("spiceroot_stew", () ->
            new ElementalFoodItem(new Item.Properties().fireResistant().stacksTo(1), Foods.RABBIT_STEW, IWorlds.Worlds.EMBERIA, 15, false, () -> Items.BOWL));
    public static final RegistryObject<Item> ICEBRUSSEL = ITEMS.register("icebrussel", () ->
            new ElementalFoodItem(new Item.Properties(), (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build(), IWorlds.Worlds.GLACEIA, 340));
    public static final RegistryObject<Item> ICEBRUSSEL_SYRUP = ITEMS.register("icebrussel_syrup", () ->
            new ElementalFoodItem(new Item.Properties().stacksTo(16), (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build(), IWorlds.Worlds.GLACEIA, 680, true, () -> Items.GLASS_BOTTLE));
    public static final RegistryObject<Item> ICEBRUSSEL_CREAM = ITEMS.register("icebrussel_cream", () ->
            new ElementalFoodItem(new Item.Properties().stacksTo(1), Foods.RABBIT_STEW, IWorlds.Worlds.GLACEIA, 1020, false, () -> Items.BOWL));
    public static final RegistryObject<Item> MULBERRY = ITEMS.register("mulberry", () ->
            new ConsumableItem(new Item.Properties().food(Foods.CARROT), MULBERRY_SEEDS, false));
    public static final RegistryObject<Item> PLUM = registerItem("plum", new Item.Properties().food(new FoodProperties.Builder().nutrition(Foods.BAKED_POTATO.getNutrition()).saturationMod(Foods.BREAD.getSaturationModifier()).build()));
    public static final RegistryObject<Item> ROSERYE_BREAD = ITEMS.register("roserye_bread", () ->
            new Item(new Item.Properties().food((new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100), 0.5F).build())));

    //INGREDIENTS
    public static final RegistryObject<Item> ROSERYE = registerDefaultItem("roserye");
    public static final RegistryObject<Item> FROZEN_BEEF = registerDefaultItem("frozen_beef");

    //SPAWN EGGS
    public static final RegistryObject<Item> FIGHTING_FISH_BUCKET = ITEMS.register("fighting_fish_bucket", () ->
            new MobBucketItem(OtherworldEntities.FIGHTING_FISH, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1)));

    //---People---//
    public static final RegistryObject<Item> HUMAN_SPAWN_EGG = ITEMS.register("human_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.HUMAN, 9073247, 3351569, new Item.Properties()));
    public static final RegistryObject<Item> ROSEIAN_SPAWN_EGG = ITEMS.register("roseian_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.ROSEIAN, 16741026, 13307734, new Item.Properties()));
    public static final RegistryObject<Item> FAIRIE_SPAWN_EGG = ITEMS.register("fairie_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.FAIRIE, 8267465, 13265125, new Item.Properties()));
    public static final RegistryObject<Item> GHOUL_SPAWN_EGG = ITEMS.register("ghoul_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.GHOUL, 3288119, 16374238, new Item.Properties()));
    public static final RegistryObject<Item> ONI_SPAWN_EGG = ITEMS.register("oni_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.ONI, 10453360, 4008735, new Item.Properties()));
    public static final RegistryObject<Item> EMBERIAN_SPAWN_EGG = ITEMS.register("emberian_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.EMBERIAN, 16747520, 16729344, new Item.Properties()));
    public static final RegistryObject<Item> ICEIAN_SPAWN_EGG = ITEMS.register("iceian_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.ICEIAN, 299714, 8580861, new Item.Properties()));

    //---Animals---//
    public static final RegistryObject<Item> ROSEIAN_RABBIT_SPAWN_EGG = ITEMS.register("roseian_rabbit_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.ROSEIAN_RABBIT, -13088, -2004078, new Item.Properties()));
    public static final RegistryObject<Item> CRYSTLING_SPAWN_EGG = ITEMS.register("crystling_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.CRYSTLING, 16751565, 16769279, new Item.Properties()));
    public static final RegistryObject<Item> FAIRLING_SPAWN_EGG = ITEMS.register("fairling_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.FAIRLING, 7419301, 11703011, new Item.Properties()));
    public static final RegistryObject<Item> GOATEER_SPAWN_EGG = ITEMS.register("goateer_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.GOATEER, 8017727, 13406570, new Item.Properties()));
    public static final RegistryObject<Item> GRIZZLY_SPAWN_EGG = ITEMS.register("grizzly_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.GRIZZLY, 5517338, 7360058, new Item.Properties()));
    public static final RegistryObject<Item> FERAL_WOLF_SPAWN_EGG = ITEMS.register("feral_wolf_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.FERAL_WOLF, 7360058, 7352368, new Item.Properties()));
    public static final RegistryObject<Item> PYROBOAR_SPAWN_EGG = ITEMS.register("pyroboar_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.PYROBOAR, 8667419, 15434501, new Item.Properties()));
    public static final RegistryObject<Item> SNOWPAKA_SPAWN_EGG = ITEMS.register("snowpaka_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.SNOWPAKA, 8442093, 11854580, new Item.Properties()));
    public static final RegistryObject<Item> FIGHTING_FISH_SPAWN_EGG = ITEMS.register("fighting_fish_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.FIGHTING_FISH, 6218739, 2712683, new Item.Properties()));
    public static final RegistryObject<Item> PHLYMP_SPAWN_EGG = ITEMS.register("phlymp_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.PHLYMP, 14208696, 16777215, new Item.Properties()));
    public static final RegistryObject<Item> WHISP_SPAWN_EGG = ITEMS.register("whisp_spawn_egg", () ->
            new ForgeSpawnEggItem(OtherworldEntities.WHISP, 16777215, 16776424, new Item.Properties()));

    //OTHER
    public static final RegistryObject<Item> SUNKEN_SOULS_BUCKET = ITEMS.register("sunken_souls_bucket",
            () -> new SolidBucketItem(OtherworldBlocks.SUNKEN_SOULS.get(), SoundEvents.SOUL_SAND_PLACE, new Item.Properties().stacksTo(1)));


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static void addSecretItem(RegistryObject<Item> item) {
        SECRET_ITEMS.add(item);
    }

    public static RegistryObject<Item> registerItem(String name, Item.Properties properties) {
        return ITEMS.register(name, () -> new Item(properties));
    }

    public static RegistryObject<Item> registerDefaultItem(String name) {
        return registerItem(name, new Item.Properties());
    }

}
