package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class ReceiveVillagerFoodAction extends Action {
    protected static final Predicate<LivingEntity> VALID_VILLAGER = (entity) -> entity instanceof Villager villager && villager.getVillagerData().getProfession() == VillagerProfession.FARMER && villager.getInventory().hasAnyMatching((stack) -> !stack.isEmpty() && stack.is(Items.BREAD));
    protected Villager villager = null;
    protected ItemStack foodToGive = ItemStack.EMPTY;
    protected boolean gaveFood = false;

    public ReceiveVillagerFoodAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.SOCIAL));
    }

    @Override
    public boolean canStart() {
        if (person.isHuman() && person.level.isDay() && !person.getCombatTracker().isInCombat()) {
            if (person.getRandom().nextFloat() < 0.01) {
                if (person.getItemInInventory(person::isEdibleItem).isEmpty()) {
                    List<LivingEntity> villagers = ActionUtils.findGroupOfEntities(person, VALID_VILLAGER, 18.0D);
                    if (!villagers.isEmpty()) {
                        this.villager = (Villager) villagers.get(0);
                        this.foodToGive = this.villager.getInventory().removeItemType(Items.BREAD, 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !this.gaveFood && person.level.isDay() && !person.getCombatTracker().isInCombat() && this.villager != null && this.villager.isAlive() && !villager.getCombatTracker().isInCombat();
    }

    @Override
    public void start() {
        super.start();
        villager.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        villager.getLookControl().setLookAt(person);
        if (villager.distanceTo(this.person) > 3.0D) {
            villager.getNavigation().moveTo(this.person, 0.5D);
        }
        else {
            BehaviorUtils.throwItem(villager, foodToGive, person.position());
            this.gaveFood = true;
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.villager != null && !gaveFood) {
            this.villager.getInventory().addItem(foodToGive);
        }
        this.gaveFood = false;
        this.foodToGive = null;
        this.villager = null;
    }
}
