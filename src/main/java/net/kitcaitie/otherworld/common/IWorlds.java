package net.kitcaitie.otherworld.common;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.registry.OtherworldBiomes;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;

public interface IWorlds {
    enum Worlds {
        ROSEIA(Otherworld.OTHERWORLD, OtherworldBiomes.ROSEIA, List.of(OtherworldTags.ROSEIAN_STARTING_STRUCTURES, OtherworldTags.ROSEIAN_OUTPOSTS)),
        ENCHANTIA(Otherworld.OTHERWORLD, OtherworldBiomes.ENCHANTIA, List.of(OtherworldTags.FAIRIE_STARTING_STRUCTURES, OtherworldTags.FAIRIE_OUTPOSTS)),
        DEEPWOODS(Otherworld.OTHERWORLD, OtherworldBiomes.DEEPWOODS, List.of(OtherworldTags.ONI_STARTING_STRUCTURES, OtherworldTags.ONI_OUTPOSTS)),
        EMBERIA(Otherworld.OTHERWORLD, OtherworldBiomes.EMBERIA, List.of(OtherworldTags.EMBERIAN_STARTING_STRUCTURES, OtherworldTags.EMBERIAN_OUTPOSTS)),
        GLACEIA(Otherworld.OTHERWORLD, OtherworldBiomes.GLACEIA, List.of(OtherworldTags.ICEIAN_STARTING_STRUCTURES, OtherworldTags.ICEIAN_OUTPOSTS)),
        OASIA(Otherworld.OTHERWORLD, OtherworldBiomes.OASIA, List.of()),
        UNDERLANDS(Otherworld.UNDERLANDS, null, List.of(OtherworldTags.GHOUL_STARTING_STRUCTURES)),
        OVERWORLD(Level.OVERWORLD, null, List.of());

        final ResourceKey<Level> dimension;
        final @Nullable ResourceKey<Biome> biome;
        final @Nullable List<TagKey<Structure>> structures;

        Worlds(ResourceKey<Level> dimension, @Nullable ResourceKey<Biome> biome, @Nullable List<TagKey<Structure>> structures) {
            this.dimension = dimension;
            this.biome = biome;
            this.structures = structures;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        @Nullable
        public ResourceKey<Biome> getBiome() {
            return biome;
        }

        public List<TagKey<Structure>> getStructures() {
            return structures;
        }
    }

    Worlds getWorldType();

}
