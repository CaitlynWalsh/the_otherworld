package net.kitcaitie.otherworld.mixins;

import net.kitcaitie.otherworld.common.story.Storyline;
import net.kitcaitie.otherworld.common.world.BlockData;
import net.kitcaitie.otherworld.common.world.OtherworldServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements OtherworldServerLevel {
    @Shadow @Nonnull public abstract MinecraftServer getServer();
    @Shadow public abstract ServerLevel getLevel();

    @Shadow public abstract DimensionDataStorage getDataStorage();
    @Unique private Storyline otherworld_storyline = null;
    @Unique private BlockData otherworld_BlockData = null;

    @Override
    public BlockData getBlockData() {
        if (otherworld_BlockData == null) {
            otherworld_BlockData = getDataStorage().computeIfAbsent(BlockData::load, BlockData::new, BlockData.FILE_ID);
        }
        return otherworld_BlockData;
    }

    @Override
    public void setBlockData(@Nullable BlockData o) {
        otherworld_BlockData = o;
    }

    @Override
    public Storyline getStoryline() {
        if (otherworld_storyline == null) {
            otherworld_storyline = Storyline.accessServerStory(getServer());
        }
        return otherworld_storyline;
    }

    @Override
    public void setStoryline(@Nullable Storyline storyline) {
        otherworld_storyline = storyline;
    }
}
