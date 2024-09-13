package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChooseBountyScreen extends SelectActionScreen {
    private final Player criminal;
    private final Bounty bounty;
    public ChooseBountyScreen(Bounty bounty) {
        super(Component.literal(bounty.getID()), Component.translatable("gui.otherworld.bounty.choose"));
        this.criminal = bounty.getCriminalPlayer(Minecraft.getInstance().level);
        this.bounty = bounty;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        this.addRenderableWidget(new StringWidget(0, 160, this.width,9, Component.translatable("gui.otherworld.bounty.rewards"), this.font));
        int i = 20;
        for (Pair<Item, Integer> pair : bounty.getRewards()) {
            this.addRenderableWidget(new StringWidget(0, 160 + i, this.width, 9, Component.literal(pair.getFirst().getDescription().getString() + ": " + pair.getSecond()), this.font));
            i += 20;
        }
    }

    @Override
    public Runnable onAccept() {
        return () -> {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(minecraft.player);
            character.setCurrentBounty(bounty);
            character.sendPacket(minecraft.player);
            this.onClose();
        };
    }

    @Override
    public Runnable onDeny() {
        return this::onClose;
    }
}
