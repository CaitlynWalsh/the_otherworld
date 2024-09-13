package net.kitcaitie.otherworld.common.world.grower;

import net.kitcaitie.otherworld.registry.OtherworldFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class MystwoodTreeGrower extends AbstractMegaTreeGrower {

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource source) {
        if (source.nextInt(4) == 0) return OtherworldFeatures.MEGA_MYSTWOOD_TREE;
        return OtherworldFeatures.MYSTWOOD_TREE;
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource source, boolean bool) {
        return null;
    }
}
