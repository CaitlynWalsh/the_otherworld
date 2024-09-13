package net.kitcaitie.otherworld.network.s2c;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class DialogueSayS2CPacket {
    private final int abstractPerson;
    private final UUID player;
    private final String event;

    public DialogueSayS2CPacket(int abstractPerson, UUID player, @Nullable String event) {
        this.abstractPerson = abstractPerson;
        this.player = player;
        this.event = event == null ? "" : event;
    }

    public DialogueSayS2CPacket(FriendlyByteBuf byteBuf) {
        this.abstractPerson = byteBuf.readVarInt();
        this.player = byteBuf.readUUID();
        this.event = byteBuf.readUtf();
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.abstractPerson);
        byteBuf.writeUUID(this.player);
        byteBuf.writeUtf(this.event);
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            Entity entity = clientLevel.getEntity(this.abstractPerson);
            if (entity instanceof AbstractPerson person) {
                Player player1 = clientLevel.getPlayerByUUID(this.player);
                if (player1 != null) {
                    OtherworldClient.DIALOGUE.say(person, player1, event.isBlank() ? null : event);
                }
            }
        }));
        context.setPacketHandled(true);
        return true;
    }

}
