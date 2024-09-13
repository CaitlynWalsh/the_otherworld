package net.kitcaitie.otherworld.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MarriageItem extends Item {

    public MarriageItem(Properties properties) {
        super(properties);
    }

    public static void marry(ItemStack stack, Player owner, LivingEntity partner) {
        stack.setCount(1);
        stack.getOrCreateTagElement("Marriage").putString("Owner", owner.getDisplayName().getString());
        stack.getOrCreateTagElement("Marriage").putUUID("Spouse", partner.getUUID());
        stack.getOrCreateTagElement("Marriage").putString("SpouseName", partner.getDisplayName().getString());
    }

    public static boolean isMarried(ItemStack stack) {
        return stack.getOrCreateTagElement("Marriage").hasUUID("Spouse");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isMarried(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isMarried(stack) ? 1 : 2;
    }

    @Override
    public Component getName(ItemStack item) {
        return isMarried(item) ? Component.literal(item.getOrCreateTagElement("Marriage").getString("Owner") + Component.translatable("family.otherworld.ring").getString()).withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE) : super.getName(item);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if (isMarried(stack)) {
            components.add(Component.literal(Component.translatable("family.otherworld.spouse").getString() + ": ")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD)
                    .append(Component.literal(stack.getOrCreateTagElement("Marriage").getString("SpouseName"))));
        }
    }
}
