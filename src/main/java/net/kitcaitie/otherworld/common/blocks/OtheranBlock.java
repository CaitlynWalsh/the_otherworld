package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.world.level.block.Block;

public class OtheranBlock extends Block implements IWorlds {
    private Worlds worlds;

    public OtheranBlock(Properties properties, Worlds worlds) {
        super(properties);
        this.worlds = worlds;
    }


    public Worlds getWorldType() {
        return worlds;
    }

}
