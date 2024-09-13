package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.world.feature.FireloggedVegetationPatchFeature;
import net.kitcaitie.otherworld.common.world.feature.OtheranLake;
import net.kitcaitie.otherworld.common.world.feature.OtherworldBlockPatchFeature;
import net.kitcaitie.otherworld.common.world.feature.decorators.FairlingDwellingDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registries.CONFIGURED_FEATURE, Otherworld.MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Otherworld.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, Otherworld.MODID);

    //ORES
    public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORES = createKey("topaz_ores");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORES = createKey("sapphire_ores");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSTONE_IRON_ORES = createKey("deepstone_iron_ores");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSTONE_COAL_ORES = createKey("deepstone_coal_ores");

    //TREES

    public static final RegistryObject<TreeDecoratorType<?>> FAIRLING_DWELLINGS = TREE_DECORATORS.register("fairling_dwelling", () -> new TreeDecoratorType<>(FairlingDwellingDecorator.CODEC));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ROSEWOOD_TREE = createKey("rosewood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTWOOD_TREE = createKey("mystwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_MYSTWOOD_TREE = createKey("mega_mystwood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> REDSPRUCE_TREE = createKey("redspruce_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_REDSPRUCE_TREE = createKey("mega_redspruce_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CINDERBARK_TREE = createKey("cinderbark_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSSWOOD_TREE = createKey("mosswood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ICEWOOD_TREE = createKey("icewood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_ICEWOOD_TREE = createKey("mega_icewood_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_TREE = createKey("willow_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_TREE_PLANTED = createKey("willow_tree_planted");

    //PLANT-LIKE FEATURES
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERS_ROSEIA = createKey("flowers_roseia");

    //LAND-ALTERING
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_POOL_DRIPLEAF = createKey("moss_pool_dripleaf");
    public static final ResourceKey<ConfiguredFeature<?, ?>> EMBERIAN_LAVA_LAKE = createKey("emberian_lava_lake");


    //CUSTOM FEATURES
    public static final RegistryObject<Feature<VegetationPatchConfiguration>> FIRELOGGED_VEGETATION_PATCH = FEATURES.register("firelogged_vegetation_patch",
            () -> new FireloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC));

    public static final RegistryObject<Feature<NetherForestVegetationConfig>> OTHERWORLD_BLOCK_PATCH = FEATURES.register("otherworld_block_patch",
            () -> new OtherworldBlockPatchFeature(NetherForestVegetationConfig.CODEC));
    public static final RegistryObject<Feature<LakeFeature.Configuration>> OTHERAN_LAKE = FEATURES.register("otherworld_lake",
            () -> new OtheranLake(LakeFeature.Configuration.CODEC));

    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(CONFIGURED_FEATURES.getRegistryKey(), new ResourceLocation(Otherworld.MODID, name));
    }

    // PLACED FEATURES
    public static final ResourceKey<PlacedFeature> ROSEGRASS_SINGLE_PLACED = placedFeatureKey("rosegrass_single");
    public static final ResourceKey<PlacedFeature> MYSTWEED_SINGLE_PLACED = placedFeatureKey("mystweed_single");
    public static final ResourceKey<PlacedFeature> CINDERGRASS_SINGLE_PLACED = placedFeatureKey("cindergrass_single");
    public static final ResourceKey<PlacedFeature> FROSTWEED_SINGLE_PLACED = placedFeatureKey("frostweed_single");

    public static ResourceKey<PlacedFeature> placedFeatureKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Otherworld.MODID, name + "_placed"));
    }

    public static void register(IEventBus bus) {
        TREE_DECORATORS.register(bus);
        FEATURES.register(bus);
        CONFIGURED_FEATURES.register(bus);
    }
}
