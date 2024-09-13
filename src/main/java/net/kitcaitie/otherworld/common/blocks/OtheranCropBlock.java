package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.function.Supplier;

public class OtheranCropBlock extends CropBlock implements IWorlds {
    private final Worlds worldType;
    private final Supplier<Item> seeds;

    public OtheranCropBlock(Properties properties, Worlds worldType, Supplier<Item> seeds) {
        super(properties);
        this.worldType = worldType;
        this.seeds = seeds;
    }

    public Worlds getWorldType() {
        return worldType;
    }

    public Item getSeeds() {
        return seeds.get();
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return getSeeds();
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        boolean flag = canPlantOn(levelReader.getBlockState(blockPos.below()));
        boolean flag1 = (levelReader.getRawBrightness(blockPos, 0) >= 8 || levelReader.canSeeSky(blockPos));
        switch (getWorldType()) {
            case EMBERIA -> {
                return flag && flag1 && levelReader.getBiome(blockPos).is(Tags.Biomes.IS_HOT);
            }
            case GLACEIA -> {
                return flag && flag1 && levelReader.getBiome(blockPos).is(Tags.Biomes.IS_SNOWY);
            }
            case UNDERLANDS -> {
                return flag && levelReader.getRawBrightness(blockPos, 0) < 5 && !levelReader.canSeeSky(blockPos);
            }
        }
        return flag && flag1;
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return canPlantOn(blockState);
    }

    protected boolean canPlantOn(BlockState blockState) {
        if (blockState.getBlock() instanceof OtheranFarmBlock) {
            return ((OtheranFarmBlock) blockState.getBlock()).getWorldType() == this.getWorldType();
        }
        return blockState.getBlock() instanceof FarmBlock && (getWorldType() == Worlds.DEEPWOODS || getWorldType() == Worlds.OVERWORLD);
    }
}
