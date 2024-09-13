package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SoldierBrain extends AIBrain {
    public SoldierBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        super(person, profiler);
    }

    @Override
    protected void initActionMap() {
        this.actionMap = Map.of(
                AIStates.CORE, List.of(
                        new BlockWithShieldAction(person),
                        new IdleFightAction(person),
                        new ReleasePrisonerAction(person),
                        new UnlockOrLockPrisonDoor(person),
                        new FeedFamilyMembers(person),
                        new TradeWithOthersAction(person),
                        new LookAroundAction(person),
                        new RandomStrollAction(person)
                ),
                AIStates.FORCED, List.of(),
                AIStates.WAR, List.of(
                        new FightInWarAction(person),
                        new BlockWithShieldAction(person),
                        new UnlockOrLockPrisonDoor(person)
                )
        );
    }
}
