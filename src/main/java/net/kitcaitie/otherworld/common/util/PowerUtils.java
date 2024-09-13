package net.kitcaitie.otherworld.common.util;

import com.google.common.collect.Lists;
import net.kitcaitie.otherworld.common.IPowerTypes;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.player.IOtherworldPlayer;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.registry.OtherworldParticles;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.List;

public class PowerUtils {
    public static final List<MobEffect> SPELLS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(MobEffects.WEAKNESS);
        list.add(MobEffects.MOVEMENT_SLOWDOWN);
        list.add(MobEffects.BLINDNESS);
        list.add(MobEffects.LEVITATION);
    });
    public static final AttributeModifier NATURAL_STRENGTH = new AttributeModifier("natural_strength", 4.0D, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier NATURAL_KNOCKBACK_RESISTANCE = new AttributeModifier("natural_knockback_resistance", 0.25F, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier NATURAL_ARMOR = new AttributeModifier("natural_armor", 4.0D, AttributeModifier.Operation.ADDITION);

    public static void createToughAttributes(IRaces iRaces, LivingEntity player) {
        if (PowerUtils.shouldCreateToughAttributes(iRaces, player)) {
            List<IPowerTypes.Powers> powers = iRaces.getPowers();
            if (powers.contains(IPowerTypes.Powers.MELEE_STRENGTH)) {
                player.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(NATURAL_STRENGTH);
            }
            if (powers.contains(IPowerTypes.Powers.RESISTANT_MELEE)) {
                player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addTransientModifier(NATURAL_KNOCKBACK_RESISTANCE);
            }
            if (powers.contains(IPowerTypes.Powers.NATURAL_ARMOR)) {
                player.getAttribute(Attributes.ARMOR).addTransientModifier(NATURAL_ARMOR);
            }
        }
    }

    public static boolean shouldCreateToughAttributes(IRaces races, LivingEntity entity) {
        if (races.hasToughAttributes()) {
            return !entity.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(NATURAL_STRENGTH)
                    && !entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).hasModifier(NATURAL_KNOCKBACK_RESISTANCE)
                    && !entity.getAttribute(Attributes.ARMOR).hasModifier(NATURAL_ARMOR);
        }
        return false;
    }

    public static void handleBasePowersAndWeaknesses(IRaces iRaces, LivingEntity entity, ServerLevel level) {
        if (iRaces.hasPowers()) {
            List<IPowerTypes.Powers> powers = iRaces.getPowers();
            List<IPowerTypes.Weakness> weaknesses = iRaces.getWeakness();
            if (powers.contains(IPowerTypes.Powers.NIGHT_VISION)) {
                BlockPos pos = entity.blockPosition().above();
                int darkness = level.isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
                if (darkness < 7) {
                    entity.removeEffect(MobEffects.BLINDNESS);
                    entity.addEffect(tickingMobEffect(MobEffects.NIGHT_VISION, 1, 619));
                }
                else {
                    entity.removeEffect(MobEffects.NIGHT_VISION);
                    entity.addEffect(tickingMobEffect(MobEffects.BLINDNESS, 0, 619));
                }
            }
            if (entity.getHealth() < entity.getMaxHealth()) {
                if (powers.contains(IPowerTypes.Powers.REGENERATION)) {
                    if (entity.isCrouching() && !entity.hasEffect(MobEffects.REGENERATION)) {
                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 2));
                    }
                }
                else if (powers.contains(IPowerTypes.Powers.SUNLIGHT_REGENERATION)) {
                    if (level.isDay() && level.canSeeSky(entity.blockPosition()) && !level.isRaining()) {
                        if (!level.dimensionType().hasCeiling() && level.dimensionType().hasSkyLight()) {
                            if (!entity.hasEffect(MobEffects.REGENERATION)) {
                                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
                            }
                        }
                    }
                }
            }
            if (iRaces.hasWings() && !entity.isCrouching()) {
                if (!entity.isFallFlying()) entity.addEffect(tickingMobEffect(MobEffects.SLOW_FALLING, 0));
            }
            if (entity.isInWaterRainOrBubble() && powers.contains(IPowerTypes.Powers.WATER_ABSORPTION)) {
                entity.addEffect(tickingMobEffect(MobEffects.ABSORPTION, 1));
            }
            if (entity.isOnFire() && powers.contains(IPowerTypes.Powers.FIRE_STRENGTH)) {
                entity.addEffect(tickingMobEffect(MobEffects.MOVEMENT_SPEED, 0));
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_BOOST, 0));
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_RESISTANCE, 0));
            }
            else if ((entity.isFreezing() || entity.isFullyFrozen()) && powers.contains(IPowerTypes.Powers.FREEZE_STRENGTH)) {
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_RESISTANCE, 1));
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_BOOST, 0));
            }
            else if (iRaces.isWeak(iRaces, entity)) {
                entity.addEffect(tickingMobEffect(MobEffects.MOVEMENT_SLOWDOWN, 0));
                if (weaknesses.contains(IPowerTypes.Weakness.WATER) && entity.isInWaterRainOrBubble()) {
                    entity.addEffect(tickingMobEffect(MobEffects.WEAKNESS, 0));
                }
            }
            if (level.getBiomeManager().getBiome(entity.blockPosition()).containsTag(Tags.Biomes.IS_SNOWY)
                    && powers.contains(IPowerTypes.Powers.FREEZE_STRENGTH)) {
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_RESISTANCE, 0));
            }
            if (level.getBiomeManager().getBiome(entity.blockPosition()).containsTag(Tags.Biomes.IS_HOT)
                    && powers.contains(IPowerTypes.Powers.FIRE_STRENGTH)) {
                entity.addEffect(tickingMobEffect(MobEffects.DAMAGE_RESISTANCE, 0));
            }
        }
    }

    public static MobEffectInstance tickingMobEffect(MobEffect effect, int amplifier) {
        return new MobEffectInstance(effect, 5, amplifier, false, false);
    }

    public static MobEffectInstance tickingMobEffect(MobEffect effect, int amplifier, int duration) {
        return new MobEffectInstance(effect, duration, amplifier, false, false);
    }

    public static void powerTick(IRaces races, LivingEntity entity) {
        handleIceianFrostPower(races, entity);
    }

    public static void handleIceianFrostPower(IRaces races, LivingEntity entity) {
        if (races.isIceian() && !entity.isPassenger()) {
            if (entity.isOnGround() && entity.level instanceof ServerLevel level) {
                BlockState blockstate = Blocks.FROSTED_ICE.defaultBlockState();
                BlockState blockstate1 = level.getBlockState(entity.blockPosition().below());
                if (level.getBlockState(entity.blockPosition()).getMaterial().isReplaceable() && blockstate1.getBlock() == Blocks.WATER && blockstate1.getFluidState().isSource() && blockstate.canSurvive(level, entity.blockPosition().below())) {
                    level.setBlockAndUpdate(entity.blockPosition().below(), blockstate);
                    level.scheduleTick(entity.blockPosition().below(), Blocks.FROSTED_ICE, Mth.nextInt(entity.getRandom(), 60, 120));
                }
            }
        }
    }

    public static boolean handleDamageTrigger(IRaces iRaces, DamageSource source, LivingEntity entity) {
        if (!entity.level.isClientSide()) {
            if (source.getEntity() != null && !entity.isDamageSourceBlocked(source) && !source.is(DamageTypeTags.IS_PROJECTILE)) {
                if (source.getEntity() instanceof Player) {
                    handleMeleeTrigger(accessPlayerCharacter((Player) source.getEntity()), (LivingEntity) source.getEntity(), entity);
                }
                if (source.getEntity() instanceof IRaces) {
                    handleMeleeTrigger(((IRaces) source.getEntity()), (LivingEntity) source.getEntity(), entity);
                }
                return false;
            }
            if (iRaces.hasPowers()) {
                return iRaces.isImmuneTo(source, entity);
            }
        }
        return false;
    }

    public static float handleDamageAmount(IRaces iRaces, LivingEntity entity, DamageSource source, float initialAmount) {
        if (iRaces.isImmuneTo(source, entity) || iRaces.healsFrom(source)) return 0;
        else if (iRaces.takesLessDamageTo(source, entity)) return initialAmount - 1;
        else if (!entity.isDamageSourceBlocked(source) && iRaces.takesExtraDamageTo(source)) return initialAmount + 1;
        return initialAmount;
    }

    public static void handleMeleeTrigger(IRaces races, LivingEntity entity, LivingEntity punched) {
        if (races.getPowers().contains(IPowerTypes.Powers.MELEE_FIRE) && !races.isWeak(races, entity)) {
            if (punched instanceof Player player && accessPlayerCharacter(player).getPowers().contains(IPowerTypes.Powers.RESISTANT_FIRE)) return;
            if (punched instanceof IPowerTypes && ((IPowerTypes)punched).getPowers().contains(IPowerTypes.Powers.RESISTANT_FIRE)) return;
            punched.setSecondsOnFire(5);
            return;
        }
        if (races.getPowers().contains(IPowerTypes.Powers.MELEE_FREEZE) && !races.isWeak(races, entity)) {
            if (punched instanceof Player player && accessPlayerCharacter(player).getPowers().contains(IPowerTypes.Powers.RESISTANT_FREEZE)) return;
            if (punched instanceof IPowerTypes && ((IPowerTypes)punched).getPowers().contains(IPowerTypes.Powers.RESISTANT_FREEZE)) return;
            punched.setTicksFrozen(480);
            return;
        }
        if (races.getPowers().contains(IPowerTypes.Powers.MELEE_MAGIC) && !races.isWeak(races, entity) && entity.getRandom().nextFloat() < 0.5F) {
            punched.addEffect(new MobEffectInstance(SPELLS.get(punched.getRandom().nextInt(SPELLS.size())), 200, entity.getRandom().nextInt(2)));
        }
    }

    public static PlayerCharacter accessPlayerCharacter(Player player) {
        if (player instanceof ServerPlayer) return ((IOtherworldPlayer)player).getPlayerCharacter();
        return PlayerCharacter.load(((IOtherworldPlayer)player).getPlayerCharacterTag());
    }

    public static void spawnParticles(IRaces races, LivingEntity entity, LevelAccessor level) {
        if (entity.getRandom().nextInt(20) == 0) {
            switch (races.getRace()) {
                case FAIRIE -> {
                    if (entity.hasEffect(MobEffects.WITHER)) {
                        level.addParticle(ParticleTypes.SMOKE, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                    }
                }
                case EMBERIAN -> {
                    if (races.isWeak(races, entity)) {
                        level.addParticle(ParticleTypes.SMOKE, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                        return;
                    }
                    level.addParticle(ParticleTypes.FLAME, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                }
                case ICEIAN -> {
                    if (races.isWeak(races, entity)) {
                        level.addParticle(ParticleTypes.SPLASH, entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                        return;
                    }
                    level.addParticle(OtherworldParticles.FLURRY.get(), entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D), (entity.getRandom().nextDouble() - 0.5D) * 2.0D, -entity.getRandom().nextDouble(), (entity.getRandom().nextDouble() - 0.5D) * 2.0D);
                }
                case OASIAN -> {
                    if (races.isWeak(races, entity)) {
                        return;
                    }
                    level.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D), (entity.getRandom().nextDouble() - 0.5D) * 2.0D, -entity.getRandom().nextDouble(), (entity.getRandom().nextDouble() - 0.5D) * 2.0D);
                }
            }
        }
    }
}
