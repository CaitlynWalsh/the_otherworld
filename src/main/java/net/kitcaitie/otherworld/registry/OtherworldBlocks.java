package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.blocks.*;
import net.kitcaitie.otherworld.common.blocks.entity.EntityNestBlockEntity;
import net.kitcaitie.otherworld.common.blocks.teleporters.PrisonerTeleportBlock;
import net.kitcaitie.otherworld.common.world.grower.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class OtherworldBlocks {

    public static final BlockSetType ROSEWOOD = new BlockSetType("rosewood");
    public static final BlockSetType REDSPRUCE = new BlockSetType("redspruce");
    public static final BlockSetType MYSTWOOD = new BlockSetType("mystwood");
    public static final BlockSetType CINDERBARK = new BlockSetType("cinderbark");
    public static final BlockSetType ICEWOOD = new BlockSetType("icewood", SoundType.GLASS, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON);
    public static final BlockSetType ROSEGOLD = new BlockSetType("rosegold", SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);
    public static final BlockSetType TOPAZ = new BlockSetType("topaz", SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);
    public static final BlockSetType SAPPHIRE = new BlockSetType("sapphire", SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Otherworld.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Otherworld.MODID);


    // PORTALS
    public static final RegistryObject<Block> OTHERWORLD_PORTAL = registerBlockNoItem("otherworld_portal", () -> new PortalBlock(Otherworld.OTHERWORLD, Level.OVERWORLD, OtherworldPOIs.OTHERWORLD_PORTAL, () -> Blocks.CRYING_OBSIDIAN));


    //TELEPORTER BLOCKS
    public static final RegistryObject<Block> ONI_PRISON_TELEPORTER = registerBlockNoCreativeItem("oni_prison_teleporter",
            () -> new PrisonerTeleportBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK), IWorlds.Worlds.DEEPWOODS, () -> OtherworldEntities.ONI::get));
    public static final RegistryObject<Block> ROSEIAN_PRISON_TELEPORTER = registerBlockNoCreativeItem("roseian_prison_teleporter",
            () -> new PrisonerTeleportBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK), IWorlds.Worlds.ROSEIA, () -> OtherworldEntities.ROSEIAN::get));
    public static final RegistryObject<Block> FAIRIE_PRISON_TELEPORTER = registerBlockNoCreativeItem("fairie_prison_teleporter",
            () -> new PrisonerTeleportBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK), IWorlds.Worlds.ENCHANTIA, () -> OtherworldEntities.FAIRIE::get));
    public static final RegistryObject<Block> EMBERIAN_PRISON_TELEPORTER = registerBlockNoCreativeItem("emberian_prison_teleporter",
            () -> new PrisonerTeleportBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK), IWorlds.Worlds.EMBERIA, () -> OtherworldEntities.EMBERIAN::get));
    public static final RegistryObject<Block> ICEIAN_PRISON_TELEPORTER = registerBlockNoCreativeItem("iceian_prison_teleporter",
            () -> new PrisonerTeleportBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK), IWorlds.Worlds.GLACEIA, () -> OtherworldEntities.ICEIAN::get));

    // UTILITY BLOCKS
    public static final RegistryObject<Block> OUTPOST_CRATE = registerBlockNoCreativeItem("outpost_crate", () ->
            new OutpostCrateBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).sound(SoundType.WOOD).color(MaterialColor.WOOD)));


    //ROSEIAN BLOCKS
    public static final RegistryObject<Block> ROSE_QUARTZ_SOIL = registerBlock("rose_quartz_soil", () ->
            new OtheranDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.COLOR_MAGENTA), IWorlds.Worlds.ROSEIA));
    public static final RegistryObject<Block> ROSEGRASS_BLOCK = registerBlock("rosegrass_block", () ->
            new OtheranGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.COLOR_PINK), IWorlds.Worlds.ROSEIA, ROSE_QUARTZ_SOIL.get()));
    public static final RegistryObject<Block> ROSE_QUARTZ_FARMLAND = registerBlock("rose_quartz_farmland", () ->
            new OtheranFarmBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND).color(MaterialColor.COLOR_MAGENTA), IWorlds.Worlds.ROSEIA, ROSE_QUARTZ_SOIL.get(), 4));
    public static final RegistryObject<Block> ROSERYE_HAYBALE = registerBlock("roserye_haybale", () ->
            new HayBlock(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK).color(MaterialColor.COLOR_PINK)));
    public static final RegistryObject<Block> ROSEGOLD_ORE = registerBlock("rosegold_ore", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_ROCK = registerBlock("rose_quartz_rock", () ->
            new OtheranBlock(BlockBehaviour.Properties.copy(Blocks.STONE).color(MaterialColor.TERRACOTTA_MAGENTA), IWorlds.Worlds.ROSEIA));
    public static final RegistryObject<Block> ROSE_QUARTZ_ROCK_SLAB = registerBlock("rose_quartz_rock_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_SLAB).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_ROCK_STAIRS = registerBlock("rose_quartz_rock_stairs", () ->
            new StairBlock(() -> ROSE_QUARTZ_ROCK.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_BRICKS = registerBlock("rose_quartz_bricks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_BRICK_SLAB = registerBlock("rose_quartz_brick_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.BRICK_SLAB).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_BRICK_STAIRS = registerBlock("rose_quartz_brick_stairs", () ->
            new StairBlock(() -> ROSE_QUARTZ_BRICKS.get().defaultBlockState() ,BlockBehaviour.Properties.copy(Blocks.BRICK_STAIRS).color(MaterialColor.TERRACOTTA_MAGENTA)));
    public static final RegistryObject<Block> ROSE_QUARTZ_CRYSTAL = registerBlock("rose_quartz_crystal", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER).noCollission().noOcclusion().color(MaterialColor.TERRACOTTA_WHITE).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 7), IWorlds.Worlds.ROSEIA));
    public static final RegistryObject<Block> BLUSHING_MUMS = registerBlock("blushing_mums", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.POPPY).color(MaterialColor.TERRACOTTA_WHITE).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 4), IWorlds.Worlds.ROSEIA));
    public static final RegistryObject<Block> ROSEGRASS = registerBlock("rosegrass", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.GRASS).color(MaterialColor.COLOR_PINK), IWorlds.Worlds.ROSEIA));

    //---Rosewood Tree Type---//
    public static final RegistryObject<Block> STRIPPED_ROSEWOOD_LOG = registerBlock("stripped_rosewood_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> ROSEWOOD_LOG = registerBlock("rosewood_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG), STRIPPED_ROSEWOOD_LOG.get()));
    public static final RegistryObject<Block> ROSEWOOD_LEAVES = registerBlock("rosewood_leaves", () ->
            new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).color(MaterialColor.TERRACOTTA_PINK)));
    public static final RegistryObject<Block> ROSEWOOD_PLANKS = registerBlock("rosewood_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> ROSEWOOD_SAPLING = registerBlock("rosewood_sapling", () ->
            new OtheranSaplingBlock(new RosewoodTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).color(MaterialColor.COLOR_PINK), IWorlds.Worlds.ROSEIA));
    public static final RegistryObject<Block> ROSEWOOD_SLAB = registerBlock("rosewood_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> ROSEWOOD_STAIRS = registerBlock("rosewood_stairs", () ->
            new StairBlock(() -> ROSEWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> ROSEWOOD_DOOR = registerBlock("rosewood_door", () ->
            new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), ROSEWOOD));

    public static final RegistryObject<Block> ROSEGOLD_BARS = registerBlock("rosegold_bars", () ->
            new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> ROSEGOLD_BARRED_DOOR = registerBlock("rosegold_barred_door", () ->
            new LockableDoorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F).noOcclusion(), ROSEGOLD, OtherworldItems.ROSEGOLD_KEY, SoundEvents.CHAIN_BREAK));


    // ENCHANTIA BLOCKS
    public static final RegistryObject<Block> ENCHANTED_SOIL = registerBlock("enchanted_soil", () ->
            new OtheranDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.COLOR_CYAN), IWorlds.Worlds.ENCHANTIA));
    public static final RegistryObject<Block> MYSTIC_MOSS = registerBlock("mystic_moss", () ->
            new OtheranGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.COLOR_PURPLE), IWorlds.Worlds.ENCHANTIA, ENCHANTED_SOIL.get()));
    public static final RegistryObject<Block> ENCHANTED_FARMLAND = registerBlock("enchanted_farmland", () ->
            new OtheranFarmBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND).color(MaterialColor.COLOR_CYAN), IWorlds.Worlds.ENCHANTIA, ENCHANTED_SOIL.get(), 4));
    public static final RegistryObject<Block> MYSTWEED = registerBlock("mystweed", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.GRASS).color(MaterialColor.COLOR_PURPLE), IWorlds.Worlds.ENCHANTIA));
    public static final RegistryObject<Block> MAJIA_POPPY = registerBlock("majia_poppy", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(MYSTWEED.get()).emissiveRendering((bs, bg, bp) -> true).hasPostProcess((bs, bg, bp) -> true).lightLevel((bs) -> 1), IWorlds.Worlds.ENCHANTIA));
    public static final RegistryObject<Block> LAPISTONE = registerBlock("lapistone", () ->
            new OtheranBlock(BlockBehaviour.Properties.copy(Blocks.STONE).color(MaterialColor.LAPIS), IWorlds.Worlds.ENCHANTIA));
    public static final RegistryObject<Block> OPAL_ORE = registerBlock("opal_ore", () ->
            new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE).color(MaterialColor.LAPIS), UniformInt.of(3, 7), IWorlds.Worlds.ENCHANTIA));

    //---Mystwood Tree Type---//
    public static final RegistryObject<Block> STRIPPED_MYSTWOOD_LOG = registerBlock("stripped_mystwood_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> MYSTWOOD_LOG = registerBlock("mystwood_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG), STRIPPED_MYSTWOOD_LOG.get()));
    public static final RegistryObject<Block> STRIPPED_MYSTWOOD_WOOD = registerBlock("stripped_mystwood_wood", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(STRIPPED_MYSTWOOD_LOG.get())));
    public static final RegistryObject<Block> MYSTWOOD_WOOD = registerBlock("mystwood_wood", () ->
            new LogBlock(BlockBehaviour.Properties.copy(MYSTWOOD_LOG.get()), STRIPPED_MYSTWOOD_WOOD.get()));
    public static final RegistryObject<Block> MYSTWOOD_LEAVES = registerBlock("mystwood_leaves", () ->
            new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).color(MaterialColor.COLOR_LIGHT_BLUE)));
    public static final RegistryObject<Block> MYSTWOOD_SAPLING = registerBlock("mystwood_sapling", () ->
            new OtheranSaplingBlock(new MystwoodTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING), IWorlds.Worlds.ENCHANTIA));
    public static final RegistryObject<Block> MYSTWOOD_PLANKS = registerBlock("mystwood_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> MYSTWOOD_SLAB = registerBlock("mystwood_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> MYSTWOOD_STAIRS = registerBlock("mystwood_stairs", () ->
            new StairBlock(() -> MYSTWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> MYSTWOOD_FENCE = registerBlock("mystwood_fence", () ->
            new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> MYSTWOOD_FENCE_GATE = registerBlock("mystwood_fence_gate", () ->
            new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE));

    public static final RegistryObject<Block> FAIRLING_DWELLING = registerBlock("fairling_dwelling", () ->
            new EntityNestBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F), OtherworldEntities.FAIRLING::get, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN,5));

    public static final RegistryObject<Block> LOCKABLE_MYSTWOOD_DOOR = registerBlock("lockable_mystwood_door", () ->
            new LockableDoorBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(4.0F).noOcclusion(), MYSTWOOD, OtherworldItems.OPAL_KEY, SoundEvents.CHAIN_BREAK));


    //DEEPWOODS BLOCKS
    public static final RegistryObject<Block> DEEPSTONE = registerBlock("deepstone", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> COBBLED_DEEPSTONE = registerBlock("cobbled_deepstone", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistryObject<Block> DEEPSTONE_IRON_ORE = registerBlock("deepstone_iron_ore", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSTONE_COAL_ORE = registerBlock("deepstone_coal_ore", () ->
            new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.COAL_ORE), UniformInt.of(0, 3), IWorlds.Worlds.DEEPWOODS));
    public static final RegistryObject<Block> THORNED_TULIP = registerBlock("thorned_tulip", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.RED_TULIP), IWorlds.Worlds.DEEPWOODS));

    //---Redspruce Tree Type---//
    public static final RegistryObject<Block> STRIPPED_REDSPRUCE_LOG = registerBlock("stripped_redspruce_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> REDSPRUCE_LOG = registerBlock("redspruce_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG), STRIPPED_REDSPRUCE_LOG.get()));
    public static final RegistryObject<Block> REDSPRUCE_LEAVES = registerBlock("redspruce_leaves", () ->
            new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).isSuffocating((bs, bg, bp) -> false)));
    public static final RegistryObject<Block> REDSPRUCE_PLANKS = registerBlock("redspruce_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> REDSPRUCE_SAPLING = registerBlock("redspruce_sapling", () ->
            new OtheranSaplingBlock(new RedspruceTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING), IWorlds.Worlds.DEEPWOODS));
    public static final RegistryObject<Block> REDSPRUCE_SLAB = registerBlock("redspruce_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> REDSPRUCE_STAIRS = registerBlock("redspruce_stairs", () ->
            new StairBlock(() -> REDSPRUCE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    //public static final RegistryObject<Block> REDSPRUCE_DOOR = registerBlock("redspruce_door", () ->
    //        new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN));

    //--- Charred Woods Blocks ---//
    public static final RegistryObject<Block> CHARRED_LOG = registerBlock("charred_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM).color(MaterialColor.COLOR_BLACK).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> IRON_BARRED_DOOR = registerBlock("iron_barred_door", () ->
            new LockableDoorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F).noOcclusion(), BlockSetType.IRON, OtherworldItems.IRON_KEY, SoundEvents.CHAIN_BREAK));


    //EMBERIAN BLOCKS
    public static final RegistryObject<Block> ASH_BLOCK = registerBlock("ash_block", () ->
        new OtheranDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.TERRACOTTA_BLACK), IWorlds.Worlds.EMBERIA));
    public static final RegistryObject<Block> CINDERGRASS_BLOCK = registerBlock("cindergrass_block", () ->
        new OtheranGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).isValidSpawn((bs, bg, bp, b) -> true).color(MaterialColor.COLOR_YELLOW).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 15), IWorlds.Worlds.EMBERIA, ASH_BLOCK.get()));
    public static final RegistryObject<Block> CINDERGRASS = registerBlock("cindergrass", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_ROOTS).color(MaterialColor.COLOR_YELLOW).sound(SoundType.GRASS), IWorlds.Worlds.EMBERIA));
    public static final RegistryObject<Block> FIRE_LILY = registerBlock("fire_lily", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(CINDERGRASS.get()).emissiveRendering((bs, bg, bp) -> true).hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8), IWorlds.Worlds.EMBERIA));
    public static final RegistryObject<Block> SOOT_BLOCK = registerBlock("soot_block", () ->
        new SootBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND).color(MaterialColor.TERRACOTTA_BLACK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> ((SootBlock)bs.getBlock()).shouldGlow(bs)).lightLevel((i) -> ((SootBlock)i.getBlock()).shouldGlow(i) ? 12 : 0), IWorlds.Worlds.EMBERIA, ASH_BLOCK.get(),3));
    public static final RegistryObject<Block> ASHSTONE = registerBlock("ashstone", () ->
        new OtheranBlock(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE).color(MaterialColor.COLOR_BLACK), IWorlds.Worlds.EMBERIA));
    public static final RegistryObject<Block> TOPAZ_ORE = registerBlock("topaz_ore", () ->
        new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.STONE).color(MaterialColor.COLOR_BLACK), UniformInt.of(3, 7), IWorlds.Worlds.EMBERIA));
    //---Cinderbark Tree Type---//
    public static final RegistryObject<Block> STRIPPED_CINDERBARK_LOG = registerBlock("stripped_cinderbark_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM).sound(SoundType.WOOD).hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8)));
    public static final RegistryObject<Block> CINDERBARK_LOG = registerBlock("cinderbark_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM).sound(SoundType.WOOD).hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 12), STRIPPED_CINDERBARK_LOG.get()));
    public static final RegistryObject<Block> CINDERBARK_LEAVES = registerBlock("cinderbark_leaves", () ->
            new FireResistantLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).color(MaterialColor.TERRACOTTA_BLACK).sound(SoundType.GRASS)
                    .hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 4)));
    public static final RegistryObject<Block> CINDERBARK_PLANKS = registerBlock("cinderbark_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8)));
    public static final RegistryObject<Block> CINDERBARK_SAPLING = registerBlock("cinderbark_sapling", () ->
            new OtheranSaplingBlock(new CinderbarkTreeGrower(), BlockBehaviour.Properties.copy(Blocks.CRIMSON_FUNGUS).sound(SoundType.GRASS).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 4), IWorlds.Worlds.EMBERIA));
    public static final RegistryObject<Block> CINDERBARK_SLAB = registerBlock("cinderbark_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_SLAB)
                    .hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8)));
    public static final RegistryObject<Block> CINDERBARK_STAIRS = registerBlock("cinderbark_stairs", () ->
            new StairBlock(() -> CINDERBARK_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CRIMSON_STAIRS)
                    .hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8)));
    public static final RegistryObject<Block> CINDERBARK_DOOR = registerBlock("cinderbark_door", () ->
            new DoorBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_DOOR).hasPostProcess((bs, br, bp) -> true).lightLevel((i) -> 8), CINDERBARK));

    public static final RegistryObject<Block> TOPAZ_BARS = registerBlock("topaz_bars", () ->
            new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> TOPAZ_BARRED_DOOR = registerBlock("topaz_barred_door", () ->
            new LockableDoorBlock(BlockBehaviour.Properties.copy(IRON_BARRED_DOOR.get()), TOPAZ, OtherworldItems.TOPAZ_KEY, SoundEvents.CHAIN_BREAK));

    //OASIA BLOCKS
    public static final RegistryObject<Block> JADE_ORE = registerBlock("jade_ore", () ->
            new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.PACKED_MUD).strength(2.5F, 6.0F).requiresCorrectToolForDrops(), UniformInt.of(3, 7), IWorlds.Worlds.OASIA));
    public static final RegistryObject<Block> STRIPPED_MOSSWOOD_LOG = registerBlock("stripped_mosswood_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> MOSSWOOD_LOG = registerBlock("mosswood_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG), STRIPPED_MOSSWOOD_LOG.get()));
    public static final RegistryObject<Block> MOSSWOOD_LEAVES = registerBlock("mosswood_leaves", () ->
            new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> MOSSWOOD_PLANKS = registerBlock("mosswood_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> MOSSWOOD_SAPLING = registerBlock("mosswood_sapling", () ->
            new OtheranSaplingBlock(new MosswoodTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING), IWorlds.Worlds.OASIA));

    //GLACEIA BLOCKS
    public static final RegistryObject<Block> FROSTSILT = registerBlock("frostsilt", () ->
        new OtheranDirtBlock(BlockBehaviour.Properties.of(Material.ICE_SOLID, MaterialColor.ICE).isValidSpawn((bs, bg, bp, b) -> true).strength(0.6f).sound(SoundType.SNOW), IWorlds.Worlds.GLACEIA));
    public static final RegistryObject<Block> PERMAFROST = registerBlock("permafrost", () ->
        new OtheranGrassBlock(BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.SNOW).isValidSpawn((bs, bg, bp, b) -> true).strength(0.7f).sound(SoundType.SNOW).randomTicks(), IWorlds.Worlds.GLACEIA, FROSTSILT.get()));
    public static final RegistryObject<Block> FROSTWEED = registerBlock("frostweed", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_ROOTS).sound(SoundType.NETHER_SPROUTS).color(MaterialColor.SNOW), IWorlds.Worlds.GLACEIA));
    public static final RegistryObject<Block> FROSTED_BELLFLOWER = registerBlock("frosted_bellflower", () ->
            new OtheranPlantBlock(BlockBehaviour.Properties.copy(FROSTWEED.get()), IWorlds.Worlds.GLACEIA));
    public static final RegistryObject<Block> GRANULAR_ICE = registerBlock("granular_ice", () ->
        new OtheranFarmBlock(BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE).strength(0.5f).sound(SoundType.SNOW).randomTicks(), IWorlds.Worlds.GLACEIA, FROSTSILT.get(), 4));
    public static final RegistryObject<Block> FROSTSTONE = registerBlock("froststone", () ->
        new OtheranBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.ICE).strength(2f).sound(SoundType.STONE).requiresCorrectToolForDrops(), IWorlds.Worlds.GLACEIA));
    public static final RegistryObject<Block> SAPPHIRE_ORE = registerBlock("sapphire_ore", () ->
        new OtheranOreBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.ICE).strength(2f).sound(SoundType.STONE).requiresCorrectToolForDrops(), UniformInt.of(3, 7), IWorlds.Worlds.GLACEIA));
    //---Icewood Tree Type---//
    public static final RegistryObject<Block> STRIPPED_ICEWOOD_LOG = registerBlock("stripped_icewood_log", () ->
        new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).color(MaterialColor.WARPED_WART_BLOCK).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ICEWOOD_LOG = registerBlock("icewood_log", () ->
        new LogBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM).color(MaterialColor.WARPED_WART_BLOCK).sound(SoundType.GLASS), STRIPPED_ICEWOOD_LOG.get()));
    public static final RegistryObject<Block> ICEWOOD_LEAVES = registerBlock("icewood_leaves", () ->
        new FireResistantLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).color(MaterialColor.ICE).sound(SoundType.NETHER_SPROUTS)));
    public static final RegistryObject<Block> ICEWOOD_PLANKS = registerBlock("icewood_planks", () ->
        new Block(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ICEWOOD_SAPLING = registerBlock("icewood_sapling", () ->
        new OtheranSaplingBlock(new IcewoodTreeGrower(), BlockBehaviour.Properties.copy(Blocks.WARPED_FUNGUS).sound(SoundType.NETHER_SPROUTS), IWorlds.Worlds.GLACEIA));
    public static final RegistryObject<Block> ICEWOOD_SLAB = registerBlock("icewood_slab", () ->
        new SlabBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_SLAB).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ICEWOOD_STAIRS = registerBlock("icewood_stairs", () ->
        new StairBlock(() -> ICEWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.WARPED_STAIRS).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> ICEWOOD_DOOR = registerBlock("icewood_door", () ->
        new DoorBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_DOOR).sound(SoundType.GLASS), ICEWOOD));

    public static final RegistryObject<Block> SAPPHIRE_BARS = registerBlock("sapphire_bars", () ->
            new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> SAPPHIRE_BARRED_DOOR = registerBlock("sapphire_barred_door", () ->
            new LockableDoorBlock(BlockBehaviour.Properties.copy(IRON_BARRED_DOOR.get()), SAPPHIRE, OtherworldItems.SAPPHIRE_KEY, SoundEvents.CHAIN_BREAK));


    //UNDERLANDS BLOCKS
    public static final RegistryObject<Block> DUSTSTONE = registerBlock("duststone", () ->
            new OtheranDirtBlock(BlockBehaviour.Properties.copy(Blocks.NETHERRACK).color(MaterialColor.COLOR_GRAY).sound(SoundType.NYLIUM), IWorlds.Worlds.UNDERLANDS));
    public static final RegistryObject<Block> WITHERGRASS_BLOCK = registerBlock("withergrass_block", () ->
            new OtheranGrassBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_NYLIUM).color(MaterialColor.COLOR_PURPLE), IWorlds.Worlds.UNDERLANDS, DUSTSTONE.get()));
    public static final RegistryObject<Block> WITHERSTONE = registerBlock("witherstone", () ->
            new OtheranBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE), IWorlds.Worlds.UNDERLANDS));
    public static final RegistryObject<Block> COBBLED_WITHERSTONE = registerBlock("cobbled_witherstone", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> ONYX_ORE = registerBlock("onyx_ore", () ->
            new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_EMERALD_ORE), UniformInt.of(2, 5), IWorlds.Worlds.UNDERLANDS));
    public static final RegistryObject<Block> CRIMSON_DIAMOND_ORE = registerBlock("crimson_diamond_ore", () ->
            new OtheranOreBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), UniformInt.of(3, 7), IWorlds.Worlds.UNDERLANDS));
    public static final RegistryObject<Block> CORRUPTED_SOUL_SAND = registerBlock("corrupted_soul_sand", () ->
            new SoulSandBlock(BlockBehaviour.Properties.copy(Blocks.SOUL_SAND)));
    public static final RegistryObject<Block> SUNKEN_SOULS = registerBlockNoItem("sunken_souls", () ->
            new SinkingBlock(BlockBehaviour.Properties.copy(CORRUPTED_SOUL_SAND.get()), OtherworldDamage.SOUL, SoundEvents.SOUL_ESCAPE, OtherworldItems.SUNKEN_SOULS_BUCKET));

    //---Willow Tree Type---//
    public static final RegistryObject<Block> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM).color(MaterialColor.COLOR_BLACK).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WILLOW_LOG = registerBlock("willow_log", () ->
            new LogBlock(BlockBehaviour.Properties.copy(STRIPPED_WILLOW_LOG.get()), STRIPPED_WILLOW_LOG.get()));
    public static final RegistryObject<Block> WILLOW_LEAVES = registerBlock("willow_leaves", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.NETHER_WART_BLOCK).strength(0.2F).noOcclusion().isSuffocating((bs, bg, bp) -> false).isViewBlocking((bs, bg, bp) -> false).color(MaterialColor.TERRACOTTA_PURPLE).sound(SoundType.MOSS)));
    public static final RegistryObject<Block> WILLOW_PLANKS = registerBlock("willow_planks", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).sound(SoundType.WOOD).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> WILLOW_SAPLING = registerBlock("willow_sapling", () ->
            new OtheranSaplingBlock(new WillowTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).color(MaterialColor.TERRACOTTA_PURPLE), IWorlds.Worlds.UNDERLANDS));
    public static final RegistryObject<Block> WILLOW_SLAB = registerBlock("willow_slab", () ->
            new SlabBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_SLAB).sound(SoundType.WOOD).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> WILLOW_STAIRS = registerBlock("willow_stairs", () ->
            new StairBlock(() -> WILLOW_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CRIMSON_STAIRS).sound(SoundType.WOOD).color(MaterialColor.COLOR_BLACK)));


    //CROPS AND GROWABLES
    public static final RegistryObject<Block> ROSERYE = registerBlockNoItem("roserye", () ->
            new OtheranCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).color(MaterialColor.COLOR_PINK), IWorlds.Worlds.ROSEIA, OtherworldItems.ROSERYE_SEEDS));
    public static final RegistryObject<Block> MULBERRY_VINE = registerBlockNoItem("mulberry_vine", () ->
            new OtheranCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).color(MaterialColor.COLOR_PURPLE), IWorlds.Worlds.ENCHANTIA, OtherworldItems.MULBERRY_SEEDS));
    public static final RegistryObject<Block> PLUM_STEM = registerBlockNoItem("plum_stem", () ->
            new HangingFruitBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE).instabreak().noCollission().sound(SoundType.BAMBOO_SAPLING).offsetType(BlockBehaviour.OffsetType.XZ), OtherworldItems.PLUM));
    public static final RegistryObject<Block> THORNBERRY_BUSH = registerBlockNoItem("thornberry_bush", () ->
            new OtheranBerryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH), IWorlds.Worlds.DEEPWOODS,
                    List.of(() -> Blocks.DIRT, () -> Blocks.PODZOL, () -> Blocks.COARSE_DIRT, () -> Blocks.GRASS_BLOCK, () -> Blocks.FARMLAND),
                    OtherworldItems.THORNBERRY, null, true));
    public static final RegistryObject<Block> SPICEROOT = registerBlockNoItem("spiceroot",  () ->
            new OtheranCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).color(MaterialColor.COLOR_RED).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel((i) -> 5), IWorlds.Worlds.EMBERIA, OtherworldItems.SPICEROOT_SEEDS));
    public static final RegistryObject<Block> ICEBRUSSEL = registerBlockNoItem("icebrussel", () ->
            new OtheranCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).color(MaterialColor.COLOR_LIGHT_BLUE), IWorlds.Worlds.GLACEIA, OtherworldItems.ICEBRUSSEL_SEEDS));
    public static final RegistryObject<Block> SOULBERRY_BUSH = registerBlockNoItem("soulberry_bush", () ->
            new OtheranBerryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH).sound(SoundType.FUNGUS).color(MaterialColor.QUARTZ), IWorlds.Worlds.UNDERLANDS, List.of(CORRUPTED_SOUL_SAND), OtherworldItems.SOULBERRY, SoundEvents.SOUL_ESCAPE, false));

    // SPAWNERS
    public static final RegistryObject<Block> OTHERLY_SUMMONER = registerBlock("otherly_summoner",
            () -> new MinionSpawnAnchorBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F)
                    .lightLevel((bs) -> bs.getValue(MinionSpawnAnchorBlock.CHARGED) ? 10 : 0),
                    OtherworldEvents.Triggers.OTHERLY_MINION_SPAWNER, 160));


    // ****** BLOCK ENTITIES ****** //
    public static final RegistryObject<BlockEntityType<EntityNestBlockEntity>> ENTITY_NEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("entity_nest_block_entity", () ->
            BlockEntityType.Builder.of(EntityNestBlockEntity::new, FAIRLING_DWELLING.get()).build(null));


    public static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
        RegistryObject<T> retVal = BLOCKS.register(name, supplier);
        registerBlockItem(name, retVal);
        return retVal;
    }

    public static <T extends Block>RegistryObject<T> registerBlockNoCreativeItem(String name, Supplier<T> supplier) {
        RegistryObject<T> retval = BLOCKS.register(name, supplier);
        OtherworldItems.addSecretItem(registerBlockItem(name, retval));
        return retval;
    }

    public static <T extends Block>RegistryObject<T> registerBlockNoItem(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return OtherworldItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

}
