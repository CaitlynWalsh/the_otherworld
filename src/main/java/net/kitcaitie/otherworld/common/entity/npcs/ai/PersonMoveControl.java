package net.kitcaitie.otherworld.common.entity.npcs.ai;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PersonMoveControl extends MoveControl {
    protected final AbstractPerson person;
    public PersonMoveControl(AbstractPerson person) {
        super(person);
        this.person = person;
    }

    @Override
    public void tick() {
        this.speedModifier = getSpeedModifier();
        if (this.operation == MoveControl.Operation.STRAFE) {
            float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float)this.speedModifier * f;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 *= f4;
            f3 *= f4;
            float f5 = Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F));
            float f6 = Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F));
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            if (!this.isWalkablePath(f7, f8)) {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
            }

            this.mob.setSpeed(f1);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = MoveControl.Operation.WAIT;
        } else if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            double d2 = this.wantedY - this.mob.getY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
            if (d3 < (double)2.5000003E-7F) {
                this.mob.setZza(0.0F);
                return;
            }

            float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level.getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
            if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = MoveControl.Operation.JUMPING;
            }
        } else if (this.operation == MoveControl.Operation.JUMPING) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.operation = MoveControl.Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0F);
        }
    }

    public void stopMoving() {
        this.wantedX = mob.getX();
        this.wantedY = mob.getY();
        this.wantedZ = mob.getZ();
        this.strafeForwards = 0.0F;
        this.strafeRight = 0.0F;
    }

    @Override
    public double getSpeedModifier() {
        return person.isUsingItem() || person.isCrouching() ? (double) ((float)(super.getSpeedModifier() * 0.4F)) : super.getSpeedModifier();
    }

    protected boolean isWalkablePath(float f1, float f2) {
        PathNavigation pathnavigation = this.mob.getNavigation();
        if (pathnavigation != null) {
            NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
            BlockPos blockPos = new BlockPos(Mth.floor(this.mob.getX() + (double)f1), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)f2));
            if (nodeevaluator != null) {
                BlockPathTypes pathType = nodeevaluator.getBlockPathType(mob.level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (pathType != BlockPathTypes.WALKABLE && pathType != BlockPathTypes.OPEN) return false;
                if (operation == Operation.STRAFE && pathType == BlockPathTypes.OPEN) {
                    BlockPos blockPos1 = blockPos.mutable().move(mob.getDirection().getOpposite(), 1).below();
                    pathType = nodeevaluator.getBlockPathType(mob.level, blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
                    if (pathType == BlockPathTypes.BLOCKED) {
                        BlockPos blockPos2 = blockPos.mutable().move(mob.getDirection().getOpposite(), 1).above();
                        pathType = nodeevaluator.getBlockPathType(mob.level, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                        return pathType == BlockPathTypes.OPEN;
                    }
                    return pathType == BlockPathTypes.WALKABLE;
                }
            }
        }
        return true;
    }

}
