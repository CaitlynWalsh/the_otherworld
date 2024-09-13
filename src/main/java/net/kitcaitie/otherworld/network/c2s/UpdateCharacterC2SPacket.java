package net.kitcaitie.otherworld.network.c2s;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.common.util.SpecialSpawner;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCharacterC2SPacket {
    private final PlayerCharacter playerCharacter;

    public UpdateCharacterC2SPacket(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public UpdateCharacterC2SPacket(FriendlyByteBuf byteBuf) {
        this.playerCharacter = new PlayerCharacter();
        this.playerCharacter.loadNBT(byteBuf.readNbt());
    }

    public void buffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(this.playerCharacter.writeNBT());
    }

    public boolean handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            boolean teleport = shouldTeleport(player, character, playerCharacter);
            boolean changeJob = playerCharacter.getOccupation() != character.getOccupation();
            character.copyFrom(playerCharacter);
            character.updateFamilyTags(player);
            character.sync(player);
            if (teleport) {
                SpecialSpawner spawner = new SpecialSpawner(player, playerCharacter.getRace());
                if (playerCharacter.getRace() != IRaces.Race.HUMAN) spawner.createLoadingScreen();
                player.getServer().execute(spawner::spawnInVillage);
            }
            if (changeJob) {
                Utils.addItemsForOccupation(playerCharacter.getOccupation(), playerCharacter, player);
            }
        });
        context.setPacketHandled(true);
        return true;
    }

    private boolean shouldTeleport(ServerPlayer player, PlayerCharacter oldData, PlayerCharacter newData) {
        if (newData.getRace() != IRaces.Race.HUMAN) {
            ResourceKey<Level> startDimension = newData.isGhoul() ? Otherworld.UNDERLANDS : Otherworld.OTHERWORLD;
            if (!player.level.dimension().equals(startDimension)) {
                if (!oldData.wasCreated() && newData.wasCreated()) {
                    newData.setStarted(false);
                    return true;
                }
                return !oldData.startedInVillage() && !newData.startedInVillage();
            }
        }
        else return true;
        return false;
    }

}
