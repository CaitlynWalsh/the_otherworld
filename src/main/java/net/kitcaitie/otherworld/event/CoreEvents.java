package net.kitcaitie.otherworld.event;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.*;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.kitcaitie.otherworld.common.entity.npcs.*;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Ghoul;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Undertaker;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Vanisher;
import net.kitcaitie.otherworld.common.player.IOtherworldPlayer;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Storyline;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.EventHandler;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Otherworld.MODID)
public class CoreEvents {
    public static final List<EntityType<?>> NO_ADDED_AGGRO_TO_HUMANS = List.of(EntityType.ENDERMAN, EntityType.ZOMBIFIED_PIGLIN, EntityType.WARDEN, EntityType.GIANT);

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ((IOtherworldPlayer)player).getPlayerCharacter().sendPacket(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity().level instanceof ServerLevel) {
            ((IOtherworldPlayer)event.getOriginal()).getPlayerCharacter().sendPacket(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerCharacter character = PlayerCharacter.load(((IOtherworldPlayer)event.getEntity()).getPlayerCharacterTag());
        if (!character.isHuman() && event.getEntity() instanceof ServerPlayer player) {
            if (player.getRespawnPosition() == null && character.getHomePos() != null && character.getHomePos() != BlockPos.ZERO) {
                if (player.getServer() != null) {
                    ServerLevel level = player.getServer().getLevel(character.isGhoul() ? Otherworld.UNDERLANDS : Otherworld.OTHERWORLD);
                    if (level != null) {
                        player.teleportTo(level, character.getHomePos().getX(), character.getHomePos().getY(), character.getHomePos().getZ(), 0.0F, 0.0F);
                        player.setRespawnPosition(level.dimension(), character.getHomePos(), 0.0F, true, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide()) {
            if (event.getEntity() instanceof Player player) {
                ((IOtherworldPlayer)player).getPlayerCharacter().sendPacket(player);
                if (player.getServer() != null && player.getServer().isSingleplayer()) {
                    Storyline.saveAndLoadData(player.getServer().overworld());
                }
                //TODO: FUTURE UPDATE
                /*
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);

                if (character.isFairie() && player.hasEffect(MobEffects.WITHER)) {
                    character.setRace(IRaces.Race.GHOUL);
                    character.setStarted(player.level.dimension().equals(Otherworld.UNDERLANDS));
                    character.syncClient((ServerPlayer) player);
                    ((ServerPlayer) player).sendSystemMessage(Component.translatable("event.otherworld.ghoul_conversion").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.ITALIC), true);
                    player.level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0F, 0.8F);
                    player.setHealth(player.getMaxHealth());
                    event.setCanceled(true);
                }
                */
            }
            if (event.getEntity().getCombatTracker().getKiller() instanceof Player player) {
                PlayerCharacter character = ((IOtherworldPlayer)player).getPlayerCharacter();
                EventHandler.updateCombatQuests(player, event.getEntity());
                Bounty bounty = character.getCurrentBounty();
                if (bounty != null) {
                    if (bounty.getCriminalId() != 0 && event.getEntity().getId() == bounty.getCriminalId()) {
                        bounty.complete(player, event.getEntity(), (ServerLevel) event.getEntity().level);
                    }
                    else if (bounty.getCriminalUuid() != null && event.getEntity().getUUID().equals(bounty.getCriminalUuid())) {
                        bounty.complete(player, event.getEntity(), (ServerLevel) event.getEntity().level);
                    }
                }
            }
        }
    }

    //TODO: FUTURE UPDATE
    /*
    @SubscribeEvent
    public static void onMobEffectApplied(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof Player player) {
            if (PowerUtils.accessPlayerCharacter(player).isFairie() && !player.hasEffect(MobEffects.WITHER) && event.getEffectInstance().getEffect().equals(MobEffects.WITHER)) {
                player.level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0F, 0.8F);
            }
        }
    }
     */

    @SubscribeEvent
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide() && event.getTarget() != null) {
            EventHandler.updateInteractQuests(event.getEntity(), event.getTarget());
        }
    }

    @SubscribeEvent
    public static void onEntitySetToHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            event.setAmount(PowerUtils.handleDamageAmount(PowerUtils.accessPlayerCharacter(player), entity, event.getSource(), event.getAmount()));
        }
        else if (entity instanceof IRaces) {
            event.setAmount(PowerUtils.handleDamageAmount((IRaces)entity, entity, event.getSource(), event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(event.player);
            PowerUtils.powerTick(character, event.player);
            if (event.phase == TickEvent.Phase.START) {
                if (event.player.level instanceof ServerLevel serverLevel) {
                    if (event.player.tickCount % 2 == 1) {
                        PowerUtils.handleBasePowersAndWeaknesses(character, event.player, serverLevel);
                    }
                }
            }
            else if (event.phase == TickEvent.Phase.END) {
                if (event.player.level instanceof ServerLevel) {
                    character.tick((ServerPlayer) event.player);
                    if (event.player.tickCount % 60 == 1) {
                        EventHandler.updateTravelQuests(event.player, event.player.level);
                    }
                }
            }
        }
        else if (event.side == LogicalSide.CLIENT) {
            if (event.phase == TickEvent.Phase.END) {
                if (event.player.level.isClientSide()) {
                    PowerUtils.spawnParticles(PowerUtils.accessPlayerCharacter(event.player), event.player, event.player.level);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.getLevel().isClientSide()) {
            if (entity instanceof Enemy && entity instanceof Mob mob) {
                if (NO_ADDED_AGGRO_TO_HUMANS.contains(mob.getType()) || mob instanceof NeutralMob) return;
                mob.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(mob, AbstractPerson.class, 10, true, false, (ent) -> ent instanceof MonsterTargetable tgt && tgt.canTarget(mob)));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Otherworld.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventBusEvents {
        @SubscribeEvent
        public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(OtherworldEntities.HUMAN.get(), Human.setAttributes());
            event.put(OtherworldEntities.ROSEIAN.get(), Roseian.setAttributes());
            event.put(OtherworldEntities.FAIRIE.get(), Fairie.setAttributes());
            event.put(OtherworldEntities.GHOUL.get(), Ghoul.setAttributes());
            event.put(OtherworldEntities.EMBERIAN.get(), Emberian.setAttributes());
            event.put(OtherworldEntities.ONI.get(), Oni.setAttributes());
            event.put(OtherworldEntities.ICEIAN.get(), Iceian.setAttributes());
            event.put(OtherworldEntities.DESCENDANT.get(), Descendant.setAttributes());

            event.put(OtherworldEntities.UNDERTAKER.get(), Undertaker.setAttributes());
            event.put(OtherworldEntities.VANISHER.get(), Vanisher.setAttributes());

            event.put(OtherworldEntities.WHISP.get(), Whisp.createAttributes());
            event.put(OtherworldEntities.PHLYMP.get(), Phlymp.createAttributes());
            event.put(OtherworldEntities.ROSEIAN_RABBIT.get(), RoseianRabbit.createAttributes());
            event.put(OtherworldEntities.CRYSTLING.get(), Crystling.createAttributes());
            event.put(OtherworldEntities.ROSADILLO.get(), Rosadillo.createAttributes());
            event.put(OtherworldEntities.FAIRLING.get(), Fairling.createAttributes());
            event.put(OtherworldEntities.GRIZZLY.get(), Grizzly.createAttributes());
            event.put(OtherworldEntities.GOATEER.get(), Goateer.createAttributes());
            event.put(OtherworldEntities.FERAL_WOLF.get(), FeralWolf.createAttributes().build());
            event.put(OtherworldEntities.PYROBOAR.get(), Pyroboar.createAttributes());
            event.put(OtherworldEntities.SNOWPAKA.get(), Llama.createAttributes().build());
            event.put(OtherworldEntities.FIGHTING_FISH.get(), BetaFish.setAttributes());

            event.put(OtherworldEntities.OTHERLY_MINION.get(), OtherlyMinion.setAttributes());
        }
    }

}
