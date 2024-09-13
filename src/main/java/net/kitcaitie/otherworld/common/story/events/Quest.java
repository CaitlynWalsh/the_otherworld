package net.kitcaitie.otherworld.common.story.events;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Quest implements Event {
    protected Type type;
    protected Impact impact;
    protected final String event;
    protected int time = 144000;
    protected EventTrigger[] triggerOnFinish;
    public EventTrigger[] triggerOnAdded;
    protected List<Pair<Supplier<Item>, Integer>> rewardItems;
    protected int experience = 0;
    protected Requirement requirement;

    protected Quest(String event, Type type, Impact impact) {
        this.event = event;
        this.type = type;
        this.impact = impact;
    }

    public Component getDisplayName() {
        return Component.translatable("quests.otherworld." + getID());
    }

    public Type getType() {
        return type;
    }

    public Impact getImpact() {
        return impact;
    }

    public String getID() {
        return event;
    }

    public Requirement getRequirements() {
        return requirement;
    }

    public CompoundTag save(long id, List<Pair<String, Integer>> progress) {
        CompoundTag tag = new CompoundTag();
        tag.putString("event", this.getID());
        ListTag tag1 = new ListTag();
        for (int i=0; i<progress.size(); i++) {
            CompoundTag tag2 = new CompoundTag();
            Pair<String, Integer> pair = progress.get(i);
            tag2.putString("obj", pair.getFirst());
            tag2.putInt("amount", pair.getSecond());
            tag1.add(tag2);
        }
        tag.put("progress", tag1);
        tag.putLong("assigner", id);
        return tag;
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("event", this.getID());
        return tag;
    }

    public Quest copy() {
        Quest quest1 = new Quest(this.event, this.type, this.impact);
        quest1.requirement = this.requirement;
        quest1.rewardItems = this.rewardItems;
        quest1.experience = this.experience;
        quest1.triggerOnAdded = this.triggerOnAdded;
        quest1.triggerOnFinish = this.triggerOnFinish;
        return quest1;
    }

    public void start(Player player, @Nullable AbstractPerson person) {
        player.level.playSound(null, player.blockPosition(), OtherworldSounds.QUEST_ASSIGN.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
        if (this.triggerOnAdded != null) {
            for (EventTrigger trigger : triggerOnAdded) {
                trigger.trigger(player, person, this);
            }
        }
    }

    public void complete(Player player, @Nullable AbstractPerson person) {
        PowerUtils.accessPlayerCharacter(player).completeQuest(this);
        if (this.rewardItems != null && !this.rewardItems.isEmpty()) {
            for (Pair<Supplier<Item>, Integer> pair : rewardItems) {
                ItemStack stack = new ItemStack(pair.getFirst().get(), pair.getSecond());
                if (!player.addItem(stack) && !player.level.isClientSide() && player.level instanceof ServerLevel level) {
                    ItemEntity entity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
                    entity.setTarget(player.getUUID());
                    entity.setPickUpDelay(0);
                    level.addFreshEntity(entity);
                }
            }
        }
        if (experience > 0) {
            player.giveExperiencePoints(this.experience);
        }
        if (triggerOnFinish != null) {
            for (EventTrigger trigger : triggerOnFinish) {
                trigger.trigger(player, person, this);
            }
        }
    }

    public static Quest load(CompoundTag tag) {
        return EventHandler.getQuest(tag.getString("event"));
    }

    public int getTime() {
        return time;
    }

    public static class Builder {
        private Quest quest;

        private Builder() {
        }

        public static Quest.Builder of(String event) {
            Builder builder = new Quest.Builder();
            builder.quest = new Quest(event, Type.NEUTRAL, Impact.NONE);
            return builder;
        }

        public Builder reward(List<Pair<Supplier<Item>, Integer>> items) {
            this.quest.rewardItems = items;
            return this;
        }

        public Builder rewardXP(int xp) {
            this.quest.experience = xp;
            return this;
        }


        public Builder type(Type type) {
            this.quest.type = type;
            return this;
        }

        public Builder time(int time) {
            this.quest.time = time;
            return this;
        }

        public Builder impact(Impact impact) {
            this.quest.impact = impact;
            return this;
        }

        public Builder afterTrigger(EventTrigger[] trigger) {
            this.quest.triggerOnFinish = trigger;
            return this;
        }

        public Builder onTrigger(EventTrigger[] trigger) {
            this.quest.triggerOnAdded = trigger;
            return this;
        }

        public Builder requirements(Requirement requirement) {
            this.quest.requirement = requirement;
            return this;
        }

        public static Quest.Builder copy(String event, Quest quest) {
            Builder builder = Builder.of(event);
            builder.quest.time = quest.time;
            builder.quest.requirement = quest.requirement;
            builder.quest.type = quest.type;
            builder.quest.impact = quest.impact;
            builder.quest.rewardItems = quest.rewardItems;
            builder.quest.triggerOnAdded = quest.triggerOnAdded;
            builder.quest.triggerOnFinish = quest.triggerOnFinish;
            builder.quest.experience = quest.experience;
            return builder;
        }

        public Quest build() {
            return quest;
        }

    }

    public static class Context {
        IRaces.Race assignRace;
        IOccupation.Occupation assignOccupation;
        IOccupation.VillagerType assignVillagerType;

        IRaces.Race playerRace;
        IOccupation.Occupation playerOccupation;
        int playerOccupationStatus = 0;
        WarEvent war;
        int male = -1;

        private Context() {
        }

        public static Context of(IRaces.Race assignRace, IOccupation.Occupation assignOccupation, IRaces.Race playerRace, IOccupation.Occupation playerOccupation) {
            Context context = new Context();
            context.assignRace = assignRace;
            context.assignOccupation = assignOccupation;
            context.playerRace = playerRace;
            context.playerOccupation = playerOccupation;
            return context;
        }

        public static Context of(IRaces.Race assignRace, IOccupation.VillagerType assignOccupation, IRaces.Race playerRace, IOccupation.Occupation playerOccupation) {
            Context context = new Context();
            context.assignRace = assignRace;
            context.assignVillagerType = assignOccupation;
            context.playerRace = playerRace;
            context.playerOccupation = playerOccupation;
            return context;
        }

        public Context war(WarEvent warEvent) {
            this.war = warEvent;
            return this;
        }

        public Context gender(boolean male) {
            if (male) {
                this.male = 1;
                return this;
            }
            this.male = 0;
            return this;
        }

        public Context occupationStatus(int occupationStatus) {
            this.playerOccupationStatus = occupationStatus;
            return this;
        }

        public static Context context(Player player, AbstractPerson person, int gender) {
            MinecraftServer server = player.getServer();
            return context(player, person, null, gender, server == null ? null : Otherworld.getStoryline(player.getServer().overworld()).getStory().getWarEvents().stream().filter((w) -> Arrays.asList(w.defender).contains(person.getRace()) || Arrays.asList(w.invader).contains(person.getRace())).findAny().orElse(null));
        }

        public static Context context(Player player, AbstractPerson person, @Nullable IOccupation.VillagerType villagerType, int gender, @Nullable WarEvent war) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            Context context;

            if (person.isVillager() && villagerType != null) {
                context = Context.of(person.getRace(), person.getJobType(), character.getRace(), character.getOccupation());
            }
            else {
                context = Context.of(person.getRace(), person.getOccupation(), character.getRace(), character.getOccupation());
            }

            if (gender == 0) {
                context.gender(false);
            }

            if (gender == 1) {
                context.gender(true);
            }

            context.occupationStatus(character.getOccupationStatus());

            context.war(war);

            return context;
        }

        public String id() {
            return (this.assignRace + "_" + this.assignOccupation + "_" + this.assignVillagerType + "_" + this.playerRace + "_" + this.playerOccupation + (this.war == null ? "" : "_" + this.war.getID()) + "_" + this.male).toLowerCase();
        }

        public boolean equals(Context context, boolean ignoreGender) {
            if (ignoreGender) {
                return context.id().contains(this.assignRace + "_" + this.assignOccupation + "_" + this.assignVillagerType + "_" + this.playerRace + "_" + this.playerOccupation + (this.war == null ? "" : "_" + this.war.getID()));
            }
            return context.id().equals(id());
        }

        public boolean maleOnly() {
            return male == 1;
        }

        public boolean femaleOnly() {
            return male == 0;
        }

        @Override
        public String toString() {
            return this.id();
        }
    }

    public static class Requirement {
        List<Pair<Supplier<Item>, Integer>> requiredItems;
        List<Pair<Supplier<EntityType<?>>, Integer>> targetEntities;
        List<Pair<Supplier<EntityType<?>>, Integer>> interactEntities;
        TagKey<Biome> requiredBiome;
        TagKey<Structure> requiredStructure;
        IOccupation.Occupation occupation;
        IOccupation.VillagerType villagerType;
        IRaces.Race race;
        Quest requiredQuest;

        public static Requirement combat(List<Pair<Supplier<EntityType<?>>, Integer>> targets) {
            Requirement requirement1 = new Requirement();
            requirement1.targetEntities = targets;
            return requirement1;
        }

        public static Requirement travelToStructure(TagKey<Structure> structureResourceKey) {
            Requirement requirement1 = new Requirement();
            requirement1.requiredStructure = structureResourceKey;
            return requirement1;
        }

        public static Requirement travelToBiome(TagKey<Biome> biomeResourceKey) {
            Requirement requirement1 = new Requirement();
            requirement1.requiredBiome = biomeResourceKey;
            return requirement1;
        }

        public static Requirement interact(List<Pair<Supplier<EntityType<?>>, Integer>> interact) {
            Requirement requirement1 = new Requirement();
            requirement1.interactEntities = interact;
            return requirement1;
        }

        public static Requirement obtainItem(List<Pair<Supplier<Item>, Integer>> targetItems) {
            Requirement requirement1 = new Requirement();
            requirement1.requiredItems = targetItems;
            return requirement1;
        }

        public Requirement occupation(IOccupation.Occupation occupation) {
            this.occupation = occupation;
            return this;
        }

        public Requirement villager(IOccupation.VillagerType villagerType) {
            this.occupation = IOccupation.Occupation.VILLAGER;
            this.villagerType = villagerType;
            return this;
        }

        public Requirement race(IRaces.Race race) {
            this.race = race;
            return this;
        }

        public Requirement quest(Quest event) {
            this.requiredQuest = event;
            return this;
        }

        public List<Pair<String, Integer>> getAmount() {
            List<Pair<String, Integer>> amount = new ArrayList<>();
            if (this.requiredItems != null) {
                for (Pair<Supplier<Item>, Integer> pair : requiredItems) {
                    amount.add(Pair.of(pair.getFirst().get().toString(), pair.getSecond()));
                }
            }
            if (this.targetEntities != null) {
                for (Pair<Supplier<EntityType<?>>, Integer> pair : targetEntities) {
                    amount.add(Pair.of(pair.getFirst().get().toString(), pair.getSecond()));
                }
            }
            if (this.interactEntities != null) {
                for (Pair<Supplier<EntityType<?>>, Integer> pair : interactEntities) {
                    amount.add(Pair.of(pair.getFirst().get().toString(), pair.getSecond()));
                }
            }
            if (this.requiredBiome != null) {
                amount.add(Pair.of(this.requiredBiome.location().toString(), 1));
            }
            if (this.requiredStructure != null) {
                amount.add(Pair.of(this.requiredStructure.location().toString(), 1));
            }
            return amount;
        }

        @Nullable
        public IOccupation.Occupation getOccupation() {
            return occupation;
        }

        @Nullable
        public IRaces.Race getRace() {
            return race;
        }

        @Nullable
        public IOccupation.VillagerType getVillagerType() {
            return villagerType;
        }

        @Nullable
        public Quest getRequiredQuest() {
            return requiredQuest;
        }

        @Nullable
        public List<Pair<EntityType<?>, Integer>> getTargetEntities() {
            if (targetEntities != null) {
                List<Pair<EntityType<?>, Integer>> entityTypes = new ArrayList<>();
                for (Pair<Supplier<EntityType<?>>, Integer> supplier : targetEntities) {
                    entityTypes.add(Pair.of(supplier.getFirst().get(), supplier.getSecond()));
                }
                return entityTypes;
            }
            return null;
        }

        @Nullable
        public List<Pair<EntityType<?>, Integer>> getInteractEntities() {
            if (interactEntities != null) {
                List<Pair<EntityType<?>, Integer>> entityTypes = new ArrayList<>();
                for (Pair<Supplier<EntityType<?>>, Integer> supplier : interactEntities) {
                    entityTypes.add(Pair.of(supplier.getFirst().get(), supplier.getSecond()));
                }
                return entityTypes;
            }
            return null;
        }

        @Nullable
        public List<Pair<Item, Integer>> getRequiredItems() {
            if (this.requiredItems != null) {
                List<Pair<Item, Integer>> items = new ArrayList<>();
                for (Pair<Supplier<Item>, Integer> pair : requiredItems) {
                    items.add(Pair.of(pair.getFirst().get(), pair.getSecond()));
                }
                return items;
            }
            return null;
        }

        @Nullable
        public TagKey<Biome> getRequiredBiome() {
            return this.requiredBiome;
        }

        @Nullable
        public TagKey<Structure> getRequiredStructure() {
            return this.requiredStructure;
        }
    }

}
