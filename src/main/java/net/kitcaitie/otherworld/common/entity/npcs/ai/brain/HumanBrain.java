package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HumanBrain extends AIBrain {
    public HumanBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        super(person, profiler);
    }

    @Override
    protected void initActionMap() {
        this.actionMap = Map.of(
                AIStates.CORE, List.of(
                        new BlockWithShieldAction(person),
                        new IdleFightAction(person),
                        new FeedFamilyMembers(person),
                        new SleepInBedAction(person),
                        new ReceiveVillagerFoodAction(person),
                        new MakeBebeAction(person),
                        new LookAroundAction(person),
                        new RandomStrollAction(person)
                ),
                AIStates.FORCED, List.of(),
                AIStates.WAR, List.of(
                        new FightInWarAction(person),
                        new BlockWithShieldAction(person),
                        new HideDuringWarAction(person)
                )
        );
    }
}
