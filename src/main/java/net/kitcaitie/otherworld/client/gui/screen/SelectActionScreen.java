package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SelectActionScreen extends Screen {
    public static final Component ACCEPT_BUTTON = Component.translatable("gui.otherworld.accept");
    public static final Component DENY_BUTTON = Component.translatable("gui.otherworld.deny");

    protected LocalPlayer player;
    protected Button acceptButton;
    protected Button denyButton;
    protected Component actionName;

    protected SelectActionScreen(Component p_96550_, Component actionName) {
        super(p_96550_);
        this.actionName = actionName;
    }

    @Override
    protected void init() {
        super.init();
        this.player = this.minecraft.player;
        this.initComponents();
    }

    @Override
    public void render(PoseStack stack, int x, int y, float tick) {
        this.renderBackground(stack);
        this.renderComponents(stack, x, y, tick);
        super.render(stack, x, y, tick);
        this.afterRender(stack, x, y, tick);
    }

    public void initComponents() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);

        this.acceptButton = rowHelper.addChild(Button.builder(ACCEPT_BUTTON, (button) -> {
            this.onAccept().run();
        }).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build(), 2, gridLayout.newCellSettings().paddingTop(20));

        this.denyButton = rowHelper.addChild(Button.builder(DENY_BUTTON, (button) -> {
            this.onDeny().run();
        }).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build(), 2, gridLayout.newCellSettings().paddingTop(20));

        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, 0, 20, this.width, this.height, 0.5F, 0.25F);

        gridLayout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(new StringWidget(0, 40, this.width, 9, this.title, this.font));
        this.addRenderableWidget(new StringWidget(0, 20, this.width, 9, this.actionName, this.font));
    }

    public void renderComponents(PoseStack stack, int x, int y, float tick) {
    }

    public void afterRender(PoseStack stack, int x, int y, float tick) {
    }

    public abstract Runnable onAccept();
    public abstract Runnable onDeny();

    @Override
    public boolean keyPressed(int i, int p_96553_, int p_96554_) {
        if (i == 256) {
            this.onDeny().run();
            this.onClose();
        }
        return super.keyPressed(i, p_96553_, p_96554_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
