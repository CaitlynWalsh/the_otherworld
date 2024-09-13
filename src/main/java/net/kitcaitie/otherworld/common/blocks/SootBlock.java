package net.kitcaitie.otherworld.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SootBlock extends OtheranFarmBlock implements IGlowable {

    public SootBlock(Properties properties, Worlds world, Block baseBlock, int hydrateDistance) {
        super(properties, world, baseBlock, hydrateDistance);
    }

    @Override
    public boolean shouldGlow(BlockState blockState) {
        return blockState.getValue(MOISTURE) == 7;
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
        if (shouldGlow(blockState)) {
            Blocks.MAGMA_BLOCK.stepOn(level, blockPos, blockState, entity);
        }
    }
}

