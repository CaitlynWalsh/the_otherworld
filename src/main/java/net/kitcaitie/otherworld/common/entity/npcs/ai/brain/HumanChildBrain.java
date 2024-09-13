package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HumanChildBrain extends AIBrain {
    public HumanChildBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        super(person, profiler);
    }

    @Override
    protected void initActionMap() {
        this.actionMap = Map.of(
                AIStates.CORE, List.of(
                        new FleeFromAttackerAction(person, 80),
                        new SleepInBedAction(person),
                        new ReceiveVillagerFoodAction(person),
                        new BabyPlayWithOthersAction(person),
                        new StayNearParentsAction(person, 0.9D, 1.0D, 3.5D),
                        new LookAroundAction(person),
                        new RandomStrollAction(person)
                ),
                AIStates.FORCED, List.of(),
                AIStates.WAR, List.of(
                        new FleeFromAttackerAction(person, 100),
                        new HideDuringWarAction(person)
                )
        );
    }
}
