package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class OtherworldDamage {

    public static final ResourceKey<DamageType> SOUL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Otherworld.MODID, "soul"));


    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity ent1, @Nullable Entity ent2) {
        return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), ent1, ent2);
    }

}
