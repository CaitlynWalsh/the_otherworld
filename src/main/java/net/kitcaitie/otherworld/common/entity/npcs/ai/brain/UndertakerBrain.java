package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UndertakerBrain extends AIBrain {
    public UndertakerBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        super(person, profiler);
    }

    @Override
    protected void initActionMap() {
        this.actionMap = Map.of(
                AIStates.CORE, List.of(
                        new UndertakerKidnapPlayerAction(person),
                        new BlockWithShieldAction(person),
                        new LookAroundAction(person),
                        new RandomStrollAction(person)
                ),
                AIStates.FORCED, List.of(),
                AIStates.WAR, List.of(
                        new FightInWarAction(person),
                        new BlockWithShieldAction(person)
                )
        );
    }
}
