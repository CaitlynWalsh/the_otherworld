package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.EnumSet;

public class MakeBebeAction extends FamilyInteractionAction {
    private AbstractPerson mother;
    private AbstractPerson father;
    private boolean madeLove;

    public MakeBebeAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.SOCIAL));
    }

    @Override
    public boolean canStart() {
        if (super.canStart() && person.wantsToBreed() && (person.getMerchantData().getVillagerType().getWorkStation() == null || !person.isVillagerWorkingHours()) && this.shouldBreed(person)) {
            if (person.level instanceof ServerLevel level && this.spouse != null) {
                Entity entity = level.getEntity(this.spouse);
                if (entity instanceof AbstractPerson lover) {
                    if (person.canBreedWith(lover) && this.shouldBreed(lover)) {
                        this.interactTarget = lover;
                        return true;
                    }
                }
            }
        }
        if (!person.getPersonData().isMarried() && !person.level.isClientSide()) {
            person.findSpouseToMarry((ServerLevel) person.level);
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !madeLove && this.spouse != null && this.interactTarget != null && this.interactTarget.isAlive() && this.shouldBreed(person) && this.shouldBreed((AbstractPerson) interactTarget);
    }

    @Override
    public void start() {
        super.start();
        this.mother = person.isMale() ? (AbstractPerson) interactTarget : person;
        this.father = person.isMale() ? person : (AbstractPerson) interactTarget;
    }

    @Override
    public void tick() {
        super.tick();
        ActionUtils.lookAt(father, mother);
        if (this.person.distanceTo(this.interactTarget) < 2.0D) {
            ActionUtils.lookAt(mother, father);
            mother.getNavigation().stop();
            if (person.level instanceof ServerLevel level) {
                madeLove = true;
                person.breedWith(level, interactTarget, null);
                person.hasVillagerDiedOrLeftRecently = false;
                ((AbstractPerson)interactTarget).hasVillagerDiedOrLeftRecently = false;
                return;
            }
        }
        ActionUtils.moveTo(father, mother, 0.9D);
    }

    @Override
    public void stop() {
        super.stop();
        this.mother = null;
        this.father = null;
        madeLove = false;
    }

    private boolean shouldBreed(AbstractPerson person) {
        return person.getAge() == 0 && !person.isStarving() && !person.isVeryHurt() && (person.isHuman() || person.hasValidHomePos()) && person.getInvolvedWar() == null;
    }
}
