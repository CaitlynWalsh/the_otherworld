package net.kitcaitie.otherworld.common.entity.npcs.data;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldEvents;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.UUID;

public class DeathData {
    private final AbstractPerson victim;
    private final long identity;
    private final UUID uuid;
    private final IRaces.Race race;
    private final IOccupation.Occupation occupation;
    private final int occupationStatus;
    private final PersonData personData;
    private final DamageSource source;

    public DeathData(AbstractPerson died, DamageSource source) {
        this.victim = died;
        this.identity = died.getIdentity();
        this.uuid = died.getUUID();
        this.race = died.getRace();
        this.occupation = died.getOccupation();
        this.occupationStatus = died.getOccupationStatus();
        this.personData = died.getPersonData();
        this.source = source;
    }

    public boolean isVip() {
        return occupationStatus == occupation.getMaxStatus() && (occupation == IOccupation.Occupation.LEADER || occupation == IOccupation.Occupation.SOLDIER);
    }

    public DamageSource getSource() {
        return source;
    }

    public IOccupation.Occupation getOccupation() {
        return occupation;
    }

    public PersonData getPersonData() {
        return personData;
    }

    public long getIdentity() {
        return identity;
    }

    public IRaces.Race getRace() {
        return race;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getOccupationStatus() {
        return occupationStatus;
    }

    public void onDeathByPlayer(ServerPlayer player) {
        if (!victim.isHuman()) {
            Story story = Otherworld.getStoryline(player.getLevel()).getStory();
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (race == character.getRace() || story.areRacesAllied(character.getRace(), race)) {
                if (character.isSoldier() && occupation == IOccupation.Occupation.SOLDIER && victim.getInvolvedWar() != null) {
                    if (!this.isVip()) return;
                }
                if (!character.isCriminal()) {
                    OtherworldEvents.Triggers.OCCUPATION_CHANGE.of(IOccupation.Occupation.CRIMINAL).trigger(player, victim, null);
                    return;
                } else if (!character.isWantedCriminal()) {
                    character.addOccupationStatus(player);
                    if (character.isWantedCriminal()) {
                        story.addCriminal(race.getHomeWorld(), player, Utils.createBountyRewardsForRace(race));
                        player.sendSystemMessage(Component.literal(Component.translatable("storyline.otherworld.most_wanted_criminal_added").getString().replace("$1 ", Component.translatable("races.otherworld." + race.name().toLowerCase() + ".name").getString()))
                                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
                    }
                }
                character.setImprisoned(false);
                character.setPrisonTime(-1);
                character.sendPacket(player);
            }
        }
    }
}
