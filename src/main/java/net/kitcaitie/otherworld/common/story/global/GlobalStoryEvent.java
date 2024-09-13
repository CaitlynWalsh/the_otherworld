package net.kitcaitie.otherworld.common.story.global;

import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.story.events.Event;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public abstract class GlobalStoryEvent implements Event {
    protected final GlobalEventType eventType;
    protected final RandomSource random;
    protected boolean hasStarted;
    protected Story story;
    protected ServerBossEvent bossBar = null;
    protected boolean loaded = false;
    public boolean disabled = false;
    protected boolean queuedForRemoval = false;

    protected String id = "";

    public GlobalStoryEvent(GlobalEventType eventType) {
        this.eventType = eventType;
        this.random = RandomSource.create();
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public boolean canStart(MinecraftServer server) {
        return false;
    }

    public boolean canContinue(MinecraftServer server) {
        return canStart(server);
    }

    public void start(MinecraftServer server) {
        this.hasStarted = true;
        story.setChanged();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isQueuedForRemoval() {
        return queuedForRemoval;
    }

    public boolean canRemove(MinecraftServer server) {
        return true;
    }

    public void onRemove() {
    }

    public void onWorldTick(MinecraftServer server) {
        if (loaded) {
            if (canContinue(server)) {
                tickEvent(server);
                tickBossBar(server);
            } else {
                stop(server);
            }
        }
    }

    @Override
    public Impact getImpact() {
        return eventType.impact;
    }

    @Override
    public Type getType() {
        return eventType.type;
    }

    public abstract void tickEvent(MinecraftServer server);

    @Nullable
    public ServerBossEvent getBossBar() {
        return bossBar;
    }

    public void tickBossBar(MinecraftServer server) {
    }

    public void updatePlayers(MinecraftServer server) {
    }

    public void stop(MinecraftServer server) {
        this.hasStarted = false;
        story.setChanged();
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean alwaysTicking() {
        return false;
    }

    enum GlobalEventType {
        WAR(Type.HARMFUL, Impact.HIGH),
        OTHER(Type.NEUTRAL, Impact.NONE);

        final Type type;
        final Impact impact;

        GlobalEventType(Type type, Impact impact) {
            this.type = type;
            this.impact = impact;
        }

        public Type getType() {
            return type;
        }

        public Impact getImpact() {
            return impact;
        }
    }

    public boolean shouldSave() {
        return true;
    }

    public abstract CompoundTag save();

    public abstract String tag();

    public String getID() {
        return id;
    }

    public abstract GlobalStoryEvent copy();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlobalStoryEvent evt) {
            if (this == evt) return true;
            return evt.getID().equals(id);
        }
        return false;
    }
}
