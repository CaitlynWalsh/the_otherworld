package net.kitcaitie.otherworld.common.story.global;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.util.SpecialSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WarEvent extends GlobalStoryEvent {
    public static final int ACTIVE_TICK = 200;
    public final IRaces.Race[] invader;
    public final IRaces.Race[] defender;
    public IRaces.Race defendingRace;
    public BlockPos invasionTargetPos;
    public BlockPos invasionSpawnPos;
    protected List<AbstractPerson> invaders = new ArrayList<>();
    protected boolean activeWar;
    protected int timeElapsed = 0;
    protected int tick = 0;
    protected float totalHealth;
    public boolean wasLost;
    public boolean victorious;

    public WarEvent(IRaces.Race[] invader, IRaces.Race[] defender) {
        super(GlobalEventType.WAR);
        this.invader = invader;
        this.defender = defender;
        this.totalHealth = -1.0F;
        this.bossBar = new ServerBossEvent(
                Component.empty(),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_10
        );
        this.id = tag() + "_" + Arrays.toString(invader) + Arrays.toString(defender);
    }

    @Override
    public String tag() {
        return "war";
    }

    @Override
    public WarEvent copy() {
        WarEvent warEvent = new WarEvent(this.invader, this.defender);
        return warEvent;
    }

    protected Component getBossBarName() {
        Component raceName = Component.translatable("races.otherworld." + invader[0].name().toLowerCase() + ".name");
        return Component.empty().append(raceName).append(" ").append(Component.translatable("event.minecraft.raid"));
    }

    @Override
    public boolean canStart(MinecraftServer server) {
        return isLoaded();
    }

    @Override
    public boolean canContinue(MinecraftServer server) {
        return !isWarOver(server);
    }

    @Override
    public void start(MinecraftServer server) {
        if (!activeWar) {
            List<IRaces.Race> defenders = Arrays.asList(defender);
            defendingRace = defenders.get(random.nextInt(defenders.size()));

            ServerPlayer player;
            if (!server.isSingleplayer()) {
                List<ServerPlayer> playerList = server.getPlayerList().getPlayers();
                player = playerList.isEmpty() ? null : playerList.get(random.nextInt(playerList.size()));
            }
            else {
                player = server.getPlayerList().getPlayers().isEmpty() ? null : server.getPlayerList().getPlayers().get(0);
            }

            if (player != null) {
                List<TagKey<Structure>> structureList = defendingRace.getHomeWorld().getStructures();
                if (structureList != null && !structureList.isEmpty()) {
                    TagKey<Structure> toInvade = structureList.get(random.nextInt(structureList.size()));
                    invasionTargetPos = SpecialSpawner.getVillageSpawnLocation(player.getLevel(), player, toInvade);
                    if (invasionTargetPos != null && invasionTargetPos != BlockPos.ZERO) {
                        invasionTargetPos = invasionTargetPos.atY(96);
                        if (story.getNearbyActiveWar(invasionTargetPos, 1152, defendingRace, null) == null) {
                            this.invasionSpawnPos = findInvasionSpawnPos(player.getLevel(), invasionTargetPos, 2);
                            if (invasionSpawnPos != null && findCitizens(server)) {
                                List<IRaces.Race> invaders = Arrays.asList(invader);
                                findSoldiers(server);
                                if (this.invaders.isEmpty() || this.invaders.size() < getInvaderGroupSizeRange()[0]) {
                                    for (int i = 0; i < random.nextInt(getInvaderGroupSizeRange()[0], getInvaderGroupSizeRange()[1]); i++) {
                                        ResourceLocation soldierType = invaders.get(random.nextInt(invaders.size())).getPersonType();
                                        if (soldierType != null) {
                                            Optional<EntityType<?>> soldier = EntityType.byString(soldierType.toString());
                                            if (soldier.isPresent()) {
                                                spawnSoldier(soldier.get(), invasionSpawnPos, player.getLevel());
                                            } else {
                                                Otherworld.LOGGER.error("WarEvent: could not spawn soldier of type " + soldierType);
                                                Otherworld.LOGGER.error("WarEvent: EntityType " + soldierType + " is not present");
                                            }
                                        }
                                    }
                                }
                                this.activeWar = true;
                                this.totalHealth = getTotalHealth();
                                this.victorious = false;
                                this.wasLost = false;
                                this.bossBar.setName(getBossBarName());
                                super.start(server);
                                return;
                            }
                        }
                    }
                }
            }
            this.defendingRace = null;
            this.invasionTargetPos = null;
            this.invasionSpawnPos = null;
        }
    }

    @Override
    public boolean alwaysTicking() {
        return this.activeWar;
    }

    public Story getStory() {
        return story;
    }

    protected void spawnSoldier(EntityType<?> soldierType, BlockPos start, ServerLevel level) {
        Entity entity = SpawnUtil.trySpawnMob((EntityType<? extends Mob>) soldierType, MobSpawnType.EVENT, level, start, 8, 6, 4, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER).orElse(null);
        if (entity instanceof AbstractPerson soldier) {
            soldier.setOccupation(IOccupation.Occupation.SOLDIER);
            soldier.getInventory().removeAllItems();
            soldier.finalizeSpawn(level, level.getCurrentDifficultyAt(soldier.blockPosition()), MobSpawnType.EVENT, null, null);
            if (!soldier.isOni() && !soldier.isFairie() && !soldier.isGhoul()) {
                soldier.setMale(true);
                AbstractPerson.nameGen.createName(soldier);
            }
            soldier.getInventory().addItem(soldier.getSoldierWeapon());
            invaders.add(soldier);
            level.addFreshEntityWithPassengers(soldier);
            soldier.joinWarEvent(this);
        }
    }

    protected void findSoldiers(MinecraftServer server) {
        if (invasionTargetPos == null) return;
        ServerLevel serverLevel = server.getLevel(Otherworld.OTHERWORLD);
        if (serverLevel == null) return;
        List<AbstractPerson> soldiers = serverLevel.getEntitiesOfClass(AbstractPerson.class, new AABB(this.invasionTargetPos).inflate(50.0D), (ent) -> Arrays.asList(invader).contains(ent.getRace()) && ent.isWarrior());
        for (AbstractPerson soldier : soldiers) {
            if (!invaders.contains(soldier)) {
                soldier.joinWarEvent(this);
                invaders.add(soldier);
                this.totalHealth += soldier.getHealth();
            }
        }
    }

    public boolean findCitizens(MinecraftServer server) {
        ServerLevel serverLevel = server.getLevel(Otherworld.OTHERWORLD);
        if (serverLevel == null) return false;
        List<AbstractPerson> citizens = serverLevel.getEntitiesOfClass(AbstractPerson.class, new AABB(this.invasionTargetPos).inflate(50.0D), (ent) -> validTarget().test(ent));
        return !citizens.isEmpty();
    }

    protected boolean isWarOver(MinecraftServer server) {
        if (this.activeWar && tick < ACTIVE_TICK) return false;
        ServerLevel serverLevel = server.getLevel(Otherworld.OTHERWORLD);
        if (serverLevel == null) return false;
        if (this.invasionTargetPos == null) return true;
        findSoldiers(server);
        if (invaders.isEmpty() && tick >= ACTIVE_TICK) {
            this.wasLost = true;
            this.victorious = false;
            return true;
        }
        if (!findCitizens(server) && tick >= ACTIVE_TICK) {
            this.wasLost = false;
            this.victorious = true;
            return true;
        }
        if (timeElapsed > 1200) {
            removeInvaders();
            return true;
        }
        return false;
    }

    public void updateBossbar() {
        if (this.totalHealth < 0.0F) {
            this.totalHealth = getTotalHealth();
        }
        this.bossBar.setProgress(Mth.clamp(this.getHealthOfLivingSoldiers() / totalHealth, 0.0F, 1.0F));
    }

    @Override
    public void tickBossBar(MinecraftServer server) {
        super.tickBossBar(server);
        updateBossbar();
        updatePlayers(server);
        bossBar.setVisible(this.hasActiveWar());
        if (this.invaders.isEmpty() || !this.hasActiveWar()) {
            bossBar.removeAllPlayers();
        }
    }

    public float getHealthOfLivingSoldiers() {
        float f = 0.0F;
        for(AbstractPerson soldier : this.invaders) {
            f += soldier.getHealth();
        }
        return f;
    }

    public float getTotalHealth() {
        float f = 0.0F;
        for (AbstractPerson soldier : this.invaders) {
            f += soldier.getMaxHealth();
        }
        return f;
    }

    private BlockPos findInvasionSpawnPos(ServerLevel level, BlockPos start, int dist) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int i=dist; i<dist*2; i++) {
            float f = level.random.nextFloat() * ((float)Math.PI * 2F);
            int j = start.getX() + Mth.floor(Mth.cos(f) * 16.0F * (float)i);
            int l = start.getZ() + Mth.floor(Mth.sin(f) * 16.0F * (float)i);
            int k = 96;
            mutable.set(j, k, l);
            if (level.hasChunksAt(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10) && level.isPositionEntityTicking(mutable) && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, level, mutable, EntityType.PLAYER) || level.getBlockState(mutable.below()).is(Blocks.SNOW) && level.getBlockState(mutable).isAir())) {
                return mutable;
            }
        }
        return null;
    }

    @Override
    public void tickEvent(MinecraftServer server) {
        ServerLevel level = server.getLevel(Otherworld.OTHERWORLD);
        if (level == null) return;

        tick++;

        if (level.isLoaded(this.invasionTargetPos)) {
            if (timeElapsed > 0) {
                invaders.removeIf(person -> person == null || !person.isAlive() || person.isRemoved());
                if (timeElapsed % 2 == 1) {
                    this.findSoldiers(server);
                }
            }
            timeElapsed++;
        }
    }

    protected void removeInvaders() {
        for (AbstractPerson person : invaders) {
            if (!person.level.hasNearbyAlivePlayer(person.blockPosition().getX(), person.blockPosition().getY(), person.blockPosition().getZ(), 60.0D)) {
                if (person.getSpawnType() == MobSpawnType.EVENT) {
                    person.discard();
                }
            }
        }
    }

    protected void forceRemoveInvaders() {
        for (AbstractPerson person : invaders) {
            if (person.getSpawnType() == MobSpawnType.EVENT) {
                person.discard();
            }
        }
    }

    @Override
    public void updatePlayers(MinecraftServer server) {
        if (this.hasStarted() && this.hasActiveWar()) {
            List<ServerPlayer> players;
            players = server.getPlayerList().getPlayers().stream().filter((player) -> story.getNearbyActiveWar(player.blockPosition(), 4608, null, null) == this).toList();
            if (!players.isEmpty()) {
                players.forEach(bossBar::addPlayer);
            }
            players = this.bossBar.getPlayers().stream().filter((player) -> story.getNearbyActiveWar(player.blockPosition(), 4608, null, null) != this).toList();
            if (!players.isEmpty()) {
                players.forEach(bossBar::removePlayer);
            }
        }
    }

    protected Predicate<AbstractPerson> validTarget() {
        return (person) -> {
            boolean flag = !person.isBaby() || activeWar;
            IRaces.Race inv = invader[0];
            if (inv == IRaces.Race.GHOUL) {
                return flag && person.getRace() != IRaces.Race.GHOUL;
            }
            if (inv == IRaces.Race.ONI || inv == IRaces.Race.EMBERIAN) {
                return flag && person.getRace() == defendingRace;
            }
            return person.getRace() == defendingRace && !person.isBaby();
        };
    }

    @Override
    public void stop(MinecraftServer server) {
        this.activeWar = false;
        this.timeElapsed = 0;
        this.tick = 0;
        this.bossBar.removeAllPlayers();
        this.bossBar.setVisible(false);
        this.invaders.clear();
        this.totalHealth = -1.0F;
        this.defendingRace = null;
        this.invasionTargetPos = null;
        this.invasionSpawnPos = null;
        this.wasLost = false;
        this.victorious = false;
        super.stop(server);
    }

    public boolean hasActiveWar() {
        return activeWar;
    }

    public int[] getInvaderGroupSizeRange() {
        return new int[]{8, 12};
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        CompoundTag invade = new CompoundTag();
        for (int i=0; i<invader.length; i++) {
            invade.putString("invader" + i, invader[i].name());
        }
        tag.put("invader", invade);

        CompoundTag defend = new CompoundTag();
        for (int i=0; i<defender.length; i++) {
            defend.putString("defender" + i, defender[i].name());
        }
        tag.put("defender", defend);
        tag.putBoolean("active", activeWar);
        tag.putBoolean("disabled", disabled);

        if (activeWar) {
            tag.putInt("time_elapsed", this.timeElapsed);
            tag.putString("defender_race", defendingRace.name());
            tag.putIntArray("target_pos", new int[]{invasionTargetPos.getX(), invasionTargetPos.getY(), invasionTargetPos.getZ()});
            tag.putFloat("invader_health", this.totalHealth);
        }

        tag.putBoolean("victory", victorious);
        tag.putBoolean("lost", wasLost);

        tag.putString("id", getID());

        return tag;
    }

    public boolean isWarFinished() {
        return victorious || wasLost;
    }

    public static WarEvent read(CompoundTag tag) {
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

        WarEvent warEvent = new WarEvent(invaders.toArray(new IRaces.Race[0]), defenders.toArray(new IRaces.Race[0]));

        warEvent.activeWar = tag.getBoolean("active");
        warEvent.disabled = tag.getBoolean("disabled");
        warEvent.loaded = false;
        warEvent.timeElapsed = 0;

        if (warEvent.activeWar) {
            if (!warEvent.disabled) {
                warEvent.hasStarted = true;
                warEvent.defendingRace = IRaces.Race.valueOf(tag.getString("defender_race"));
                int[] pos = tag.getIntArray("target_pos");
                warEvent.invasionTargetPos = new BlockPos(pos[0], pos[1], pos[2]);
                warEvent.totalHealth = tag.getFloat("invader_health");
            }
        }
        else {
            warEvent.wasLost = tag.getBoolean("lost");
            warEvent.victorious = tag.getBoolean("victory");
        }

        return warEvent;
    }

    @Override
    public String toString() {
        return getID();
    }
}
