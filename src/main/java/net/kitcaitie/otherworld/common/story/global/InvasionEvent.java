package net.kitcaitie.otherworld.common.story.global;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.*;
import java.util.function.Predicate;

public class InvasionEvent extends WarEvent {
    private ServerPlayer invasionLeader;
    private UUID invasionLeaderUUID;
    private final TagKey<Structure> toInvade;
    private boolean wasLoadedAfterFinished = false;

    public InvasionEvent(ServerPlayer invader, TagKey<Structure> toInvade, IRaces.Race[] invaderRace, IRaces.Race[] defenderRace) {
        super(invaderRace, defenderRace);
        this.invasionLeader = invader;
        this.invasionLeaderUUID = invader.getUUID();
        this.toInvade = toInvade;
    }

    public InvasionEvent(UUID invader, TagKey<Structure> toInvade, IRaces.Race[] invaderRace, IRaces.Race[] defenderRace) {
        super(invaderRace, defenderRace);
        this.invasionLeaderUUID = invader;
        this.toInvade = toInvade;
    }

    @Override
    public boolean hasActiveWar() {
        return activeWar || invasionTargetPos != null || this.wasLoadedAfterFinished;
    }

    @Override
    public void start(MinecraftServer server) {
        if (invasionLeaderUUID != null) {
            if (server.getLevel(Otherworld.OTHERWORLD) != null) {
                this.invasionLeader = (ServerPlayer) server.getLevel(Otherworld.OTHERWORLD).getPlayerByUUID(invasionLeaderUUID);
            }
        }
        if (invasionLeader != null && invasionLeader.isAlive()) {
            StructureStart structureStart = invasionLeader.getLevel().structureManager().getStructureWithPieceAt(invasionLeader.blockPosition(), toInvade);
            if (structureStart.isValid()) {
                this.defendingRace = Arrays.stream(IRaces.Race.values()).filter((r) -> r.getHomeWorld() != null && r.getHomeWorld().getStructures().stream().anyMatch((tag) -> tag.equals(toInvade))).findAny().orElse(null);
                if (defendingRace != null) {
                    this.invasionTargetPos = structureStart.getBoundingBox().getCenter().atY(96);
                    this.invasionSpawnPos = invasionLeader.blockPosition().atY(96);
                    if (!wasLoadedAfterFinished) {
                        List<IRaces.Race> invaders = Arrays.asList(invader);
                        for (int i = 0; i < random.nextInt(getInvaderGroupSizeRange()[0], getInvaderGroupSizeRange()[1]); i++) {
                            ResourceLocation soldierType = invaders.get(random.nextInt(invaders.size())).getPersonType();
                            if (soldierType != null) {
                                Optional<EntityType<?>> soldier = EntityType.byString(soldierType.toString());
                                if (soldier.isPresent()) {
                                    spawnSoldier(soldier.get(), invasionSpawnPos, invasionLeader.getLevel());
                                } else {
                                    Otherworld.LOGGER.error("InvasionEvent: could not spawn soldier of type " + soldierType);
                                    Otherworld.LOGGER.error("InvasionEvent: EntityType " + soldierType + " is not present");
                                }
                            }
                        }
                    } else this.findSoldiers(server);
                    this.activeWar = true;
                    this.totalHealth = getTotalHealth();
                    this.bossBar.setName(getBossBarName());
                    this.hasStarted = true;
                    story.setChanged();
                    return;
                }
                else Otherworld.LOGGER.error("InvasionEvent: could not start raid triggered by: " + invasionLeader + " | defendingRace is null.");
            }
        }
        this.defendingRace = null;
        this.invasionTargetPos = null;
        this.invasionSpawnPos = null;
    }

    @Override
    public void updatePlayers(MinecraftServer server) {
        if (this.invasionLeader != null) {
            if (this.hasStarted() && this.hasActiveWar()) {
                bossBar.addPlayer(this.invasionLeader);
            }
        }
    }

    @Override
    public void onRemove() {
        this.forceRemoveInvaders();
        this.bossBar.removeAllPlayers();
    }

    @Override
    public boolean canContinue(MinecraftServer server) {
        isWarOver(server);
        return true;
    }

    @Override
    public void tickEvent(MinecraftServer server) {
        ServerLevel level = server.getLevel(Otherworld.OTHERWORLD);

        if (level == null) return;

        if (invasionLeader == null) {
            this.invasionLeader = (ServerPlayer) server.getLevel(Otherworld.OTHERWORLD).getPlayerByUUID(invasionLeaderUUID);
        }

        if (invasionLeader != null && server.getTickCount() % 40 == 1) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(invasionLeader);
            if (character.getInvolvedInvasion() == null) {
                character.setInvolvedInvasion(this);
                character.sendPacket(invasionLeader);
            }
        }

        tick++;

        if (isWarFinished()) {
            if (this.activeWar) {
                this.stop(server);
                return;
            }
            if (!queuedForRemoval) this.queuedForRemoval = true;
            return;
        }

        if (level.isLoaded(this.invasionTargetPos)) {
            if (timeElapsed > 0) {
                invaders.removeIf(person -> person == null || !person.isAlive());
                if (timeElapsed % 2 == 1) {
                    this.findSoldiers(server);
                }
            }
            timeElapsed++;
        }
    }

    @Override
    public boolean canRemove(MinecraftServer server) {
        if (tick < ACTIVE_TICK) return false;
        return invasionLeader == null || !invasionLeader.isAlive() || invasionTargetPos.distToCenterSqr(invasionLeader.position()) > 1000.0D;
    }

    @Override
    public void stop(MinecraftServer server) {
        this.activeWar = false;
        if (victorious || wasLost) {
            this.bossBar.setName(Component.literal(this.bossBar.getName().getString() + " - " + Component.translatable(victorious ? "event.minecraft.raid.victory" : "event.minecraft.raid.defeat").getString()));
        }
        if (victorious && invasionLeader != null && invasionLeader.isAlive()) {
            invasionLeader.giveExperiencePoints(200);
            invasionLeader.sendSystemMessage(Component.translatable("event.otherworld.invasion_victory").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GREEN));
            if (!wasLoadedAfterFinished) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(invasionLeader);
                if (character.isSoldier()) {
                    character.addOccupationStatus(invasionLeader);
                    character.sendPacket(invasionLeader);
                }
            }
        }
        else if (wasLost && invasionLeader != null && invasionLeader.isAlive()) {
            invasionLeader.sendSystemMessage(Component.translatable("event.otherworld.invasion_defeat").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_RED));
        }
    }

    @Override
    public float getHealthOfLivingSoldiers() {
        float f = 0.0F;
        for(AbstractPerson soldier : this.invaders) {
            f += soldier.getHealth();
        }
        if (invasionLeader != null) {
            f += invasionLeader.getHealth();
        }
        return f;
    }

    @Override
    public float getTotalHealth() {
        float f = 0.0F;
        for(AbstractPerson soldier : this.invaders) {
            f += soldier.getHealth();
        }
        if (invasionLeader != null) {
            f += invasionLeader.getHealth();
        }
        return f;
    }

    @Override
    protected Predicate<AbstractPerson> validTarget() {
        return super.validTarget().and(IOccupation::isSoldier);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putUUID("invasionLeader", invasionLeaderUUID);
        tag.putString("toInvade", toInvade.location().toString());
        return tag;
    }

    public static InvasionEvent load(CompoundTag tag) {
        List<IRaces.Race> invaders = new ArrayList<>();
        List<IRaces.Race> defenders = new ArrayList<>();

        CompoundTag invade = tag.getCompound("invader");
        for (int i=0; i<invade.size(); i++) {
            invaders.add(IRaces.Race.valueOf(invade.getString("invader" + i)));
        }

        CompoundTag defend = tag.getCompound("defender");
        for (int i=0; i<defend.size(); i++) {
            defenders.add(IRaces.Race.valueOf(defend.getString("defender" + i)));
        }

        UUID player = tag.getUUID("invasionLeader");
        TagKey<Structure> invadeStructure = TagKey.create(Registries.STRUCTURE, new ResourceLocation(tag.getString("toInvade")));

        InvasionEvent warEvent = new InvasionEvent(player, invadeStructure, invaders.toArray(new IRaces.Race[0]), defenders.toArray(new IRaces.Race[0]));

        warEvent.activeWar = tag.getBoolean("active");
        warEvent.disabled = tag.getBoolean("disabled");
        warEvent.loaded = false;

        if ((warEvent.activeWar || tag.contains("target_pos")) && !warEvent.disabled) {
            warEvent.hasStarted = true;
            warEvent.timeElapsed = tag.getInt("time_elapsed");
            warEvent.defendingRace = IRaces.Race.valueOf(tag.getString("defender_race"));
            int[] pos = tag.getIntArray("target_pos");
            warEvent.invasionTargetPos = new BlockPos(pos[0], pos[1], pos[2]);
            warEvent.totalHealth = tag.getFloat("invader_health");
        }
        else {
            warEvent.wasLost = tag.getBoolean("lost");
            warEvent.victorious = tag.getBoolean("victory");
            if (warEvent.victorious || warEvent.wasLost) {
                warEvent.wasLoadedAfterFinished = true;
            }
        }

        warEvent.bossBar.setName(warEvent.getBossBarName());

        return warEvent;
    }

    @Override
    public boolean alwaysTicking() {
        return true;
    }

    @Override
    public String tag() {
        return "invasion";
    }

    @Override
    public InvasionEvent copy() {
        return new InvasionEvent(invasionLeaderUUID, toInvade, invader, defender);
    }
}
