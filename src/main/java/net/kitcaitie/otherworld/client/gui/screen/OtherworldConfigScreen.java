package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class OtherworldConfigScreen extends Screen {
    private Button clientButton;
    private Button commonButton;
    private Button serverButton;
    private Button closeButton;

    public OtherworldConfigScreen() {
        super(Component.translatable("gui.otherworld.config"));
    }

    @Override
    protected void init() {
        super.init();

        this.closeButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), (button) -> this.onClose()).bounds((this.width - 200) / 2, this.height - 26, 200, 20).build());

        this.clientButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.otherworld.client"), (b) -> this.openScreen(new ClientScreen()))
                .pos(this.closeButton.getX() + 25, this.closeButton.getY() - 150).build());

        this.commonButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.otherworld.common"), (b) -> this.openScreen(new CommonScreen()))
                .pos(this.closeButton.getX() + 25, this.closeButton.getY() - 110).build());

        this.serverButton = (Minecraft.getInstance().level != null && Minecraft.getInstance().hasSingleplayerServer()) ? this.addRenderableWidget(new Button.Builder(Component.translatable("gui.otherworld.server"), (b) -> this.openScreen(new ServerScreen()))
                .pos(this.closeButton.getX() + 25, this.closeButton.getY() - 70).build()) : null;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float delta) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);
        super.render(stack, x, y, delta);
    }

    private void openScreen(Screen screen) {
        this.onClose();
        this.minecraft.setScreen(screen);
    }

    protected static OptionInstance<Integer> createIntegerOption(Component name, int defaultValue, int min, int max, OptionInstance.CaptionBasedToString<Integer> captionBasedToString, Consumer<Integer> setValue) {
        return new OptionInstance<>(name.getString(), OptionInstance.noTooltip(), captionBasedToString, new OptionInstance.IntRange(min, max), defaultValue, setValue);
    }

    protected static OptionInstance<Double> createDoubleOption(Component name, double defaultValue, double maxValue, OptionInstance.CaptionBasedToString<Double> captionBasedToString, Consumer<Double> setValue) {
        return new OptionInstance<>(name.getString(), OptionInstance.noTooltip(), captionBasedToString, OptionInstance.UnitDouble.INSTANCE, Codec.doubleRange(0.0D, maxValue), defaultValue, setValue);
    }

    private static Component percentValueLabel(Component p_231898_, double p_231899_) {
        return Component.translatable("options.percent_value", p_231898_, (int)(p_231899_ * 100.0D));
    }

    @OnlyIn(Dist.CLIENT)
    protected static class ClientScreen extends Screen {
        private Button closeButton;
        private OptionsList optionsList;
        protected ClientScreen() {
            super(Component.translatable("gui.otherworld.client"));
        }

        @Override
        protected void init() {
            super.init();
            this.optionsList = new OptionsList(minecraft, this.width, this.height, 24, this.height - 32, 25);

            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.useChrTex").getString(), OtherworldConfigs.CLIENT.useCharacterTextures.get(), (b) -> OtherworldConfigs.CLIENT.useCharacterTextures.set(b)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.renderLayers").getString(), OtherworldConfigs.CLIENT.renderLayersOnPlayer.get(), (b) -> OtherworldConfigs.CLIENT.renderLayersOnPlayer.set(b)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.renderClothes").getString(), OtherworldConfigs.CLIENT.renderClothesOnPlayer.get(), (b) -> OtherworldConfigs.CLIENT.renderClothesOnPlayer.set(b)));

            this.addRenderableWidget(this.optionsList);
            this.closeButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), (button) -> this.onClose()).bounds((this.width - 200) / 2, this.height - 26, 200, 20).build());
        }

        @Override
        public void render(PoseStack stack, int x, int y, float delta) {
            super.renderBackground(stack);
            drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);
            super.render(stack, x, y, delta);
        }

        @Override
        public void onClose() {
            OtherworldConfigs.CLIENT_SPEC.save();
            super.onClose();
            this.minecraft.setScreen(new OtherworldConfigScreen());
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class CommonScreen extends Screen {
        private Button closeButton;
        private OptionsList optionsList;
        protected CommonScreen() {
            super(Component.translatable("gui.otherworld.common"));
        }

        @Override
        protected void init() {
            super.init();
            this.optionsList = this.addRenderableWidget(new OptionsList(minecraft, this.width, this.height, 24, this.height - 32, 25));
            this.closeButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), (button) -> this.onClose()).bounds((this.width - 200) / 2, this.height - 26, 200, 20).build());
        }

        @Override
        public void onClose() {
            //OtherworldConfigs.COMMON_SPEC.save();
            super.onClose();
            this.minecraft.setScreen(new OtherworldConfigScreen());
        }

        @Override
        public void render(PoseStack stack, int x, int y, float delta) {
            super.renderBackground(stack);
            drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);
            super.render(stack, x, y, delta);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class ServerScreen extends Screen {
        private Button closeButton;
        private OptionsList optionsList;

        protected ServerScreen() {
            super(Component.translatable("gui.otherworld.server"));
        }

        @Override
        protected void init() {
            super.init();
            this.optionsList = this.addRenderableWidget(new OptionsList(minecraft, this.width, this.height, 24, this.height - 32, 25));
            this.optionsList.addBig(createIntegerOption(Component.translatable("config.otherworld.storyMode"), OtherworldConfigs.SERVER.storyMode.get(), 0, 2, (c, integer) -> {
                switch (integer) {
                    case 0 -> {
                        return Options.genericValueLabel(c, Component.translatable("config.otherworld.storyMode.main"));
                    }
                    case 1 -> {
                        return Options.genericValueLabel(c, Component.translatable("config.otherworld.storyMode.icexfire"));
                    }
                    case 2 -> {
                        return Options.genericValueLabel(c, Component.translatable("config.otherworld.storyMode.quiet"));
                    }
                    default -> {
                        return Options.genericValueLabel(c, Component.literal("?"));
                    }
                }
            }, (integer) -> OtherworldConfigs.SERVER.storyMode.set(integer)));
            this.optionsList.addBig(createIntegerOption(Component.translatable("config.otherworld.permissionLevelToBreakStructures"), OtherworldConfigs.SERVER.permissionLevelToBreakStructures.get(), 0, 4, Options::genericValueLabel,
                    (integer) -> OtherworldConfigs.SERVER.permissionLevelToBreakStructures.set(integer)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.allowSoldierArrests").getString(), OtherworldConfigs.SERVER.allowSoldierArrests.get(), (bool) -> OtherworldConfigs.SERVER.allowSoldierArrests.set(bool)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.allowWarPrisonerArrests").getString(), OtherworldConfigs.SERVER.allowWarPrisonerArrests.get(), (bool) -> OtherworldConfigs.SERVER.allowWarPrisonerArrests.set(bool)));
            this.optionsList.addBig(createIntegerOption(Component.translatable("config.otherworld.breedingCooldownAge"), (OtherworldConfigs.SERVER.breedingCooldownAge.get() / 60) / 20, 1, 60, Options::genericValueLabel, (integer) -> OtherworldConfigs.SERVER.breedingCooldownAge.set((integer * 60) * 20)));
            this.optionsList.addBig(createIntegerOption(Component.translatable("config.otherworld.childLimit"), OtherworldConfigs.SERVER.childLimit.get(), -1, 12, (c, integer) -> {
                if (integer == -1) return Options.genericValueLabel(c, CommonComponents.OPTION_OFF);
                return Options.genericValueLabel(c, integer);
            }, (integer) -> OtherworldConfigs.SERVER.childLimit.set(integer)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.doNaturalMarriage").getString(), OtherworldConfigs.SERVER.doNaturalMarriage.get(), (bool) -> OtherworldConfigs.SERVER.doNaturalMarriage.set(bool)));
            this.optionsList.addBig(OptionInstance.createBoolean(Component.translatable("config.otherworld.doNaturalBreeding").getString(), OtherworldConfigs.SERVER.doNaturalBreeding.get(), (bool) -> OtherworldConfigs.SERVER.doNaturalBreeding.set(bool)));
            this.closeButton = this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), (button) -> this.onClose()).bounds((this.width - 200) / 2, this.height - 26, 200, 20).build());
        }

        @Override
        public void onClose() {
            OtherworldConfigs.SERVER_SPEC.save();
            super.onClose();
            this.minecraft.setScreen(new OtherworldConfigScreen());
        }

        @Override
        public void render(PoseStack stack, int x, int y, float delta) {
            super.renderBackground(stack);
            drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0xFFFFFF);
            super.render(stack, x, y, delta);
        }
    }
}
