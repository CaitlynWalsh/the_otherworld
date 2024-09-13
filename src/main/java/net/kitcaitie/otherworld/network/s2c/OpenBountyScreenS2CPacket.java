package net.kitcaitie.otherworld.network.s2c;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenBountyScreenS2CPacket {
    private final Bounty bounty;

    public OpenBountyScreenS2CPacket(Bounty bounty) {
        this.bounty = bounty;
    }

    public OpenBountyScreenS2CPacket(FriendlyByteBuf byteBuf) {
        this.bounty = Bounty.load(byteBuf.readNbt());
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(this.bounty.save());
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                OtherworldClient.openBountyScreen(this.bounty)));
        context.setPacketHandled(true);
        return true;
    }

}
