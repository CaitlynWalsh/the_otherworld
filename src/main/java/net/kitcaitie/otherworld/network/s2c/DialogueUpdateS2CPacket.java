package net.kitcaitie.otherworld.network.s2c;

import net.kitcaitie.otherworld.OtherworldClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DialogueUpdateS2CPacket {
    private final List<String> warEvents;

    public DialogueUpdateS2CPacket(List<String> warEvent) {
        this.warEvents = warEvent;
    }

    public DialogueUpdateS2CPacket(FriendlyByteBuf byteBuf) {
        List<String> list = new ArrayList<>();
        int size = byteBuf.readInt();
        for (int i=0; i<size; i++) {
            list.add(byteBuf.readUtf());
        }
        this.warEvents = list;
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(warEvents.size());
        for (String war : warEvents) {
            byteBuf.writeUtf(war);
        }
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            OtherworldClient.updateDialogueWarEvents(this.warEvents);
        }));
        context.setPacketHandled(true);
        return true;
    }

}
