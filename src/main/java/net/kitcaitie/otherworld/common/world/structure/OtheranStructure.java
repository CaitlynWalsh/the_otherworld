package net.kitcaitie.otherworld.common.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kitcaitie.otherworld.registry.OtherworldStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import javax.annotation.Nullable;
import java.util.Optional;

public class OtheranStructure extends Structure {

    private static final int MIN_HEIGHT = 93;
    private static final int MAX_HEIGHT = 96;

    public static final Codec<OtheranStructure> CODEC = RecordCodecBuilder.<OtheranStructure>mapCodec(instance ->
            instance.group(OtheranStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, OtheranStructure::new)).codec();

    protected final Holder<StructureTemplatePool> startPool;
    protected final Optional<ResourceLocation> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final Optional<Heightmap.Types> projectStartToHeightmap;
    protected final int maxDistanceFromCenter;

    protected OtheranStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Nullable
    private BlockPos checkSpawningHeight(Structure.GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        int height = context.chunkGenerator().getFirstOccupiedHeight(x, z, projectStartToHeightmap.orElse(Heightmap.Types.WORLD_SURFACE_WG), context.heightAccessor(), context.randomState());
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) return null;
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(x, startY, z);
        if (context.heightAccessor().isOutsideBuildHeight(blockPos) || blockPos.getY() <= context.chunkGenerator().getSeaLevel()) return null;
        return blockPos;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!checkIfPositionIsValidToSpawn(context)) return Optional.empty();
        BlockPos blockPos = this.checkSpawningHeight(context);
        if (blockPos != null) {
            Optional<Structure.GenerationStub> structurePiecesGenerator =
                    JigsawPlacement.addPieces(
                            context,
                            this.startPool,
                            this.startJigsawName,
                            this.size,
                            blockPos,
                            false,
                            this.projectStartToHeightmap,
                            this.maxDistanceFromCenter);
            if (structurePiecesGenerator.isPresent()) {
                int pos = structurePiecesGenerator.get().getPiecesBuilder().getBoundingBox().minY();
                if (pos < MIN_HEIGHT || pos > MAX_HEIGHT) return Optional.empty(); // NO STRUCTURES IN THE VOID!
            }
            return structurePiecesGenerator;
        }
        return Optional.empty();
    }

    protected boolean checkIfPositionIsValidToSpawn(GenerationContext context) {
        int i = 1;

        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(context.chunkPos().getBlockX(i), context.chunkGenerator().getSeaLevel(), context.chunkPos().getBlockZ(i), 2, context.randomState().sampler())) {
            if (!context.validBiome().test(holder)) {
                return false;
            }
        }
        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(context.chunkPos().getBlockX(-i), context.chunkGenerator().getSeaLevel(), context.chunkPos().getBlockZ(-i), 2, context.randomState().sampler())) {
            if (!context.validBiome().test(holder)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public BoundingBox adjustBoundingBox(BoundingBox box) {
        return super.adjustBoundingBox(box).inflatedBy(20);
    }

    @Override
    public StructureType<?> type() {
        return OtherworldStructures.OTHERAN_STRUCTURE.get();
    }
}
