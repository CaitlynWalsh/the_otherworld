package net.kitcaitie.otherworld.common.story;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.StoryEvent;
import net.kitcaitie.otherworld.common.story.global.GlobalStoryEvent;
import net.kitcaitie.otherworld.common.story.global.InvasionEvent;
import net.kitcaitie.otherworld.common.story.global.UndertakerSpawningEvent;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class Story {
    private final RandomSource random;
    private final String nameId;
    private Map<IWorlds.Worlds, Chapter> chapters;
    private List<WarEvent> warEvents = new ArrayList<>();
    private List<GlobalStoryEvent> globalStoryEvents = new ArrayList<>();
    private boolean changed;

    public Story(String name, boolean loaded) {
        this.nameId = name;
        this.random = RandomSource.create();
        if (loaded)
            this.chapters = new HashMap<>();
        else
            this.chapters = createChapters();
    }

    public void tick(MinecraftServer server) {
        if (server != null && server.getTickCount() > 1) {
            if (server.getTickCount() % 5 == 1) {
                tickEventList(globalStoryEvents, server);
                tickEventList(warEvents, server);
            }
        }
    }

    protected <T extends GlobalStoryEvent> void tickEventList(List<T> events, MinecraftServer server) {
        if (!events.isEmpty()) {
            for (T event : events) {
                if (event.isQueuedForRemoval() && event.canRemove(server)) {
                    removeGlobalEvent(event);
                    return;
                }
                if (event.isLoaded()) {
                    if (!event.hasStarted() && !event.disabled && (event.alwaysTicking() || random.nextFloat() <= Float.parseFloat(OtherworldConfigs.SERVER.storyEventChance.get())) && event.canStart(server)) {
                        event.start(server);
                        setChanged();
                    }
                    else if (event.hasStarted() && !event.disabled) {
                        event.onWorldTick(server);
                        event.updatePlayers(server);
                    }
                    else if (event.hasStarted() && event.disabled) {
                        event.stop(server);
                        setChanged();
                    }
                }
                else event.setLoaded(true);
            }
        }
    }

    public void setChanged() {
        this.changed = true;
    }

    protected void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public String getNameId() {
        return nameId;
    }

    public void addStoryEvent(IWorlds.Worlds world, UUID key, StoryEvent event) {
        this.getChapters().get(world).addStoryEvent(key, event);
        setChanged();
    }

    public void addCriminal(IWorlds.Worlds world, LivingEntity criminal, List<Pair<Supplier<Item>, Integer>> rewards) {
        this.getChapters().get(world).addCriminalWithBounty(criminal, rewards);
        setChanged();
    }

    public List<Bounty> getBounties(IWorlds.Worlds world) {
       return this.getChapters().get(world).bounties;
    }

    public List<UUID> getMostWantedPlayers(IWorlds.Worlds worlds) {
        return this.getChapters().get(worlds).mostWantedPlayers;
    }

    public List<Integer> getMostWantedNpcs(IWorlds.Worlds worlds) {
        return this.getChapters().get(worlds).mostWantedNpcs;
    }

    public Map<IWorlds.Worlds, Chapter> getChapters() {
        return chapters;
    }

    public void pushChapter(IWorlds.Worlds world, Chapter newChapter) {
        chapters.put(world, newChapter);
    }

    public void pushAllChapters(Map<IWorlds.Worlds, Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<WarEvent> getWarEvents() {
        return warEvents;
    }

    public List<WarEvent> getAllWarEvents() {
        List<WarEvent> list = new ArrayList<>(warEvents);
        this.globalStoryEvents.forEach((g) -> {
            if (g instanceof WarEvent) {
                list.add((WarEvent) g);
            }
        });
        return list;
    }

    protected void addWarEvent(int i, WarEvent warEvent) {
        warEvent.setStory(this);
        this.warEvents.add(i, warEvent);
        setChanged();
    }

    public void addWarEvent(WarEvent warEvent) {
        warEvent.setStory(this);
        this.warEvents.add(warEvent);
        setChanged();
    }

    public void removeWarEvent(WarEvent warEvent) {
        warEvents.remove(warEvent);
        warEvent.onRemove();
        setChanged();
    }

    @Nullable
    public WarEvent getNearbyActiveWar(BlockPos blockPos, int dist, @Nullable IRaces.Race defender, @Nullable IRaces.Race invader) {
        if (blockPos != null) {
            for (WarEvent warEvent1 : getAllWarEvents()) {
                if (warEvent1.hasActiveWar() || warEvent1.invasionTargetPos != null) {
                    if (defender == invader) {
                        if ((defender != null && warEvent1.defendingRace != defender) || (invader != null && !Arrays.asList(warEvent1.invader).contains(invader))) continue;
                    }
                    else {
                        if (defender != null && warEvent1.defendingRace != defender) continue;
                        if (invader != null && !Arrays.asList(warEvent1.invader).contains(invader)) continue;
                    }
                    if (warEvent1.invasionTargetPos.closerThan(blockPos, dist)) return warEvent1;
                }
            }
        }
        return null;
    }

    private static Map<IWorlds.Worlds, Chapter> createChapters() {
        Map<IWorlds.Worlds, Chapter> map = Map.of(
                IWorlds.Worlds.OVERWORLD, new Chapter(),
                IWorlds.Worlds.UNDERLANDS, new Chapter(),
                IWorlds.Worlds.ENCHANTIA, new Chapter(),
                IWorlds.Worlds.ROSEIA, new Chapter(),
                IWorlds.Worlds.DEEPWOODS, new Chapter(),
                IWorlds.Worlds.EMBERIA, new Chapter(),
                IWorlds.Worlds.GLACEIA, new Chapter()
        );
        return new HashMap<>(map);
    }

    public void save(CompoundTag tag) {
        tag.putString("story", getNameId());
        CompoundTag chapterTag = new CompoundTag();
        List<IWorlds.Worlds> worlds = new ArrayList<>(chapters.keySet());

        for (IWorlds.Worlds world : worlds) {
            Chapter chapter = chapters.get(world);
            chapterTag.put(world.name(), chapter.save());
        }

        saveEventList(tag, "wars", warEvents);

        ListTag listTag = new ListTag();
        for (int i=0; i<globalStoryEvents.size(); i++) {
            if (globalStoryEvents.get(i).shouldSave()) {
                listTag.add(globalStoryEvents.get(i).save());
            }
        }
        tag.put("globalEvents", listTag);

        tag.put("chapters", chapterTag);
    }

    public static Story read(CompoundTag tag) {
        Story story = new Story(tag.getString("story"), true);
        List<IWorlds.Worlds> worlds = List.of(IWorlds.Worlds.OVERWORLD, IWorlds.Worlds.UNDERLANDS, IWorlds.Worlds.ENCHANTIA, IWorlds.Worlds.ROSEIA, IWorlds.Worlds.DEEPWOODS, IWorlds.Worlds.EMBERIA, IWorlds.Worlds.GLACEIA);

        CompoundTag chapterTag = tag.getCompound("chapters");
        for (IWorlds.Worlds w : worlds) {
            story.pushChapter(w, Chapter.read(chapterTag.getCompound(w.name())));
        }

        CompoundTag warTag = tag.getCompound("wars");
        for (int i=0; i<warTag.size(); i++) {
            WarEvent warEvent = WarEvent.read(warTag.getCompound(String.valueOf(i)));
            warEvent.setStory(story);
            warEvent.setLoaded(true);
            story.addWarEvent(warEvent);
        }

        ListTag globalEvents = tag.getList("globalEvents", 10);
        for (int i=0; i<globalEvents.size(); i++) {
            GlobalStoryEvent event = loadGlobalEvent(globalEvents.getCompound(i));
            if (event != null) {
                event.setStory(story);
                event.setLoaded(true);
                story.addGlobalEvent(event);
            }
        }

        return story;
    }

    @Nullable
    private static GlobalStoryEvent loadGlobalEvent(CompoundTag tag) {
        if (tag.contains("id")) {
            String id = tag.getString("id");
            if (id.contains("invasion")) {
                return InvasionEvent.load(tag);
            }
            if (id.equals(UndertakerSpawningEvent.tag)) {
                return new UndertakerSpawningEvent();
            }
        }
        return null;
    }

    private void saveEventList(CompoundTag tag, String tagName, List<? extends GlobalStoryEvent> list) {
        CompoundTag eventTag = new CompoundTag();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).shouldSave()) {
                eventTag.put(String.valueOf(i), list.get(i).save());
            }
        }
        tag.put(tagName, eventTag);
    }

    public void addGlobalEvent(GlobalStoryEvent event) {
        this.globalStoryEvents.add(event);
        event.setStory(this);
        setChanged();
    }

    public void removeGlobalEvent(GlobalStoryEvent event) {
        this.globalStoryEvents.remove(event);
        event.onRemove();
        setChanged();
    }

    public boolean areRacesAtWar(IRaces.Race race1, IRaces.Race race2) {
        return warEvents.stream().anyMatch((event) -> !event.disabled && ((Arrays.asList(event.defender).contains(race1) || Arrays.asList(event.defender).contains(race2)) && (Arrays.asList(event.invader).contains(race1) || Arrays.asList(event.invader).contains(race2))));
    }

    public boolean areRacesAllied(IRaces.Race race1, IRaces.Race race2) {
        return warEvents.stream().anyMatch((event) -> !event.disabled && new HashSet<>(Arrays.asList(event.invader)).containsAll(List.of(race1, race2)));
    }

    public void removeCriminal(LivingEntity entity, ServerLevel level) {
        getChapters().forEach((worlds, chapter) -> {
            chapter.mostWantedPlayers.removeIf((uuid) -> uuid.equals(entity.getUUID()));
            chapter.mostWantedNpcs.removeIf((id) -> id == entity.getId());
            chapter.bounties.removeIf((bounty) -> bounty.getCriminalId() == entity.getId() || bounty.getCriminalUuid().equals(entity.getUUID()));
        });
        level.getPlayers((plr) -> PowerUtils.accessPlayerCharacter(plr).getCurrentBounty() != null).forEach((plr) -> {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(plr);
            Bounty bounty = character.getCurrentBounty();
            if (bounty.getCriminalId() == entity.getId()) {
                character.setCurrentBounty(null);
                character.sendPacket(plr);
            } else if (bounty.getCriminalUuid().equals(entity.getUUID())) {
                character.setCurrentBounty(null);
                character.sendPacket(plr);
            }
        });
    }
}
