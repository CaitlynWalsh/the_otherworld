package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class OtherworldTags {

    // BLOCKS //
    public static final TagKey<Block> OTHERAN_SPAWNABLE_ON = createBlockTag("otheran_spawnable_on");
    public static final TagKey<Block> TILLABLE_BLOCKS = createBlockTag("tillable_blocks");
    public static final TagKey<Block> FARMLAND = createBlockTag("farmland");
    public static final TagKey<Block> OTHERWORLD_NATURAL_STONE = createBlockTag("otherworld_natural_stone");
    public static final TagKey<Block> BREAKABLE_STRUCTURE_BLOCKS = createBlockTag("breakable_structure_blocks");

    // ITEMS //
    public static final TagKey<Item> CRAFT_TO_BONEMEAL = createItemTags("craft_to_bonemeal");
    public static final TagKey<Item> VALUABLES = createItemTags("valuables");
    public static final TagKey<Item> OTHERLY_MINION_SUMMON_ITEMS = createItemTags("otherly_minion_summon_items");
    public static final TagKey<Item> KEYS = createItemTags("keys");

    // ENTITIES //

    // STRUCTURES //
    public static final TagKey<Structure> OTHERWORLD_UNBREAKABLE_STRUCTURES = createStructureTag("otherworld_unbreakable_structures");
    public static final TagKey<Structure> ONI_STARTING_STRUCTURES = createStructureTag("oni_spawn_structures");
    public static final TagKey<Structure> ONI_OUTPOSTS = createStructureTag("oni_outposts");
    public static final TagKey<Structure> FAIRIE_STARTING_STRUCTURES = createStructureTag("fairie_spawn_structures");
    public static final TagKey<Structure> FAIRIE_OUTPOSTS = createStructureTag("fairie_outposts");
    public static final TagKey<Structure> ROSEIAN_STARTING_STRUCTURES = createStructureTag("roseian_spawn_structures");
    public static final TagKey<Structure> ROSEIAN_OUTPOSTS = createStructureTag("roseian_outposts");
    public static final TagKey<Structure> EMBERIAN_STARTING_STRUCTURES = createStructureTag("emberian_spawn_structures");
    public static final TagKey<Structure> EMBERIAN_OUTPOSTS = createStructureTag("emberian_outposts");
    public static final TagKey<Structure> ICEIAN_STARTING_STRUCTURES = createStructureTag("iceian_spawn_structures");
    public static final TagKey<Structure> ICEIAN_OUTPOSTS = createStructureTag("iceian_outposts");
    public static final TagKey<Structure> GHOUL_STARTING_STRUCTURES = createStructureTag("ghoul_spawn_structures");


    public static TagKey<Block> createBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(Otherworld.MODID, name));
    }

    public static TagKey<Item> createItemTags(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(Otherworld.MODID, name));
    }

    public static TagKey<EntityType<?>> createEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Otherworld.MODID, name));
    }

    public static TagKey<Structure> createStructureTag(String name) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(Otherworld.MODID, name));
    }
}
