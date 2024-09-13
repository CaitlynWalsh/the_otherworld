package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.blocks.LockableDoorBlock;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.PersonNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.function.Predicate;

public class DoorInteractAction extends Action {
    protected static final Predicate<BlockState> VALID_DOOR = (state) -> DoorBlock.isWoodenDoor(state) && ((!(state.getBlock() instanceof LockableDoorBlock)) || !state.getValue(LockableDoorBlock.LOCKED));
    protected BlockPos door;
    protected boolean hasDoor;
    private boolean passed;
    private int forgetTime;
    private float doorOpenDirX;
    private float doorOpenDirZ;

    public DoorInteractAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.INTERACTING, Flags.USE_BLOCK));
    }

    @Override
    public boolean canStart() {
        PersonNavigation pathNavigation = (PersonNavigation) this.person.getNavigation();
        if (!pathNavigation.canOpenDoors()) return false;
        Path path = pathNavigation.getPath();
        if (path != null && !path.isDone()) {
            for (int i=0; i<path.getNodeCount(); i++) {
                Node node = path.getNode(i);
                this.door = new BlockPos(node.x, node.y + 1, node.z);
                if (!(person.distanceToSqr(door.getX(), person.getY(), door.getZ()) > 2.0D)) {
                    this.hasDoor = VALID_DOOR.test(person.level.getBlockState(door));
                    if (hasDoor) {
                        return true;
                    }
                }
            }
            this.door = this.person.blockPosition().above();
            this.hasDoor = VALID_DOOR.test(person.level.getBlockState(door));
            return hasDoor;
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return this.hasDoor && !passed && this.forgetTime > 0;
    }

    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float)((double)this.door.getX() + 0.5D - this.person.getX());
        this.doorOpenDirZ = (float)((double)this.door.getZ() + 0.5D - this.person.getZ());
        this.forgetTime = 20;
        super.start();
        this.setOpen(true);
    }

    @Override
    public void tick() {
        --this.forgetTime;
        float f = (float)((double)this.door.getX() + 0.5D - this.person.getX());
        float f1 = (float)((double)this.door.getZ() + 0.5D - this.person.getZ());
        float f2 = this.doorOpenDirX * f + this.doorOpenDirZ * f1;
        if (f2 < 0.0F) {
            this.passed = true;
        }
    }

    @Override
    public void stop() {
        if (this.isOpen()) {
            this.setOpen(false);
        }
        super.stop();
    }

    @Override
    public Priority getPriority() {
        return Priority.P0;
    }

    protected boolean isOpen() {
        if (!this.hasDoor) return false;
        BlockState blockState = person.level.getBlockState(door);
        if (!(blockState.getBlock() instanceof DoorBlock)) {
            this.hasDoor = false;
            return false;
        }
        return blockState.getValue(DoorBlock.OPEN);
    }

    protected void setOpen(boolean open) {
        if (this.hasDoor) {
            BlockState blockState = person.level.getBlockState(door);
            if (blockState.getBlock() instanceof DoorBlock) {
                person.swing(InteractionHand.MAIN_HAND);
                ((DoorBlock) blockState.getBlock()).setOpen(person, person.level, blockState, door, open);
            }
        }
    }
}
