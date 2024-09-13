package net.kitcaitie.otherworld.client.gui.screen;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChooseOccupationScreen extends SelectActionScreen {
    protected final AbstractPerson assigner;
    protected final IOccupation.Occupation occupation;

    public ChooseOccupationScreen(AbstractPerson assigner, IOccupation.Occupation occupation) {
        super(Component.translatable("gui.otherworld.choose_occupation"), Component.translatable("occupation.otherworld." + occupation.name().toLowerCase()));
        this.assigner = assigner;
        this.occupation = occupation;
    }

    @Override
    public Runnable onAccept() {
        return () -> {
            PlayerCharacter playerCharacter = PowerUtils.accessPlayerCharacter(player);
            playerCharacter.setOccupation(occupation);
            playerCharacter.sendPacket(player);
            player.sendSystemMessage(Component.literal(Component.translatable("event.otherworld.occupation_change." + occupation.name().toLowerCase()).getString()).withStyle(ChatFormatting.AQUA));
            this.onClose();
        };
    }

    @Override
    public Runnable onDeny() {
        return () -> {
            OtherworldClient.DIALOGUE.say(assigner, player, occupation.name().toLowerCase() + DialogueEvent.DENY.getString());
            this.onClose();
        };
    }
}
