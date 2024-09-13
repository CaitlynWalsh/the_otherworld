package net.kitcaitie.otherworld.common.player;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.EventHandler;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.kitcaitie.otherworld.common.story.global.InvasionEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.common.util.SpecialSpawner;
import net.kitcaitie.otherworld.network.NetworkMessages;
import net.kitcaitie.otherworld.network.c2s.AddQuestC2SPacket;
import net.kitcaitie.otherworld.network.c2s.UpdateCharacterC2SPacket;
import net.kitcaitie.otherworld.network.s2c.UpdateCharacterS2CPacket;
import net.kitcaitie.otherworld.registry.OtherworldEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCharacter implements IRaces, IOccupation {
    public static final int MAX_VARIANTS = 3;
    public static final boolean defaultMaleValue = true;
    public static final Race defaultRace = Race.HUMAN;
    public static final Occupation defaultOccupation = Occupation.TRAVELER;

    public final RandomSource random = RandomSource.create();
    private boolean male = defaultMaleValue;
    private Race race = defaultRace;
    private Occupation occupation = defaultOccupation;
    private int occupationStatus = 0;
    private BlockPos homePos = BlockPos.ZERO;
    private long spouseId = 0;
    private List<Long> childrenIds = new ArrayList<>();
    private List<String> completedQuests = new ArrayList<>();
    @Nullable
    private Bounty currentBounty = null;
    private List<Pair<Long, Quest>> quests = new ArrayList<>();
    private Map<Integer, ItemStack> questItems = new HashMap<>();
    public Map<Quest, Integer> questTimer = new HashMap<>();
    private Map<Quest, List<Pair<String, Integer>>> questProgress = new HashMap<>();
    private int texture = -1;
    private boolean created = false;
    private boolean teleported = false;
    private boolean unlockedMaleQuests = this.male;
    public CompoundTag spouseTag = new CompoundTag();
    public List<CompoundTag> kidTags = new ArrayList<>();
    private boolean imprisoned = false;
    private boolean shouldImprison = false;
    private int prisonTimer = -1;
    public ListTag fakeInventoryTag = new ListTag();
    @Nullable
    public InvasionEvent involvedInvasion;
    @Nullable
    public Pair<String, Boolean> warData;
    public boolean ghoulTarget = false;

    public static PlayerCharacter load(CompoundTag playerCharacterTag) {
        PlayerCharacter character = new PlayerCharacter();
        character.loadNBT(playerCharacterTag);
        return character;
    }

    public boolean isMarried() {
        return this.getSpouseId() != 0;
    }


    public List<Long> getChildren() {
        return this.childrenIds;
    }

    public long getSpouseId() {
        return spouseId;
    }

    private void setSpouseId(long spouseId) {
        this.spouseId = spouseId;
    }

    public void setSpouse(AbstractPerson person) {
        this.setSpouseId(person.getIdentity());
        this.spouseTag = createStoredTag(person);
    }

    public static CompoundTag createStoredTag(AbstractPerson person) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", person.getEncodeId());
        tag.putBoolean("Baby", person.isBaby());
        tag.putString("Name", person.getDisplayName().getString());
        person.writeData(tag);
        tag.remove("SpawnType");
        tag.remove("PersonData");
        tag.remove("Inventory");
        tag.remove("HomePos");
        tag.remove("DespawnTime");
        tag.remove("HungerLevel");
        tag.remove("HungerTick");
        tag.remove("Saturation");
        tag.remove("Exhaustion");
        tag.remove("WorkPos");
        tag.remove("Trades");
        tag.remove("LastRestock");
        tag.remove("RestocksToday");
        tag.remove("Experience");
        tag.remove("VillageChanged");
        return tag;
    }

    public void setPrisoner(ServerPlayer player, ServerLevel level, boolean inJail, boolean warPrisoner) {
        if (inJail) {
            FakeInventory.saveAndLoadPrevious(player.getInventory(), this.fakeInventoryTag);
            player.getInventory().clearContent();
            player.resetFallDistance();
            player.setInvulnerable(false);
            player.setHealth(player.getMaxHealth());
            this.shouldImprison = false;
            this.imprisoned = true;
            if (!warPrisoner) {
                this.prisonTimer = (this.getOccupationStatus() * 2) + 5;
                player.sendSystemMessage(Component.literal(Component.translatable("event.otherworld.imprisoned").getString().replace("#", Integer.toString(prisonTimer))).withStyle(ChatFormatting.RED), true);
            }
        }
        else {
            this.shouldImprison = true;
            this.imprisoned = false;
            player.setInvulnerable(true);
            level.getServer().execute(() -> {
                OtherworldEvents.Triggers.TRAVEL_TO_STRUCTURE.of(SpecialSpawner.getOutpostType(player, this)).trigger(player, null, null);
            });
        }
    }

    public void removePrisonStatus() {
        this.shouldImprison = false;
    }

    public void setImprisoned(boolean imprisoned) {
        this.imprisoned = imprisoned;
    }

    public int getPrisonTime() {
        return this.prisonTimer;
    }

    public void setPrisonTime(int time) {
        this.prisonTimer = time;
    }

    public void removeSpouse() {
        this.spouseId = 0;
        this.spouseTag = new CompoundTag();
    }

    public void addChild(AbstractPerson person) {
        this.childrenIds.add(person.getIdentity());
        this.kidTags.add(createStoredTag(person));
    }

    public void removeChild(AbstractPerson child) {
        this.removeChild(child.getIdentity());
    }

    public void removeChild(long id) {
        if (this.childrenIds.contains(id)) {
            this.kidTags.remove(this.childrenIds.indexOf(id));
            this.childrenIds.remove(id);
        }
    }

    @Override
    public boolean isImprisoned() {
        return !this.shouldImprison && this.imprisoned;
    }

    @Override
    public Race getRace() {
        return race;
    }

    public boolean isMale() {
        return male;
    }

    public boolean unlockedMaleQuests() {
        return unlockedMaleQuests;
    }

    @Override
    public Occupation getOccupation() {
        return occupation;
    }

    @Override
    public int getOccupationStatus() {
        return occupationStatus;
    }

    public void addOccupationStatus(Player player) {
        occupationStatus = Math.min(occupationStatus + 1, getOccupation().getMaxStatus());
        if (occupationStatus == getOccupation().getMaxStatus()) {
            player.sendSystemMessage(Component.translatable("occupation.otherworld.max_status." + getOccupation().name().toLowerCase()).withStyle(ChatFormatting.ITALIC, ChatFormatting.BOLD, isCriminal() ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN));
        }
        else player.sendSystemMessage(Component.translatable("occupation.otherworld.add_status." + getOccupation().name().toLowerCase()).withStyle(ChatFormatting.ITALIC, isCriminal() ? ChatFormatting.RED : ChatFormatting.GREEN));
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void setMale(boolean male) {
        this.male = male;
        this.unlockedMaleQuests = male;
    }

    public void resetQuests() {
        this.quests.clear();
        this.questProgress.clear();
        this.questTimer.clear();
    }

    public void setOccupation(Occupation occupation, int status) {
        boolean flag = this.occupation != occupation;
        this.occupation = occupation;
        this.occupationStatus = Math.min(status, occupation.getMaxStatus());
        if (flag) {
            if (occupation == Occupation.SOLDIER && !isMale()) {
                this.unlockedMaleQuests = true;
            }
            resetQuests();
        }
    }

    public void setOccupation(Occupation occupation) {
        this.setOccupation(occupation, 0);
    }

    public BlockPos getHomePos() {
        return homePos;
    }

    public void setHomePos(BlockPos home) {
        this.homePos = home;
    }

    public int getTextureId() {
        return texture;
    }

    public void setTextureId(int tex) {
        if (this.canHaveVariant()) this.texture = tex; // TODO: ERASE THE canHaveVariant AFTER MAKING MORE VARIANTS
        else this.texture = 0;
    }
    public boolean wasCreated() {
        return this.created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean startedInVillage() {
        return this.teleported;
    }
    public void setStarted(boolean started) {
        this.teleported = started;
    }

    public void tick(ServerPlayer player) {
        if (player.tickCount > 1) {

            this.handleWars();

            if (player.tickCount % 1200 == 1 ) {
                boolean changed = false;

                if (this.prisonTimer > 0) {
                    --this.prisonTimer;
                    player.displayClientMessage(Component.literal(Component.translatable("event.otherworld.prison_timer").getString().replace("#", Integer.toString(prisonTimer))).withStyle(ChatFormatting.AQUA), true);
                    changed = true;
                }

                if (this.incrementQuestTimer(player, 1)) {
                    changed = true;
                }

                if (changed) this.sendPacket(player);
            }
        }
    }

    private void handleWars() {
        if (involvedInvasion != null && !involvedInvasion.hasActiveWar() && involvedInvasion.wasLost != involvedInvasion.victorious) {
            this.warData = Pair.of(involvedInvasion.getID(), involvedInvasion.victorious);
            this.involvedInvasion = null;
        }
    }

    @Nullable
    public InvasionEvent getInvolvedInvasion() {
        return involvedInvasion;
    }

    public void setInvolvedInvasion(@Nullable InvasionEvent event) {
        this.involvedInvasion = event;
    }

    @Nullable
    public Bounty getCurrentBounty() {
        return this.currentBounty;
    }

    public void setCurrentBounty(@Nullable Bounty bounty) {
        this.currentBounty = bounty;
    }

    @Nullable
    public static AbstractPerson getFamilyMember(ServerPlayer player, long id) {
        AbstractPerson person;
        for (Entity ent : player.getPassengers()) {
            if (ent instanceof AbstractPerson person1 && person1.getIdentity() == id) return person1;
        }
        List<? extends AbstractPerson> list = player.getLevel().getEntities(EntityTypeTest.forClass(AbstractPerson.class), (e) -> e.getIdentity() == id);
        if (!list.isEmpty()) {
            person = list.get(0);
            return person;
        }
        return null;
    }

    private boolean incrementQuestTimer(Player player, int amount) {
        boolean flag = false;
        for (Quest quest : questTimer.keySet()) {
            if (isQuestCompleted(quest)) continue;

            int i = questTimer.get(quest) - amount;
            if (i <= 0 && !isQuestCompleted(quest)) {
                removeQuest(quest);
                if (!player.level.isClientSide()) {
                    this.sendPacket(player);
                }
                player.sendSystemMessage(Component.literal(Component.translatable("quests.otherworld." + quest.getID()).getString() + Component.translatable("quests.otherworld.out_of_time").getString()).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
                continue;
            }

            questTimer.put(quest, i);
            flag = true;
        }
        return flag;
    }

    public boolean isQuestCompleted(Quest quest) {
        List<Pair<String, Integer>> required = quest.getRequirements().getAmount();
        int i = 0;
        for (Pair<String, Integer> pair : getQuestProgress(quest)) {
            if (required.stream().filter(((p) -> p.getFirst().equals(pair.getFirst()))).anyMatch((p) -> pair.getSecond() >= p.getSecond())) i++;
        }
        return i >= required.size();
    }

    public boolean addQuest(AbstractPerson person, Quest quest) {
        return addQuest(person == null ? 0 : person.getIdentity(), person == null ? -1 : person.getId(), quest);
    }

    public boolean addQuest(long identity, int id, Quest quest) {
        if (!completedQuests.contains(quest.getID()) && this.quests.size() < 2) {
            if (this.quests.stream().noneMatch((p) -> p.getSecond().getID().equals(quest.getID()))) {
                this.quests.add(Pair.of(identity, quest));
                List<Pair<String, Integer>> list = new ArrayList<>();
                for (Pair<String, Integer> pair : quest.getRequirements().getAmount()) {
                    list.add(Pair.of(pair.getFirst(), 0));
                }
                this.questProgress.put(quest, list);
                this.questTimer.put(quest, (quest.getTime() / 60) / 20);
                NetworkMessages.sendToServer(new AddQuestC2SPacket(id, quest));
                return true;
            }
        }
        return false;
    }

    public void addQuestProgress(Quest quest, int progress, String obj) {
        List<Pair<String, Integer>> list = this.questProgress.getOrDefault(quest, null);
        if (list != null) {
            list.stream().filter((p) -> obj.equals(p.getFirst())).findFirst().ifPresent((p) -> {
                list.remove(p);
                Pair<?, Integer> pair = quest.getRequirements().getAmount().stream().filter((t) -> t.getFirst().equals(obj)).findFirst().orElse(null);
                if (pair != null) {
                    p = Pair.of(obj, Integer.min(p.getSecond() + progress, pair.getSecond()));
                }
                list.add(p);
                this.questProgress.put(quest, list);
            });
        }
    }

    public void setQuestProgress(Quest quest, int progress, String obj) {
        List<Pair<String, Integer>> list = this.questProgress.getOrDefault(quest, null);
        if (list != null) {
            list.stream().filter((p) -> obj.equals(p.getFirst())).findFirst().ifPresent((p) -> {
                list.remove(p);
                Pair<?, Integer> pair = quest.getRequirements().getAmount().stream().filter((t) -> t.getFirst().equals(obj)).findFirst().orElse(null);
                if (pair != null) {
                    p = Pair.of(obj, Integer.min(progress, pair.getSecond()));
                }
                list.add(p);
                this.questProgress.put(quest, list);
            });
        }
    }

    public List<Pair<String, Integer>> getQuestProgress(Quest quest) {
        return getQuestProgress().getOrDefault(quest, new ArrayList<>());
    }

    public List<Pair<Long, Quest>> getQuests() {
        return quests;
    }

    public void removeQuest(Quest quest) {
        this.quests.stream().filter((p) -> p.getSecond().getID().equals(quest.getID())).findFirst().ifPresent(pair -> this.quests.remove(pair));
        this.questProgress.remove(quest);
        this.questTimer.remove(quest);
    }

    public boolean hasQuest(Quest quest) {
        return this.quests.stream().anyMatch((p) -> p.getSecond() != null && p.getSecond().getID().equals(quest.getID()));
    }

    public boolean canHaveQuest(Quest quest) {
        return !this.completedQuests.contains(quest.getID()) && (quest.getRequirements().getRequiredQuest() == null || this.completedQuests.stream().anyMatch((s) -> quest.getRequirements().getRequiredQuest().getID().equals(s)));
    }

    public boolean canHaveQuest(Quest.Context context) {
        if (context.maleOnly() && !this.isMale() && !this.unlockedMaleQuests) return false;
        return !(context.femaleOnly() && this.isMale());
    }

    public Quest assignedQuest(AbstractPerson person, Player player, Quest.Context context) {
        Pair<Long, Quest> pair = this.quests.stream().filter((p) -> p.getFirst() == person.getIdentity() || (p.getFirst() == 0 && person.canAssignQuest(context, player, true))).findFirst().orElse(null);
        if (pair != null) {
            return pair.getSecond();
        }
        return null;
    }

    public boolean receivedQuestFrom(AbstractPerson person) {
        return this.quests.stream().anyMatch((p) -> p.getFirst() == person.getIdentity());
    }

    public boolean areQuestsFull() {
        return this.quests.size() >= 2;
    }

    public void completeQuest(Quest quest) {
        this.quests.stream().filter((p) -> p.getSecond().getID().equals(quest.getID())).findFirst().ifPresent(pair -> {
            this.quests.remove(pair);
            this.completedQuests.add(pair.getSecond().getID());
            this.questTimer.remove(pair.getSecond());
        });
    }

    public long getQuestAssignerID(Quest quest) {
        Pair<Long, Quest> pair = this.quests.stream().filter((p) -> p.getSecond().getID().equals(quest.getID())).findFirst().orElse(null);
        if (pair != null) {
            return pair.getFirst();
        }
        return 0;
    }

    public void setQuestAssignerID(long id, Quest quest) {
        Pair<Long, Quest> pair = this.quests.stream().filter((p) -> p.getSecond().getID().equals(quest.getID())).findFirst().orElse(null);
        if (pair != null) {
            this.quests.remove(pair);
            pair = Pair.of(id, pair.getSecond());
            this.quests.add(pair);
        }
    }

    public Map<Quest, List<Pair<String, Integer>>> getQuestProgress() {
        return questProgress;
    }

    public Map<Integer, ItemStack> getQuestItems() {
        return this.questItems;
    }

    public void copyFrom(PlayerCharacter character) {
        this.male = character.male;
        this.race = character.race;
        this.occupation = character.occupation;
        this.occupationStatus = character.occupationStatus;
        this.homePos = character.homePos;
        this.spouseId = character.spouseId;
        this.childrenIds = character.childrenIds;
        this.quests = character.quests;
        this.questItems = character.questItems;
        this.questProgress = character.questProgress;
        this.questTimer = character.questTimer;
        this.completedQuests = character.completedQuests;
        this.currentBounty = character.currentBounty;
        this.unlockedMaleQuests = character.unlockedMaleQuests;
        this.texture = character.texture;
        this.created = character.created;
        this.spouseTag = character.spouseTag;
        this.kidTags = character.kidTags;
        this.shouldImprison = character.shouldImprison;
        this.imprisoned = character.imprisoned;
        this.prisonTimer = character.prisonTimer;
        this.involvedInvasion = character.involvedInvasion;
        this.warData = character.warData;
        this.ghoulTarget = character.ghoulTarget;
    }

    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Male", male);
        tag.putString("Race", race.name());
        tag.putString("Occupation", occupation.name());
        tag.putInt("OccupationStatus", occupationStatus);
        tag.putLong("HomePos", homePos.asLong());
        tag.putLong("SpouseID", spouseId);
        tag.put("SpouseTag", spouseTag);
        tag.putLongArray("ChildrenIDs", this.childrenIds);

        ListTag listTag = new ListTag();
        listTag.addAll(kidTags);
        tag.put("ChildrenTags", listTag);

        CompoundTag tag1 = new CompoundTag();
        for (int i = 0; i<this.quests.size(); i++) {
            Pair<Long, Quest> pair = this.quests.get(i);
            if (pair != null && pair.getSecond() != null) {
                tag1.put(Integer.toString(i), pair.getSecond().save(pair.getFirst(), this.questProgress.getOrDefault(pair.getSecond(), new ArrayList<>())));
            }
        }
        tag.put("Quests", tag1);
        CompoundTag tag2 = new CompoundTag();
        for (int i=0; i<completedQuests.size(); i++) {
            tag2.putString(Integer.toString(i), completedQuests.get(i));
        }
        tag.put("CompletedQuests", tag2);
        
        ListTag items = new ListTag();
        List<Integer> slots = new ArrayList<>(questItems.keySet());
        for (int i=0; i<questItems.size(); i++) {
            CompoundTag item = new CompoundTag();
            item.putInt("slot", slots.get(i));
            item.put("item", questItems.get(slots.get(i)).save(new CompoundTag()));
            items.add(item);
        }
        tag.put("QuestItems", items);

        CompoundTag tag3 = new CompoundTag();
        List<Quest> quests = new ArrayList<>(questTimer.keySet());
        for (int i=0; i<quests.size(); i++) {
            Quest quest = quests.get(i);
            CompoundTag timeTag = new CompoundTag();
            timeTag.putString("id", quest.getID());
            timeTag.putInt("time", questTimer.get(quest));
            tag3.put(Integer.toString(i), timeTag);
        }
        tag.put("QuestTimer", tag3);

        if (this.currentBounty != null) {
            tag.put("Bounty", currentBounty.save());
        }

        tag.putInt("Texture", texture);
        tag.putBoolean("Created", created);
        tag.putBoolean("Started", teleported);
        tag.putBoolean("UnlockedMaleQuests", unlockedMaleQuests);

        tag.putBoolean("Imprisoned", imprisoned);
        tag.putBoolean("ShouldImprison", shouldImprison);
        tag.putInt("PrisonTime", prisonTimer);
        tag.put("PreviousInv", fakeInventoryTag);

        if (this.warData != null) {
            tag.putString("warData1", warData.getFirst());
            tag.putBoolean("warData2", warData.getSecond());
        }

        tag.putBoolean("GhoulTarget", ghoulTarget);

        return tag;
    }

    public void loadNBT(CompoundTag data) {
        if (data != null) {
            setMale(data.getBoolean("Male"));
            setRace(Race.valueOf(data.getString("Race")));
            setOccupation(Occupation.valueOf(data.getString("Occupation")), data.getInt("OccupationStatus"));
            setHomePos(BlockPos.of(data.getLong("HomePos")));
            setSpouseId(data.getLong("SpouseID"));
            this.childrenIds = new ArrayList<>();
            for (Long id : data.getLongArray("ChildrenIDs")) {
                this.childrenIds.add(id);
            }

            this.spouseTag = data.getCompound("SpouseTag");
            data.getList("ChildrenTags", 10).forEach((tag -> this.kidTags.add((CompoundTag) tag)));

            CompoundTag quests = data.getCompound("Quests");
            for (int i=0; i<quests.size(); i++) {
                List<Pair<String, Integer>> list = new ArrayList<>();
                CompoundTag tag = quests.getCompound(Integer.toString(i));
                Quest quest = Quest.load(tag);
                this.quests.add(Pair.of(tag.getLong("assigner"), quest));
                ListTag tag1 = tag.getList("progress", 10);
                for (int j=0; j<tag1.size(); j++) {
                    CompoundTag tag2 = tag1.getCompound(j);
                    list.add(Pair.of(tag2.getString("obj"), tag2.getInt("amount")));
                }
                this.questProgress.put(quest, list);
            }
            CompoundTag completed = data.getCompound("CompletedQuests");
            for (int i=0; i<completed.size(); i++) {
                this.completedQuests.add(completed.getString(Integer.toString(i)));
            }
            
            ListTag items = data.getList("QuestItems", 10);
            for (int i=0; i<items.size(); i++) {
                CompoundTag item = items.getCompound(i);
                this.questItems.put(item.getInt("slot"), ItemStack.of(item.getCompound("item")));
            }

            CompoundTag timer = data.getCompound("QuestTimer");
            for (int i=0; i<timer.size(); i++) {
                CompoundTag tag1 = timer.getCompound(Integer.toString(i));
                Quest quest = EventHandler.getQuest(tag1.getString("id"));
                if (quest == null) {
                    Otherworld.LOGGER.error(this + ": Could not load quest " + tag1.getString("id") + ": null.");
                    continue;
                }
                this.questTimer.put(quest, tag1.getInt("time"));
            }

            if (data.contains("Bounty")) {
                this.currentBounty = Bounty.load(data.getCompound("Bounty"));
            }

            setTextureId(data.getInt("Texture"));
            setCreated(data.getBoolean("Created"));
            setStarted(data.getBoolean("Started"));

            if (data.contains("warData1")) {
                this.warData = Pair.of(data.getString("warData1"), data.getBoolean("warData2"));
            }

            this.unlockedMaleQuests = data.getBoolean("UnlockedMaleQuests");
            this.imprisoned = data.getBoolean("Imprisoned");
            this.shouldImprison = data.getBoolean("ShouldImprison");
            this.prisonTimer = data.getInt("PrisonTime");
            this.fakeInventoryTag = data.getList("PreviousInv", 10);
            this.ghoulTarget = data.getBoolean("GhoulTarget");
        }
    }

    public void sync(Player player) {
        if (this.texture == -1) this.setTextureId(random.nextInt(MAX_VARIANTS));
        PowerUtils.createToughAttributes(this, player);
        ((IOtherworldPlayer)player).setPlayerCharacter(this);
    }

    public void sendPacket(@Nullable Player player) {
        if (player instanceof ServerPlayer) {
            syncClient((ServerPlayer) player);
        }
        else syncServer();
    }

    protected void syncClient(ServerPlayer player) {
        this.sync(player);
        this.updateFamilyTags(player);
        NetworkMessages.sendToPlayer(new UpdateCharacterS2CPacket(this), player);
        //Otherworld.LOGGER.debug("Sending PlayerCharacter Packet to Client: " + this);
    }

    protected void syncServer() {
        NetworkMessages.sendToServer(new UpdateCharacterC2SPacket(this));
        //Otherworld.LOGGER.debug("Sending PlayerCharacter Packet to Server: " + this);
    }

    public void updateFamilyTags(ServerPlayer player) {
        if (this.isMarried()) {
            AbstractPerson spouse = getFamilyMember(player, getSpouseId());
            if (spouse != null) {
                this.spouseTag = createStoredTag(spouse);
            }
        }
        if (!this.getChildren().isEmpty()) {
            this.getChildren().forEach((id) -> {
                AbstractPerson child = getFamilyMember(player, id);
                if (child != null) {
                    this.kidTags.set(getChildren().indexOf(id), createStoredTag(child));
                }
            });
        }
    }

    public boolean canBeReleasedFromJail() {
        return prisonTimer == 0;
    }

    public void releaseFromJail(ServerPlayer player) {
        this.setOccupation(Occupation.TRAVELER);
        Otherworld.getStoryline(player.getLevel()).getStory().removeCriminal(player, player.getLevel());
        this.prisonTimer = -1;
        this.imprisoned = false;
    }

    @Override
    public String toString() {
        return "PlayerCharacter {\nRace:" + this.race + ",\nMale:" + this.male + ",\nJob:" + this.occupation + ",\nJobStatus:" + this.occupationStatus + ",\nHomePos:" + this.homePos + ",\nSpouseID:" + this.spouseId + ",\nChildrenIDs:" + this.childrenIds + ",\nQuests:" + this.quests + ",\nQuestItems:" + this.questItems + ",\nQuestProgress:" + this.questProgress + ",\nQuestTimer:" + this.questTimer + ",\nCompletedQuests:" + this.completedQuests + ",\nCurrentBounty:" + this.currentBounty + ",\nUnlockedMaleQuests:" + this.unlockedMaleQuests + ",\nTexture:" + this.texture + ",\nCreated:" + this.created + ",\nSpouseTag:" + this.spouseTag + ",\nKidTags:" + this.kidTags + ",\nShouldImprison:" + this.shouldImprison + ",\nImprisoned:" + this.imprisoned + ",\nPrisonTimer:" + prisonTimer + "\n}";
    }
}
