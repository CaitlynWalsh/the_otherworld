package net.kitcaitie.otherworld.common.story.global;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Undertaker;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.MobSpawnType;

public class UndertakerSpawningEvent extends GlobalStoryEvent {
    public static final String tag = "undertaker_spawner";

    public UndertakerSpawningEvent() {
        super(GlobalEventType.OTHER);
        this.id = tag();
    }

    @Override
    public boolean canStart(MinecraftServer server) {
        return true;
    }

    @Override
    public void tickEvent(MinecraftServer server) {
        if (random.nextFloat() < 0.04F) {
            ServerPlayer randomPlayer = server.getPlayerList().getPlayers().stream().filter((p) -> PowerUtils.accessPlayerCharacter(p).isGhoul() && !PowerUtils.accessPlayerCharacter(p).startedInVillage()).findFirst().orElse(null);
            if (randomPlayer != null && !randomPlayer.level.dimension().equals(Otherworld.UNDERLANDS) && randomPlayer.level.isNight() && randomPlayer.level.getEntitiesOfClass(Undertaker.class, randomPlayer.getBoundingBox().inflate(20.0D)).isEmpty()) {
                SpawnUtil.trySpawnMob(OtherworldEntities.UNDERTAKER.get(), MobSpawnType.EVENT, randomPlayer.getLevel(), randomPlayer.blockPosition(), 10, 8, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER);
            }
        }
    }

    @Override
    public boolean alwaysTicking() {
        return true;
    }


    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getID());
        return tag;
    }

    @Override
    public String tag() {
        return tag;
    }

    @Override
    public UndertakerSpawningEvent copy() {
        return new UndertakerSpawningEvent();
    }
}
