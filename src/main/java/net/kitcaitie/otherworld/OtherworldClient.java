package net.kitcaitie.otherworld;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.client.DialogueListener;
import net.kitcaitie.otherworld.client.gui.screen.*;
import net.kitcaitie.otherworld.client.layers.ClothesLayer;
import net.kitcaitie.otherworld.client.layers.EmberianGlowLayer;
import net.kitcaitie.otherworld.client.layers.FairieWingsLayer;
import net.kitcaitie.otherworld.client.layers.HornedLayer;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.particle.FlurryParticle;
import net.kitcaitie.otherworld.common.player.IOtherworldPlayer;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.kitcaitie.otherworld.registry.OtherworldKeybinding;
import net.kitcaitie.otherworld.registry.OtherworldParticles;
import net.kitcaitie.otherworld.registry.OtherworldScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OtherworldClient {
    public static Dialogue DIALOGUE;
    public static final List<IRaces.Race> STARTER_RACES = List.of(IRaces.Race.HUMAN, IRaces.Race.ONI, IRaces.Race.ROSEIAN, IRaces.Race.FAIRIE, IRaces.Race.EMBERIAN, IRaces.Race.ICEIAN);
    public static boolean characterScreenOpen = false;

    public static void initialize() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new OtherworldConfigScreen()));

        eventBus.addListener(OtherworldClient::clientSetup);
        eventBus.addListener(OtherworldClient::renderLayers);
        eventBus.addListener(OtherworldClient::registerOverlays);
        eventBus.addListener(OtherworldClient::registerParticleProviders);
        eventBus.addListener(OtherworldClient::onKeyRegister);
        eventBus.addListener(OtherworldClient::addReloadListener);

        MinecraftForge.EVENT_BUS.addListener(OtherworldClient::onKeyPressed);
    }

    private static void addReloadListener(RegisterClientReloadListenersEvent event) {
        DIALOGUE = new Dialogue();
        event.registerReloadListener(DialogueListener.INSTANCE);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> OtherworldScreens.register(event));
    }

    private static void renderLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer rendererWide = event.getSkin("default");
        PlayerRenderer rendererSlim = event.getSkin("slim");
        if (rendererWide != null) {
            rendererWide.addLayer(new ClothesLayer<>(rendererWide));
            rendererWide.addLayer(new EmberianGlowLayer<>(rendererWide));
            rendererWide.addLayer(new FairieWingsLayer<>(rendererWide, event.getEntityModels()));
            rendererWide.addLayer(new HornedLayer<>(rendererWide, event.getEntityModels()));
        }
        if (rendererSlim != null) {
            rendererSlim.addLayer(new ClothesLayer<>(rendererSlim));
            rendererSlim.addLayer(new EmberianGlowLayer<>(rendererSlim));
            rendererSlim.addLayer(new FairieWingsLayer<>(rendererSlim, event.getEntityModels()));
            rendererSlim.addLayer(new HornedLayer<>(rendererSlim, event.getEntityModels()));
        }
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(OtherworldParticles.FLURRY.get(), FlurryParticle.Provider::new);
    }

    private static void registerOverlays(RegisterGuiOverlaysEvent event) {
        OtherworldScreens.registerOverlays(event);
    }

    private static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(OtherworldKeybinding.TOGGLE_PLAYER_CHARACTER_SCREEN);
    }

    private static void onKeyPressed(InputEvent.Key event) {
        if (Minecraft.getInstance().player != null) {
            if (OtherworldKeybinding.TOGGLE_PLAYER_CHARACTER_SCREEN.consumeClick()) {
                if (!characterScreenOpen) {
                    if (Minecraft.getInstance().screen == null) {
                        OtherworldClient.openKeyBoundCharacterScreen();
                    }
                }
                else {
                    Minecraft.getInstance().setScreen(null);
                    OtherworldClient.characterScreenOpen = false;
                }
            }
        }
    }

    public static PlayerCharacter getPlayerCharacter() {
        return PlayerCharacter.load(((IOtherworldPlayer)Minecraft.getInstance().player).getPlayerCharacterTag());
    }

    public static void generateRandomCharacter() {
        Player player = Minecraft.getInstance().player;
        PlayerCharacter character = new PlayerCharacter();
        character.setRace(STARTER_RACES.get(player.getRandom().nextInt(STARTER_RACES.size())));
        character.setMale(player.getRandom().nextBoolean());
        character.setCreated(true);
        character.setStarted(false);
        character.sendPacket(player);
    }

    private static void openScreen(Screen screen) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().setScreen(screen);
        }
    }

    public static void handleCharacterScreen() {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().setScreen(new CreateCharacterScreen());
        }
    }

    public static void openKeyBoundCharacterScreen() {
        Minecraft.getInstance().setScreen(new PlayerCharacterScreen());
        OtherworldClient.characterScreenOpen = true;
    }

    public static void updateDialogueWarEvents(List<String> warEvents) {
        DIALOGUE.activeWarEvents = warEvents;
    }

    public static void openQuestScreen(AbstractPerson person, Quest quest) {
        openScreen(new ChooseQuestScreen(person, quest));
    }

    public static void openOccupationScreen(AbstractPerson person, IOccupation.Occupation occupation) {
        openScreen(new ChooseOccupationScreen(person, occupation));
    }

    public static void openBountyScreen(Bounty bounty) {
        openScreen(new ChooseBountyScreen(bounty));
    }
}
