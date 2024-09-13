package net.kitcaitie.otherworld.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class OtherworldKeybinding {

    public static final String KEY_CATEGORY_OTHERWORLD = "key.category.otherworld.otherworld";
    public static final String KEY_OPEN_PLAYER_CHARACTER_SCREEN = "key.otherworld.player_character_screen";

    public static final KeyMapping TOGGLE_PLAYER_CHARACTER_SCREEN = new KeyMapping(KEY_OPEN_PLAYER_CHARACTER_SCREEN,
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KEY_CATEGORY_OTHERWORLD);

}
