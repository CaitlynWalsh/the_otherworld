package net.kitcaitie.otherworld.common;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public interface IRaces extends IPowerTypes {

    enum Race {
        HUMAN(IWorlds.Worlds.OVERWORLD, null, null),
        ONI(IWorlds.Worlds.DEEPWOODS, new ResourceLocation(Otherworld.MODID, "oni"), null),
        ROSEIAN(IWorlds.Worlds.ROSEIA, new ResourceLocation(Otherworld.MODID, "roseian"), null),
        FAIRIE(IWorlds.Worlds.ENCHANTIA, new ResourceLocation(Otherworld.MODID, "fairie"), null),
        EMBERIAN(IWorlds.Worlds.EMBERIA, new ResourceLocation(Otherworld.MODID, "emberian"), null),
        ICEIAN(IWorlds.Worlds.GLACEIA, new ResourceLocation(Otherworld.MODID, "iceian"), null),
        GHOUL(IWorlds.Worlds.UNDERLANDS, new ResourceLocation(Otherworld.MODID, "ghoul"), null),

        // ---MIXED RACES--- //
        OASIAN(IWorlds.Worlds.OASIA, null, Pair.of(ICEIAN.ordinal(), EMBERIAN.ordinal())),
        ONIMAN(null, null, Pair.of(HUMAN.ordinal(), ONI.ordinal())),
        FAIRIAN(null, null, Pair.of(FAIRIE.ordinal(), ROSEIAN.ordinal()));

        @Nullable IWorlds.Worlds world;
        @Nullable ResourceLocation personType;
        @Nullable Pair<Integer, Integer> parents;

        Race(IWorlds.Worlds world, @Nullable ResourceLocation personType, @Nullable Pair<Integer, Integer> parents) {
            this.world = world;
            this.personType = personType;
            this.parents = parents;
        }

        @Nullable
        public IWorlds.Worlds getHomeWorld() {
            return world;
        }

        @Nullable
        public ResourceLocation getPersonType() {
            return personType;
        }

        public boolean isMixedRace() {
            return ordinal() > 6;
        }

        public static Race getRaceFromBreeding(Race parent1, Race parent2) {
            if (parent1 == parent2) return parent1;
            Race[] values = values();
            return Arrays.stream(values).filter((race) -> {
                if (race.parents != null) {
                    int i = race.parents.getFirst();
                    int i1 = race.parents.getSecond();

                    if (parent1.ordinal() == i || parent1.ordinal() == i1) {
                        return parent2.ordinal() == i || parent2.ordinal() == i1;
                    }
                }
                return false;
            }).findFirst().orElse(null);
        }
    }

    Race getRace();

    default List<Powers> getPowers() {
        switch (getRace()) {
            case ROSEIAN -> {
                return List.of(Powers.REGENERATION, Powers.HEALING, Powers.RESISTANT_WITHER);
            }
            case FAIRIE -> {
                return List.of(Powers.RESISTANT_MAGIC, Powers.MELEE_MAGIC);
            }
            case ONI -> {
                return List.of(Powers.MELEE_STRENGTH, Powers.RESISTANT_MELEE, Powers.NATURAL_ARMOR);
            }
            case EMBERIAN -> {
                return List.of(Powers.RESISTANT_FIRE, Powers.FIRE_STRENGTH, Powers.MELEE_FIRE);
            }
            case ICEIAN -> {
                return List.of(Powers.RESISTANT_FREEZE, Powers.FREEZE_STRENGTH, Powers.MELEE_FREEZE);
            }
            case GHOUL -> {
                return List.of(Powers.NIGHT_VISION, Powers.RESISTANT_WITHER, Powers.WITHER_REGENERATION);
            }
            case FAIRIAN -> {
                return List.of(Powers.REGENERATION, Powers.HEALING, Powers.RESISTANT_MAGIC);
            }
            case ONIMAN -> {
                return List.of(Powers.RESISTANT_MELEE);
            }
            case OASIAN -> {
                return List.of(Powers.WATER_ABSORPTION, Powers.SUNLIGHT_REGENERATION, Powers.RESISTANT_POISON);
            }
        }
        return List.of();
    }

    default List<Weakness> getWeakness() {
        switch (getRace()) {
            case ROSEIAN, FAIRIAN -> {
                return List.of(Weakness.MELEE);
            }
            case FAIRIE -> {
                return List.of(Weakness.WITHER);
            }
            case ONI, ONIMAN -> {
                return List.of(Weakness.MAGIC);
            }
            case EMBERIAN -> {
                return List.of(Weakness.FREEZE, Weakness.WATER, Weakness.COLD_BIOMES);
            }
            case ICEIAN -> {
                return List.of(Weakness.FIRE, Weakness.HOT_BIOMES);
            }
            case GHOUL -> {
                return List.of(Weakness.HEALING);
            }
            case OASIAN -> {
                return List.of(Weakness.FIRE, Weakness.FREEZE);
            }
        }
        return List.of();
    }

    default List<Abilities> getSpecialAbilities() {
        switch (getRace()) {
            case ROSEIAN -> {
                return List.of(Abilities.HEALING_AURA);
            }
            case FAIRIE -> {
                return List.of(Abilities.MAGIC_ABILITY);
            }
            case ONI, ONIMAN -> {
                return List.of(Abilities.STRENGTH_BOOST);
            }
            case EMBERIAN -> {
                return List.of(Abilities.FIRE_AURA);
            }
            case ICEIAN -> {
                return List.of(Abilities.FROST_AURA);
            }
            case GHOUL -> {
                return List.of(Abilities.WITHER_AURA);
            }
            case FAIRIAN -> {
                return List.of(Abilities.HEALING_AURA, Abilities.MAGIC_ABILITY);
            }
        }
        return List.of();
    }

    default boolean hasPowers() {
        return !isHuman();
    }
    default boolean isAggressiveRace() {
        return isHuman() || isEmberian() || isOni() || getRace() == Race.ONIMAN || isGhoul();
    }

    default boolean isRoseian() {
        return getRace() == Race.ROSEIAN;
    }

    default boolean isFairie() {
        return getRace() == Race.FAIRIE;
    }

    default boolean isOni() {
        return getRace() == Race.ONI;
    }

    default boolean isEmberian() {
        return getRace() == Race.EMBERIAN;
    }

    default boolean isIceian() {
        return getRace() == Race.ICEIAN;
    }

    default boolean isGhoul() {
        return getRace() == Race.GHOUL;
    }
    default boolean isHuman() {
        return getRace() == Race.HUMAN;
    }
    default boolean isMixedRace() {
        return getRace().isMixedRace();
    }

    default boolean hasWings() {
        return isFairie() || getRace() == Race.FAIRIAN;
    }

    default boolean hasHorns() {
        return isOni() || getRace() == Race.ONIMAN;
    }

    default boolean canHaveVariant() {
        return isOni();
    }

}
