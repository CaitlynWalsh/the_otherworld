package net.kitcaitie.otherworld.common.entity.npcs.ai;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PersonNavigation extends GroundPathNavigation {
    protected boolean isRecomputingPath;
    protected boolean currentlyBlind;
    protected Path blindPath;

    public PersonNavigation(AbstractPerson person, Level level) {
        super(person, level);
        this.setCanFloat(true);
        this.setCanOpenDoors(true);
    }

    @Override
    public boolean moveTo(@Nullable Path p_26537_, double p_26538_) {
        if (this.isBlind()) {
            if (blindPath == null || blindPath.isDone()) {
                Vec3 vec3 = DefaultRandomPos.getPos((PathfinderMob) mob, 4, 2);
                if (vec3 != null) {
                    this.blindPath = createPath(Utils.vecToBpos(vec3), 1);
                }
            }
            return super.moveTo(this.blindPath, 0.86F);
        }
        return super.moveTo(p_26537_, p_26538_);
    }

    @Override
    public void tick() {
        if (shouldFreezeNavigation()) {
            this.stop();
        }
        else {
            this.checkBlindness();
            super.tick();
        }
    }

    private boolean shouldFreezeNavigation() {
        return mob.isPassenger() || mob.isSleeping() || mob.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get());
    }

    @Override
    public void recomputePath() {
        if (this.level.getGameTime() - this.timeLastRecompute > 20L) {
            this.isRecomputingPath = this.getTargetPos() != null;
            super.recomputePath();
        } else {
            this.hasDelayedRecomputation = true;
            this.isRecomputingPath = false;
        }
    }

    public void checkBlindness() {
        if (this.tick > 0 && this.tick % 10 == 1) {
            this.currentlyBlind = ((AbstractPerson)mob).isBlind();
        }
    }

    public boolean isRecomputingPath() {
        return isRecomputingPath;
    }

    public boolean isBlind() {
        return this.currentlyBlind;
    }
}
