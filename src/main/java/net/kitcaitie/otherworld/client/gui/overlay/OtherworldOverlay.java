package net.kitcaitie.otherworld.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
@OnlyIn(Dist.CLIENT)
public abstract class OtherworldOverlay implements IGuiOverlay {

    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (shouldRender(gui)) {
            renderTexture(gui, poseStack, partialTick, screenWidth, screenHeight);
        }
    }

    protected abstract void renderTexture(ForgeGui gui, PoseStack stack, float ticks, int screenWidth, int screenHeight);

    protected abstract boolean shouldRender(ForgeGui gui);
}
