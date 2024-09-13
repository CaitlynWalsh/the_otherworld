package net.kitcaitie.otherworld.common.entity.npcs.data;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PersonData {
    private Map<UUID, RelationshipData> relationships = new HashMap<>();
    private Pair<UUID, RelationshipData> spouse = null;
    private List<Pair<Long, String>> parents = new ArrayList<>(2);
    private List<Pair<UUID, RelationshipData>> playerParents = new ArrayList<>(2);
    private Map<Long, String> children = new HashMap<>();
    private boolean dirty;

    public PersonData() {
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public CompoundTag saveData() {
        CompoundTag data = new CompoundTag();

        CompoundTag relations = new CompoundTag();
        List<UUID> entities = new ArrayList<>(relationships.keySet());
        for (int i=0; i<entities.size(); i++) {
            UUID entity = entities.get(i);
            CompoundTag tag = new CompoundTag();
            tag.putUUID("id", entity);
            tag.putString("status", getRelationship(entity).getString());
            relations.put(Integer.toString(i), tag);
        }
        data.put("RelationshipData", relations);


        if (spouse != null) {
            CompoundTag spouseTag = new CompoundTag();
            spouseTag.putUUID("id", spouse.getFirst());
            spouseTag.putString("status", spouse.getSecond().getString());
            data.put("Spouse", spouseTag);
        }

        if (!parents.isEmpty()) {
            CompoundTag parentTag = new CompoundTag();
            for (int i=0; i<Integer.min(parents.size(), 2); i++) {
                Pair<Long, String> pair = parents.get(i);
                CompoundTag tag = new CompoundTag();
                tag.putLong("id", pair.getFirst());
                tag.putString("name", pair.getSecond());
                parentTag.put(Integer.toString(i), tag);
            }
            data.put("Parents", parentTag);
        }

        if (!playerParents.isEmpty()) {
            CompoundTag playerTag = new CompoundTag();
            for (int i=0; i<Integer.min(playerParents.size(), 2); i++) {
                Pair<UUID, RelationshipData> pair = playerParents.get(i);
                CompoundTag tag = new CompoundTag();
                tag.putUUID("id", pair.getFirst());
                tag.putString("status", pair.getSecond().getString());
                playerTag.put(Integer.toString(i), tag);
            }
            data.put("ParentPlayers", playerTag);
        }

        if (!children.isEmpty()) {
            CompoundTag childrenTag = new CompoundTag();
            List<Long> childIds = new ArrayList<>(children.keySet());
            for (int i=0; i<childIds.size(); i++) {
                Long id = childIds.get(i);
                CompoundTag tag = new CompoundTag();
                tag.putLong("id", id);
                tag.putString("name", children.getOrDefault(id, ""));
                childrenTag.put(Integer.toString(i), tag);
            }
            data.put("Children", childrenTag);
        }

        data.putBoolean("Dirty", isDirty());

        return data;
    }

    public static PersonData readData(CompoundTag tag) {
        PersonData personData = new PersonData();

        if (tag.contains("RelationshipData")) {
            CompoundTag relations = tag.getCompound("RelationshipData");
            for (int i=0; i<relations.size(); i++) {
                CompoundTag tag1 = relations.getCompound(Integer.toString(i));
                personData.setRelationship(tag1.getUUID("id"), new RelationshipData().fromString(tag1.getString("status")));
            }
        }

        if (tag.contains("Spouse")) {
            CompoundTag spouseTag = tag.getCompound("Spouse");
            personData.setSpouse(spouseTag.getUUID("id"), new RelationshipData().fromString(spouseTag.getString("status")));
        }

        if (tag.contains("Parents")) {
            CompoundTag parentTag = tag.getCompound("Parents");
            for (int i=0; i<parentTag.size(); i++) {
                CompoundTag parent = parentTag.getCompound(Integer.toString(i));
                personData.parents.add(Pair.of(parent.getLong("id"), parent.getString("name")));
            }
        }

        if (tag.contains("ParentPlayers")) {
            CompoundTag playerTag = tag.getCompound("ParentPlayers");
            for (int i=0; i<playerTag.size(); i++) {
                CompoundTag player = playerTag.getCompound(Integer.toString(i));
                personData.playerParents.add(Pair.of(player.getUUID("id"), new RelationshipData().fromString(player.getString("status"))));
            }
        }

        if (tag.contains("Children")) {
            CompoundTag childrenTag = tag.getCompound("Children");
            for (int i = 0; i < childrenTag.size(); i++) {
                CompoundTag child = childrenTag.getCompound(Integer.toString(i));
                personData.addChild(child.getLong("id"), child.getString("name"));
            }
        }

        personData.setDirty(false);

        return personData;
    }

    public void setSpouse(AbstractPerson self, LivingEntity living) {
        setSpouse(living.getUUID(), new RelationshipData().relationship(RelationshipData.Status.NEUTRAL, RelationshipData.Family.SPOUSE));
        if (living instanceof Player player) {
            PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
            chr.setSpouse(self);
            if (!player.level.isClientSide()) {
                player.sendSystemMessage(Component.literal(self.getDisplayName().getString() + Component.translatable("family.otherworld.marriage").getString())
                        .withStyle(ChatFormatting.GOLD)
                        .withStyle(ChatFormatting.ITALIC));
            }
            chr.sendPacket(player);
        }
        else if (living instanceof AbstractPerson person) {
            PersonData data = person.getPersonData();
            data.setSpouse(self.getUUID(), new RelationshipData().relationship(RelationshipData.Status.NEUTRAL, RelationshipData.Family.SPOUSE));
            data.setDirty();
            person.setPersonData(data);
        }
        this.setDirty();
    }

    public void removeSpouse(Component name, @Nullable Component deathMessage, long id, ServerLevel level, boolean death) {
        if (this.spouse != null) {
            Player player = level.getPlayerByUUID(this.spouse.getFirst());
            if (player != null) {
                PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
                chr.removeSpouse();
                chr.sendPacket(player);
                if (death && deathMessage != null) {
                    player.sendSystemMessage(Component.literal(deathMessage.getString())
                            .withStyle(ChatFormatting.DARK_RED)
                            .withStyle(ChatFormatting.ITALIC));
                } else if (!death) {
                    player.sendSystemMessage(Component.literal(name.getString() + Component.translatable("family.otherworld.remove_spouse").getString())
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.ITALIC));
                }
            }
            else {
                Entity entity = level.getEntity(this.spouse.getFirst());
                if (entity instanceof AbstractPerson person && person.isAlive()) {
                    PersonData data = person.getPersonData();
                    data.spouse = null;
                    data.setDirty();
                    person.setPersonData(data);
                }
            }
        }
        this.spouse = null;
        this.setDirty();
    }

    public void removePlayerParents(long id, @Nullable Component deathMessage, ServerLevel level, boolean death) {
        if (!this.playerParents.isEmpty()) {
            for (Pair<UUID, RelationshipData> pair : playerParents) {
                Player s = level.getPlayerByUUID(pair.getFirst());
                if (s != null) {
                    PlayerCharacter chr = PowerUtils.accessPlayerCharacter(s);
                    chr.removeChild(id);
                    chr.sendPacket(s);
                    if (death && deathMessage != null) {
                        s.sendSystemMessage(Component.literal(deathMessage.getString())
                                .withStyle(ChatFormatting.DARK_RED)
                                .withStyle(ChatFormatting.ITALIC));
                    } else {
                        if (death) {
                            Otherworld.LOGGER.warn(s.getName().getString() + "'s child with ID: " + id + " has died without notification.");
                        }
                        else Otherworld.LOGGER.warn("AbstractPerson with ID: " + id + " is no longer " + s.getName().getString() + "'s child.");
                    }
                }
            }
        }
        this.setDirty();
    }

    public boolean hasTwoParents() {
        return this.parents.size() + this.playerParents.size() >= 2;
    }

    public void setSpouse(UUID player, RelationshipData data) {
        this.spouse = Pair.of(player, data);
        this.relationships.put(player, data);
        this.setDirty();
    }

    public void setParents(AbstractPerson self, @Nullable LivingEntity parent1, @Nullable LivingEntity parent2, @Nullable PlayerCharacter character) {
        addParent(self, parent1, character);
        addParent(self, parent2, character);
    }

    public void addParent(AbstractPerson self, LivingEntity entity, @Nullable PlayerCharacter character) {
        if (!hasTwoParents()) {
            if (entity instanceof AbstractPerson person) {
                PersonData data = person.getPersonData();
                if (!data.playerParents.isEmpty()) {
                    for (Pair<UUID, ?> pair : data.playerParents) {
                       this.setRelationship(pair.getFirst(), new RelationshipData().relationship(RelationshipData.Status.NEUTRAL, RelationshipData.Family.RELATIVE));
                    }
                }
                this.parents.add(Pair.of(person.getIdentity(), person.getName().getString()));
                data.addChild(self);
                data.setDirty();
                person.setPersonData(data);
            } else if (entity instanceof Player player && character != null) {
                UUID name = player.getUUID();
                RelationshipData relationshipData = getRelationship(player).relationship(getRelationship(player).relationStatus, RelationshipData.Family.CHILD);
                this.playerParents.add(Pair.of(name, relationshipData));
                this.relationships.put(name, relationshipData);
                character.addChild(self);
            }
        }
        this.setDirty();
    }

    public void removeParent(AbstractPerson self, LivingEntity entity) {
        if (entity instanceof AbstractPerson person) {
            this.parents.forEach((p) -> {
                if (p.getFirst() == person.getIdentity()) {
                    this.parents.remove(p);
                }
            });
            if (person.isAlive()) {
                PersonData data = person.getPersonData();
                data.removeChild(person, self);
                person.setPersonData(data);
            }
        }
        else if (entity instanceof Player player) {
            this.playerParents.forEach((p) -> {
                if (p.getFirst().equals(player.getUUID())) {
                    this.playerParents.remove(p);
                }
            });
        }
        this.setDirty();
    }

    public void removeChild(AbstractPerson self, AbstractPerson child) {
        this.children.forEach((l, s) -> {
            if (child.getIdentity() == l) this.children.remove(l);
        });
        if (child.isAlive()) {
            PersonData data = child.getPersonData();
            data.parents.forEach((p) -> {
                if (p.getFirst() == self.getIdentity()) {
                    data.parents.remove(p);
                }
            });
            data.setDirty();
            child.setPersonData(data);
        }
        this.setDirty();
    }

    public boolean hasParents() {
        return !(this.parents.isEmpty() || this.playerParents.isEmpty());
    }

    public List<Pair<Long, String>> getParents() {
        return parents;
    }

    public List<Pair<UUID, RelationshipData>> getPlayerParents() {
        return playerParents;
    }

    public Map<Long, String> getChildren() {
        return children;
    }

    public Map<UUID, RelationshipData> getRelationships() {
        return relationships;
    }

    public Pair<UUID, RelationshipData> getSpouse() {
        return spouse;
    }

    public RelationshipData getRelationship(LivingEntity player) {
        return getRelationship(player.getUUID());
    }

    public boolean isHostileTowards(LivingEntity player) {
        return getRelationship(player).relationStatus == RelationshipData.Status.HOSTILE;
    }

    public boolean isNeutralTowards(LivingEntity player) {
        return getRelationship(player).relationStatus == RelationshipData.Status.NEUTRAL;
    }

    public boolean isFriendlyTowards(LivingEntity player) {
        return getRelationship(player).relationStatus == RelationshipData.Status.FRIENDLY;
    }

    public boolean isLoverOf(LivingEntity player) {
        return !isMarriedTo(player) && getRelationship(player).familyType == RelationshipData.Family.LOVER;
    }

    public boolean isInLove() {
        return this.getRelationships().values().stream().anyMatch((data) -> data.familyType == RelationshipData.Family.LOVER);
    }

    public boolean isMarriedTo(LivingEntity player) {
        return getRelationship(player).familyType == RelationshipData.Family.SPOUSE;
    }

    public boolean isMarried() {
        return this.spouse != null;
    }

    public void addChild(AbstractPerson person) {
        addChild(person.getIdentity(), person.getName().getString());
    }

    public void addChild(Long id, String name) {
        this.children.put(id, name);
        this.setDirty();
    }

    public boolean isChildOf(LivingEntity entity) {
        if (entity instanceof AbstractPerson person) {
            return this.parents.stream().anyMatch((p) -> p.getFirst() == person.getIdentity());
        }
        else if (entity instanceof Player player) {
            return this.playerParents.stream().anyMatch((p) -> p.getFirst().equals(player.getUUID()));
        }
        return false;
    }

    public static PersonData copyFrom(PersonData personData) {
        PersonData personData1 = new PersonData();
        personData1.relationships = personData.relationships;
        personData1.spouse = personData.spouse;
        personData1.parents = personData.parents;
        personData1.playerParents = personData.playerParents;
        personData1.children = personData.children;
        return personData1;
    }

    public boolean isFamilyWith(LivingEntity entity) {
        return this.isChildOf(entity) || (entity instanceof Player player && this.isMarriedTo(player));
    }

    public void setRelationship(UUID entity, RelationshipData status) {
        this.relationships.put(entity, status);
        this.setDirty();
    }

    public RelationshipData getRelationship(UUID entity) {
        return this.relationships.getOrDefault(entity, new RelationshipData().relationship(RelationshipData.Status.NEUTRAL, null));
    }

    public void modifyRelationship(AbstractPerson self, LivingEntity player, RelationshipData.Status status) {
        modifyRelationship(self, player, status, getRelationship(player.getUUID()).familyType);
    }

    public void modifyRelationship(AbstractPerson self, LivingEntity player, RelationshipData.Status status, RelationshipData.Family family) {
        setRelationship(player.getUUID(), getRelationship(player.getUUID()).relationship(status, family));
    }

    public void addRelationPoints(AbstractPerson self, LivingEntity player, int amount) {
        RelationshipData data = getRelationship(player.getUUID());
        int points = data.relationPoints + amount;
        data = checkRelationPoints(data, points);
        setRelationship(player.getUUID(), data);
    }

    public void removeRelationPoints(AbstractPerson self, LivingEntity player, int amount) {
        addRelationPoints(self, player, -amount);
    }

    protected RelationshipData checkRelationPoints(RelationshipData data, int points) {
        if (points >= 80 && data.relationPoints < 80) {
            return data.relationship(data.incrementStatus(), data.familyType, 0);
        }
        else if (points < 0 && data.relationPoints >= 0) {
            return data.relationship(data.decreaseStatus(), data.familyType, 0);
        }
        return data.relationship(data.relationStatus, data.familyType, points);
    }

    public void clearAllRelations(AbstractPerson self, ServerLevel level, boolean death) {
        long identity = self.getIdentity();
        UUID selfUUID = self.getUUID();
        Component name = self.getName();
        Component deathMessage = null;

        if (death) {
            deathMessage = self.getCombatTracker().getDeathMessage();
        }

        if (death) {
            boolean alertedVillager = false;
            if (this.parents.size() == 2) {
                if (level.getEntity(this.parents.get(0).getFirst().intValue()) instanceof AbstractPerson person) {
                    person.hasVillagerDiedOrLeftRecently = true;
                    alertedVillager = true;
                }
            }
            if (!alertedVillager) {
                for (UUID uuid : this.relationships.keySet()) {
                    if (uuid != null) {
                        Entity entity = level.getEntity(uuid);
                        if (entity instanceof AbstractPerson person && person.isAlive()) {
                            if (!alertedVillager && !person.hasVillagerDiedOrLeftRecently && person.isVillager()) {
                                person.hasVillagerDiedOrLeftRecently = true;
                                alertedVillager = true;
                            }
                            PersonData data = person.getPersonData();
                            data.relationships.remove(selfUUID);
                            if (data.spouse != null && data.spouse.getFirst().equals(selfUUID)) {
                                data.spouse = null;
                            }
                            data.parents.removeIf((pair) -> pair.getFirst().equals(identity));
                            data.children.remove(identity);
                            data.setDirty();
                            person.setPersonData(data);
                        }
                    }
                }
            }
        }

        this.removeSpouse(name, deathMessage, identity, level, death);
        this.removePlayerParents(identity, deathMessage, level, death);
        this.parents.clear();
        this.children.clear();
        this.relationships.clear();

        level.getPlayers((s) -> PowerUtils.accessPlayerCharacter(s).getQuests().stream().anyMatch((p) -> p.getFirst().equals(identity))).forEach((s) ->
                PowerUtils.accessPlayerCharacter(s).getQuests().stream().filter((p) -> p.getFirst().equals(identity)).forEach((q) -> {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(s);
                    character.setQuestAssignerID(0L, q.getSecond());
                    character.sendPacket(s);
                }));

        this.setDirty();
    }

    public static void createPerson(AbstractPerson person) {
        person.setMale(person.getRandom().nextBoolean());
        AbstractPerson.nameGen.createName(person);
    }

    public static class RelationshipData {
        public Family familyType;
        public Status relationStatus;
        public int relationPoints = 0;

        public enum Status {
            FRIENDLY,
            NEUTRAL,
            HOSTILE
        }

        public enum Family {
            LOVER,
            SPOUSE,
            CHILD,
            RELATIVE
        }

        public Status incrementStatus() {
            if (this.relationStatus.ordinal() > Status.FRIENDLY.ordinal()) {
                return Status.values()[this.relationStatus.ordinal() - 1];
            }
            return this.relationStatus;
        }

        public Status decreaseStatus() {
            if (this.relationStatus.ordinal() < Status.HOSTILE.ordinal()) {
                return Status.values()[this.relationStatus.ordinal() + 1];
            }
            return this.relationStatus;
        }

        public RelationshipData relationship(Status status, @javax.annotation.Nullable Family family, int points) {
            this.relationStatus = status;
            this.familyType = family;
            this.relationPoints = points;
            return this;
        }

        public RelationshipData relationship(Status status, @javax.annotation.Nullable Family family) {
            this.relationStatus = status;
            this.familyType = family;
            return this;
        }

        public String getString() {
            if (this.familyType != null) {
                return this.relationStatus.name() + "_" + this.familyType.name() + "_" + this.relationPoints;
            }
            return this.relationStatus.name() + "_" + this.relationPoints;
        }

        public RelationshipData fromString(String string) {
            String[] values = string.split("_");
            if (values.length == 3) {
                this.relationStatus = Status.valueOf(values[0]);
                this.familyType = Family.valueOf(values[1]);
                this.relationPoints = Integer.parseInt(values[2]);
            }
            else {
                this.relationStatus = Status.valueOf(values[0]);
                this.relationPoints = Integer.parseInt(values[1]);
            }
            return this;
        }
    }
    
}
