package net.kitcaitie.otherworld.common.entity.npcs.ai;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

public class PersonLookControl extends LookControl {
    public PersonLookControl(AbstractPerson person) {
        super(person);
    }

    @Override
    public void tick() {
        if (((AbstractPerson)mob).isBlind() || mob.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get())) {
            this.setLookAt(Vec3.atBottomCenterOf(mob.blockPosition()));
        }
        super.tick();
    }
}
