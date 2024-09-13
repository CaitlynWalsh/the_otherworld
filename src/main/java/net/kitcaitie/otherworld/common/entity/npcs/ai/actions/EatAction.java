package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class EatAction extends UseItemAction {
    protected FoodProperties properties = null;

    public EatAction(AbstractPerson person) {
        super(person, InteractionHand.MAIN_HAND);
        this.setFlags(EnumSet.of(Flags.INTERACTING, Flags.EATING, Flags.USE_ITEM));
    }

    @Override
    public boolean canStart() {
        if (!person.isUsingItem()) {
            if ((person.isHungry() || person.isHurt())) {
                this.item = person.getItemInInventory(person::isEdibleItem);
                if (!item.isEmpty() && canEat(item)) {
                    this.properties = item.getFoodProperties(this.person);
                    return true;
                }
                this.item = null;
            }
        }
        return false;
    }

    private boolean canEat(ItemStack itemStack) {
        return person.getHungerLevel() < 20 || itemStack.getFoodProperties(person).canAlwaysEat();
    }

    @Override
    public void completeUsingItem() {
        if (this.properties != null && !person.isDeadOrDying()) {
            person.eatFood(this.properties.getNutrition(), this.properties.getSaturationModifier());
        }
        super.completeUsingItem();
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.properties = null;
    }

    @Override
    public Priority getPriority() {
        return Priority.P1;
    }

}
