package net.kitcaitie.otherworld.common.blocks;

import net.minecraft.world.level.block.state.BlockState;


public interface IGlowable {

    boolean shouldGlow(BlockState blockState);

    int getLightLevel();


}
