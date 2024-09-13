package net.kitcaitie.otherworld.network.s2c;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCharacterS2CPacket {
    private PlayerCharacter playerCharacter;

    public UpdateCharacterS2CPacket(FriendlyByteBuf byteBuf) {
        this.playerCharacter = new PlayerCharacter();
        this.playerCharacter.loadNBT(byteBuf.readNbt());
    }

    public UpdateCharacterS2CPacket(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(playerCharacter.writeNBT());
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (!this.playerCharacter.wasCreated()) {
                    if (OtherworldConfigs.SERVER.randomizedCharacters.get()) {
                        OtherworldClient.generateRandomCharacter();
                    }
                    OtherworldClient.handleCharacterScreen();
                }
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
