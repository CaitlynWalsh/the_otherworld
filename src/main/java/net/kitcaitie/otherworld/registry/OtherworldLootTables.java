package net.kitcaitie.otherworld.registry;

import com.google.common.collect.Sets;
import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class OtherworldLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();

    public static final ResourceLocation ROSEIAN_RABBIT_WOOL = register("entities/roseian_rabbit_wool");


    public static ResourceLocation register(String namespace) {
        return register(new ResourceLocation(Otherworld.MODID, namespace));
    }

    public static ResourceLocation register(ResourceLocation resourceLocation) {
        if (LOCATIONS.add(resourceLocation)) {
            return resourceLocation;
        }
        else {
            throw new IllegalArgumentException(resourceLocation + " has already been registered in OtherworldLootTables");
        }
    }
}
