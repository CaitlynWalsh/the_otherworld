package net.kitcaitie.otherworld.network.s2c;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.story.events.EventHandler;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenQuestScreenS2CPacket {
    private final int abstractPerson;
    private final String questID;

    public OpenQuestScreenS2CPacket(int abstractPerson, String questID) {
        this.abstractPerson = abstractPerson;
        this.questID = questID;
    }

    public OpenQuestScreenS2CPacket(FriendlyByteBuf byteBuf) {
        this.abstractPerson = byteBuf.readVarInt();
        this.questID = byteBuf.readUtf();
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(this.abstractPerson);
        byteBuf.writeUtf(this.questID);
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(this.abstractPerson);
                if (entity instanceof AbstractPerson person) {
                    Quest quest = EventHandler.getQuest(this.questID);
                    if (quest != null) OtherworldClient.openQuestScreen(person, quest);
                }
            }
        }));
        context.setPacketHandled(true);
        return true;
    }

}
