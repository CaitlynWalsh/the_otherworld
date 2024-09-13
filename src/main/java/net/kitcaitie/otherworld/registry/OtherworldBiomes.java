package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;

public class OtherworldBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(Registries.BIOME, Otherworld.MODID);

    public static final ResourceKey<Biome> ENCHANTIA = createKey("enchantia");
    public static final ResourceKey<Biome> ROSEIA = createKey("roseia");
    public static final ResourceKey<Biome> DEEPWOODS = createKey("deepwoods");
    public static final ResourceKey<Biome> EMBERIA = createKey("emberia");
    public static final ResourceKey<Biome> GLACEIA = createKey("glaceia");

    public static final ResourceKey<Biome> OASIA = createKey("oasia");

    public static final ResourceKey<Biome> WITHERING_FOREST = createKey("withering_forest");
    public static final ResourceKey<Biome> CORRUPTED_DUNES = createKey("corrupted_dunes");

    public static ResourceKey<Biome> createKey(String name) {
        return ResourceKey.create(BIOMES.getRegistryKey(), new ResourceLocation(Otherworld.MODID, name));
    }

}
