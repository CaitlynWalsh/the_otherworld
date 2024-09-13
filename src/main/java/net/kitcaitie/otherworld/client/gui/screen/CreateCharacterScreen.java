package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.*;
import net.kitcaitie.otherworld.common.entity.npcs.ghoul.Ghoul;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
@OnlyIn(Dist.CLIENT)
public class CreateCharacterScreen extends Screen {
    public static final ResourceLocation CREATE_CHARACTER_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/gui/create_character.png");
    protected static final int maxPages = OtherworldClient.STARTER_RACES.size() - 1;
    protected static final int PAGE_SIZE = 158;
    protected int leftPos;
    protected int topPos;
    private final int imageWidth;
    private final int imageHeight;
    protected int pageY;
    private int currentPage = 0;
    private int genderPage = 0;
    private final Player player;
    private PageButton forwardButton;
    private PageButton backButton;
    private int stage;
    private int raceID;
    private boolean isMale;

    public CreateCharacterScreen() {
        super(Component.empty());
        this.player = Minecraft.getInstance().player;
        this.isMale = true;
        this.imageWidth = 256;
        this.imageHeight = 204;

    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.pageY = topPos + 20;
        this.stage = 0;
        this.createButtons();
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float v) {
        this.renderDirtBackground(poseStack);

        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, CREATE_CHARACTER_TEXTURE);
        blit(poseStack, this.leftPos, pageY, 0, 0, 256, 166, 256, 204);
        RenderSystem.disableBlend();

        int left = 15;
        int top = 20;
        int line = 20;
        switch (stage) {
            case 0 -> {
                IRaces.Race race = OtherworldClient.STARTER_RACES.get(this.currentPage);
                Component name = getNameDisplay(race);
                this.font.draw(poseStack, name, getLeftTextPos(left), (float)(pageY + top), 0);
                List<Component> components = getDescriptionDisplay(race);
                for (int c=0; c<components.size(); c++) {
                    Component component = components.get(c);
                    this.font.draw(poseStack, component, getLeftTextPos(left), (float)(pageY + top) + (line + (10 * c)), 0);
                }
            }
            case 1 -> {
                this.font.draw(poseStack, getGenderDisplay(), getLeftTextPos(left), (float)(pageY + top + line + 10), 0);
            }
            case 2 -> {
                String str = Component.translatable("gui." + Otherworld.MODID + ".choose_this_character").getString();
                String[] strs = str.split(" ");
                for (int s=0; s<strs.length; s++) {
                    String string = strs[s];
                    this.font.draw(poseStack, Component.literal(string).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC), getLeftTextPos(left), (float)(pageY + top) + (line + (10 * s)), 0);
                }
            }
        }
        LivingEntity entity = getDisplayEntity(OtherworldClient.STARTER_RACES.get(this.currentPage), genderPage == 0);
        int entX = leftPos + 60;
        int entY = pageY + 140;
        InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack, entX, entY, 50, (float)(leftPos) - x, (float)(pageY + 50) - y, entity);

        super.render(poseStack, x, y, v);
        poseStack.popPose();

        if (entity instanceof AbstractPerson) entity.discard();
    }

    private LivingEntity getDisplayEntity(IRaces.Race race, boolean male) {
        switch (race) {
            case HUMAN -> {
                Human human = OtherworldEntities.HUMAN.get().create(player.level);
                human.setMale(male);
                return human;
            }
            case ONI -> {
                Oni oni = OtherworldEntities.ONI.get().create(player.level);
                oni.setMale(male);
                return oni;
            }
            case ROSEIAN -> {
                Roseian roseian = OtherworldEntities.ROSEIAN.get().create(player.level);
                roseian.setMale(male);
                return roseian;
            }
            case FAIRIE -> {
                Fairie fairie = OtherworldEntities.FAIRIE.get().create(player.level);
                fairie.setMale(male);
                return fairie;
            }
            case EMBERIAN -> {
                Emberian emberian = OtherworldEntities.EMBERIAN.get().create(player.level);
                emberian.setMale(male);
                return emberian;
            }
            case ICEIAN -> {
                Iceian iceian = OtherworldEntities.ICEIAN.get().create(player.level);
                iceian.setMale(male);
                return iceian;
            }
            case GHOUL -> {
                Ghoul ghoul = OtherworldEntities.GHOUL.get().create(player.level);
                ghoul.setMale(male);
                return ghoul;
            }
            default -> {
                return player;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.stage <= 2 && this.stage > 0) {
            this.stage--;
        }
        this.updateButtonVisibility();
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    protected void createButtons() {
        this.addRenderableWidget(new PageSelectButton(leftPos + 170, pageY + 130, b -> {
            this.selectButtonPressed();
        }));
        this.forwardButton = this.addRenderableWidget(new PageButton( leftPos + 220,  pageY + 130, true, (button) -> {
            this.pageForward();
        }, true));
        this.backButton = this.addRenderableWidget(new PageButton(leftPos + 140, pageY + 130, false, (button) -> {
            this.pageBack();
        }, true));
        this.updateButtonVisibility();
    }

    private float getLeftTextPos(int offset) {
        return (float)(this.leftPos + PAGE_SIZE - offset);
    }

    protected void pageForward() {
        if (stage == 0) {
            if (this.currentPage < maxPages) {
                ++this.currentPage;
            } else {
                this.currentPage = 0;
            }
        }
        else if (stage == 1) {
            if (this.genderPage == 0) {
                ++this.genderPage;
            } else if (this.genderPage == 1) {
                --this.genderPage;
            }
        }
    }

    protected void pageBack() {
        if (stage == 0) {
            if (this.currentPage > 0) {
                --this.currentPage;
            } else {
                this.currentPage = maxPages;
            }
        }
        else if (stage == 1) {
            if (this.genderPage == 0) {
                ++this.genderPage;
            } else if (this.genderPage == 1) {
                --this.genderPage;
            }
        }
    }

    protected void selectButtonPressed() {
        if (stage == 0) {
            this.raceID = this.currentPage;
        }
        else if (stage == 1) {
            this.isMale = genderPage == 0;
        }
        else if (this.stage == 2) {
            PlayerCharacter playerCharacter = new PlayerCharacter();
            playerCharacter.setRace(OtherworldClient.STARTER_RACES.get(this.raceID));
            playerCharacter.setMale(this.isMale);
            playerCharacter.setCreated(true);
            playerCharacter.setStarted(false);
            playerCharacter.sendPacket(player);
            player.closeContainer();
            return;
        }
        this.stage++;
        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
       this.forwardButton.visible = stage < 2;
       this.backButton.visible = stage < 2;
    }

    private Component getNameDisplay(IRaces.Race currentRace) {
        return Component.translatable("races." +Otherworld.MODID+ "." +currentRace.name().toLowerCase() + ".name")
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC);
    }

    private Component getGenderDisplay() {
        return Component.translatable("gender." + Otherworld.MODID + "." + (genderPage == 0 ? "male" : "female"))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC);
    }

    private List<Component> getDescriptionDisplay(IRaces.Race currentRace) {
        String translation =  Component.translatable("races." +Otherworld.MODID+ "." +currentRace.name().toLowerCase() + ".description").getString();
        String[] seperated = translation.split("[:]");
        List<Component> components = new ArrayList<>();
        for (int i=0; i<seperated.length; i++) {
            components.add(Component.literal(seperated[i]).withStyle(ChatFormatting.ITALIC));
        }
        return components;
    }

    public boolean keyPressed(int i, int i1, int i2) {
        if (super.keyPressed(i, i1, i2)) {
            return true;
        } else {
            switch (i) {
                case 266:
                    this.pageBack();
                    return true;
                case 267:
                    this.pageForward();
                    return true;
                default:
                    return false;
            }
        }
    }

    static class PageSelectButton extends Button {

        protected PageSelectButton(int x, int y, OnPress onPress) {
            super(x, y, 41, 18, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        }

        @Override
        public void render(PoseStack poseStack, int i, int i1, float v) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, CREATE_CHARACTER_TEXTURE);
            int j = 46;
            int j1 = 170;
            if (this.isHoveredOrFocused()) {
                j += 45;
            }
            blit(poseStack, this.getX(), this.getY(), (float) j, (float)j1, 41, 18, 256, 204);
        }
    }
}
