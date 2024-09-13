package net.kitcaitie.otherworld.common.story.events;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventHandler {

    // QUESTS //
    public static Quest chooseQuest(Player player, Quest.Context context) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        List<Pair<String, Quest>> list = OtherworldEvents.Quests.REGISTRY.get(context.id());
        if (list == null) {
            return null;
        }
        List<Pair<String, Quest>> list1 = list.stream().filter((p) -> !character.hasQuest(p.getSecond()) && character.canHaveQuest(context) && character.canHaveQuest(p.getSecond())).toList();
        if (list1.isEmpty()) {
            return null;
        }
        return list1.get(player.getRandom().nextInt(list1.size())).getSecond().copy();
    }

    public static Quest getQuest(Quest.Context context, Quest quest) {
        Pair<String, Quest> pair = OtherworldEvents.Quests.REGISTRY.getOrDefault(context.id(), new ArrayList<>()).stream().filter((p) -> p.getSecond().getID().equals(quest.getID())).findFirst().orElse(null);
        if (pair != null) {
            return pair.getSecond().copy();
        }
        return null;
    }

    public static Quest getQuest(String name) {
        for (String key : OtherworldEvents.Quests.REGISTRY.keySet()) {
            for (Pair<String, Quest> quest : OtherworldEvents.Quests.REGISTRY.get(key)) {
                if (quest.getFirst().equals(name)) {
                    return quest.getSecond();
                }
            }
        }
        return null;
    }

    public static boolean completeQuest(Player player, AbstractPerson person) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        Pair<Long, Quest> pair = character.getQuests().stream().filter((p) -> p.getFirst() == person.getIdentity() || (p.getFirst() == 0 && person.canAssignQuest(Quest.Context.context(player, person, -1), player, true))).findFirst().orElse(null);
        if (pair != null) {
            Quest quest = pair.getSecond();
            List<Pair<String, Integer>> progress = character.getQuestProgress().getOrDefault(quest, new ArrayList<>());
            int flag = 0;
            for (Pair<String, Integer> pair1 : progress) {
                Pair<String, Integer> pair2 = quest.getRequirements().getAmount().stream().filter((q) -> q.getFirst().equals(pair1.getFirst())).findFirst().orElse(null);
                if (pair2 != null && pair1.getSecond() >= pair2.getSecond()) {
                    flag++;
                }
            }
            if (flag >= progress.size()) {
                quest.complete(player, person);
                person.sayTo(player, quest.getID() + DialogueEvent.COMPLETE.getString());
                player.level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                character.removeQuest(quest);
                character.sendPacket(player);
                return true;
            }
        }
        return false;
    }

    public static boolean completeQuest(Player player, @Nullable AbstractPerson person, Quest quest) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        List<Pair<String, Integer>> progress = character.getQuestProgress().getOrDefault(quest, new ArrayList<>());
        int flag = 0;
        for (Pair<String, Integer> pair1 : progress) {
            Pair<String, Integer> pair2 = quest.getRequirements().getAmount().stream().filter((q) -> q.getFirst().equals(pair1.getFirst())).findFirst().orElse(null);
            if (pair2 != null && pair1.getSecond() >= pair2.getSecond()) {
                flag++;
            }
        }
        if (flag >= progress.size()) {
            quest.complete(player, person);
            if (person != null) {
                person.sayTo(player, quest.getID() + DialogueEvent.COMPLETE.getString());
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            character.removeQuest(quest);
            character.sendPacket(player);
            return true;
        }
        return false;
    }

    public static void updateCombatQuests(Player player, LivingEntity target) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        List<Pair<Long, Quest>> quests = character.getQuests().stream().filter((p) -> {
            Quest.Requirement requirement = p.getSecond().getRequirements();
            List<Pair<EntityType<?>, Integer>> entityTypes = requirement.getTargetEntities();
            if (entityTypes == null) return false;
            return entityTypes.stream().anyMatch((e) -> e.getFirst().equals(target.getType()) && (requirement.occupation == null || (target instanceof AbstractPerson person && person.getOccupation().equals(requirement.occupation))));
        }).toList();
        if (!quests.isEmpty()) {
            for (Pair<Long, Quest> pair : quests) {
                character.addQuestProgress(pair.getSecond(), 1, target.getType().toString());
                character.sync(player);
                player.level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F);
            }
        }
    }

    public static void updateInteractQuests(Player player, Entity interact) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        List<Pair<Long, Quest>> quests = character.getQuests().stream().filter((p) -> {
            Quest.Requirement requirement = p.getSecond().getRequirements();
            List<Pair<EntityType<?>, Integer>> entityTypes = requirement.getInteractEntities();
            if (entityTypes == null) return false;
            return entityTypes.stream().anyMatch((e) -> e.getFirst().equals(interact.getType()) && (requirement.occupation == null || (interact instanceof AbstractPerson person && person.getOccupation().equals(requirement.occupation))));
        }).toList();
        if (!quests.isEmpty()) {
            for (Pair<Long, Quest> pair : quests) {
                character.addQuestProgress(pair.getSecond(), 1, interact.getType().toString());
                character.sync(player);
                if (!completeQuest(player, null, pair.getSecond())) {
                    player.level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F);
                }
                else if (interact instanceof AbstractPerson person) {
                    person.sayTo(player, pair.getSecond().getID() + DialogueEvent.COMPLETE.getString());
                }
            }
        }
    }

    public static boolean giveQuestItems(Player player, AbstractPerson person, ItemStack stack) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        AtomicInteger amount = new AtomicInteger(-1);
        List<Pair<Long, Quest>> quests = character.getQuests().stream().filter((p) -> {
            if (p.getFirst() == person.getIdentity() || (p.getFirst() == 0 && person.canAssignQuest(Quest.Context.context(player, person, -1), player, true))) {
                List<Pair<Item, Integer>> items = p.getSecond().getRequirements().getRequiredItems();
                if (items == null) return false;
                Pair<Item, Integer> itm = items.stream().filter((p1) -> stack.is(p1.getFirst())).findFirst().orElse(null);
                if (itm != null && stack.is(itm.getFirst())) {
                    amount.set(itm.getSecond());
                    return true;
                }
            }
            return false;
        }).toList();
        if (!quests.isEmpty() && amount.get() > 0) {
            Quest quest = quests.get(0).getSecond();
            int j = Integer.min(amount.get(), stack.getCount());
            character.addQuestProgress(quest, j, stack.getItem().toString());
            stack.shrink(j);
            completeQuest(player, person, quest);
            return true;
        }
        return false;
    }

    public static void updateTravelQuests(Player player, Level level) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        for (Pair<Long, Quest> pair : character.getQuests()) {
            if (pair.getSecond().getRequirements().getRequiredBiome() != null) {
                if (level.getBiome(player.blockPosition()).is(pair.getSecond().getRequirements().getRequiredBiome())) {
                    character.addQuestProgress(pair.getSecond(), 1, pair.getSecond().getRequirements().requiredBiome.location().toString());
                    completeQuest(player, null, pair.getSecond());
                    return;
                }
            }

            if (level instanceof ServerLevel level1) {
                if (pair.getSecond().getRequirements().getRequiredStructure() != null) {
                    if (level1.structureManager().getStructureWithPieceAt(player.blockPosition(), pair.getSecond().getRequirements().getRequiredStructure()).isValid()) {
                        character.addQuestProgress(pair.getSecond(), 1, pair.getSecond().getRequirements().requiredStructure.location().toString());
                        completeQuest(player, null, pair.getSecond());
                        return;
                    }
                }
            }
        }
    }

}
