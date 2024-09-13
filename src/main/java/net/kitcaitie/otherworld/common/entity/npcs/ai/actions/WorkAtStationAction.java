package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class WorkAtStationAction extends Action {

    public WorkAtStationAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.INTERACTING, Flags.USE_BLOCK));
    }

    @Override
    public boolean canStart() {
        if (!person.isBaby() && person.isVillager()) {
            if (person.isVillagerWorkingHours() && person.getRandom().nextFloat() < 0.01F) {
                if (person.canChangeProfession() && person.getJobType().getWorkStation() == null) {
                    if (person.getJobType() == IOccupation.VillagerType.UNEMPLOYED) {
                        BlockPos blockPos = ActionUtils.findBlock(person, isValidWorkingStation(), person.blockPosition(), 60, 10);
                        if (blockPos != null && !isTaken(blockPos)) {
                            person.setJobType(Arrays.stream(IOccupation.VillagerType.values()).filter((v) -> v.getWorkStation() != null && person.level.getBlockState(blockPos).is(v.getWorkStation())).findAny().orElse(IOccupation.VillagerType.UNEMPLOYED));
                            if (person.getJobType() != IOccupation.VillagerType.UNEMPLOYED) {
                                person.setWorkPos(blockPos);
                                return true;
                            }
                        }
                    }
                    return false;
                } else if (!person.getWorkPos().equals(BlockPos.ZERO)) {
                    if (isValidStation().test(person.level.getBlockState(person.getWorkPos())))
                        return true;
                    else {
                        person.setJobType(IOccupation.VillagerType.UNEMPLOYED);
                        person.setWorkPos(BlockPos.ZERO);
                        return false;
                    }
                } else {
                    BlockPos blockPos = ActionUtils.findBlock(person, isValidStation(), person.blockPosition(), 60, 10);
                    if (blockPos != null && !isTaken(blockPos)) {
                        person.setWorkPos(blockPos);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Priority getPriority() {
        return Priority.P5;
    }

    @Override
    public boolean canContinue() {
        return person.isVillagerWorkingHours() && person.getWorkPos() != BlockPos.ZERO && person.getJobType() != IOccupation.VillagerType.FARMER;
    }

    @Override
    public void start() {
        super.start();
        if (person.shouldRestock()) person.restock();
    }

    @Override
    public void tick() {
        super.tick();

        if (!isValidStation().test(person.level.getBlockState(person.getWorkPos()))) {
            person.setJobType(IOccupation.VillagerType.UNEMPLOYED);
            person.setWorkPos(BlockPos.ZERO);
            return;
        }

        ActionUtils.maybeMoveTo(person, person.getWorkPos().relative(person.getMotionDirection().getOpposite(), 1), 0.85D);
        ActionUtils.lookAt(person, person.getWorkPos());

        if (person.shouldRestock()) {
            person.restock();
        }
    }

    protected Predicate<BlockState> isValidStation() {
        return (state -> person.getJobType().getWorkStation() != null && state.is(person.getJobType().getWorkStation()));
    }

    protected Predicate<BlockState> isValidWorkingStation() {
        return (state -> Arrays.stream(IOccupation.VillagerType.values()).anyMatch((v) -> v.getWorkStation() != null && state.is(v.getWorkStation())));
    }

    protected boolean isTaken(BlockPos blockPos) {
        List<AbstractPerson> list = ActionUtils.findGroupOfPeople(person, (ppl) -> ppl.getJobType().getWorkStation() != null && ppl.getWorkPos().equals(blockPos) && ppl.level.getBlockState(ppl.getWorkPos()).is(ppl.getJobType().getWorkStation()), 100.0D);
        return !list.isEmpty();
    }
}
