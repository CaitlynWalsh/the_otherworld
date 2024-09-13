package net.kitcaitie.otherworld.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

public interface IOccupation {

    enum Occupation {
        TRAVELER(0),
        VILLAGER(0),
        SOLDIER(4),
        SPY(0),
        BRUTE(0),
        LEADER(0),
        ROUGE(0),
        CRIMINAL(4),
        OUTCAST(0);

        private final int maxStatus;

        Occupation(int maxStatus) {
            this.maxStatus = maxStatus;
        }

        public int getMaxStatus() {
            return maxStatus;
        }
    }

    enum VillagerType {
        FARMER(Blocks.COMPOSTER),
        COOK(Blocks.SMOKER),
        PRIEST(Blocks.LECTERN),
        WEAVER(Blocks.LOOM),
        MERCHANT(Blocks.BARREL),
        UNEMPLOYED(null);

        private final @Nullable Block workStation;

        VillagerType(@Nullable Block workStation) {
            this.workStation = workStation;
        }

        public @Nullable Block getWorkStation() {
            return workStation;
        }
    }

    Occupation getOccupation();

    default int getOccupationStatus() {
        return 0;
    }


    default boolean isVillager() {
        return getOccupation() == Occupation.VILLAGER;
    }

    default boolean isSoldier() {
        return getOccupation() == Occupation.SOLDIER;
    }

    default boolean isBrute() {
        return getOccupation() == Occupation.BRUTE;
    }

    default boolean isWarrior() {
        return isSoldier() || isBrute();
    }

    default boolean isLeader() {
        return getOccupation() == Occupation.LEADER;
    }

    default boolean isCriminal() {
        return getOccupation() == Occupation.CRIMINAL;
    }

    default boolean isExCriminal() {
        return getOccupation() == Occupation.OUTCAST;
    }

    default boolean isRouge() {
        return getOccupation() == Occupation.ROUGE;
    }

    default boolean isSpy() {
        return getOccupation() == Occupation.SPY;
    }

    default boolean isCaughtSpying() {
        return isSpy() && getOccupationStatus() <= -1;
    }

    default boolean isWantedCriminal() {
        return isCriminal() && getOccupationStatus() >= Occupation.CRIMINAL.getMaxStatus();
    }

    default boolean isImprisoned() {
        return false;
    }
}
