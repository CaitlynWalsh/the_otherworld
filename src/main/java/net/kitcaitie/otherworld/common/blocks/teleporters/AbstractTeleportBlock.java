package net.kitcaitie.otherworld.common.blocks.teleporters;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public abstract class AbstractTeleportBlock extends Block implements IWorlds {
    private final Worlds world;

    public AbstractTeleportBlock(Properties properties, IWorlds.Worlds worlds) {
        super(properties);
        this.world = worlds;
    }

    @Override
    public Worlds getWorldType() {
        return world;
    }

    public abstract Predicate<LivingEntity> validTargets();

    public static boolean teleport(BlockState blockState, BlockPos blockPos, LivingEntity toTeleport) {
        if (blockState.getBlock() instanceof AbstractTeleportBlock block) {
            if (block.validTargets().test(toTeleport)) {
                toTeleport.moveTo(blockPos.above().getCenter());
                block.onTeleport(blockState, blockPos, toTeleport);
            }
        }
        return false;
    }

    protected abstract void onTeleport(BlockState state, BlockPos blockPos, LivingEntity toTeleport);

}
