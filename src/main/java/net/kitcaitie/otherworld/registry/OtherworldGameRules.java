package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class OtherworldGameRules {

    public static GameRules.Key<GameRules.IntegerValue> RULE_STORYMODE;
    public static GameRules.Key<GameRules.BooleanValue> RULE_RANDOMIZED_CHARACTERS;

    public static void init() {
        RULE_STORYMODE = GameRules.register("otherworld:storyMode", GameRules.Category.MISC, GameRules.IntegerValue.create(0, (server, value) -> {
            server.getPlayerList().getPlayers().forEach((player -> {
                if (server.getPlayerList().isOp(player.getGameProfile())) {
                    player.sendSystemMessage(Component.literal("The Otherworld:").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
                    player.sendSystemMessage(Component.translatable("storyline.otherworld.gamerule_reset").withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
                    player.sendSystemMessage(Component.literal("Previous Value: " + OtherworldConfigs.SERVER.storyMode.get().toString()).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
                }
            }));
            OtherworldConfigs.SERVER.storyMode.set(Math.min(Math.max(value.get(), 0), 2));
        }));
        RULE_RANDOMIZED_CHARACTERS = GameRules.register("otherworld:randomized_characters", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, value) -> OtherworldConfigs.SERVER.randomizedCharacters.set(value.get())));
    }

}
