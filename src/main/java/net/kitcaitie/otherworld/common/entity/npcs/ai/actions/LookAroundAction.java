package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class LookAroundAction extends Action {
    protected LivingEntity lookAt;
    private double relX;
    private double relZ;
    private int lookTime;
    protected final TargetingConditions lookAtContext;

    public LookAroundAction(AbstractPerson person) {
        super(person);
        this.lookAtContext = TargetingConditions.forNonCombat().range(8.0D);
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.LOOKING_RANDOM));
    }

    @Override
    public boolean canStart() {
        if (this.person.getRandom().nextFloat() <= 0.04F) {
            if (this.person.isAggressive()) return false;
            else if (this.person.getRandom().nextFloat() <= 0.8F) {
                this.lookAt = this.person.level.getNearestEntity(this.person.level.getEntitiesOfClass(LivingEntity.class,
                        this.person.getBoundingBox().inflate(8.0D, 3.0D, 8.0D), (entity) -> true),
                        this.lookAtContext, this.person, this.person.getX(), this.person.getEyeY(), this.person.getZ());
            }
            this.lookTime = 40 + this.person.getRandom().nextInt(20);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return this.lookTime > 0 && !person.isAggressive();
    }

    @Override
    public void start() {
        double d0 = (Math.PI * 2D) * this.person.getRandom().nextDouble();
        this.relX = Math.cos(d0);
        this.relZ = Math.sin(d0);
        super.start();
    }

    @Override
    public void stop() {
        this.lookAt = null;
        super.stop();
    }

    @Override
    public void tick() {
        --lookTime;
        if (this.lookAt != null && this.lookAt.isAlive()) {
            ActionUtils.lookAt(this.person, lookAt);
        } else {
            ActionUtils.lookAt(this.person, this.person.getX() + this.relX, this.person.getEyeY(), this.person.getZ() + this.relZ);
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P10;
    }

}
