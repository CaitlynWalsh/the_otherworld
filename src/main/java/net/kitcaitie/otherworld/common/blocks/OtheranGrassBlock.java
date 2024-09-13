package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.registry.OtherworldFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OtheranGrassBlock extends OtheranDirtBlock implements BonemealableBlock {
    public Block dirt;

    public OtheranGrassBlock(Properties properties, Worlds worlds, Block dirt) {
        super(properties, worlds);
        this.dirt = dirt;
    }

    public Block getDirt() {
        return dirt;
    }

    private static boolean canBeGrass(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockpos = blockPos.above();
        boolean checkBlockAbove = !levelReader.getBlockState(blockPos.above()).isCollisionShapeFullBlock(levelReader, blockPos.above()) && !levelReader.isWaterAt(blockPos.above());
        BlockState blockstate = levelReader.getBlockState(blockpos);
        if (blockState.getBlock() instanceof OtheranGrassBlock) {
            switch (((OtheranGrassBlock)blockState.getBlock()).getWorldType()) {
                case EMBERIA -> {
                    if (!levelReader.getBiome(blockPos).containsTag(Tags.Biomes.IS_HOT)) return false;
                    return checkBlockAbove;
                }
                case GLACEIA -> {
                    if (!levelReader.getBiome(blockPos).containsTag(Tags.Biomes.IS_SNOWY)) return false;
                    if (blockstate.getFluidState().is(FluidTags.LAVA) || blockstate.getMaterial() == Material.FIRE) {
                        return false;
                    }
                    if (blockstate.getMaterial() == Material.SNOW) {
                        return true;
                    }
                    return checkBlockAbove;
                }
            }
            return checkBlockAbove;
        }
        return false;
    }

    private static boolean areGrowthNeedsMet(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (blockState.getBlock() instanceof OtheranGrassBlock) {
            boolean checkBlockAbove = !level.getBlockState(blockPos.above()).isCollisionShapeFullBlock(level, blockPos.above()) && !level.isWaterAt(blockPos.above());
            boolean flag = level.getMaxLocalRawBrightness(blockPos.above()) >= 9;
            switch (((OtheranGrassBlock)blockState.getBlock()).getWorldType()) {
                case EMBERIA -> {
                    return checkBlockAbove && level.getBiome(blockPos).containsTag(Tags.Biomes.IS_HOT) && flag;
                }
                case GLACEIA -> {
                    return checkBlockAbove && level.getBiome(blockPos).containsTag(Tags.Biomes.IS_SNOWY) && (level.canSeeSky(blockPos) || flag);
                }
                case UNDERLANDS -> {
                    return checkBlockAbove && level.getMaxLocalRawBrightness(blockPos.above()) < 5;
                }
            }
            return checkBlockAbove && flag;
        }
        return false;
    }

    private static boolean canPropagate(BlockState blockState, LevelReader level, BlockPos blockPos) {
        return canBeGrass(blockState, level, blockPos.above()) && areGrowthNeedsMet(blockState, level, blockPos);
    }

    public void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource source) {
        if (!level.isAreaLoaded(blockPos, 1)) return;
        if (!canBeGrass(blockState, level, blockPos)) {
            level.setBlockAndUpdate(blockPos, getDirt().defaultBlockState());
        }
        else {
            if (!level.isAreaLoaded(blockPos, 3)) return;
            if (areGrowthNeedsMet(blockState, level, blockPos)) {
                BlockState blockState1 = this.defaultBlockState();
                for (int i=0; i<4; i++) {
                    BlockPos blockPos1 = blockPos.offset(source.nextInt(3) - 1, source.nextInt(5) - 3, source.nextInt(3) - 1);
                    if (level.getBlockState(blockPos1).is(getDirt()) && canPropagate(blockState, level, blockPos1)) {
                        level.setBlockAndUpdate(blockPos1, blockState1);
                    }
                }
            }
        }
    }


    @Override
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos blockPos, BlockState blockState, boolean b) {
        return reader.isEmptyBlock(blockPos.above());
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos blockPos, BlockState blockState) {
        BlockPos above = blockPos.above();
        ResourceKey<PlacedFeature> grassKey = getBonemealGrass();
        Optional<Holder.Reference<PlacedFeature>> grass = Optional.empty();

        if (grassKey != null) {
            grass = serverLevel.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(grassKey);
        }

        label49:
        for (int i=0; i<128; i++) {
            BlockPos blockPos1 = above;

            for(int j = 0; j < i / 16; ++j) {
                blockPos1 = blockPos1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!serverLevel.getBlockState(blockPos1.below()).is(this) || serverLevel.getBlockState(blockPos1).isCollisionShapeFullBlock(serverLevel, blockPos1)) {
                    continue label49;
                }
            }

            BlockState blockState1 = serverLevel.getBlockState(blockPos1);
            if (blockState1.is(blockState.getBlock()) && random.nextInt(10) == 0) {
                ((BonemealableBlock)blockState.getBlock()).performBonemeal(serverLevel, random, blockPos1, blockState1);
            }

            if (blockState1.isAir()) {
                Holder<PlacedFeature> feature;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> features = serverLevel.getBiome(blockPos1).get().getGenerationSettings().getFlowerFeatures();
                    if (features.isEmpty()) continue;

                    feature = ((RandomPatchConfiguration)features.get(0).config()).feature();
                }
                else {
                    if (grass.isEmpty()) continue;

                    feature = grass.get();
                }
                feature.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, blockPos1);
            }
        }
    }

    @Nullable
    private ResourceKey<PlacedFeature> getBonemealGrass() {
        switch (getWorldType()) {
            case ROSEIA -> {
                return OtherworldFeatures.ROSEGRASS_SINGLE_PLACED;
            }
            case ENCHANTIA -> {
                return OtherworldFeatures.MYSTWEED_SINGLE_PLACED;
            }
            case EMBERIA -> {
                return OtherworldFeatures.CINDERGRASS_SINGLE_PLACED;
            }
            case GLACEIA -> {
                return OtherworldFeatures.FROSTWEED_SINGLE_PLACED;
            }
        }
        return null;
    }

}
