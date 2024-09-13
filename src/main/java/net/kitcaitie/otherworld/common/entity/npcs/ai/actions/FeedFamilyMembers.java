package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class FeedFamilyMembers extends FamilyInteractionAction {
    private ItemStack foodOffering = ItemStack.EMPTY;
    protected boolean gaveFood = false;
    public FeedFamilyMembers(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.SOCIAL));
    }

    @Override
    public boolean canStart() {
        super.canStart();
        if (this.spouse != null || !this.children.isEmpty()) {
            if (person.getInvolvedWar() == null && !person.isAggressive() && this.person.getLastHurtByMob() == null) {
                List<LivingEntity> list = ActionUtils.findGroupOfEntities(person, (entity) -> {
                    if ((this.spouse != null && this.spouse.equals(entity.getUUID())) || (entity instanceof AbstractPerson child && this.children.contains(child.getIdentity()))) {
                        return !getFoodOffering(entity).isEmpty();
                    }
                    return false;
                }, 12.0D);
                if (!list.isEmpty()) {
                    this.interactTarget = list.get(0);
                    this.foodOffering = getFoodOffering(this.interactTarget);
                    if (!foodOffering.isEmpty()) {
                        foodOffering = foodOffering.split(foodOffering.getCount() / 2);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !gaveFood && this.interactTarget != null && this.interactTarget.isAlive() && this.person.getInvolvedWar() == null && !this.person.isAggressive() && this.person.getLastHurtByMob() == null;
    }

    @Override
    public void tick() {
        if (ActionUtils.giveFoodToPersonTick(person, interactTarget, foodOffering)) {
            this.gaveFood = true;
        }
        super.tick();
    }

    @Override
    public void stop() {
        super.stop();
        this.foodOffering = ItemStack.EMPTY;
        this.gaveFood = false;
    }

    private ItemStack getFoodOffering(LivingEntity givingTo) {
        boolean flag = false;

        if (givingTo instanceof Player player && !player.getInventory().hasAnyMatching((item) -> person.isEdibleFor(PowerUtils.accessPlayerCharacter(player), item))) {
            flag = true;
        }
        else if (givingTo instanceof AbstractPerson person2 && !person2.getInventory().hasAnyMatching(person2::isEdibleItem)) {
            flag = true;
        }

        if (flag) {
            IRaces races = (givingTo instanceof Player player ? PowerUtils.accessPlayerCharacter(player) : (givingTo instanceof AbstractPerson ? (AbstractPerson) givingTo : null));
            if (races != null) {
                ItemStack itemStack = person.getItemInInventory((item) -> person.isEdibleFor(races, item) && item.getCount() > 1);
                if (itemStack.isEmpty()) return ItemStack.EMPTY;
                if (givingTo instanceof AbstractPerson person1 && !person1.getInventory().canAddItem(itemStack)) return ItemStack.EMPTY;
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
