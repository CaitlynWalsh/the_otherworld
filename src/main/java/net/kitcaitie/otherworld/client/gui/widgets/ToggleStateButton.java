package net.kitcaitie.otherworld.client.gui.widgets;

import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class ToggleStateButton extends StateSwitchingButton {
    private final Runnable onPress;
    public ToggleStateButton(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_, Runnable onPress) {
        super(p_94615_, p_94616_, p_94617_, p_94618_, p_94619_);
        this.onPress = onPress;
    }

    @Override
    public void onClick(double p_93634_, double p_93635_) {
        super.onClick(p_93634_, p_93635_);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> onPress::run);
    }
}
