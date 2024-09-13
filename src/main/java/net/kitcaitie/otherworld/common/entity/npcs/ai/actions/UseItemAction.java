package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public abstract class UseItemAction extends Action {
    protected final InteractionHand hand;
    protected ItemStack item;

    public UseItemAction(AbstractPerson person, InteractionHand hand) {
        super(person);
        this.hand = hand;
    }

    public int useTicks() {
        return this.item == null ? 0 : this.item.getUseDuration();
    }

    @Override
    public boolean canContinue() {
        return !item.isEmpty() && this.item.getUseDuration() - this.person.getTicksUsingItem() < this.useTicks();
    }

    @Override
    public void start() {
        super.start();
        this.person.useItem(this.item, hand);
    }

    @Override
    public void stop() {
        if (this.item != null) {
            if (this.item.getUseDuration() - this.person.getTicksUsingItem() >= this.useTicks()) {
                completeUsingItem();
            }
            stopUsingItem();
        }
        super.stop();
    }

    public void completeUsingItem() {
        this.item.shrink(1);
    }

    public void stopUsingItem() {
        this.item = null;
    }

}
