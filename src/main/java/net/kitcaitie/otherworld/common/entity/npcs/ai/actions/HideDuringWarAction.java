package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HideDuringWarAction extends Action {
    protected WarEvent involvedWar;

    public HideDuringWarAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.WAR, Flags.HIDE));
    }

    @Override
    public boolean canStart() {
        if (person.isBaby() || !person.isWarrior()) {
            this.involvedWar = person.getInvolvedWar();
            if (this.involvedWar != null) {
                return involvedWar.hasActiveWar() && involvedWar.defendingRace == person.getRace();
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return canStart();
    }

    @Override
    public void start() {
        super.start();
        this.person.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (person.hasValidHomePos()) {
            ActionUtils.moveTo(this.person, person.getHomePos(), 1.2D);
        }
        else if (person.getNavigation().isDone()) {
            ActionUtils.moveTo(person, DefaultRandomPos.getPosAway(person, 10, 7, Vec3.atCenterOf(involvedWar.invasionTargetPos)), 1.2D);
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P3;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }
}
