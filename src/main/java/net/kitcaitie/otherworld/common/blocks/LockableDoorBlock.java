package net.kitcaitie.otherworld.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LockableDoorBlock extends DoorBlock {
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    private final Supplier<Item> key;
    private final BlockSetType blockSetType;
    private final @Nullable SoundEvent unlockSound;

    public LockableDoorBlock(Properties properties, BlockSetType type, Supplier<Item> key, @Nullable SoundEvent unlockSound) {
        super(properties, type);
        this.key = key;
        this.blockSetType = type;
        this.unlockSound = unlockSound;
        this.registerDefaultState(this.getStateDefinition().any().setValue(LOCKED, true));
    }

    @Override
    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        BlockState blockState = super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
        if (blockState.is(this)) {
            return blockState.setValue(LOCKED, p_52796_.getValue(LOCKED));
        }
        return blockState;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter p_52765_, BlockPos p_52766_, PathComputationType p_52767_) {
        return !state.getValue(LOCKED) && super.isPathfindable(state, p_52765_, p_52766_, p_52767_);
    }

    public static InteractionResult unlockOrLockDoor(BlockState state, Level level, BlockPos pos, ItemStack stack, boolean lock) {
        if (state.getBlock() instanceof LockableDoorBlock door && stack.is(door.getKey())) {
            level.setBlock(pos, state.setValue(LOCKED, lock), 10);
            BlockPos pos1 = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            level.setBlock(pos1, level.getBlockState(pos1).setValue(LOCKED, lock), 10);
            if (door.getUnlockSound() != null) level.playSound(null, pos, door.getUnlockSound(), SoundSource.BLOCKS);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult useDoor(BlockState state, Level level, BlockPos pos, LivingEntity entity, ItemStack stack) {
        if (state.getBlock() instanceof LockableDoorBlock door) {
            if (stack != null && stack.is(door.getKey())) {
                InteractionResult result = unlockOrLockDoor(state, level, pos, stack, !state.getValue(LOCKED));
                if (result == InteractionResult.SUCCESS) {
                    level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
                }
                return result;
            }
            else if (!state.getValue(LOCKED)) {
                state = state.cycle(OPEN);
                level.setBlock(pos, state, 10);
                level.playSound(null, pos, state.getValue(OPEN) ? door.blockSetType.doorOpen() : door.blockSetType.doorClose(), SoundSource.BLOCKS);
                level.gameEvent(entity, door.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
                entity.swing(InteractionHand.MAIN_HAND);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean bool) {
        boolean flag = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
        if (!this.defaultBlockState().is(block) && flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN)) {
                level.playSound(null, pos, flag ? blockSetType.doorOpen() : blockSetType.doorClose(), SoundSource.BLOCKS);
                level.gameEvent(null, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }

            if (state.getValue(LOCKED)) level.setBlock(pos, state.setValue(POWERED, false).setValue(OPEN, false), 2);
            else level.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
        }
    }

    public Item getKey() {
        return this.key.get();
    }

    @Nullable
    public SoundEvent getUnlockSound() {
        return unlockSound;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useDoor(state, level, pos, player, player.getItemInHand(hand));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52803_) {
        super.createBlockStateDefinition(p_52803_);
        p_52803_.add(LOCKED);
    }
}
