package net.kitcaitie.otherworld.common;

import net.kitcaitie.otherworld.common.items.Armors;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

import java.util.List;

public interface IPowerTypes {

    enum Powers {
        REGENERATION,
        WITHER_REGENERATION,
        HEALING,
        NIGHT_VISION,
        MELEE_STRENGTH,
        FIRE_STRENGTH,
        FREEZE_STRENGTH,
        RESISTANT_MELEE,
        RESISTANT_FIRE,
        RESISTANT_FREEZE,
        RESISTANT_MAGIC,
        RESISTANT_WITHER,
        RESISTANT_POISON,
        MELEE_FIRE,
        MELEE_FREEZE,
        MELEE_MAGIC,
        NATURAL_ARMOR,
        WATER_ABSORPTION,
        SUNLIGHT_REGENERATION
    }

    enum Abilities {
        HEALING_AURA,
        FIRE_AURA,
        FROST_AURA,
        WITHER_AURA,
        STRENGTH_BOOST,
        MAGIC_ABILITY
    }

    enum Weakness {
        FIRE,
        WATER,
        FREEZE,
        COLD_BIOMES,
        HOT_BIOMES,
        WITHER,
        MAGIC,
        MELEE,
        HEALING
    }

    List<Powers> getPowers();
    List<Weakness> getWeakness();
    List<Abilities> getSpecialAbilities();

    default boolean hasFireResistance() {
        return getPowers().contains(Powers.RESISTANT_FIRE);
    }

    default boolean hasFreezeResistance() {
        return getPowers().contains(Powers.RESISTANT_FREEZE);
    }

    default boolean hasToughAttributes() {
        return getPowers().contains(Powers.MELEE_STRENGTH) || getPowers().contains(Powers.RESISTANT_MELEE) || getPowers().contains(Powers.NATURAL_ARMOR);
    }

    default double getMeleeStrength() {
        return 2.0D;
    }

    default float getMeleeResistantKnockback() {
        return 0.25F;
    }

    default double getNaturalArmor() {
        return 4.0D;
    }

    default boolean isImmuneTo(DamageSource source, LivingEntity entity) {
        if (source.is(DamageTypeTags.IS_FIRE) && hasFireResistance()) return true;
        if ((source.is(DamageTypeTags.IS_FREEZING) || source.getDirectEntity() instanceof Snowball) && hasFreezeResistance()) return true;
        if (hasElementalProtection(entity) && hasFreezeResistance()) {
            return source.is(DamageTypes.HOT_FLOOR);
        }
        if (source.is(DamageTypes.WITHER) && getPowers().contains(Powers.RESISTANT_WITHER)) return true;
        return source.is(DamageTypes.MAGIC) && getPowers().contains(Powers.RESISTANT_MAGIC);
    }

    default boolean isImmuneTo(MobEffect mobEffect) {
        if (mobEffect == MobEffects.POISON && getPowers().contains(Powers.RESISTANT_POISON)) return true;
        if (mobEffect == MobEffects.WITHER && getPowers().contains(Powers.RESISTANT_WITHER)) return true;
        return false;
    }

    default boolean takesLessDamageTo(DamageSource source, LivingEntity entity) {
        if ((source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING)) && hasElementalProtection(entity)) return true;
        return source.getEntity() != null && !source.is(DamageTypeTags.IS_PROJECTILE) && getPowers().contains(Powers.RESISTANT_MELEE);
    }

    default boolean takesExtraDamageTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FIRE) && getWeakness().contains(Weakness.FIRE)) return true;
        if (source.is(DamageTypeTags.IS_FREEZING) && getWeakness().contains(Weakness.FREEZE)) return true;
        if (source.getEntity() != null && getWeakness().contains(Weakness.MELEE)) return true;
        if (source.is(DamageTypes.WITHER) && getWeakness().contains(Weakness.WITHER)) return true;
        return source.is(DamageTypes.MAGIC) && getWeakness().contains(Weakness.MAGIC);
    }

    default boolean healsFrom(DamageSource source) {
        return source.is(DamageTypes.WITHER) && getPowers().contains(Powers.WITHER_REGENERATION);
    }

    default boolean isWeak(IPowerTypes powers, LivingEntity entity) {
        if (hasElementalProtection(entity)) return false;

        if (hasFireResistance() && entity.isOnFire()) return false;
        if (hasFreezeResistance() && (entity.isFreezing() || entity.isFullyFrozen())) return false;

        List<Weakness> weaknesses = powers.getWeakness();

        if (weaknesses.contains(Weakness.WATER)) {
            if (entity.isInWaterRainOrBubble()) {
                return true;
            }
        }
        if (weaknesses.contains(Weakness.FIRE)) {
            if (entity.isOnFire()) {
                return true;
            }
        }
        if (weaknesses.contains(Weakness.FREEZE)) {
            if (entity.isFreezing() || entity.isFullyFrozen()) {
                return true;
            }
        }
        if (weaknesses.contains(Weakness.HOT_BIOMES)) {
            if (entity.level.getBiome(entity.blockPosition()).is(Tags.Biomes.IS_HOT)) {
                return true;
            }
        }
        if (weaknesses.contains(Weakness.COLD_BIOMES)) {
            if (entity.level.getBiome(entity.blockPosition()).is(Tags.Biomes.IS_SNOWY)) {
                return true;
            }
        }

        return false;
    }

    default boolean hasElementalProtection(LivingEntity entity) {
        int flag = 0;

        for (ItemStack itemStack : entity.getArmorSlots()) {
            if (itemStack.getItem() instanceof ArmorItem armorItem) {
                if (hasFreezeResistance() && armorItem.getMaterial().getName().equals(Armors.SAPPHIRE.getName())) {
                    flag++;
                }
                else if (hasFireResistance() && armorItem.getMaterial().getName().equals(Armors.TOPAZ.getName())) {
                    flag++;
                }
            }
        }

        return flag >= 4;
    }

    // TODO: FUTURE UPDATE
    default boolean isTransformed() {
        return false;
    }

}
