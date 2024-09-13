package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;

public class OtheranOreBlock extends DropExperienceBlock implements IWorlds {
    private final Worlds world;

    public OtheranOreBlock(Properties properties, IntProvider provider, Worlds world) {
        super(properties, provider);
        this.world = world;
    }

    @Override
    public Worlds getWorldType() {
        return world;
    }
}
