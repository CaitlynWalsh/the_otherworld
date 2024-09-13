package net.kitcaitie.otherworld.common.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.kitcaitie.otherworld.common.blocks.HangingFruitBlock;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.IPlantable;

import java.util.function.Predicate;

public class BlockData extends SavedData {
    public static final String FILE_ID = "otherworld_block_data";
    private LongSet placedBlocks = new LongOpenHashBigSet();

    public static final Predicate<BlockState> IS_VALID_BLOCK = (state) ->
           state.getFluidState().isEmpty() && !state.is(OtherworldTags.BREAKABLE_STRUCTURE_BLOCKS) && !state.canBeReplaced() && !(state.getBlock() instanceof IPlantable || state.getBlock() instanceof HangingFruitBlock);

    public BlockData() {
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLongArray("blocks", placedBlocks.toLongArray());
        return tag;
    }

    public static BlockData load(CompoundTag tag) {
        BlockData blockData = new BlockData();
        blockData.placedBlocks = new LongOpenHashBigSet(tag.getLongArray("blocks"));
        return blockData;
    }

    public static void saveAndLoadData(LevelAccessor level) {
        if (level instanceof ServerLevel slevel) {
            BlockData data = slevel.getDataStorage().computeIfAbsent(BlockData::load, BlockData::new, FILE_ID);
            slevel.getDataStorage().set(FILE_ID, data);
            ((OtherworldServerLevel)slevel).setBlockData(data);
        }
    }

    public boolean removeBlock(BlockPos blockPos) {
        if (placedBlocks.remove(blockPos.asLong())) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public boolean addBlock(BlockPos blockPos) {
        if (!containsBlock(blockPos)) {
            this.placedBlocks.add(blockPos.asLong());
            this.setDirty();
            return true;
        }
        return false;
    }

    public boolean containsBlock(BlockPos blockPos) {
        return placedBlocks.contains(blockPos.asLong());
    }
}
