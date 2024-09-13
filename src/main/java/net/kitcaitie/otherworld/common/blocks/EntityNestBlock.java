package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.blocks.entity.EntityNestBlockEntity;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EntityNestBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FULL = BooleanProperty.create("full");
    protected final Supplier<EntityType<? extends LivingEntity>> entityType;
    protected final int maxOccupants;
    protected final @Nullable SoundEvent enterNestSound;
    protected final @Nullable SoundEvent leaveNestSound;

    public EntityNestBlock(Properties properties, Supplier<EntityType<? extends LivingEntity>> entityType, @Nullable SoundEvent enter, @Nullable SoundEvent exit, int maxOccupants) {
        super(properties);
        this.entityType = entityType;
        this.maxOccupants = maxOccupants;
        this.enterNestSound = enter;
        this.leaveNestSound = exit;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FULL, false));
    }

    @Override public BlockState rotate(BlockState blockState, Rotation rotation) { return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING))); }
    @Override public BlockState mirror(BlockState blockState, Mirror mirror) { return blockState.rotate(mirror.getRotation(blockState.getValue(FACING))); }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (!level.isClientSide() && entity instanceof EntityNestBlockEntity nestBlock) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getItemInHand(player.getUsedItemHand())) == 0) {
                nestBlock.releaseAllInhabitors(player, state, true);
                if (!nestBlock.getContents().isEmpty()) {
                    popResource(level, pos, nestBlock.getContents());
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    public EntityType<? extends LivingEntity> getEntityType() {
        return this.entityType.get();
    }

    public @Nullable SoundEvent getEnterNestSound() {
        return enterNestSound;
    }

    public @Nullable SoundEvent getLeaveNestSound() {
        return leaveNestSound;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return p_152180_.isClientSide ? null : createTickerHelper(p_152182_, OtherworldBlocks.ENTITY_NEST_BLOCK_ENTITY.get(), EntityNestBlockEntity::serverTick);
    }


    public int getMaxOccupants() {
        return maxOccupants;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntityNestBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(FULL);
    }

    public interface NestInhabitor {
        BlockPos getNestPos();
        void setNestPos(BlockPos pos);

        default void onLeaveNest(Entity entity, BlockPos pos, EntityNestBlockEntity nestBlock, int ticks, @Nullable Player player, boolean emergency) {
            this.setNestPos(pos);
            if (entity instanceof AgeableMob ageable) {
                int i = ageable.getAge();
                if (i < 0) ageable.setAge(Math.min(0, i + ticks));
                else ageable.setAge(Math.max(0, i - ticks));

                if (ageable instanceof Animal animal) {
                    animal.setInLoveTime(Math.max(0, animal.getInLoveTime() - i));
                }
            }
            this.afterLeaveNest(pos, nestBlock, player, emergency);
        }

        default void onEnterNest(Entity entity, BlockPos pos, BlockState state, EntityNestBlockEntity nestBlock) {
        }

        default void afterLeaveNest(BlockPos blockPos, EntityNestBlockEntity entity, @Nullable Player player, boolean emergency) {
        }

        default void saveNestData(CompoundTag tag) {
            tag.putLong("NestPos", getNestPos().asLong());
        }

        default void readNestData(CompoundTag tag) {
            setNestPos(BlockPos.of(tag.getLong("NestPos")));
        }

    }
}
