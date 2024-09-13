package net.kitcaitie.otherworld.common.blocks.teleporters;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PrisonerTeleportBlock extends AbstractTeleportBlock  {
    protected final Supplier<Supplier<EntityType<? extends AbstractPerson>>> soldierType;
    public static final Predicate<LivingEntity> IS_PRISONER = (entity) -> {
        if (entity instanceof Player player && player.hasEffect(OtherworldMobEffects.CAPTURED.get())) {
            return !PowerUtils.accessPlayerCharacter(player).isImprisoned();
        }
        return false;
    };

    public PrisonerTeleportBlock(Properties properties, IWorlds.Worlds worlds, Supplier<Supplier<EntityType<? extends AbstractPerson>>> soldierType) {
        super(properties.randomTicks(), worlds);
        this.soldierType = soldierType;
    }

    @Override
    public void tick(BlockState p_222954_, ServerLevel level, BlockPos pos, RandomSource p_222957_) {
        this.spawnSoldiersIfNeeded(pos, level);
    }

    @Override
    public Predicate<LivingEntity> validTargets() {
        return IS_PRISONER.and((entity) -> {
            if (entity instanceof ServerPlayer player) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                try {
                    if (character.isCriminal()) {
                        return Objects.equals(character.getRace().getHomeWorld(), getWorldType());
                    }

                    Story story = Otherworld.getStoryline(player.getLevel()).getStory();

                    return story.areRacesAtWar(Arrays.stream(IRaces.Race.values()).filter((r) -> Objects.equals(r.getHomeWorld(), getWorldType())).findFirst().get(), PowerUtils.accessPlayerCharacter(player).getRace());
                }
                catch (Exception e) {
                    return false;
                }
            }
            return false;
        });
    }

    @Override
    protected void onTeleport(BlockState state, BlockPos blockPos, LivingEntity toTeleport) {
        if (toTeleport instanceof ServerPlayer player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            character.setPrisoner(player, player.getLevel(), true, !character.isCriminal() || character.getRace().getHomeWorld() != getWorldType());
            character.sendPacket(player);
            if (character.isWantedCriminal()) {
                Story story = Otherworld.getStoryline(player.getLevel()).getStory();
                story.getChapters().values().forEach((chapter) -> {
                    chapter.getMostWantedPlayers().removeIf((uuid) -> uuid.equals(player.getUUID()));
                    chapter.getBounties().removeIf((bounty) -> Objects.equals(bounty.getCriminalUuid(), player.getUUID()));
                });
            }
        }
    }

    private void spawnSoldiersIfNeeded(BlockPos blockPos, ServerLevel level) {
        EntityType<? extends AbstractPerson> type = soldierType.get().get();
        List<AbstractPerson> list = level.getEntitiesOfClass(AbstractPerson.class, new AABB(blockPos).inflate(20.0D), (ent) -> ent.getType().equals(type));
        if (list.isEmpty() || list.size() < 4) {
            WarEvent warEvent = Otherworld.getStoryline(level).getStory().getNearbyActiveWar(blockPos, 9216, Arrays.stream(IRaces.Race.values()).filter((r) -> Objects.equals(r.getHomeWorld(), getWorldType())).findFirst().get(), null);
            if (warEvent == null) {
                Optional<? extends AbstractPerson> person = SpawnUtil.trySpawnMob(type, MobSpawnType.NATURAL, level, blockPos, 12, 10, 6, (p_216416_, p_216417_, p_216418_, p_216419_, p_216420_) ->
                        SpawnUtil.Strategy.ON_TOP_OF_COLLIDER.canSpawnOn(p_216416_, p_216417_, p_216418_, p_216419_, p_216420_) && p_216418_.is(OtherworldTags.OTHERAN_SPAWNABLE_ON)
                );
                person.ifPresent((p) -> p.setHomePos(p.blockPosition()));
            }
        }
    }


}
