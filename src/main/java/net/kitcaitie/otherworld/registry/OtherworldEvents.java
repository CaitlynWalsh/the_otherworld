package net.kitcaitie.otherworld.registry;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.story.events.EventTrigger;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.kitcaitie.otherworld.common.story.events.StoryEvent;
import net.kitcaitie.otherworld.common.story.global.InvasionEvent;
import net.kitcaitie.otherworld.common.story.global.UndertakerSpawningEvent;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.common.util.SpecialSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// TODO: ADD MORE QUESTS AND EVENTS
public class OtherworldEvents {

    // ********** QUESTS ********** //

    public static void register() {
        GlobalStoryEvents.init();
        Quests.init();
        StoryEvents.init();
    }

    public static class Quests {
        public static final Map<String, List<Pair<String, Quest>>> REGISTRY = new HashMap<>();

        // CONTEXT
        private static final Quest.Context ONI_SOLDIER_ONI_TRAVELER = Quest.Context.of(IRaces.Race.ONI, IOccupation.Occupation.SOLDIER, IRaces.Race.ONI, IOccupation.Occupation.TRAVELER);
        private static final Quest.Context ONI_SOLDIER_ONI_SOLDIER = Quest.Context.of(IRaces.Race.ONI, IOccupation.Occupation.SOLDIER, IRaces.Race.ONI, IOccupation.Occupation.SOLDIER);
        private static final Quest.Context ROSEIAN_SOLDIER_ROSEIAN_TRAVELER = Quest.Context.of(IRaces.Race.ROSEIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.ROSEIAN, IOccupation.Occupation.TRAVELER);
        private static final Quest.Context ROSEIAN_SOLDIER_ROSEIAN_SOLDIER = Quest.Context.of(IRaces.Race.ROSEIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.ROSEIAN, IOccupation.Occupation.SOLDIER);
        private static final Quest.Context ROSEIAN_SOLDIER_FAIRIE_SOLDIER = Quest.Context.of(IRaces.Race.ROSEIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.FAIRIE, IOccupation.Occupation.SOLDIER);
        private static final Quest.Context FAIRIE_SOLDIER_FAIRIE_SOLDIER = Quest.Context.of(IRaces.Race.FAIRIE, IOccupation.Occupation.SOLDIER, IRaces.Race.FAIRIE, IOccupation.Occupation.SOLDIER);
        private static final Quest.Context EMBERIAN_SOLDIER_EMBERIAN_TRAVELER = Quest.Context.of(IRaces.Race.EMBERIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.EMBERIAN, IOccupation.Occupation.TRAVELER);
        private static final Quest.Context EMBERIAN_SOLDIER_EMBERIAN_SOLDIER = Quest.Context.of(IRaces.Race.EMBERIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.EMBERIAN, IOccupation.Occupation.SOLDIER);
        private static final Quest.Context ICEIAN_SOLDIER_ICEIAN_TRAVELER = Quest.Context.of(IRaces.Race.ICEIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.ICEIAN, IOccupation.Occupation.TRAVELER);
        private static final Quest.Context ICEIAN_SOLDIER_ICEIAN_SOLDIER = Quest.Context.of(IRaces.Race.ICEIAN, IOccupation.Occupation.SOLDIER, IRaces.Race.ICEIAN, IOccupation.Occupation.SOLDIER);

        // QUESTS
        protected static final Quest KILL_2_GRIZZLY = Quest.Builder.of("slay_2_grizzly").reward(List.of(Pair.of(() -> Items.IRON_INGOT, 6))).rewardXP(40).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.GRIZZLY::get, 2)))).afterTrigger(new EventTrigger[]{Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.SOLDIER)}).build();
        protected static final Quest KILL_4_FERAL_WOLVES = Quest.Builder.of("slay_4_feral_wolves").reward(List.of(Pair.of(() -> Items.IRON_INGOT, 4))).rewardXP(20).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.FERAL_WOLF::get, 4)))).afterTrigger(new EventTrigger[]{Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.SOLDIER)}).build();
        protected static final Quest KILL_4_ROSEIAN_SOLDIERS = Quest.Builder.of("slay_4_roseian_soldiers").time(288000).rewardXP(60).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.ROSEIAN::get, 4))).occupation(IOccupation.Occupation.SOLDIER)).reward(List.of(Pair.of(() -> Items.IRON_INGOT, 12))).build();
        protected static final Quest KILL_4_FAIRIE_SOLDIERS = Quest.Builder.of("slay_4_fairie_soldiers").time(288000).rewardXP(60).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.FAIRIE::get, 4))).occupation(IOccupation.Occupation.SOLDIER)).reward(List.of(Pair.of(() -> Items.IRON_INGOT, 14))).build();
        protected static final Quest LOCATE_ROSEIAN_OUTPOST = Quest.Builder.of("locate_roseian_outpost").time(576000).rewardXP(40).requirements(Quest.Requirement.travelToStructure(OtherworldTags.ROSEIAN_OUTPOSTS).quest(KILL_4_ROSEIAN_SOLDIERS)).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.ROSEIAN_OUTPOSTS)}).build();
        protected static final Quest LOCATE_FAIRIE_OUTPOST = Quest.Builder.of("locate_fairie_outpost").time(576000).rewardXP(40).requirements(Quest.Requirement.travelToStructure(OtherworldTags.FAIRIE_OUTPOSTS).quest(KILL_4_FAIRIE_SOLDIERS)).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.FAIRIE_OUTPOSTS)}).build();

        protected static final Quest OBTAIN_20_IRON_INGOTS = Quest.Builder.of("obtain_20_iron_ingots").rewardXP(30).requirements(Quest.Requirement.obtainItem(List.of(Pair.of(() -> Items.IRON_INGOT, 20)))).reward(List.of(Pair.of(OtherworldItems.ROSEGOLD_COIN, 12))).build();
        protected static final Quest KILL_3_ONI_SOLDIERS = Quest.Builder.of("slay_3_oni_soldiers").rewardXP(60).time(288000).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.ONI::get, 3))).occupation(IOccupation.Occupation.SOLDIER)).reward(List.of(Pair.of(OtherworldItems.ROSEGOLD_COIN, 12))).build();
        protected static final Quest LOCATE_ONI_OUTPOST = Quest.Builder.of("locate_oni_outpost").time(576000).rewardXP(40).requirements(Quest.Requirement.travelToStructure(OtherworldTags.ONI_OUTPOSTS).quest(KILL_3_ONI_SOLDIERS)).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.ONI_OUTPOSTS)}).build();
        protected static final Quest OBTAIN_ONI_HORN = Quest.Builder.of("obtain_oni_horn").requirements(Quest.Requirement.obtainItem(List.of(Pair.of(OtherworldItems.ONI_HORN, 1)))).reward(List.of(Pair.of(OtherworldItems.ROSEGOLD_COIN, 16))).rewardXP(60).afterTrigger(new EventTrigger[]{Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.SOLDIER)}).build();

        protected static final Quest TALK_TO_ROSEIAN_SOLDIER = Quest.Builder.of("talk_to_roseian_soldier").requirements(Quest.Requirement.interact(List.of(Pair.of(OtherworldEntities.ROSEIAN::get, 1))).occupation(IOccupation.Occupation.SOLDIER)).rewardXP(40).build();
        protected static final Quest FIND_CLUES_OF_VANISHING_FIGURES = Quest.Builder.of("find_clues_of_vanishing_figures").time(576000).requirements(Quest.Requirement.obtainItem(List.of(Pair.of(OtherworldItems.WITHERDUST, 1)))).reward(List.of(Pair.of(OtherworldItems.OPAL, 6))).rewardXP(40).build();
        protected static final Quest INSPECT_DARK_HOODED_FIGURE = Quest.Builder.of("inspect_dark_hooded_figure").time(576000).requirements(Quest.Requirement.obtainItem(List.of(Pair.of(OtherworldItems.OMINOUS_BOOK, 1))).quest(FIND_CLUES_OF_VANISHING_FIGURES)).reward(List.of(Pair.of(OtherworldItems.OPAL, 6))).onTrigger(new EventTrigger[]{Triggers.ADD_ITEM.of((Supplier<ItemStack>) Items.BOW::getDefaultInstance), Triggers.ADD_ITEM.of((Supplier<ItemStack>) () -> PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, 8), OtherworldMobEffects.SEDATIVE.get()))}).afterTrigger(new EventTrigger[]{Triggers.SET_GHOUL_TARGET.of(Boolean.TRUE)}).build();
        protected static final Quest FAIRIE_KILL_4_ONI_SOLDIERS = Quest.Builder.of("fairie_slay_4_oni_soldiers").rewardXP(60).time(288000).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.ONI::get, 3))).occupation(IOccupation.Occupation.SOLDIER).quest(TALK_TO_ROSEIAN_SOLDIER)).reward(List.of(Pair.of(OtherworldItems.ROSEGOLD_COIN, 12))).build();
        protected static final Quest FAIRIE_LOCATE_ONI_OUTPOST = Quest.Builder.of("fairie_locate_oni_outpost").time(576000).rewardXP(40).requirements(Quest.Requirement.travelToStructure(OtherworldTags.ONI_OUTPOSTS).quest(FAIRIE_KILL_4_ONI_SOLDIERS)).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.ONI_OUTPOSTS)}).build();

        protected static final Quest OBTAIN_SAPPHIRE_KEY = Quest.Builder.of("obtain_sapphire_key").requirements(Quest.Requirement.obtainItem(List.of(Pair.of(OtherworldItems.SAPPHIRE_KEY, 1)))).reward(List.of(Pair.of(OtherworldItems.TOPAZ_COIN, 16))).rewardXP(40).afterTrigger(new EventTrigger[]{Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.SOLDIER)}).build();
        protected static final Quest KILL_4_ICEIAN_SOLDIERS = Quest.Builder.of("slay_4_iceian_soldiers").time(288000).rewardXP(60).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.ICEIAN::get, 4))).occupation(IOccupation.Occupation.SOLDIER)).reward(List.of(Pair.of(OtherworldItems.TOPAZ_COIN, 12), Pair.of(OtherworldItems.SPICEROOT_JUICE, 1))).build();
        protected static final Quest LOCATE_ICEIAN_OUTPOST = Quest.Builder.of("locate_iceian_outpost").time(576000).requirements(Quest.Requirement.travelToStructure(OtherworldTags.ICEIAN_OUTPOSTS).quest(KILL_4_ICEIAN_SOLDIERS)).rewardXP(40).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.ICEIAN_OUTPOSTS)}).build();

        protected static final Quest OBTAIN_TOPAZ_KEY = Quest.Builder.of("obtain_topaz_key").requirements(Quest.Requirement.obtainItem(List.of(Pair.of(OtherworldItems.TOPAZ_KEY, 1)))).reward(List.of(Pair.of(OtherworldItems.SAPPHIRE_COIN, 16))).rewardXP(40).afterTrigger(new EventTrigger[]{Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.SOLDIER)}).build();
        protected static final Quest KILL_4_EMBERIAN_SOLDIERS = Quest.Builder.of("slay_4_emberian_soldiers").time(288000).rewardXP(60).requirements(Quest.Requirement.combat(List.of(Pair.of(OtherworldEntities.EMBERIAN::get, 4))).occupation(IOccupation.Occupation.SOLDIER)).reward(List.of(Pair.of(OtherworldItems.SAPPHIRE_COIN, 12), Pair.of(OtherworldItems.ICEBRUSSEL_SYRUP, 1))).build();
        protected static final Quest LOCATE_EMBERIAN_OUTPOST = Quest.Builder.of("locate_emberian_outpost").time(576000).requirements(Quest.Requirement.travelToStructure(OtherworldTags.EMBERIAN_OUTPOSTS).quest(KILL_4_EMBERIAN_SOLDIERS)).rewardXP(40).afterTrigger(new EventTrigger[]{Triggers.WAR_TRIGGER.of(OtherworldTags.EMBERIAN_OUTPOSTS)}).build();

        protected static void init() {
            //*** ONI QUESTS ***//
            registerQuests(ONI_SOLDIER_ONI_TRAVELER, List.of(
                    KILL_2_GRIZZLY,
                    KILL_4_FERAL_WOLVES
            ));
            registerQuests(ONI_SOLDIER_ONI_TRAVELER.war(GlobalStoryEvents.ONI_INVADE_ROSEIAN_WAR), List.of(
                    KILL_2_GRIZZLY,
                    KILL_4_FERAL_WOLVES
            ));
            registerQuests(ONI_SOLDIER_ONI_SOLDIER, List.of(

            ));
            registerQuests(ONI_SOLDIER_ONI_SOLDIER.war(GlobalStoryEvents.ONI_INVADE_ROSEIAN_WAR), List.of(
                    KILL_4_ROSEIAN_SOLDIERS,
                    KILL_4_FAIRIE_SOLDIERS,
                    LOCATE_ROSEIAN_OUTPOST,
                    LOCATE_FAIRIE_OUTPOST
            ));

            //*** ROSEIAN QUESTS ***//
            registerQuests(ROSEIAN_SOLDIER_ROSEIAN_TRAVELER.war(GlobalStoryEvents.ROSEIAN_INVADE_ONI_WAR).gender(false), List.of(
                    OBTAIN_ONI_HORN // to allow roseian females to be soldiers.
            ));
            registerQuests(ROSEIAN_SOLDIER_ROSEIAN_SOLDIER, List.of(
                    OBTAIN_20_IRON_INGOTS
            ));
            registerQuests(ROSEIAN_SOLDIER_ROSEIAN_SOLDIER.war(GlobalStoryEvents.ROSEIAN_INVADE_ONI_WAR), List.of(
                    OBTAIN_20_IRON_INGOTS,
                    KILL_3_ONI_SOLDIERS,
                    LOCATE_ONI_OUTPOST
            ));
            registerQuests(ROSEIAN_SOLDIER_FAIRIE_SOLDIER.war(GlobalStoryEvents.ROSEIAN_INVADE_ONI_WAR), List.of(
                    FAIRIE_KILL_4_ONI_SOLDIERS,
                    FAIRIE_LOCATE_ONI_OUTPOST
            ));

            //*** FAIRIE QUESTS ***//
            registerQuests(FAIRIE_SOLDIER_FAIRIE_SOLDIER, List.of(
                    FIND_CLUES_OF_VANISHING_FIGURES,
                    INSPECT_DARK_HOODED_FIGURE
            ));
            registerQuests(FAIRIE_SOLDIER_FAIRIE_SOLDIER.war(GlobalStoryEvents.ROSEIAN_INVADE_ONI_WAR), List.of(
                    TALK_TO_ROSEIAN_SOLDIER,
                    FIND_CLUES_OF_VANISHING_FIGURES,
                    INSPECT_DARK_HOODED_FIGURE
            ));

            //*** EMBERIAN QUESTS ***//
            registerQuests(EMBERIAN_SOLDIER_EMBERIAN_TRAVELER.war(GlobalStoryEvents.EMBERIAN_INVADE_ICEIAN_WAR).gender(false), List.of(
                    OBTAIN_SAPPHIRE_KEY // allow emberian females to be soldiers
            ));
            registerQuests(EMBERIAN_SOLDIER_EMBERIAN_SOLDIER.war(GlobalStoryEvents.EMBERIAN_INVADE_ICEIAN_WAR), List.of(
                    KILL_4_ICEIAN_SOLDIERS,
                    LOCATE_ICEIAN_OUTPOST
            ));

            //*** ICEIAN QUESTS ***//
            registerQuests(ICEIAN_SOLDIER_ICEIAN_TRAVELER.war(GlobalStoryEvents.ICEIAN_INVADE_EMBERIAN_WAR).gender(false), List.of(
                    OBTAIN_TOPAZ_KEY // allow iceian females to be soldiers
            ));
            registerQuests(ICEIAN_SOLDIER_ICEIAN_SOLDIER.war(GlobalStoryEvents.ICEIAN_INVADE_EMBERIAN_WAR), List.of(
                    KILL_4_EMBERIAN_SOLDIERS,
                    LOCATE_EMBERIAN_OUTPOST
            ));
        }

        private static void registerQuests(Quest.Context context, List<Quest> quests) {
            registerQuests(null, context, quests);
        }

        private static void registerQuests(@Nullable Quest.Context original, Quest.Context context, List<Quest> quests) {
            List<Pair<String, Quest>> list = new ArrayList<>(REGISTRY.getOrDefault(original != null ? original.id() : context.id(), new ArrayList<>()));
            for (Quest quest : quests) {
                list.add(Pair.of(quest.getID(), quest));
            }
            REGISTRY.put(context.id(), list);
        }

    }

    public static class StoryEvents {
        public static List<String> REGISTRY = new ArrayList<>();

        protected static void init() {

        }

        private static String registerEvent(String storyEvent) {
            REGISTRY.add(storyEvent);
            return storyEvent;
        }
    }

    public static class GlobalStoryEvents {
        public static List<WarEvent> WAR_EVENTS = new ArrayList<>();
        public static final WarEvent ONI_INVADE_ROSEIAN_WAR = registerWarEvent(new IRaces.Race[]{IRaces.Race.ONI}, new IRaces.Race[]{IRaces.Race.ROSEIAN});
        public static final WarEvent ROSEIAN_INVADE_ONI_WAR = registerWarEvent(new IRaces.Race[]{IRaces.Race.ROSEIAN, IRaces.Race.FAIRIE}, new IRaces.Race[]{IRaces.Race.ONI});
        public static final WarEvent EMBERIAN_INVADE_ICEIAN_WAR = registerWarEvent(new IRaces.Race[]{IRaces.Race.EMBERIAN}, new IRaces.Race[]{IRaces.Race.ICEIAN});
        public static final WarEvent ICEIAN_INVADE_EMBERIAN_WAR = registerWarEvent(new IRaces.Race[]{IRaces.Race.ICEIAN}, new IRaces.Race[]{IRaces.Race.EMBERIAN});
      //  public static final WarEvent GHOUL_INVASION_WAR = registerWarEvent(new IRaces.Race[]{IRaces.Race.GHOUL}, new IRaces.Race[]{IRaces.Race.FAIRIE, IRaces.Race.ROSEIAN, IRaces.Race.ONI, IRaces.Race.EMBERIAN, IRaces.Race.ICEIAN});

        public static final UndertakerSpawningEvent UNDERTAKER_SPAWNER = new UndertakerSpawningEvent();

        protected static void init() {

        }

        private static WarEvent registerWarEvent(IRaces.Race[] invader, IRaces.Race[] defender) {
            WarEvent warEvent = new WarEvent(invader, defender);
            WAR_EVENTS.add(warEvent);
            return warEvent;
        }
    }

    public static class Triggers {
        public static final EventTrigger OCCUPATION_CHANGE = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            if (!context.player.level.isClientSide()) {
                PlayerCharacter playerCharacter = PowerUtils.accessPlayerCharacter(context.player);
                if (context.object instanceof IOccupation.Occupation occupation) {
                    playerCharacter.setOccupation(occupation, 0);
                    playerCharacter.sendPacket(context.player);
                    context.player.sendSystemMessage(Component.literal(Component.translatable("event.otherworld.occupation_change." + occupation.name().toLowerCase()).getString()).withStyle(occupation == IOccupation.Occupation.CRIMINAL ? ChatFormatting.RED : ChatFormatting.AQUA));
                }
            }
        }));
        public static final EventTrigger ADD_QUEST = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            PlayerCharacter playerCharacter = PowerUtils.accessPlayerCharacter(context.player);
            if (context.object instanceof Quest quest) {
                playerCharacter.addQuest(null, quest);
                playerCharacter.sendPacket(context.player);
                context.player.sendSystemMessage(Component.literal(Component.translatable("event.otherworld.quest_added").getString().replace("%", quest.getDisplayName().getString())).withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA));
            }
        }));
        public static final EventTrigger ADD_ITEM = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            if (context.object instanceof Supplier<?> supplier && supplier.get() instanceof ItemStack stack) {
                if (!context.player.level.isClientSide()) {
                    context.player.addItem(stack);
                }
            }
        }));
        public static final EventTrigger SET_GHOUL_TARGET = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            if (context.object instanceof Boolean bool && !context.player.level.isClientSide()) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(context.player);
                character.ghoulTarget = bool;
                character.sendPacket(context.player);
                if (bool) {
                    context.player.level.playSound(null, context.player.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.AMBIENT, 1.0F, 1.0F);
                    context.player.level.playSound(null, context.player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.AMBIENT, 0.4F, 1.0F);
                    context.player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, false));
                    context.player.sendSystemMessage(Component.translatable("event.otherworld.ghoul_target").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                }
            }
        }));

        public static final EventTrigger WAR_START = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            Level level = context.player.getLevel();
            if (level instanceof ServerLevel && context.object instanceof WarEvent warEvent) {
                WarEvent warEvent1 = warEvent.copy();
                warEvent1.setLoaded(true);
                Otherworld.getStoryline((ServerLevel) level).getStory().addWarEvent(warEvent1);
                level.getServer().sendSystemMessage(Component.literal(Component.translatable("storyline.otherworld.war_start").getString()
                        .replace("$%1 ", Component.translatable("races.otherworld." + warEvent1.defender[0].name().toLowerCase() + ".name").getString())
                        .replace("$%2 ", Component.translatable("races.otherworld." + warEvent1.invader[0].name().toLowerCase() + ".name").getString()))
                        .withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD));
            }
        }));

        public static final EventTrigger WAR_TRIGGER = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            Level level = context.player.getLevel();
            if (level instanceof ServerLevel serverLevel && context.object instanceof TagKey<?> key) {
                if (key.registry().equals(Registries.STRUCTURE)) {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(context.player);
                    Story story = Otherworld.getStoryline(serverLevel).getStory();
                    WarEvent warEvent = story.getWarEvents().stream().filter((w) -> story.areRacesAtWar(character.getRace(), w.defender[0])).findFirst().orElse(null);
                    if (warEvent != null) {
                        InvasionEvent invasionEvent = new InvasionEvent((ServerPlayer) context.player, (TagKey<Structure>) key, warEvent.invader, warEvent.defender);
                        invasionEvent.setLoaded(true);
                        story.addGlobalEvent(invasionEvent);
                        character.setInvolvedInvasion(invasionEvent);
                        character.sendPacket(context.player);
                    }
                }
            }
        }));

        public static final EventTrigger WAR_END = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            Level level = context.player.getLevel();
            if (level instanceof ServerLevel && context.object instanceof WarEvent warEvent) {
                Otherworld.getStoryline((ServerLevel) level).getStory().getWarEvents().stream().filter((w) -> w.equals(warEvent)).findFirst().ifPresent((w) -> {
                    w.disabled = true;
                    level.getServer().sendSystemMessage(Component.literal(Component.translatable("storyline.otherworld.war_end").getString()
                            .replace("$%1 ", Component.translatable("races.otherworld." + w.defender[0].name().toLowerCase() + ".name").getString())
                            .replace("$%2 ", Component.translatable("races.otherworld." + w.invader[0].name().toLowerCase() + ".name").getString()))
                            .withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.BOLD));
                });
            }
        }));
        public static final EventTrigger ADD_STORY_EVENT = new EventTrigger((context) -> new EventTrigger.Trigger(context, () -> {
            Level level = context.player.getLevel();
            if (level instanceof ServerLevel && context.object instanceof StoryEvent event) {
                Otherworld.getStoryline((ServerLevel) level).getStory().addStoryEvent(event.getWorld(), event.getPlayer(), event);
            }
        }));

        public static final EventTrigger OTHERLY_MINION_SPAWNER = new EventTrigger((context -> new EventTrigger.Trigger(context, () -> {
            if (context.object instanceof BlockPos pos && context.player.level instanceof ServerLevel level) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                bolt.setDamage(0.0F);
                bolt.moveTo(Vec3.atBottomCenterOf(pos));
                level.addFreshEntity(bolt);

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                OtherlyMinion minion = OtherworldEntities.OTHERLY_MINION.get().create(level);
                minion.moveTo(pos, 0.0F, 0.0F);
                minion.setSummoner(context.player);
                minion.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
                level.addFreshEntity(minion);
            }
        })));

        public static final EventTrigger TRAVEL_TO_STRUCTURE = new EventTrigger((context -> new EventTrigger.Trigger(context, () -> {
            if (context.player.level instanceof ServerLevel) {
                if (context.object != null) {
                    TagKey<Structure> tagKey = (TagKey<Structure>) context.object;
                    BlockPos blockPos = SpecialSpawner.findVillageToStart(context.player, PowerUtils.accessPlayerCharacter(context.player).getRace(), tagKey);
                    if (blockPos != null) {
                        context.player.teleportTo(blockPos.getX(), PowerUtils.accessPlayerCharacter(context.player).isGhoul() ? 34 : 96, blockPos.getZ());
                    }
                }
            }
        })));
    }
}
