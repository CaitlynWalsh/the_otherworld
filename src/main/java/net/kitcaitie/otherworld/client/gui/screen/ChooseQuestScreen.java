package net.kitcaitie.otherworld.client.gui.screen;

import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChooseQuestScreen extends SelectActionScreen {
    protected final Quest quest;
    protected final AbstractPerson assigner;

    public ChooseQuestScreen(AbstractPerson assigner, Quest quest) {
        super(Component.translatable("gui.otherworld.choose_quest"), quest.getDisplayName());
        this.assigner = assigner;
        this.quest = quest;
    }

    @Override
    public Runnable onAccept() {
        return () -> {
            PlayerCharacter playerCharacter = PowerUtils.accessPlayerCharacter(player);
            playerCharacter.addQuest(assigner, quest);
            playerCharacter.sendPacket(player);
            OtherworldClient.DIALOGUE.say(assigner, player, quest.getID() + DialogueEvent.ASSIGN.getString());
            this.onClose();
        };
    }

    @Override
    public Runnable onDeny() {
        return () -> {
            OtherworldClient.DIALOGUE.say(assigner, player, quest.getID() + DialogueEvent.DENY.getString());
            this.onClose();
        };
    }
}
