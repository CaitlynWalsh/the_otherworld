package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

public class OtheranPlantBlock extends BushBlock implements IWorlds {
    private final Worlds world;
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public OtheranPlantBlock(Properties properties, Worlds worlds) {
        super(properties.offsetType(OffsetType.XZ));
        this.world = worlds;
    }

    @Override
    public Worlds getWorldType() {
        return world;
    }

    public VoxelShape getShape(BlockState p_53517_, BlockGetter p_53518_, BlockPos p_53519_, CollisionContext p_53520_) {
        Vec3 vec3 = p_53517_.getOffset(p_53518_, p_53519_);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return getWorldType() == Worlds.UNDERLANDS ? PlantType.CAVE : PlantType.PLAINS;
    }

}
