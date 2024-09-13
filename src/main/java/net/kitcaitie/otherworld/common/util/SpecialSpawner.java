package net.kitcaitie.otherworld.common.util;

import io.netty.buffer.Unpooled;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.client.gui.menu.LoadingScreenMenu;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SpecialSpawner {
    protected final Player player;
    protected final IRaces.Race playerRace;

    public SpecialSpawner(Player player, IRaces.Race race) {
        this.player = player;
        this.playerRace = race;
    }

    public void createLoadingScreen() {
        if (this.playerRace != IRaces.Race.HUMAN) {
            if (!player.level.isClientSide()) {
                if (!player.getServer().getWorldData().worldGenOptions().generateStructures()) return;
                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new LoadingScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer())
                                .writeBlockPos(player.blockPosition()));
                    }
                });
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_AMBIENT, SoundSource.AMBIENT, 0.9F, 1.0F);
            player.level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER, SoundSource.AMBIENT, 0.9F, 0.8F);
        }
    }

    public boolean spawnInVillage() {
        if (!player.level.isClientSide()) {
            try {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                BlockPos spawnPos = findVillageToStart(player, playerRace, null);
                if (playerRace != IRaces.Race.HUMAN && spawnPos != null) {
                    ResourceKey<Level> startDimension;
                    if (playerRace == IRaces.Race.GHOUL) startDimension = Otherworld.UNDERLANDS;
                    else startDimension = Otherworld.OTHERWORLD;
                    ServerLevel level = serverPlayer.level.getServer().getLevel(startDimension);
                    serverPlayer.teleportTo(level, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
                    serverPlayer.setRespawnPosition(startDimension, spawnPos, 0.0F, true, false);
                    serverPlayer.level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.AMBIENT, 0.9F, 1.0F);
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                    character.setHomePos(spawnPos);
                    character.setStarted(true);
                    character.sendPacket(serverPlayer);
                    return true;
                } else if (playerRace == IRaces.Race.HUMAN) {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                    character.setStarted(true);
                    character.sendPacket(serverPlayer);
                    return true;
                } else {
                    Otherworld.LOGGER.error("SpecialSpawner: Could not find spawning position for " + player.getDisplayName().getString() + ": 'spawnPos' is null.");
                    Otherworld.LOGGER.debug("SpecialSpawner: Structure is: " + getVillageType(playerRace) + ".");
                    player.closeContainer();
                    return false;
                }
            }
            catch (Exception e) {
                Otherworld.LOGGER.error("SpecialSpawner: Could not find spawning position for " + player.getDisplayName().getString() + ": " + e.getMessage());
                Otherworld.LOGGER.debug("SpecialSpawner: Structure is: " + getVillageType(playerRace) + ".");
                player.closeContainer();
                return false;
            }
        }
        return false;
    }

    public static BlockPos findVillageToStart(Player player, IRaces.Race race, @Nullable TagKey<Structure> type) {
        TagKey<Structure> villageType = type == null ? getVillageType(race) : type;
        if (!player.level.isClientSide() && villageType != null) {
            ResourceKey<Level> startDimension;
            if (race != IRaces.Race.HUMAN) {
                if (race == IRaces.Race.GHOUL) startDimension = Otherworld.UNDERLANDS;
                else startDimension = Otherworld.OTHERWORLD;
            }
            else return null;
            ServerLevel level = player.level.getServer().getLevel(startDimension);
            BlockPos blockPos = getVillageSpawnLocation(level, player, villageType);
            if (blockPos != null) {
                return blockPos.atY(startDimension == Otherworld.UNDERLANDS ? 34 : 96);
            }
        }
        return null;
    }

    public static BlockPos getStablePos(Entity entity, BlockPos blockPos, Predicate<BlockPos> predicate, double range) {
        if (predicate.test(blockPos)) return blockPos;
        else {
            Stream<BlockPos> blocks = BlockPos.betweenClosedStream(entity.getBoundingBox().inflate(range)).filter(predicate);
            Optional<BlockPos> optional = blocks.findAny();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    @Nullable
    public static TagKey<Structure> getVillageType(IRaces.Race races) {
        IWorlds.Worlds worlds = races.getHomeWorld();
        if (worlds != null) {
            List<TagKey<Structure>> keys = worlds.getStructures();
            if (keys != null && !keys.isEmpty()) {
                return keys.get(0);
            }
        }
        return null;
    }

    public static TagKey<Structure> getOutpostType(ServerPlayer player, PlayerCharacter character) {
        IWorlds.Worlds worlds = null;

        if (character.isCriminal()) worlds = character.getRace().getHomeWorld();
        else {
            Story story = Otherworld.getStoryline(player.getLevel()).getStory();
            for (IRaces.Race race : IRaces.Race.values()) {
                if (story.areRacesAtWar(race, character.getRace())) {
                    worlds = race.getHomeWorld();
                    break;
                }
            }
        }

        if (worlds != null) {
            List<TagKey<Structure>> keys = worlds.getStructures();
            if (keys != null && keys.size() >= 2) {
                return keys.get(1);
            }
        }
        return null;
    }

    public static ArmorStand createDummy(ServerLevel level, Player player, ResourceKey<Level> dimension) {
        ServerLevel newLevel = level.getServer().getLevel(dimension);
        ArmorStand armorStand = new ArmorStand(newLevel, player.getX(), player.getY(), player.getZ());
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setNoGravity(true);
        newLevel.addFreshEntity(armorStand);
        return armorStand;
    }

    public static BlockPos getVillageSpawnLocation(ServerLevel level, Entity entity, TagKey<Structure> type) {
        if (type == null) {
            Otherworld.LOGGER.error("SpecialSpawner: Cannot find structure! TagKey<Structure> type is null.");
            return null;
        }
        return level.findNearestMapStructure(type, new BlockPos((int)entity.getX(), (int)entity.getY(), (int)entity.getZ()), 1000, false);
    }

}
