package net.kitcaitie.otherworld.registry;

import com.google.common.collect.ImmutableSet;
import net.kitcaitie.otherworld.Otherworld;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldPOIs {
    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, Otherworld.MODID);

    public static final RegistryObject<PoiType> OTHERWORLD_PORTAL = POIS.register("otherworld_portal", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.OTHERWORLD_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1));

    public static final RegistryObject<PoiType> FAIRLING_HOMES = POIS.register("fairling_home", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.FAIRLING_DWELLING.get().getStateDefinition().getPossibleStates()), 0, 1));

    // PRISONS
    public static final RegistryObject<PoiType> ONI_PRISON = POIS.register("oni_prison", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.ONI_PRISON_TELEPORTER.get().getStateDefinition().getPossibleStates()), 0, 1));
    public static final RegistryObject<PoiType> ROSEIAN_PRISON = POIS.register("roseian_prison", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.ROSEIAN_PRISON_TELEPORTER.get().getStateDefinition().getPossibleStates()), 0, 1));
    public static final RegistryObject<PoiType> FAIRIE_PRISON = POIS.register("fairie_prison", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.FAIRIE_PRISON_TELEPORTER.get().getStateDefinition().getPossibleStates()), 0, 1));
    public static final RegistryObject<PoiType> EMBERIAN_PRISON = POIS.register("emberian_prison", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.EMBERIAN_PRISON_TELEPORTER.get().getStateDefinition().getPossibleStates()), 0, 1));
    public static final RegistryObject<PoiType> ICEIAN_PRISON = POIS.register("iceian_prison", () ->
            new PoiType(ImmutableSet.copyOf(OtherworldBlocks.ICEIAN_PRISON_TELEPORTER.get().getStateDefinition().getPossibleStates()), 0, 1));


    public static void register(IEventBus eventBus) {
        POIS.register(eventBus);
    }
}
