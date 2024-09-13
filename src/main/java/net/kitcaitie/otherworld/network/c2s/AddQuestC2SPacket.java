package net.kitcaitie.otherworld.network.c2s;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddQuestC2SPacket {

    private final int id;
    private final Quest quest;

    public AddQuestC2SPacket(int id, Quest quest) {
        this.id = id;
        this.quest = quest;
    }

    public AddQuestC2SPacket(FriendlyByteBuf byteBuf) {
        this.id = byteBuf.readVarInt();
        this.quest = Quest.load(byteBuf.readNbt());
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(id);
        byteBuf.writeNbt(quest.save());
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            Entity entity = player.level.getEntity(id);
            quest.start(player, entity != null ? (AbstractPerson) entity : null);
        });
        context.setPacketHandled(true);
        return true;
    }

}
