package net.kitcaitie.otherworld.registry;

import com.mojang.serialization.Codec;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.world.structure.OtheranStructure;
import net.kitcaitie.otherworld.common.world.structure.OtheranVillageStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldStructures {

    public static final ResourceKey<Structure> ONI_CAMP = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(Otherworld.MODID, "oni_camp"));
    public static final ResourceKey<Structure> ROSEIAN_TOWN = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(Otherworld.MODID, "roseian_town"));

    //TODO: FINISH RES KEYS

    public static final DeferredRegister<StructureType<?>> STRUCTURES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Otherworld.MODID);

    public static final RegistryObject<StructureType<OtheranStructure>> OTHERAN_STRUCTURE =
            STRUCTURES.register("otheran_structure", () -> explicitStructureTypeTyping(OtheranStructure.CODEC));

    public static final RegistryObject<StructureType<OtheranVillageStructure>> OTHERAN_VILLAGE =
            STRUCTURES.register("otheran_village", () -> explicitStructureTypeTyping(OtheranVillageStructure.CODEC));


    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(Codec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus eventBus) {
        STRUCTURES.register(eventBus);
    }

}
