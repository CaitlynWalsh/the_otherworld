package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class RandomStrollAction extends Action {
    protected Vec3 wantedPos;

    public RandomStrollAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.MOVING_WANDER));
    }

    @Override
    public boolean canStart() {
        if (person.isVehicle()) return false;
        if (person.isAggressive()) return false;
        else if (this.person.getRandom().nextFloat() <= (person.isBlind() ? 0.6F : 0.01F)) {
            Vec3 vec3 = this.getPosition();
            if (vec3 == null) return false;
            else {
                this.wantedPos = vec3;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !person.getNavigation().isDone() && !person.isVehicle() && !person.isAggressive();
    }

    @Override
    public void start() {
        super.start();
        ActionUtils.moveTo(person, wantedPos, 0.82D);
    }

    @Override
    public void stop() {
        person.getNavigation().stop();
        super.stop();
    }

    @Nullable
    protected Vec3 getPosition() {
        if (person.isInWaterOrBubble()) {
            return LandRandomPos.getPos(person, 15, 7);
        }
        if (person.hasValidHomePos() && person.getHomePos().distToCenterSqr(person.position()) >= 30.0D) {
            return DefaultRandomPos.getPosTowards(person, 10, 7, Vec3.atBottomCenterOf(person.getHomePos()), (double)((float)Math.PI / 2F));
        }
        return DefaultRandomPos.getPos(person, 10, 7);
    }

    @Override
    public Priority getPriority() {
        return Priority.P10;
    }
}
