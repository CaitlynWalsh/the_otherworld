package net.kitcaitie.otherworld.common.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class FireloggedVegetationPatchFeature extends VegetationPatchFeature {

    public FireloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }

    protected Set<BlockPos> placeGroundPatch(WorldGenLevel level, VegetationPatchConfiguration config, RandomSource random, BlockPos pos, Predicate<BlockState> state, int i, int i1) {
        Set<BlockPos> set = super.placeGroundPatch(level, config, random, pos, state, i, i1);
        Set<BlockPos> set1 = new HashSet<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockPos : set) {
            if (!isExposed(level, set, blockPos, mutable)) {
                set1.add(blockPos);
            }
        }

        for (BlockPos blockPos : set1) {
            level.setBlock(blockPos, Blocks.LAVA.defaultBlockState(), 2);
        }

        return set1;
    }

    private static boolean isExposed(WorldGenLevel level, Set<BlockPos> set, BlockPos pos, BlockPos.MutableBlockPos pos1) {
        return isExposedDirection(level, pos, pos1, Direction.NORTH) || isExposedDirection(level, pos, pos1, Direction.EAST) || isExposedDirection(level, pos, pos1, Direction.SOUTH) || isExposedDirection(level, pos, pos1, Direction.WEST) || isExposedDirection(level, pos, pos1, Direction.DOWN);
    }

    private static boolean isExposedDirection(WorldGenLevel level, BlockPos pos, BlockPos.MutableBlockPos pos1, Direction direction) {
        pos1.setWithOffset(pos, direction);
        return !level.getBlockState(pos1).isFaceSturdy(level, pos1, direction.getOpposite());
    }

}
