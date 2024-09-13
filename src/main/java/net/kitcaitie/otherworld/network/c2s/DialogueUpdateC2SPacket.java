package net.kitcaitie.otherworld.network.c2s;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.network.NetworkMessages;
import net.kitcaitie.otherworld.network.s2c.DialogueUpdateS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class DialogueUpdateC2SPacket {

    public DialogueUpdateC2SPacket() {
    }

    public DialogueUpdateC2SPacket(FriendlyByteBuf byteBuf) {
    }

    public void buffer(FriendlyByteBuf byteBuf) {
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            List<WarEvent> wars = Otherworld.getStoryline(player.getLevel()).getStory().getWarEvents();
            NetworkMessages.sendToPlayer(new DialogueUpdateS2CPacket(convertList(wars)), player);
        });
        context.setPacketHandled(true);
        return true;
    }

    private List<String> convertList(List<WarEvent> wars) {
        List<String> list = new ArrayList<>();
        for (WarEvent w : wars) {
            StringBuilder ret = new StringBuilder();
            for (IRaces.Race race : w.invader) {
                ret.append(race.name().toLowerCase(Locale.ROOT)).append("_");
            }
            ret.deleteCharAt(ret.length() - 1);
            ret.append("/");
            for (IRaces.Race race : w.defender) {
                ret.append(race.name().toLowerCase(Locale.ROOT)).append("_");
            }
            ret.deleteCharAt(ret.length() - 1);
            list.add(ret.toString());
        }
        return list;
    }

}
