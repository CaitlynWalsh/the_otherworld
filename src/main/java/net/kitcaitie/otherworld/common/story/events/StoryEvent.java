package net.kitcaitie.otherworld.common.story.events;

import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class StoryEvent implements Event {

    public enum Event {
        PLAYER_KILLS,
        PLAYER_GETS_KILLED

    }

    protected final IWorlds.Worlds world;
    protected final UUID player;
    protected final long person;
    protected final Type type;
    protected final Impact impact;
    protected final Event event;

    public StoryEvent(Player player, Event event, Type type, Impact impact, IWorlds.Worlds world) {
        this(player, null, event, type, impact, world);
    }

    public StoryEvent(Player player, @Nullable AbstractPerson person, Event event, Type type, Impact impact, IWorlds.Worlds world) {
        this(player.getUUID(), person != null ? person.getIdentity() : 0L, event, type, impact, world);
    }

    public StoryEvent(UUID uuid, long personID, Event event, Type type, Impact impact, IWorlds.Worlds world) {
        this.player = uuid;
        this.person = personID;
        this.event = event;
        this.type = type;
        this.impact = impact;
        this.world = world;
    }

    public static StoryEvent playerKills(Player killer, AbstractPerson killed, IWorlds.Worlds affectedWorld, Type type, Impact impact) {
        return new StoryEvent(killer, killed, Event.PLAYER_KILLS, type, impact, affectedWorld);
    }

    public static StoryEvent playerGetsKilled(Player killed, AbstractPerson killer, IWorlds.Worlds affectedWorld, Type type, Impact impact) {
        return new StoryEvent(killed, killer, Event.PLAYER_GETS_KILLED, type, impact, affectedWorld);
    }


    public UUID getPlayer() {
        return player;
    }

    public long getPerson() {
        return person;
    }

    public Type getType() {
        return type;
    }

    public Impact getImpact() {
        return impact;
    }

    public Event getEvent() {
        return event;
    }

    public IWorlds.Worlds getWorld() {
        return world;
    }

    @Override
    public String getID() {
        return "storyevent_" + getEvent() + "_" + getType().name().toLowerCase() + "_" + getImpact().name().toLowerCase();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("event", getEvent().name());
        tag.putUUID("player", getPlayer());
        tag.putLong("personID", getPerson());
        tag.putString("type", getType().name());
        tag.putString("impact", getImpact().name());
        tag.putString("world", getWorld().name());
        return tag;
    }

    public static StoryEvent load(CompoundTag tag) {
        Event event = Event.valueOf(tag.getString("event"));
        UUID player = tag.getUUID("player");
        long personID = tag.getLong("personID");
        Type type = Type.valueOf(tag.getString("type"));
        Impact impact = Impact.valueOf(tag.getString("impact"));
        IWorlds.Worlds world = IWorlds.Worlds.valueOf(tag.getString("world"));
        return new StoryEvent(player, personID, event, type, impact, world);
    }
}
