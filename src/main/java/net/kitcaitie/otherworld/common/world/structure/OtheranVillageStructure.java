package net.kitcaitie.otherworld.common.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kitcaitie.otherworld.registry.OtherworldStructures;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class OtheranVillageStructure extends OtheranStructure {
    public static final Codec<OtheranVillageStructure> CODEC = RecordCodecBuilder.<OtheranVillageStructure>mapCodec(instance ->
            instance.group(OtheranVillageStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, OtheranVillageStructure::new)).codec();

    protected OtheranVillageStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(settings, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);

    }

    @Override
    protected boolean checkIfPositionIsValidToSpawn(GenerationContext context) {
        int i = 20;

        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(context.chunkPos().getBlockX(i), context.chunkGenerator().getSeaLevel(), context.chunkPos().getBlockZ(i), 29, context.randomState().sampler())) {
            if (!context.validBiome().test(holder)) {
                return false;
            }
        }
        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(context.chunkPos().getBlockX(-i), context.chunkGenerator().getSeaLevel(), context.chunkPos().getBlockZ(-i), 29, context.randomState().sampler())) {
            if (!context.validBiome().test(holder)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public StructureType<?> type() {
        return OtherworldStructures.OTHERAN_VILLAGE.get();
    }

}
