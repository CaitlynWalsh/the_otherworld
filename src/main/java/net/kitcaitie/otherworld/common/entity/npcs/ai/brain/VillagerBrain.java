package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VillagerBrain extends AIBrain {

    public VillagerBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        super(person, profiler);
    }

    @Override
    protected void initActionMap() {
        this.actionMap = Map.of(
                AIStates.CORE, List.of(
                        new BlockWithShieldAction(person),
                        new IdleFightAction(person),
                        new FleeFromAttackerAction(person, 60),
                        new FeedFamilyMembers(person),
                        new TradeWithOthersAction(person),
                        new FarmerWorkAction(person),
                        new WorkAtStationAction(person),
                        new MakeBebeAction(person),
                        new SleepInBedAction(person),
                        new LookAroundAction(person),
                        new RandomStrollAction(person)
                ),
                AIStates.FORCED, List.of(),
                AIStates.WAR, List.of(
                        new BlockWithShieldAction(person),
                        new FleeFromAttackerAction(person, 80),
                        new HideDuringWarAction(person)
                )
        );
    }
}
