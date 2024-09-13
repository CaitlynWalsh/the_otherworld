package net.kitcaitie.otherworld.client.gui.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.client.gui.menu.LoadingScreenMenu;
import net.kitcaitie.otherworld.common.entity.boss.OtherlyMinion;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
@OnlyIn(Dist.CLIENT)
public class LoadingScreen extends AbstractContainerScreen<LoadingScreenMenu> {
    public static final ResourceLocation LOADING_SCREEN_TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/gui/loading_screen.png");
    protected final Player player;
    protected boolean male;

    public LoadingScreen(LoadingScreenMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.player = inventory.player;
        this.imageWidth = 256;
        this.imageHeight = 144;
    }

    @Override
    protected void init() {
        super.init();
        this.male = this.player.getRandom().nextBoolean();
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float v) {
        this.renderBackground(poseStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, getLoadingScreenTexture());
        blit(poseStack, 0, 0,0.0F, 0.0F, this.imageWidth + this.width + 4, this.imageHeight + this.height + 4, this.width + 4, this.height + 4);
        OtherlyMinion minion = getOtherlyMinion();
        int minX = this.minecraft.getWindow().getGuiScaledWidth();
        int minY = this.minecraft.getWindow().getGuiScaledHeight();
        renderOtherlyMinion(minion, minX - (minX/2), minY - (minY/4) + 50, (float)((minX - (minX/2)) - (x)), (float)((minY - (minY/4) + 50) - 50 - (y)), 80);
        RenderSystem.disableBlend();
        super.render(poseStack, x, y, v);
        minion.discard();
    }

    public OtherlyMinion getOtherlyMinion() {
        OtherlyMinion minion = OtherworldEntities.OTHERLY_MINION.get().create(player.level);
        minion.setMale(male);
        minion.setPose(Pose.FALL_FLYING);
        return minion;
    }

    //Custom "InventoryScreen.renderEntityInInventory" to make the OtherlyMinion shaded black on screen (Makes it look ominous :3)
    public static void renderOtherlyMinion(OtherlyMinion minion, int x, int y, float lookX, float lookY, int size) {
        float f = (float)Math.atan((double)(lookX / 40.0F)) / 12.0F;
        float f1 = (float)Math.atan((double)(lookY / 40.0F)) / 12.0F;
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((float)x, (float)y, 1050.0F);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0F, 0.0F, 1000.0F);
        posestack1.scale((float)size, (float)size, (float)size);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        posestack1.mulPose(quaternionf);
        float f2 = minion.yBodyRot;
        float f3 = minion.getYRot();
        float f4 = minion.getXRot();
        float f5 = minion.yHeadRotO;
        float f6 = minion.yHeadRot;
        minion.yBodyRot = 180.0F + f * 20.0F;
        minion.setYRot(180.0F + f * 40.0F);
        minion.setXRot(-f1 * 20.0F);
        minion.yHeadRot = minion.getYRot();
        minion.yHeadRotO = minion.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternionf1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternionf1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(minion, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, LightTexture.FULL_BLOCK - LightTexture.FULL_SKY);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        minion.yBodyRot = f2;
        minion.setYRot(f3);
        minion.setXRot(f4);
        minion.yHeadRotO = f5;
        minion.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return OtherworldClient.getPlayerCharacter().startedInVillage();
    }

    public ResourceLocation getLoadingScreenTexture() {
        return LOADING_SCREEN_TEXTURE;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int i, int i1) {
    }
}
