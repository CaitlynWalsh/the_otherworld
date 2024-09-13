package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;

import java.util.EnumSet;

public class IdleFightAction extends MeleeFightAction {
    public IdleFightAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING_COMBAT, Flags.LOOKING_COMBAT, Flags.ATTACKING));
    }

    @Override
    public boolean canStart() {
        return super.canStart() && person.getInvolvedWar() == null && (!person.isBaby() && (person.isWarrior() || person.isMale() || person.isAggressiveRace()));
    }

    @Override
    public boolean canContinue() {
        return super.canContinue() && canStart() && person.isAlive() && !person.isRemoved();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fleeing) return;
        if (this.target != null && canAttack(false).test(target)) {
            if (!person.isAggressive()) person.setAggressive(true);
            if (ticksUntilNextAttack <= 0 || target instanceof AbstractPerson) {
                ActionUtils.lookAndMoveTo(this.person, this.target, 1.2D);
                doMeleeAttack(this.target);
            }
            else {
                ActionUtils.strafeAwayFrom(person, target, 0.7F);
            }
        }
        else if (person.isAggressive()) {
            ActionUtils.stopMoving(person);
            person.setAggressive(false);
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P3;
    }

    @Override
    public boolean stopLowerPriorities() {
        return this.target != null;
    }
}
