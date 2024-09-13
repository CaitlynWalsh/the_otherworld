package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShieldItem;

import java.util.EnumSet;

public class BlockWithShieldAction extends UseItemAction {
    protected LivingEntity target;
    public BlockWithShieldAction(AbstractPerson person) {
        super(person, InteractionHand.OFF_HAND);
        this.setFlags(EnumSet.of(Flags.BLOCKING, Flags.USE_ITEM));
    }

    @Override
    public boolean canStart() {
        if (!person.isUsingItem()) {
            if (this.person.getLastHurtByMob() != null) {
                this.target = person.getLastHurtByMob();
                this.item = person.getItemInHand(hand);
                if (!this.item.isEmpty() && this.item.getItem() instanceof ShieldItem) {
                    return true;
                }
                this.item = person.getItemInInventory((stack) -> stack.getItem() instanceof ShieldItem);
                if (!this.item.isEmpty()) {
                    this.person.setItemInHand(InteractionHand.OFF_HAND, this.item);
                    return true;
                }
            }
        }
        this.item = null;
        this.target = null;
        return false;
    }

    @Override
    public boolean canContinue() {
        return !item.isEmpty() && this.target != null;
    }

    @Override
    public void tick() {
        this.target = person.getLastHurtByMob();
        if (this.target != null) {
            if (this.person.isVeryHurt()) {
                ActionUtils.strafeAwayFrom(this.person, this.target, 0.7F);
            }
            else {
                ActionUtils.lookAndTurnTo(this.person, this.target);
            }
            if (this.target.getLastHurtMob() == null || !this.target.getLastHurtMob().is(this.person)) {
                this.target = null;
            }
        }
    }

    @Override
    public EnumSet<Flags> disabledFlags() {
        return EnumSet.of(Flags.EATING);
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        person.stopUsingItem();
        this.target = null;
    }

    @Override
    public void completeUsingItem() {
    }

    @Override
    public Priority getPriority() {
        return Priority.P0;
    }

}
