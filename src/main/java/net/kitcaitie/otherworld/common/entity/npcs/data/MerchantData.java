package net.kitcaitie.otherworld.common.entity.npcs.data;

import net.kitcaitie.otherworld.common.IOccupation;
import net.minecraft.nbt.CompoundTag;

public class MerchantData {
    public static final int MIN_VILLAGER_LEVEL = 1;
    public static final int MAX_VILLAGER_LEVEL = 5;
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
    private IOccupation.VillagerType villagerType;
    private int level;
    private boolean dirty;

    public MerchantData(IOccupation.VillagerType villagerType, int level) {
        this.villagerType = villagerType;
        this.level = Math.max(1, level);
    }

    public CompoundTag writeNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("VillagerType", this.villagerType.name());
        tag.putInt("Level", this.level);
        tag.putBoolean("Dirty", this.dirty);
        return tag;
    }

    public boolean isDirty() {
        return dirty;
    }

    private MerchantData setDirty() {
        this.dirty = true;
        return this;
    }

    public static MerchantData readNbt(CompoundTag tag) {
        return new MerchantData(IOccupation.VillagerType.valueOf(tag.getString("VillagerType")), tag.getInt("Level"));
    }

    public IOccupation.VillagerType getVillagerType() {
        return villagerType;
    }

    public int getLevel() {
        return level;
    }

    public MerchantData setVillagerType(IOccupation.VillagerType villagerType) {
        return new MerchantData(villagerType, this.level).setDirty();
    }

    public MerchantData setLevel(int level) {
        return new MerchantData(this.villagerType, level).setDirty();
    }

    public static int getMinXpPerLevel(int i) {
        return canLevelUp(i) ? NEXT_LEVEL_XP_THRESHOLDS[i - 1] : 0;
    }

    public static int getMaxXpPerLevel(int i) {
        return canLevelUp(i) ? NEXT_LEVEL_XP_THRESHOLDS[i] : 0;
    }

    public static boolean canLevelUp(int level) {
        return level >= MIN_VILLAGER_LEVEL && level < MAX_VILLAGER_LEVEL;
    }
}
