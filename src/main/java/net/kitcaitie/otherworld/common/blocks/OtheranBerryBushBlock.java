package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class OtheranBerryBushBlock extends SweetBerryBushBlock implements IWorlds {
    private final Worlds world;
    private final List<Supplier<Block>> soil;
    private final Supplier<Item> berry;
    @Nullable private final SoundEvent pickSound;
    private final boolean thorns;

    public OtheranBerryBushBlock(Properties properties, Worlds world, List<Supplier<Block>> soil, Supplier<Item> berry, @Nullable SoundEvent pickSound, boolean thorns) {
        super(properties);
        this.world = world;
        this.soil = soil;
        this.berry = berry;
        this.pickSound = pickSound;
        this.thorns = thorns;
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return this.hasThorns() ? BlockPathTypes.DANGER_OTHER : BlockPathTypes.OPEN;
    }

    public List<Supplier<Block>> getSoil() {
        return soil;
    }

    public Item getBerryType() {
        return berry.get();
    }

    @Nullable
    public SoundEvent getPickSound() {
        return pickSound;
    }

    public boolean hasThorns() {
        return thorns;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        boolean flag = facing == Direction.UP;
        for (Supplier<Block> supplier : getSoil()) {
            if (world.getBlockState(pos).is(supplier.get())) return flag;
        }
        return false;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return canPlaceOn(state);
    }

    protected boolean canPlaceOn(BlockState state) {
        for (Supplier<Block> supplier : getSoil()) {
            if (state.is(supplier.get())) return true;
        }
        return false;
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        if (getWorldType() == Worlds.UNDERLANDS) {
            return PlantType.NETHER;
        }
        return PlantType.PLAINS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.below();
        if (state.getBlock() == this) {
            return ((OtheranBerryBushBlock)state.getBlock()).canPlaceOn(reader.getBlockState(blockpos));
        }
        return this.mayPlaceOn(reader.getBlockState(blockpos), reader, blockpos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        int i = state.getValue(AGE);
        boolean flag = i == 3;
        if (!flag && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        else if (flag) {
            int j = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(getBerryType(), j + 1));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            if (getPickSound() != null) {
                level.playSound(null, pos, getPickSound(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            }
            BlockState state1 = state.setValue(AGE, 2);
            level.setBlock(pos, state1, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state1));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (hasThorns()) {
            super.entityInside(state, level, pos, entity);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
        if (source.nextInt(3) == 0) {
            int i = state.getValue(AGE);
            if (i < 3) {
                switch (getWorldType()) {
                    case UNDERLANDS -> {
                        if (level.getMaxLocalRawBrightness(pos.above(), 0) > 5) {
                            return;
                        }
                    }
                    default -> {
                        if (level.getMaxLocalRawBrightness(pos.above(), 0) < 9) {
                            return;
                        }
                    }
                }
                BlockState blockstate = state.setValue(AGE, i + 1);
                level.setBlock(pos, blockstate, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockstate));
            }
        }
    }

    @Override
    public Worlds getWorldType() {
        return world;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return getBerryType().getDefaultInstance();
    }
}
