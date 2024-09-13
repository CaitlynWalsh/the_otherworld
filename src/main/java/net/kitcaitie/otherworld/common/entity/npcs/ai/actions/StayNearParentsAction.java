package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

public class StayNearParentsAction extends FamilyInteractionAction {
    protected final double speed;
    protected final double farSpeed;
    protected final double range;
    protected LivingEntity parent1;
    protected LivingEntity parent2;

    public StayNearParentsAction(AbstractPerson person, double speed, double farSpeed, double range) {
        super(person);
        this.speed = speed;
        this.farSpeed = farSpeed;
        this.range = range;
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.LOOKING, Flags.FOLLOW, Flags.SOCIAL));
    }

    @Override
    public boolean canStart() {
        super.canStart();
        if (person.getPersonData().hasParents() && person.getRandom().nextInt(160) == 0) {
            if (this.person.getInvolvedWar() == null && !person.isAggressive() && this.person.getLastHurtByMob() == null) {
                if (this.parent1 == null) {
                    List<LivingEntity> list = ActionUtils.findGroupOfEntities(person, (entity) -> {
                        if (person.getPersonData().isChildOf(entity)) {
                            return !person.getPersonData().isHostileTowards(entity) && !entity.isSleeping();
                        }
                        return false;
                    }, 18.0D);
                    if (list.isEmpty()) return false;
                    this.parent1 = list.get(0);
                    if (list.size() >= 2) {
                        this.parent2 = list.get(1);
                    }
                }
                this.interactTarget = person.getRandom().nextBoolean() && parent2 != null ? parent2 : parent1;
                return EntitySelector.NO_SPECTATORS.test(interactTarget);
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        boolean flag = this.interactTarget != null && this.interactTarget.isAlive() && this.person.getInvolvedWar() == null && !person.isAggressive() && this.person.getLastHurtByMob() == null;
        if (this.interactTarget instanceof Player player) {
            return flag && !this.person.getPersonData().isHostileTowards(player);
        }
        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        double distance = this.person.distanceTo(this.interactTarget);
        if (distance > range) {
            ActionUtils.lookAt(this.person, this.interactTarget);
            ActionUtils.maybeMoveTo(this.person, this.interactTarget, distance > (float) range + (float) range ? this.farSpeed : this.speed);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.person.getNavigation().stop();
    }
}
