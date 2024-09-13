package net.kitcaitie.otherworld.common.player;

import net.minecraft.nbt.CompoundTag;

public interface IOtherworldPlayer {
    PlayerCharacter getPlayerCharacter();

    CompoundTag getPlayerCharacterTag();

    void setPlayerCharacter(PlayerCharacter character);

    void setPlayerCharacterTag(CompoundTag tag);
}
