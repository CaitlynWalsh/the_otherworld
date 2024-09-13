package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;

public abstract class RangedAttackAction extends MeleeFightAction {

    public RangedAttackAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.ATTACKING, Flags.MOVING_COMBAT, Flags.LOOKING_COMBAT, Flags.USE_ITEM));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fleeing) return;
        if (this.target != null) {
            person.setAggressive(true);
            if (this.target.distanceTo(this.person) <= 6.0D) {
                ActionUtils.strafeAwayFrom(person, target, 0.7F);
            }
            else {
                ActionUtils.lookAndMoveTo(person, target, 0.9D);
            }
            this.doMeleeAttack(target);
        }
        else {
            person.setAggressive(false);
        }
    }

    @Override
    public void doMeleeAttack(LivingEntity target) {
        if (this.ticksUntilNextAttack <= 0) {
            if (ActionUtils.rangedAttack(person, target)) {
                this.ticksUntilNextAttack = attackInterval;
            }
        }
    }
}
