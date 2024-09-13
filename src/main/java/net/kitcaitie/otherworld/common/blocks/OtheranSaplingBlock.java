package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;

public class OtheranSaplingBlock extends SaplingBlock implements IWorlds {
    private final Worlds world;

    public OtheranSaplingBlock(AbstractTreeGrower grower, Properties properties, Worlds worlds) {
        super(grower, properties);
        this.world = worlds;
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return getWorldType() == Worlds.UNDERLANDS ? PlantType.CAVE : PlantType.PLAINS;
    }

    public Worlds getWorldType() {
        return world;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 1)) return;
        boolean flag = randomSource.nextInt(7) == 0;
        boolean flag1 = serverLevel.getMaxLocalRawBrightness(blockPos.above()) >= 9;
        switch (getWorldType()) {
            case UNDERLANDS -> {
                if (serverLevel.getMaxLocalRawBrightness(blockPos.above()) < 5 && flag) {
                    this.advanceTree(serverLevel, blockPos, blockState, randomSource);
                }
                return;
            }
            case EMBERIA -> {
                if (serverLevel.getBiome(blockPos).is(Tags.Biomes.IS_HOT)) {
                    if (flag1 && flag) {
                        this.advanceTree(serverLevel, blockPos, blockState, randomSource);
                    }
                }
                return;
            }
            case GLACEIA -> {
                if (serverLevel.getBiome(blockPos).is(Tags.Biomes.IS_SNOWY)) {
                    if ((flag1 || serverLevel.canSeeSky(blockPos)) && flag) {
                        this.advanceTree(serverLevel, blockPos, blockState, randomSource);
                    }
                }
                return;
            }
        }
        if (flag1 && flag) {
            this.advanceTree(serverLevel, blockPos, blockState, randomSource);
        }
    }
}
