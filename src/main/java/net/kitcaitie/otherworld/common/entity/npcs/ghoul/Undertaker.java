package net.kitcaitie.otherworld.common.entity.npcs.ghoul;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.AIBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.UndertakerBrain;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Undertaker extends Ghoul {
    public Undertaker(EntityType<? extends Undertaker> type, Level level) {
        super(type, level);
        this.tradingPlayer = null;
    }

    @Override
    public Occupation getOccupation() {
        return Occupation.TRAVELER;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return true;
    }

    @Override
    protected AIBrain chooseAIBrain(Level level) {
        return new UndertakerBrain(this, level.getProfilerSupplier());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
    }

    @Override
    public boolean isMale() {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData retVal = super.finalizeSpawn(accessor, instance, type, data, tag);
        this.despawnTimer = 2400;
        return retVal;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isAggressive() && PowerUtils.accessPlayerCharacter(player).isGhoul() && this.level.dimension().equals(Otherworld.UNDERLANDS)) {
            this.sayTo(player, "undertaker");
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public void setAge(int p_146763_) {
    }

    @Override
    public void setPersonData(PersonData personData) {
    }

    @Override
    public PersonData getPersonData() {
        return new PersonData();
    }

    @Override
    public void openTradingScreen(Player p_45302_, Component p_45303_, int p_45304_) {
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
    }

    @Override
    public BlockPos getHomePos() {
        return BlockPos.ZERO;
    }

    @Override
    public void setHomePos(BlockPos pos) {
    }

    @Override
    public boolean canHaveName() {
        return false;
    }
}
