package net.kitcaitie.otherworld.common.world.grower;

import net.kitcaitie.otherworld.registry.OtherworldFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RosewoodTreeGrower extends AbstractTreeGrower {

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_) {
        return OtherworldFeatures.ROSEWOOD_TREE;
    }
}
