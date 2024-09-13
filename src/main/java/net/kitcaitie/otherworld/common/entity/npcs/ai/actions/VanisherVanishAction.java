package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Predicate;

public class VanisherVanishAction extends Action {
    private boolean vanishingFromPlayer;
    private static final Predicate<LivingEntity> VANISH_FROM = (entity) -> {
        if (entity instanceof Player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter((Player) entity);
            if (character.ghoulTarget) return false;
            return !character.isGhoul();
        }
        else if (entity instanceof AbstractPerson races) {
            return !races.isGhoul();
        }
        return false;
    };

    public VanisherVanishAction(AbstractPerson person) {
        super(person);
    }

    @Override
    public boolean canStart() {
        if (!person.isAggressive() && !person.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get())) {
            LivingEntity entity = findEntityToVanishFrom();
            if (entity == null) return false;
            if (entity instanceof Player) this.vanishingFromPlayer = true;
            return person.hasLineOfSight(entity);
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        if (vanishingFromPlayer && person.getRandom().nextBoolean()) {
            person.spawnAtLocation(OtherworldItems.WITHERDUST.get());
        }
        person.discard();
    }

    @Override
    public Priority getPriority() {
        return Priority.P0;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    private LivingEntity findEntityToVanishFrom() {
        List<LivingEntity> list = person.level.getEntitiesOfClass(LivingEntity.class, person.getBoundingBox().inflate(12.0D), VANISH_FROM);
        if (list.isEmpty()) return null;
        return list.get(0);
    }
}
