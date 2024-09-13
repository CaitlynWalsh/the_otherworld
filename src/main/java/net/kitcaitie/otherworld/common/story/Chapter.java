package net.kitcaitie.otherworld.common.story;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.Event;
import net.kitcaitie.otherworld.common.story.events.StoryEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class Chapter {
    protected Map<UUID, List<StoryEvent>> storyEvents = new HashMap<>();
    protected List<UUID> mostWantedPlayers = new ArrayList<>();
    protected List<Integer> mostWantedNpcs = new ArrayList<>();
    protected List<Bounty> bounties = new ArrayList<>();

    public Chapter() {
    }

    public void addStoryEvent(UUID player, StoryEvent event) {
        List<StoryEvent> events = getStoryEvents(player);
        events.add(event);
        storyEvents.put(player, events);
    }

    public List<StoryEvent> getStoryEvents(UUID player) {
        return storyEvents.getOrDefault(player, new ArrayList<>());
    }

    public List<UUID> getMostWantedPlayers() {
        return this.mostWantedPlayers;
    }

    public void addCriminalWithBounty(LivingEntity entity, @Nullable List<Pair<Supplier<Item>, Integer>> reward) {
        if (entity instanceof Player) this.mostWantedPlayers.add(entity.getUUID());
        else this.mostWantedNpcs.add(entity.getId());
        this.bounties.add(createBountyFor(entity, reward));
    }

    private static Bounty createBountyFor(LivingEntity entity, @Nullable List<Pair<Supplier<Item>, Integer>> reward) {
        Bounty bounty;

        if (entity instanceof ServerPlayer player) {
            bounty = new Bounty(player.getDisplayName().getString(), player.getUUID(), Event.Impact.HIGH, reward == null ? new ArrayList<>() : reward, Math.max(60, player.experienceLevel * 2));
        }
        else {
            bounty = new Bounty(entity.getDisplayName().getString(), entity.getId(), Event.Impact.AVERAGE, reward == null ? new ArrayList<>() : reward, 60);
        }

        return bounty;
    }

    public Map<UUID, List<StoryEvent>> getEventMap() {
        return storyEvents;
    }

    public CompoundTag save() {
        Map<UUID, List<StoryEvent>> map = getEventMap();
        List<UUID> uuids = new ArrayList<>(map.keySet());
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag storyEvents = new CompoundTag();
        for (int i=0; i<uuids.size(); i++) {
            ListTag listTag = new ListTag();
            List<StoryEvent> events = getStoryEvents(uuids.get(i));
            for (int s=0; s<events.size(); s++) {
                listTag.add(s, events.get(s).save());
            }
            storyEvents.putUUID("uuid" + i, uuids.get(i));
            storyEvents.put("p" + i, listTag);
        }
        compoundTag.put("storyEvents", storyEvents);

        CompoundTag mostWanted = new CompoundTag();
        for (int i = 0; i< mostWantedPlayers.size(); i++) {
            mostWanted.putUUID(Integer.toString(i), mostWantedPlayers.get(i));
        }
        compoundTag.put("criminalPlayers", mostWanted);

        CompoundTag wantedNpcs = new CompoundTag();
        for (int i = 0; i< mostWantedNpcs.size(); i++) {
            wantedNpcs.putInt(Integer.toString(i), mostWantedNpcs.get(i));
        }
        compoundTag.put("criminalNpcs", wantedNpcs);

        ListTag bounty = new ListTag();
        for (int i=0; i<this.bounties.size(); i++) {
            bounty.add(this.bounties.get(i).save());
        }
        compoundTag.put("bounties", bounty);

        return compoundTag;
    }

    public static Chapter read(CompoundTag tag) {
        Map<UUID, List<StoryEvent>> eventMap = new HashMap<>();
        CompoundTag storyEventTag = tag.getCompound("storyEvents");
        for (int i=0; i<storyEventTag.size(); i++) {
            List<StoryEvent> storyEvents = new ArrayList<>();
            ListTag listTag = storyEventTag.getList("p" + i, 9);
            for (int s=0; s<listTag.size(); s++) {
                storyEvents.add(s, StoryEvent.load(listTag.getCompound(s)));
            }
            UUID uuid = storyEventTag.getUUID("uuid" + i);
            eventMap.put(uuid, storyEvents);
        }

        List<UUID> criminals = new ArrayList<>();
        CompoundTag criminalTag = tag.getCompound("criminals");
        for (int i=0; i<criminalTag.size(); i++) {
            if (criminalTag.hasUUID(Integer.toString(i))) {
                criminals.add(criminalTag.getUUID(Integer.toString(i)));
            }
        }

        List<Integer> criminalNpcs = new ArrayList<>();
        CompoundTag npcTag = tag.getCompound("criminalNpcs");
        for (int i=0; i<npcTag.size(); i++) {
            criminalNpcs.add(tag.getInt(Integer.toString(i)));
        }

        List<Bounty> bounty = new ArrayList<>();
        ListTag bountyTag = tag.getList("bounties", 10);
        for (Tag tag1 : bountyTag) {
            bounty.add(Bounty.load((CompoundTag)tag1));
        }


        Chapter chapter = new Chapter();
        chapter.storyEvents = eventMap;
        chapter.mostWantedPlayers = criminals;
        chapter.mostWantedNpcs = criminalNpcs;
        chapter.bounties = bounty;
        return chapter;
    }

    public List<Bounty> getBounties() {
        return bounties;
    }
}
