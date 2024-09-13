package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class VanisherTargetPlayerAction extends RangedAttackAction {
    protected static final Predicate<Player> IS_FAIRIE = (player) -> PowerUtils.accessPlayerCharacter(player).ghoulTarget;

    public VanisherTargetPlayerAction(AbstractPerson person) {
        super(person);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart()) return false;
        if (person.getInvolvedWar() == null && person.isAlive() && !person.isRemoved()) {
            Player player = findTarget();
            if (player == null) return false;
            this.target = player;
            return true;
        }
        return false;
    }

    @Override
    public Priority getPriority() {
        return Priority.P1;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    @Override
    public boolean canContinue() {
        return super.canContinue() && this.target != null && IS_FAIRIE.test((Player) target);
    }

    @Nullable
    protected Player findTarget() {
        List<Player> players = person.level.getEntitiesOfClass(Player.class, person.getBoundingBox().inflate(16.0D), IS_FAIRIE);
        if (players.isEmpty()) return null;
        return players.get(0);
    }
}
