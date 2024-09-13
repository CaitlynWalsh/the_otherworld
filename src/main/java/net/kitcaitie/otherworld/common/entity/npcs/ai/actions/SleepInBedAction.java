package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.EnumSet;
import java.util.function.Predicate;

public class SleepInBedAction extends Action {
    protected BlockPos bedPos;
    protected int cooldown;

    public SleepInBedAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.SLEEPING));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0) --cooldown;
        if (person.isVehicle() || person.isPassenger()) return false;
        if (person.canSleep() && person.getLastHurtByMob() == null) {
            if (person.level.isNight() && !person.isSleeping()) {
                if (person.hasValidHomePos() && person.getHomePos().distToCenterSqr(person.position()) < 1000.0D) {
                    BlockState bstate = person.level.getBlockState(person.getHomePos());
                    if (isValidBed().test(bstate)) {
                        this.bedPos = person.getHomePos();
                        return true;
                    }
                    else {
                        this.person.setHomePos(BlockPos.ZERO);
                        return false;
                    }
                } else if (cooldown <= 0) {
                    BlockPos pos = ActionUtils.findBlock(person, isValidBed(), person.blockPosition(), 15, 10);
                    if (pos != null) {
                        this.bedPos = pos;
                        return true;
                    }
                    this.cooldown = 20;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !person.isSleeping() && person.level.isNight() && this.bedPos != null && this.bedPos.distToCenterSqr(person.position()) < 1000.0D && isValidBed().test(person.level.getBlockState(bedPos));
    }

    @Override
    public void tick() {
        if (bedPos.distToCenterSqr(person.position()) <= 4.5D) {
            person.getNavigation().stop();
            person.swing(InteractionHand.MAIN_HAND);
            person.startSleeping(bedPos);
            if (!person.hasValidHomePos() || (person.getHomePos() != bedPos &&
                    !isValidBed().test(person.level.getBlockState(person.getHomePos())))) {
                person.setHomePos(bedPos);
            }
        }
        else {
            ActionUtils.lookAt(person, bedPos);
            ActionUtils.maybeMoveTo(person, bedPos, 0.85D);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.bedPos = null;
    }

    @Override
    public Priority getPriority() {
        return Priority.P4;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    public Predicate<BlockState> isValidBed() {
        return (blk) -> blk.getBlock() instanceof BedBlock
                && !blk.getValue(BlockStateProperties.OCCUPIED)
                && blk.getValue(BlockStateProperties.BED_PART) == BedPart.HEAD;
    }
}
