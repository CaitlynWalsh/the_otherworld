package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class FightInWarAction extends MeleeFightAction {
    protected final AbstractPerson person;
    protected final TargetingConditions warTargeting;
    protected @Nullable WarEvent warEvent;

    public FightInWarAction(AbstractPerson person) {
        super(person);
        this.person = person;
        this.warTargeting = TargetingConditions.forCombat().selector(this::shouldSoldierAttack).range(16.0D);
        this.setFlags(EnumSet.of(Flags.LOOKING_COMBAT, Flags.MOVING_COMBAT, Flags.ATTACKING, Flags.WAR));
    }

    @Override
    public boolean canStart() {
        if (person.isBaby()) return false;
        this.warEvent = person.getInvolvedWar();
        return this.warEvent != null && this.warEvent.hasActiveWar() && !this.warEvent.isWarFinished() && person.isWarrior() && super.canStart();
    }

    @Override
    public boolean canContinue() {
        return super.canContinue() && canStart();
    }

    @Override
    public void start() {
        person.getNavigation().stop();
        person.setAggressive(true);
        super.start();
    }

    @Override
    public void tick() {
        if (person.getTarget() == null) {
            List<LivingEntity> list = ActionUtils.findGroupOfEntities(person, (ent) -> warTargeting.test(person, ent), 16.0D);
            if (!list.isEmpty()) person.setTarget(list.get(0));
        }
        super.tick();
        if (this.fleeing) return;
        if (warEvent != null && warEvent.hasActiveWar()) {
            if (this.target == null) {
                if (!isInWarBounds()) {
                    ActionUtils.moveTo(person, warEvent.invasionTargetPos, 1.2D);
                }
                else {
                    ActionUtils.maybeMoveTo(person, warEvent.invasionTargetPos.offset(person.getRandom().nextInt(30), 0, person.getRandom().nextInt(30)), 1.2D);
                }
            } else if (canAttack(false).test(target)) {
                ActionUtils.lookAt(person, this.target);
                if (ticksUntilNextAttack <= 0) {
                    ActionUtils.moveTo(this.person, this.target, 1.2D);
                    doMeleeAttack(this.target);
                }
                else {
                    ActionUtils.strafeAwayFrom(person, target, 0.4F);
                }
            }
        }
    }

    protected boolean isInvader() {
        return Arrays.asList(this.warEvent.invader).contains(person.getRace());
    }

    @Override
    public void stop() {
        super.stop();
        this.warEvent = null;
        person.setAggressive(false);
    }

    protected boolean isInWarBounds() {
        return this.warEvent.invasionTargetPos.closerToCenterThan(this.person.position(), 50.0D);
    }

    @Override
    public Priority getPriority() {
        return Priority.P3;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    @Override
    protected boolean shouldSoldierAttack(LivingEntity entity) {
        if (entity instanceof AbstractPerson person1) {
            if (this.person.isGhoul()) {
                return isInvader() ? warEvent.defendingRace == person1.getRace() : Arrays.asList(warEvent.invader).contains(person1.getRace());
            }
            return (isInvader() ? warEvent.defendingRace == person1.getRace() : Arrays.asList(warEvent.invader).contains(person1.getRace())) && (person.isAggressiveRace() || (!person1.isBaby() && person1.isMale()));
        } else if (entity instanceof Player player && !person.getPersonData().isFriendlyTowards(player)) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (isInvader() && warEvent.defendingRace == character.getRace()) {
                return person.isAggressiveRace() || character.isMale();
            } else if (Arrays.asList(warEvent.invader).contains(character.getRace())) {
                return !person.getPersonData().isFamilyWith(player);
            }
        }
        return false;
    }
}
