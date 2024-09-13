package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;
import java.util.List;

public class PickUpItemAction extends Action {
    protected ItemEntity pickUpItem;

    public PickUpItemAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.INTERACTING));
    }

    @Override
    public boolean canStart() {
        ItemEntity itemEntity = findItem();
        if (itemEntity != null && person.getInventory().canAddItem(itemEntity.getItem())) {
            this.pickUpItem = itemEntity;
            return true;
        }
        return false;
    }
    @Override
    public boolean canContinue() {
        return pickUpItem != null && pickUpItem.isAlive() && !person.getNavigation().isStuck();
    }

    @Override
    public void start() {
        person.getNavigation().stop();
        super.start();
    }

    @Override
    public void tick() {
        ActionUtils.lookAt(person, pickUpItem);
        if (person.distanceTo(pickUpItem) <= 2.5D) {
            if (!pickUpItem.hasPickUpDelay()) {
                person.pickUp(pickUpItem);
                return;
            }
        }
        ActionUtils.maybeMoveTo(person, pickUpItem, 0.85D);
    }

    @Override
    public void stop() {
        this.pickUpItem = null;
        super.stop();
    }

    @Override
    public Priority getPriority() {
        return Priority.P2;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    protected ItemEntity findItem() {
        List<ItemEntity> list = person.level.getEntitiesOfClass(ItemEntity.class, person.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                (item) -> person.hasLineOfSight(item) && person.wantsToPickUp(item.getItem()) && (item.getOwner() == null
                        || !item.getOwner().is(person)));
        if (!list.isEmpty()) {
            return list.get(person.getRandom().nextInt(list.size()));
        }
        return null;
    }
}
