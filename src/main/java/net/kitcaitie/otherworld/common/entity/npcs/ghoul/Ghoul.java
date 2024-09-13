package net.kitcaitie.otherworld.common.entity.npcs.ghoul;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

public class Ghoul extends AbstractPerson {

    public Ghoul(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, -1.0F);
    }

    @Override
    public Race getRace() {
        return Race.GHOUL;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.GHOUL;
    }

    @Override
    public boolean canBreedWith(LivingEntity entity) {
        return false;
    }

    @Override
    public @Nullable AbstractPerson getBaby(ServerLevel level, LivingEntity partner) {
        return null;
    }

    @Override
    public boolean canSleep() {
        return false;
    }

}
