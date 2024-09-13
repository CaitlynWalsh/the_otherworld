package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.*;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.kitcaitie.otherworld.common.entity.npcs.*;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Ghoul;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Undertaker;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Vanisher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Otherworld.MODID);

    //PEOPLE
    public static final RegistryObject<EntityType<Human>> HUMAN = ENTITIES.register("human", () ->
            EntityType.Builder.of(Human::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "human").toString()));

    public static final RegistryObject<EntityType<Fairie>> FAIRIE = ENTITIES.register("fairie", () ->
            EntityType.Builder.of(Fairie::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "fairie").toString()));

    public static final RegistryObject<EntityType<Roseian>> ROSEIAN = ENTITIES.register("roseian", () ->
            EntityType.Builder.of(Roseian::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "roseian").toString()));

    public static final RegistryObject<EntityType<Oni>> ONI = ENTITIES.register("oni", () ->
            EntityType.Builder.of(Oni::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "oni").toString()));

    public static final RegistryObject<EntityType<Emberian>> EMBERIAN = ENTITIES.register("emberian", () ->
            EntityType.Builder.of(Emberian::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "emberian").toString()));

    public static final RegistryObject<EntityType<Iceian>> ICEIAN = ENTITIES.register("iceian", () ->
            EntityType.Builder.of(Iceian::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "emberian").toString()));

    public static final RegistryObject<EntityType<Ghoul>> GHOUL = ENTITIES.register("ghoul", () ->
            EntityType.Builder.of(Ghoul::new, MobCategory.CREATURE).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "ghoul").toString()));
    public static final RegistryObject<EntityType<Descendant>> DESCENDANT = ENTITIES.register("descendant", () ->
            EntityType.Builder.of(Descendant::new, MobCategory.MISC).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "descendant").toString()));

    // MONSTERS
    public static final RegistryObject<EntityType<Undertaker>> UNDERTAKER = ENTITIES.register("undertaker", () ->
            EntityType.Builder.of(Undertaker::new, MobCategory.MISC).sized(0.6F, 1.8F).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "undertaker").toString()));
    public static final RegistryObject<EntityType<Vanisher>> VANISHER = ENTITIES.register("vanisher", () ->
            EntityType.Builder.of(Vanisher::new, MobCategory.MONSTER).sized(0.6F, 1.8F).clientTrackingRange(8)
                    .build(new ResourceLocation(Otherworld.MODID, "vanisher").toString()));

    //BOSSES
    public static final RegistryObject<EntityType<OtherlyMinion>> OTHERLY_MINION = ENTITIES.register("otherly_minion", () ->
            EntityType.Builder.of(OtherlyMinion::new, MobCategory.MISC).fireImmune().sized(0.6f, 2.85f).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "otherly_minion").toString()));

    //AMBIENT
    public static final RegistryObject<EntityType<Whisp>> WHISP = ENTITIES.register("whisp", () ->
            EntityType.Builder.of(Whisp::new, MobCategory.AMBIENT).fireImmune().sized(0.35F, 0.35F).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "whisp").toString()));


    //ANIMALS
    public static final RegistryObject<EntityType<Phlymp>> PHLYMP = ENTITIES.register("phlymp", () ->
            EntityType.Builder.of(Phlymp::new, MobCategory.AMBIENT).sized(1.0F, 2.8F).clientTrackingRange(5)
                    .build(new ResourceLocation(Otherworld.MODID, "phlymp").toString()));
    public static final RegistryObject<EntityType<RoseianRabbit>> ROSEIAN_RABBIT = ENTITIES.register("roseian_rabbit", () ->
            EntityType.Builder.of(RoseianRabbit::new, MobCategory.CREATURE).sized(0.5f, 0.7f).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "roseian_rabbit").toString()));

    public static final RegistryObject<EntityType<Crystling>> CRYSTLING = ENTITIES.register("crystling", () ->
            EntityType.Builder.of(Crystling::new, MobCategory.CREATURE).sized(0.5f, 0.5f).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "crystling").toString()));

    public static final RegistryObject<EntityType<Rosadillo>> ROSADILLO = ENTITIES.register("rosadillo", () ->
            EntityType.Builder.of(Rosadillo::new, MobCategory.CREATURE).sized(0.6f, 1.0f).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "rosadillo").toString()));

    public static final RegistryObject<EntityType<Fairling>> FAIRLING = ENTITIES.register("fairling", () ->
            EntityType.Builder.of(Fairling::new, MobCategory.CREATURE).sized(0.35F, 0.6F).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "fairling").toString()));

    public static final RegistryObject<EntityType<Grizzly>> GRIZZLY = ENTITIES.register("grizzly", () ->
            EntityType.Builder.of(Grizzly::new, MobCategory.CREATURE).sized(1.8F, 2.4F).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "grizzly").toString()));

    public static final RegistryObject<EntityType<Goateer>> GOATEER = ENTITIES.register("goateer", () ->
            EntityType.Builder.of(Goateer::new, MobCategory.CREATURE).sized(1.4F, 1.6F).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "goateer").toString()));

    public static final RegistryObject<EntityType<FeralWolf>> FERAL_WOLF = ENTITIES.register("feral_wolf", () ->
            EntityType.Builder.of(FeralWolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "feral_wolf").toString()));

    public static final RegistryObject<EntityType<Pyroboar>> PYROBOAR = ENTITIES.register("pyroboar", () ->
            EntityType.Builder.of(Pyroboar::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(4)
                    .build(new ResourceLocation(Otherworld.MODID, "pyroboar").toString()));

    public static final RegistryObject<EntityType<Snowpaka>> SNOWPAKA = ENTITIES.register("snowpaka", () ->
            EntityType.Builder.of(Snowpaka::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10)
                    .build(new ResourceLocation(Otherworld.MODID, "snowpaka").toString()));

    public static final RegistryObject<EntityType<BetaFish>> FIGHTING_FISH = ENTITIES.register("fighting_fish", () ->
            EntityType.Builder.of(BetaFish::new, MobCategory.WATER_AMBIENT).sized(0.4F, 0.3F).clientTrackingRange(3)
                    .build(new ResourceLocation(Otherworld.MODID, "fighting_fish").toString()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
