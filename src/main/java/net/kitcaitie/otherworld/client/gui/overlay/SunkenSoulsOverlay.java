package net.kitcaitie.otherworld.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
@OnlyIn(Dist.CLIENT)
public class SunkenSoulsOverlay extends OtherworldOverlay {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Otherworld.MODID, "textures/block/underlands/corrupted_soul_sand.png");

    @Override
    protected void renderTexture(ForgeGui gui, PoseStack stack, float ticks, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, (double)screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)screenWidth, (double)screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected boolean shouldRender(ForgeGui gui) {
        Player player = gui.getMinecraft().player;
        return player != null && player.level.getBlockState(Utils.vecToBpos(player.getEyePosition())).is(OtherworldBlocks.SUNKEN_SOULS.get());
    }

}
