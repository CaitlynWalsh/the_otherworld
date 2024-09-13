package net.kitcaitie.otherworld.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class ConsumableItem extends Item {
    private final Supplier<Item> remaining;
    private final boolean applyToNpcs;

    public ConsumableItem(Properties p_41383_, Supplier<Item> remainingItem, boolean applyToNpcs) {
        super(p_41383_);
        this.remaining = remainingItem;
        this.applyToNpcs = applyToNpcs;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (stack.isEmpty()) {
            return remaining.get().getDefaultInstance();
        }
        else {
            ItemStack itemStack = remaining.get().getDefaultInstance();
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false);
                }
            }
            else if (applyToNpcs && entity instanceof InventoryCarrier carrier) {
                if (!carrier.getInventory().canAddItem(itemStack)) {
                    entity.spawnAtLocation(itemStack);
                }
                else {
                    carrier.getInventory().addItem(itemStack);
                }
            }
            return stack;
        }
    }
}
