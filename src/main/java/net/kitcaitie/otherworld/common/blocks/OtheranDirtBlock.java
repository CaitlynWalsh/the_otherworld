package net.kitcaitie.otherworld.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class OtheranDirtBlock extends OtheranBlock {
    public OtheranDirtBlock(Properties properties, Worlds worlds) {
        super(properties, worlds);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos);
        if (plantType == PlantType.CROP) return false;
        if (getWorldType() == Worlds.UNDERLANDS) {
            return plantType == PlantType.CAVE || plantType == PlantType.NETHER;
        }
        return plantType == PlantType.PLAINS;
    }
}
