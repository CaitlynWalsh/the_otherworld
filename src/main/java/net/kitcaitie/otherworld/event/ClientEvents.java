package net.kitcaitie.otherworld.event;

import net.kitcaitie.otherworld.client.model.*;
import net.kitcaitie.otherworld.client.model.layers.FairieWings;
import net.kitcaitie.otherworld.client.model.layers.OniHorns;
import net.kitcaitie.otherworld.client.renderer.*;
import net.kitcaitie.otherworld.common.entity.npcs.Human;
import net.kitcaitie.otherworld.common.entity.npcs.Roseian;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Ghoul;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OtherworldEntities.HUMAN.get(), PersonEntityRenderer<Human>::new);
        event.registerEntityRenderer(OtherworldEntities.ROSEIAN.get(), PersonEntityRenderer<Roseian>::new);
        event.registerEntityRenderer(OtherworldEntities.FAIRIE.get(), FairieRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.GHOUL.get(), PersonEntityRenderer<Ghoul>::new);
        event.registerEntityRenderer(OtherworldEntities.ONI.get(), OniRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.EMBERIAN.get(), EmberianRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.ICEIAN.get(), IceianRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.DESCENDANT.get(), DescendantRenderer::new);

        event.registerEntityRenderer(OtherworldEntities.UNDERTAKER.get(), UndertakerRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.VANISHER.get(), VanisherRenderer::new);

        event.registerEntityRenderer(OtherworldEntities.WHISP.get(), WhispRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.PHLYMP.get(), PhlympRenderer::new);

        event.registerEntityRenderer(OtherworldEntities.ROSEIAN_RABBIT.get(), RoseianRabbitRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.CRYSTLING.get(), CrystlingRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.ROSADILLO.get(), RosadilloRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.FAIRLING.get(), FairlingRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.GRIZZLY.get(), GrizzlyRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.GOATEER.get(), GoateerRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.FERAL_WOLF.get(), FeralWolfRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.PYROBOAR.get(), PyroboarRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.SNOWPAKA.get(), SnowpakaRenderer::new);
        event.registerEntityRenderer(OtherworldEntities.FIGHTING_FISH.get(), BetaFishRenderer::new);

        event.registerEntityRenderer(OtherworldEntities.OTHERLY_MINION.get(), OtherlyMinionRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OniHorns.LAYER_LOCATION, OniHorns::createBodyLayer);
        event.registerLayerDefinition(FairieWings.LAYER_LOCATION, FairieWings::createBodyLayer);
        event.registerLayerDefinition(ModelWhisp.LAYER_LOCATION, ModelWhisp::createBodyLayer);
        event.registerLayerDefinition(ModelPhlymp.LAYER_LOCATION, ModelPhlymp::createBodyLayer);
        event.registerLayerDefinition(ModelRoseianRabbit.LAYER_LOCATION, ModelRoseianRabbit::createBodyLayer);
        event.registerLayerDefinition(ModelCrystling.LAYER_LOCATION, ModelCrystling::createBodyLayer);
        event.registerLayerDefinition(ModelRosadillo.LAYER_LOCATION, ModelRosadillo::createBodyLayer);
        event.registerLayerDefinition(ModelFairling.LAYER_LOCATION, ModelFairling::createBodyLayer);
        event.registerLayerDefinition(ModelGrizzly.LAYER_LOCATION, ModelGrizzly::createBodyLayer);
        event.registerLayerDefinition(ModelOtherlyMinion.LAYER_LOCATION, ModelOtherlyMinion::createBodyLayer);
        event.registerLayerDefinition(ModelGoateer.LAYER_LOCATION, ModelGoateer::createBodyLayer);
        event.registerLayerDefinition(ModelPyroboar.LAYER_LOCATION, ModelPyroboar::createBodyLayer);
        event.registerLayerDefinition(ModelSnowpaka.LAYER_LOCATION, ModelSnowpaka::createBodyLayer);
        event.registerLayerDefinition(ModelBetaFish.LAYER_LOCATION, ModelBetaFish::createBodyLayer);
    }

}
