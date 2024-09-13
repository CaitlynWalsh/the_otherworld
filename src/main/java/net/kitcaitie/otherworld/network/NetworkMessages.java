package net.kitcaitie.otherworld.network;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.network.c2s.AddQuestC2SPacket;
import net.kitcaitie.otherworld.network.c2s.DialogueUpdateC2SPacket;
import net.kitcaitie.otherworld.network.c2s.UpdateCharacterC2SPacket;
import net.kitcaitie.otherworld.network.s2c.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Otherworld.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Player Character Update Packets
        net.messageBuilder(UpdateCharacterC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).encoder(UpdateCharacterC2SPacket::buffer).decoder(UpdateCharacterC2SPacket::new).consumerMainThread(UpdateCharacterC2SPacket::handler).add();
        net.messageBuilder(UpdateCharacterS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(UpdateCharacterS2CPacket::buffer).decoder(UpdateCharacterS2CPacket::new).consumerMainThread(UpdateCharacterS2CPacket::handler).add();
        // Quest Update Packets
        net.messageBuilder(AddQuestC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).encoder(AddQuestC2SPacket::buffer).decoder(AddQuestC2SPacket::new).consumerMainThread(AddQuestC2SPacket::handler).add();
        // Dialogue Say Packet
        net.messageBuilder(DialogueSayS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(DialogueSayS2CPacket::buffer).decoder(DialogueSayS2CPacket::new).consumerMainThread(DialogueSayS2CPacket::handler).add();
        // Update Dialogue Data Packets
        net.messageBuilder(DialogueUpdateC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).encoder(DialogueUpdateC2SPacket::buffer).decoder(DialogueUpdateC2SPacket::new).consumerMainThread(DialogueUpdateC2SPacket::handler).add();
        net.messageBuilder(DialogueUpdateS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(DialogueUpdateS2CPacket::buffer).decoder(DialogueUpdateS2CPacket::new).consumerMainThread(DialogueUpdateS2CPacket::handler).add();
        // Open Quest Screen Packet
        net.messageBuilder(OpenQuestScreenS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(OpenQuestScreenS2CPacket::buffer).decoder(OpenQuestScreenS2CPacket::new).consumerMainThread(OpenQuestScreenS2CPacket::handler).add();
        // Open Occupation Screen Packet
        net.messageBuilder(OpenOccupationScreenS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(OpenOccupationScreenS2CPacket::buffer).decoder(OpenOccupationScreenS2CPacket::new).consumerMainThread(OpenOccupationScreenS2CPacket::handler).add();
        // Open Bounty Screen Packet
        net.messageBuilder(OpenBountyScreenS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).encoder(OpenBountyScreenS2CPacket::buffer).decoder(OpenBountyScreenS2CPacket::new).consumerMainThread(OpenBountyScreenS2CPacket::handler).add();
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        INSTANCE.registerMessage(packetId, messageType, encoder, decoder, messageConsumer);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
