package net.kitcaitie.otherworld;

import com.mojang.logging.LogUtils;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.entity.*;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.Emberian;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Vanisher;
import net.kitcaitie.otherworld.common.entity.npcs.inv.CraftingManager;
import net.kitcaitie.otherworld.common.items.OtherworldTab;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.story.StoryModes;
import net.kitcaitie.otherworld.common.story.Storyline;
import net.kitcaitie.otherworld.common.world.BlockData;
import net.kitcaitie.otherworld.common.world.OtherworldServerLevel;
import net.kitcaitie.otherworld.network.NetworkMessages;
import net.kitcaitie.otherworld.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(Otherworld.MODID)
public class Otherworld {
    // TODO: UPDATE UNDERLANDS AND CREATE UNDERLANDS PORTAL

    public static final String MODID = "otherworld";
    public static String VERSION = "";
    public static final StoryModes STORY_MODES = StoryModes.INSTANCE;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Level> OTHERWORLD = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Otherworld.MODID, "otherworld"));
    public static final ResourceKey<Level> UNDERLANDS = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Otherworld.MODID, "underlands"));


    public Otherworld() {
        VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();

        LOGGER.info("The Otherworld (Version: " + VERSION + ") has begun initializing! Good luck on your adventure!");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OtherworldConfigs.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OtherworldConfigs.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, OtherworldConfigs.SERVER_SPEC);

        OtherworldBlocks.register(modEventBus);
        OtherworldItems.register(modEventBus);
        OtherworldEntities.register(modEventBus);
        OtherworldMobEffects.register(modEventBus);
        OtherworldParticles.register(modEventBus);
        OtherworldMenus.register(modEventBus);
        OtherworldFeatures.register(modEventBus);
        OtherworldStructures.register(modEventBus);
        OtherworldEntityData.register(modEventBus);
        OtherworldSounds.register(modEventBus);
        OtherworldPOIs.register(modEventBus);
        OtherworldRecipes.register(modEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> OtherworldClient::initialize);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerEntitySpawns);
        modEventBus.addListener(OtherworldTab::registerCreativeTabs);

        MinecraftForge.EVENT_BUS.addListener(this::onLevelSave);
        MinecraftForge.EVENT_BUS.addListener(this::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onWakeUp);
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Storyline getStoryline(ServerLevel level) {
        return ((OtherworldServerLevel)level).getStoryline();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(OtherworldGameRules::init);
        event.enqueueWork(NetworkMessages::register);
        event.enqueueWork(OtherworldEvents::register);
    }

    private void onLevelLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            Storyline.saveAndLoadData(event.getLevel());
            BlockData.saveAndLoadData(event.getLevel());
            CraftingManager.Registry.initRecipes(event.getLevel());
            syncConfigGameRules((ServerLevel) event.getLevel());
        }
    }

    private static void syncConfigGameRules(ServerLevel level) {
        if (OtherworldConfigs.SERVER_SPEC.isLoaded()) {
            OtherworldConfigs.SERVER.randomizedCharacters.set(level.getGameRules().getRule(OtherworldGameRules.RULE_RANDOMIZED_CHARACTERS).get());
        }
    }

    private void onLevelSave(LevelEvent.Save event) {
        if (!event.getLevel().isClientSide()) {
            Storyline.saveAndLoadData(event.getLevel());
            BlockData.saveAndLoadData(event.getLevel());
            syncConfigGameRules((ServerLevel) event.getLevel());
        }
    }

    private void onWakeUp(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            level.getServer().overworld().setDayTime(event.getNewTime());
            if (level.isRaining() || level.isThundering()) {
                level.getServer().overworld().setWeatherParameters(Mth.randomBetweenInclusive(level.random, 12000, 180000), Mth.randomBetweenInclusive(level.random, 12000, 180000), false, false);
            }
        }
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.haveTime()) {
            Story story = getStoryline(event.getServer().overworld()).getStory();
            story.tick(event.getServer());
        }
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == OtherworldTab.OTHERWORLD_TAB) {
            for (RegistryObject<Item> object : OtherworldItems.ITEMS.getEntries()) {
                if (!OtherworldItems.SECRET_ITEMS.contains(object))
                    event.accept(new ItemStack(object.get()));
            }
        }
    }

    private void registerEntitySpawns(SpawnPlacementRegisterEvent event) {
        event.register(OtherworldEntities.HUMAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);
        event.register(OtherworldEntities.ONI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);
        event.register(OtherworldEntities.ROSEIAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);
        event.register(OtherworldEntities.FAIRIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);
        event.register(OtherworldEntities.EMBERIAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Emberian::checkEmberianSpawning, null);
        event.register(OtherworldEntities.ICEIAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);
        event.register(OtherworldEntities.GHOUL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractPerson::checkPersonSpawning, null);

        event.register(OtherworldEntities.WHISP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Whisp::checkSpawnRules, null);
        event.register(OtherworldEntities.PHLYMP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Phlymp::checkPhlympSpawnRules, null);

        event.register(OtherworldEntities.CRYSTLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.ROSEIAN_RABBIT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.ROSADILLO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.FAIRLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Fairling::checkSpawnRules, null);
        event.register(OtherworldEntities.GRIZZLY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.GOATEER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.PYROBOAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.SNOWPAKA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, OtheranAnimal::checkSpawnRules, null);
        event.register(OtherworldEntities.FIGHTING_FISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BetaFish::checkBetaFishSpawnRules, null);
        event.register(OtherworldEntities.VANISHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Vanisher::checkVanisherSpawnRules, null);
    }

}
