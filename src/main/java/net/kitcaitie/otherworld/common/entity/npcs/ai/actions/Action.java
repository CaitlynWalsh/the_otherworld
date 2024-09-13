package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.util.Mth;

import java.util.EnumSet;

public abstract class Action {

    protected final AbstractPerson person;

    protected EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);
    protected boolean started;

    public Action(AbstractPerson person) {
        this.person = person;
    }

    public abstract boolean canStart();

    public boolean canContinue() {
        return canStart();
    }

    public Priority getPriority() {
        return Priority.P6;
    }

    public boolean stopLowerPriorities() {
        return false;
    }

    public void start() {
        this.started = true;
    }

    public void tick() {
    }

    public void stop() {
        this.started = false;
    }

    public void setFlags(EnumSet<Flags> enumSet) {
        this.flags = enumSet;
    }

    public EnumSet<Flags> getFlags() {
        return flags;
    }

    public boolean isStarted() {
        return started;
    }

    public EnumSet<Flags> disabledFlags() {
        return EnumSet.noneOf(Flags.class);
    }

    protected static int reducedTickDelay(int i) {
        return Mth.positiveCeilDiv(i, 2);
    }

    public enum Flags {
        LOOKING,
        MOVING,
        INTERACTING,
        JUMPING,
        ATTACKING,
        EATING,
        CRAFTING,
        BLOCKING,
        SLEEPING,
        MOVING_COMBAT,
        LOOKING_COMBAT,
        INTERACTING_COMBAT,
        MOVING_WANDER,
        LOOKING_RANDOM,
        USE_ITEM,
        USE_BLOCK,
        PLACE_BLOCK,
        DESTROY_BLOCK,
        WAR,
        PLAYING,
        FLEE,
        HIDE,
        FOLLOW,
        SOCIAL
    }

    public enum Priority {
        P0,
        P1,
        P2,
        P3,
        P4,
        P5,
        P6,
        P7,
        P8,
        P9,
        P10,
        P11,
        P12
    }
}
