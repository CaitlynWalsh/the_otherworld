package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.client.gui.widgets.ToggleStateButton;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.events.Bounty;
import net.kitcaitie.otherworld.common.story.events.Quest;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PlayerCharacterScreen extends Screen {
    private static final ResourceLocation INDEX_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    protected static final int PAGE_SIZE = 158;
    protected static final int MAX_PAGES = 3;
    private final Player player;
    private PlayerCharacter playerCharacter;
    private CompoundTag spouse;
    private List<CompoundTag> kids;
    protected int leftPos;
    protected int topPos;
    private final int imageWidth;
    private final int imageHeight;
    protected int pageY;
    private int currentPage = 0;
    private int childrenIndex = 0;
    private PageButton nextPageButton;
    private PageButton pageBackButton;
    private ToggleStateButton nextIndexButton;
    private ToggleStateButton lastIndexButton;

    public PlayerCharacterScreen() {
        super(Minecraft.getInstance().player.getName());
        this.player = Minecraft.getInstance().player;
        this.imageWidth = 256;
        this.imageHeight = 204;
    }

    @Override
    protected void init() {
        super.init();
        this.playerCharacter = OtherworldClient.getPlayerCharacter();
        this.spouse = playerCharacter.spouseTag;
        this.kids = playerCharacter.kidTags;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.pageY = topPos + 20;
        this.createButtons();
    }

    protected void createButtons() {
        this.nextPageButton = this.addRenderableWidget(new PageButton( leftPos + 220,  pageY + 130, true, (button) -> {
            this.pageForward();
        }, true));
        this.pageBackButton = this.addRenderableWidget(new PageButton(leftPos + 140, pageY + 130, false, (button) -> {
            this.pageBack();
        }, true));
        this.nextIndexButton = new ToggleStateButton(leftPos + 230, pageY + 60, 12, 17, false, () -> {
            if (this.kids != null) {
                if (this.childrenIndex >= kids.size()-1) childrenIndex = 0;
                else childrenIndex ++;
            }
        });
        this.nextIndexButton.initTextureValues(0, 208, 13, 18, INDEX_BUTTON_LOCATION);
        this.lastIndexButton = new ToggleStateButton(leftPos + 140, pageY + 60, 12, 17, false, () -> {
            if (this.kids != null) {
                if (this.childrenIndex > 0) childrenIndex --;
                else childrenIndex = (kids.size() - 1);
            }
        });
        this.lastIndexButton.initTextureValues(14, 208, 13, 18, INDEX_BUTTON_LOCATION);

        this.addRenderableWidget(this.nextIndexButton);
        this.addRenderableWidget(this.lastIndexButton);

        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if (kids == null || kids.size() <= 1) {
            this.nextIndexButton.visible = false;
            this.lastIndexButton.visible = false;
        }
        else if (this.currentPage != 2) {
            this.nextIndexButton.visible = false;
            this.lastIndexButton.visible = false;
        } else {
            this.nextIndexButton.visible = true;
            this.lastIndexButton.visible = true;
        }
    }

    protected void pageForward() {
        if (this.currentPage < (playerCharacter.getCurrentBounty() != null ? MAX_PAGES : 2)) {
            ++this.currentPage;
        } else {
            this.currentPage = 0;
        }
        this.updateButtonVisibility();
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        } else {
            this.currentPage = playerCharacter.getCurrentBounty() != null ? MAX_PAGES : 2;
        }
        this.updateButtonVisibility();
    }


    @Override
    public void onClose() {
        super.onClose();
        OtherworldClient.characterScreenOpen = false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float tick) {
        this.renderBackground(poseStack);

        poseStack.pushPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, CreateCharacterScreen.CREATE_CHARACTER_TEXTURE);
        blit(poseStack, this.leftPos, pageY, 0, 0, 256, 166, 256, 204);
        RenderSystem.disableBlend();

        int entX = leftPos + 60;
        int entY = pageY + 140;
        int left = 15;
        int top = 20;
        int line = 20;
        switch (currentPage) {
            case 0 -> {
                this.font.draw(poseStack, player.getName().copy().withStyle(ChatFormatting.BOLD), getLeftTextPos(left), (float)(pageY + top), 0);
                // Race
                this.font.draw(poseStack, Component.literal(Component.translatable("races.otherworld.race").getString() + ": " + Component.translatable("races.otherworld." + playerCharacter.getRace().name().toLowerCase() + ".name").getString()), getLeftTextPos(left),(float)(pageY + top) + line + 10, 0);
                // Gender
                this.font.draw(poseStack, Component.literal(Component.translatable("gender.otherworld.gender").getString() + ": " + Component.translatable("gender.otherworld." + (playerCharacter.isMale() ? "male" : "female")).getString()), getLeftTextPos(left),(float)(pageY + top) + line + 20, 0);
                // Occupation
                this.font.draw(poseStack, Component.literal(Component.translatable("occupation.otherworld.occupation").getString() + ": " + Component.translatable("occupation.otherworld." + playerCharacter.getOccupation().name().toLowerCase()).getString()), getLeftTextPos(left), (float)(pageY + top) + line + 30, 0);

                InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack, entX, entY, 50, (float)(leftPos) - x, (float)(pageY + 50) - y, this.player);
            }
            case 1 -> {
                this.font.draw(poseStack, Component.translatable("quests.otherworld.quest").withStyle(ChatFormatting.BOLD), getLeftTextPos(left), (float)(pageY + top), 0);
                int offset = 0;
                for (int i=0; i<playerCharacter.getQuests().size(); i++) {
                    Quest quest = playerCharacter.getQuests().get(i).getSecond();
                    // Quest Name
                    this.font.draw(poseStack, Component.literal(quest.getDisplayName().getString()), getLeftTextPos(left), (float)(pageY + top) + (line + (10 * i) + offset), 0);
                    // Progress
                    Component progress = getTotalQuestProgress(quest);
                    for (int j=0; j<progress.getSiblings().size(); j++) {
                        this.font.draw(poseStack, progress.getSiblings().get(j), getLeftTextPos(left), (float)(pageY + top) + (line + (10 * i) + offset + 10 * (j + 1) + 2), 0);
                    }
                    // Counter
                    this.font.draw(poseStack, Component.literal(Component.translatable("quests.otherworld.time_left").getString() + ": " + playerCharacter.questTimer.get(quest) + " Min"), getLeftTextPos(left),(float)(pageY + top) + (line + (10 * i) + offset + 10 * (progress.getSiblings().size() + 1) + 4), 0);
                    offset += 40;
                }
            }
            case 2 -> {
                AbstractPerson renderSpouse = null;
                AbstractPerson renderKid = null;
                this.font.draw(poseStack, Component.translatable("family.otherworld.children").withStyle(ChatFormatting.BOLD), getLeftTextPos(left), (float)(pageY + top - 5), 0);
                this.font.draw(poseStack, Component.translatable("family.otherworld.spouse").withStyle(ChatFormatting.BOLD), entX, (float)(pageY + top - 5), 0);
                if (!spouse.isEmpty()) {
                    renderSpouse = createRenderCopy(spouse);
                    if (renderSpouse != null) InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack, entX, entY, 50, 0, 0, renderSpouse);
                }
                if (!kids.isEmpty()) {
                    if (childrenIndex < kids.size() && !kids.get(childrenIndex).isEmpty()) {
                        renderKid = createRenderCopy(kids.get(childrenIndex));
                        if (renderKid != null) InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack,this.leftPos + PAGE_SIZE + (left * 2), entY, 50, 0, 0, renderKid);
                    }
                }
                if (renderSpouse != null) renderSpouse.discard();
                if (renderKid != null) renderKid.discard();
            }
            case 3 -> {
                this.font.draw(poseStack, Component.translatable("quests.otherworld.bounty").withStyle(ChatFormatting.BOLD), getLeftTextPos(left), (float)(pageY + top), 0);
                Bounty bounty = playerCharacter.getCurrentBounty();
                if (bounty != null) {
                    // Bounty ID
                    this.font.draw(poseStack, bounty.getID(), getLeftTextPos(left), (float)(pageY + top) + line, 0);

                    // Bounty Rewards
                    this.font.draw(poseStack, Component.translatable("gui.otherworld.bounty.rewards").withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE), getLeftTextPos(left), (float)(pageY + top) + line + 20, 0);
                    Component bountyRewards = getBountyRewards(bounty);
                    int offset = 40;
                    for (int i=0; i<bountyRewards.getSiblings().size(); i++) {
                        this.font.draw(poseStack, bountyRewards.getSiblings().get(i), getLeftTextPos(left), (float)(pageY + top) + (line + (10 * i) + offset), 0);
                        offset += 5;
                    }

                    // Render Criminal
                    try {
                        InventoryScreen.renderEntityInInventoryFollowsMouse(poseStack, entX, entY, 50, 0, 0, (LivingEntity) bounty.getCriminal(minecraft.level));
                    }
                    catch (Throwable ignored) {}
                }
            }
        }

        poseStack.popPose();

        super.render(poseStack, x, y, tick);
    }

    @Nullable
    private AbstractPerson createRenderCopy(CompoundTag tag) {
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(tag.getString("Type")));
        if (type != null) {
            Entity entity = type.create(minecraft.level);
            if (entity instanceof AbstractPerson copy) {
                copy.readData(tag);
                copy.setBaby(tag.getBoolean("Baby"));
                copy.setCustomName(Component.literal(tag.getString("Name")));
                copy.setPos(player.position());
                return copy;
            }
            entity.discard();
        }
        return null;
    }

    private float getLeftTextPos(int offset) {
        return (float)(this.leftPos + PAGE_SIZE - offset);
    }

    private Component getTotalQuestProgress(Quest quest) {
        MutableComponent progress = Component.empty();
        Quest.Requirement requirement = quest.getRequirements();
        List<Pair<String, Integer>> questProgress = playerCharacter.getQuestProgress(quest);
        for (int i=0; i<questProgress.size(); i++) {
            Pair<String, Integer> items = questProgress.get(i);
            if (requirement.getRequiredItems() != null) {
                for (Pair<Item, Integer> pair : requirement.getRequiredItems()) {
                    if (items.getFirst().equals(pair.getFirst().toString())) {
                        progress.append(Component.literal(pair.getFirst().getDescription().getString() + ": " + items.getSecond() + "/" + pair.getSecond()));
                    }
                }
            }
            else if (requirement.getTargetEntities() != null) {
                for (Pair<EntityType<?>, Integer> pair : requirement.getTargetEntities()) {
                    if (items.getFirst().equals(pair.getFirst().toString())) {
                        progress.append(Component.literal(pair.getFirst().getDescription().getString() + ": " + items.getSecond() + "/" + pair.getSecond()));
                    }
                }
            }
        }
        return progress;
    }

    private Component getBountyRewards(Bounty bounty) {
        MutableComponent component = Component.empty();
        List<Pair<Item, Integer>> rewards = bounty.getRewards();
        for (Pair<Item, Integer> pair : rewards) {
            component.append(Component.literal(pair.getFirst().getDescription().getString() + ": " + pair.getSecond()));
        }
        return component;
    }
}
