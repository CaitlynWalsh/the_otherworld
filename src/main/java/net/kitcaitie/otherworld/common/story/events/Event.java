package net.kitcaitie.otherworld.common.story.events;

import net.minecraft.nbt.CompoundTag;

public interface Event {

    enum Type {
        HELPFUL,
        NEUTRAL,
        HARMFUL
    }
    enum Impact {
        EXTREME,
        HIGH,
        MEDIUM,
        AVERAGE,
        LOW,
        NONE
    }

    String getID();

    Impact getImpact();

    Type getType();

    CompoundTag save();

}
