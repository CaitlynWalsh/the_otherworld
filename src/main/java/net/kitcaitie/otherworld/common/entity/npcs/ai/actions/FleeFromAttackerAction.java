package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FleeFromAttackerAction extends Action {
    protected final int forgetTime;
    protected LivingEntity attacker;
    protected int forgetTimer;

    public FleeFromAttackerAction(AbstractPerson person, int forgetTime) {
        super(person);
        this.forgetTime = forgetTime;
        this.setFlags(EnumSet.of(Flags.LOOKING_COMBAT, Flags.MOVING_COMBAT, Flags.FLEE));
    }

    @Override
    public boolean canStart() {
        this.attacker = person.getLastHurtByMob();
        return (person.isBaby() || person.isVeryHurt() || (!person.isWarrior() && !person.isMale() && !person.isAggressiveRace())) && this.attacker != null;
    }

    @Override
    public boolean canContinue() {
        return this.attacker != null && forgetTimer > 0;
    }

    @Override
    public void start() {
        super.start();
        this.forgetTimer = forgetTime;
    }

    @Override
    public void tick() {
        super.tick();
        ActionUtils.lookAt(person, attacker);
        Vec3 vec3 = DefaultRandomPos.getPosAway(person, 16, 7, attacker.position());
        if (vec3 != null) {
            ActionUtils.moveTo(person, vec3, 1.2D);
        }
        --forgetTimer;
    }

    @Override
    public void stop() {
        super.stop();
        this.attacker = null;
        this.forgetTimer = 0;
    }

    @Override
    public Priority getPriority() {
        return Priority.P3;
    }

    @Override
    public EnumSet<Flags> disabledFlags() {
        return EnumSet.of(Flags.USE_BLOCK, Flags.SOCIAL);
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

}
