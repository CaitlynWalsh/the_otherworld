package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FloatInWaterAction extends Action {
    public FloatInWaterAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.JUMPING));
        person.getNavigation().setCanFloat(true);
    }

    @Override
    public boolean canStart() {
        return this.person.isInWater() || (person.isEmberian() && person.isInLava());
    }

    @Override
    public void tick() {
        if (person.isInWater() && person.getRandom().nextFloat() < 0.8F) {
            this.person.getJumpControl().jump();
        }
        else if (person.isInLava()) {
            this.person.setDeltaMovement(new Vec3(person.getDeltaMovement().x, 0.05F, person.getDeltaMovement().z));
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P0;
    }
}
