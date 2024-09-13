package net.kitcaitie.otherworld.common.entity;

import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Whisp extends AmbientCreature {
    @Nullable
    private BlockPos targetPosition;
    public Whisp(EntityType<? extends Whisp> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0F).add(Attributes.ATTACK_DAMAGE, 0.0F).add(Attributes.MOVEMENT_SPEED, 0.3F).build();
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
    }

    public static boolean checkSpawnRules(EntityType<? extends Whisp> type, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        return source.nextInt(60) == 0;
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    public boolean isPushable() {
        return false;
    }

    protected void doPush(Entity p_27415_) {
    }

    protected void pushEntities() {
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.tickCount % 10 == 0) {
            Vec3 vec3 = this.position();
            ((ServerLevel) this.level).sendParticles(ParticleTypes.CLOUD, vec3.x, vec3.y + 0.5D, vec3.z, 4, this.getBbWidth(), this.getBbHeight(), this.getBbWidth(), 0.2D);
        }

        if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
            this.targetPosition = null;
        }

        if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0D)) {
            this.targetPosition = BlockPos.containing(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
        }

        double d2 = (double)this.targetPosition.getX() + 0.5D - this.getX();
        double d0 = (double)this.targetPosition.getY() + 0.1D - this.getY();
        double d1 = (double)this.targetPosition.getZ() + 0.5D - this.getZ();
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec31 = vec3.add((Math.signum(d2) * 0.5D - vec3.x) * (double)0.1F, (Math.signum(d0) * (double)0.7F - vec3.y) * (double)0.1F, (Math.signum(d1) * 0.5D - vec3.z) * (double)0.1F);
        this.setDeltaMovement(vec31);
    }

    private void spawnDeathParticles(ServerLevel level) {
        Vec3 vec3 = this.position();
        level.sendParticles(ParticleTypes.CLOUD, vec3.x, vec3.y + 0.5D, vec3.z, 12, this.getBbWidth(), this.getBbHeight(), this.getBbWidth(), 0.2D);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.isSecondaryUseActive()) {
            this.spawnAtLocation(OtherworldItems.WHISP.get().getDefaultInstance());
            this.remove(RemovalReason.KILLED);
            if (!level.isClientSide()) this.spawnDeathParticles((ServerLevel) level);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
