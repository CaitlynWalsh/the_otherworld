package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.common.IWorlds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ElementalFoodItem extends Item implements IWorlds {
    private final int effectDuration;
    private final Worlds world;
    private final boolean drink;
    @Nullable private final Supplier<Item> remainingItem;

    public ElementalFoodItem(Properties properties, FoodProperties foodProperties, Worlds worlds, int effectDuration) {
        this(properties, foodProperties, worlds, effectDuration, false, null);
    }

    public ElementalFoodItem(Properties properties, FoodProperties foodProperties, Worlds worlds, int effectDuration, boolean drink, Supplier<Item> remainingItem) {
        super(properties.food(foodProperties));
        this.world = worlds;
        this.effectDuration = effectDuration;
        this.drink = drink;
        this.remainingItem = remainingItem;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        switch (getWorldType()) {
            case EMBERIA -> entity.setSecondsOnFire(effectDuration);
            case GLACEIA -> entity.setTicksFrozen(effectDuration);
        }
        ItemStack stack = super.finishUsingItem(itemStack, level, entity);

        if (remainingItem != null && entity instanceof Player player) {
            if (player.getAbilities().instabuild) {
                return stack;
            }

            ItemStack newStack = remainingItem.get().getDefaultInstance();

            if (!stack.isEmpty()) {
                player.addItem(newStack);
            }
            else return newStack;
        }

        return stack;
    }

    @Override
    public Worlds getWorldType() {
        return world;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return this.drink ? UseAnim.DRINK : super.getUseAnimation(p_41452_);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        if (remainingItem != null) {
            return remainingItem.get().getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
}
