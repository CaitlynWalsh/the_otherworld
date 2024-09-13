package net.kitcaitie.otherworld.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;

public class OtheranFarmBlock extends OtheranBlock {
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private final int hydrateDistance;
    private final Block baseBlock;

    public OtheranFarmBlock(Properties properties, Worlds world, Block baseBlock, int hydrateDistance) {
        super(properties.randomTicks(), world);
        this.hydrateDistance = hydrateDistance;
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.getStateDefinition().any().setValue(MOISTURE, Integer.valueOf(0)));
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public int getHydrateDistance() {
        return this.hydrateDistance;
    }

    public TagKey<Fluid> getHydrateType() {
        if (getWorldType() == Worlds.EMBERIA) {
            return FluidTags.LAVA;
        }
        if (getWorldType() == Worlds.GLACEIA || getWorldType() == Worlds.UNDERLANDS) {
            return null;
        }
        return FluidTags.WATER;
    }

    public boolean isNearHydratingFluid(LevelReader reader, BlockPos blockPos) {
        for (BlockPos blockPos1 :BlockPos.betweenClosed(blockPos.offset(-getHydrateDistance(), 0, -getHydrateDistance()), blockPos.offset(getHydrateDistance(), 1, getHydrateDistance()))) {
            if (reader.getFluidState(blockPos1).is(getHydrateType())) {
                return true;
            }
        }
        return false;
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (direction == Direction.UP && !blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }

        return super.updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
    }

    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockState blockstate = levelReader.getBlockState(blockPos.above());
        return !(blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return !this.defaultBlockState().canSurvive(placeContext.getLevel(), placeContext.getClickedPos()) ? getBaseBlock().defaultBlockState() : super.getStateForPlacement(placeContext);
    }

    public boolean useShapeForLightOcclusion(BlockState p_53295_) {
        return true;
    }

    public VoxelShape getShape(BlockState p_53290_, BlockGetter p_53291_, BlockPos p_53292_, CollisionContext p_53293_) {
        return SHAPE;
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (getHydrateType() == null) {
            return;
        }
        int i = blockState.getValue(MOISTURE);
        boolean flag = getHydrateType() == FluidTags.WATER && serverLevel.isRainingAt(blockPos.above());
        if (flag || isNearHydratingFluid(serverLevel.getLevel(), blockPos)) {
            if (i < 7) {
                serverLevel.setBlock(blockPos, blockState.setValue(MOISTURE, Integer.valueOf(7)), 2);
            }
        }
        else {
            if (i > 0) {
                serverLevel.setBlock(blockPos, blockState.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            }
            else if (!isUnderCrops(serverLevel.getLevel(), blockPos)) {
                turnToDirt(blockState, serverLevel, blockPos);
            }
        }
    }

    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
        if (!level.isClientSide && ForgeHooks.onFarmlandTrample(level, blockPos, getBaseBlock().defaultBlockState(), f, entity)) {
            turnToDirt(blockState, (ServerLevel) level, blockPos);
        }
        super.fallOn(level, blockState, blockPos, entity, f);
    }

    public void turnToDirt(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos) {
        serverLevel.setBlockAndUpdate(blockPos, pushEntitiesUp(blockState, getBaseBlock().defaultBlockState(), serverLevel, blockPos));
    }

    public boolean isUnderCrops(BlockGetter blockGetter, BlockPos blockPos) {
        BlockState plantState = blockGetter.getBlockState(blockPos.above());
        return plantState.getBlock() instanceof IPlantable;
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.canSurvive(serverLevel.getLevel(), blockPos)) {
            turnToDirt(blockState, serverLevel, blockPos);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE);
    }

    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType path) {
        return false;
    }
}
