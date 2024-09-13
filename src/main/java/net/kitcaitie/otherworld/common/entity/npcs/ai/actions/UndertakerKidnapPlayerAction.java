package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Undertaker;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.common.util.SpecialSpawner;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

public class UndertakerKidnapPlayerAction extends Action {
    private Player player;
    private BlockPos spawnPos;
    private boolean teleported = false;

    public UndertakerKidnapPlayerAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.FOLLOW, Flags.MOVING, Flags.LOOKING, Flags.INTERACTING));
    }

    @Override
    public boolean canStart() {
        if (person.level.dimension().equals(Otherworld.UNDERLANDS)) return false;
        if (person.level.isDay()) return false;
        List<Player> players = person.level.getEntitiesOfClass(Player.class, person.getBoundingBox().inflate(20.0D), (p) -> PowerUtils.accessPlayerCharacter(p).isGhoul() && !PowerUtils.accessPlayerCharacter(p).startedInVillage());
        if (players.isEmpty()) return false;
        this.player = players.get(0);
        return true;
    }


    @Override
    public void start() {
        super.start();
        person.getNavigation().stop();
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 1200, 3));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 3));
        if (!player.level.isClientSide()) {
            ((ServerPlayer)player).sendSystemMessage(Component.translatable("event.otherworld.undertaker_spawn").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.ITALIC), true);
        }
        this.spawnPos = SpecialSpawner.findVillageToStart(player, IRaces.Race.GHOUL, null);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.spawnPos != null) {
            ActionUtils.lookAndMoveTo(person, player, 0.9D);
            if (person.distanceTo(player) <= 2.0D) {
                if (player instanceof ServerPlayer serverPlayer) {
                    ServerLevel underlands = serverPlayer.getServer().getLevel(Otherworld.UNDERLANDS);
                    serverPlayer.teleportTo(underlands, this.spawnPos.getX(), this.spawnPos.getY(), this.spawnPos.getZ(), 0.0F, 0.0F);
                    serverPlayer.setRespawnPosition(Otherworld.UNDERLANDS, this.spawnPos, 0.0F, true, false);
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(serverPlayer);
                    character.setStarted(true);
                    character.sendPacket(serverPlayer);
                    spawnCopy(underlands);
                    serverPlayer.removeEffect(MobEffects.DARKNESS);
                    serverPlayer.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    this.teleported = true;
                }
            }
        }
    }

    @Override
    public void stop() {
        if (player != null) {
            player.removeEffect(MobEffects.DARKNESS);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
        this.player = null;
        super.stop();
        if (teleported) {
            this.person.discard();
        }
    }

    @Override
    public boolean canContinue() {
        return !teleported && spawnPos != null && player != null && player.isAlive() && PowerUtils.accessPlayerCharacter(player).isGhoul();
    }

    @Override
    public Priority getPriority() {
        return Priority.P1;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    private void spawnCopy(ServerLevel level) {
        Undertaker copy = OtherworldEntities.UNDERTAKER.get().create(level);
        copy.finalizeSpawn(level, level.getCurrentDifficultyAt(this.spawnPos), MobSpawnType.EVENT, null, null);
        copy.moveTo(this.spawnPos, 0.0F, 0.0F);
        copy.setPersonData(person.getPersonData());
        copy.setCustomName(person.getDisplayName());
        level.addFreshEntity(copy);
    }
}
