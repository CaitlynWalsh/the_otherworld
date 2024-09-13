package net.kitcaitie.otherworld.common.world;

import net.kitcaitie.otherworld.common.story.Storyline;
import org.jetbrains.annotations.Nullable;

public interface OtherworldServerLevel {

    Storyline getStoryline();
    BlockData getBlockData();

    void setStoryline(@Nullable Storyline storyline);
    void setBlockData(@Nullable BlockData o);
}
