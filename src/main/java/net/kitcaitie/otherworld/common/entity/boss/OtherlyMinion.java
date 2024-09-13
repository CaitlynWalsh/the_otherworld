package net.kitcaitie.otherworld.common.entity.boss;

import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.ActionUtils;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OtherlyMinion extends PathfinderMob implements RangedAttackMob {
    protected static final List<MobEffect> SPELLS = List.of(MobEffects.LEVITATION, MobEffects.BLINDNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS);
    protected static final EntityDataAccessor<Boolean> MALE = SynchedEntityData.defineId(OtherlyMinion.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> SUMMONER = SynchedEntityData.defineId(OtherlyMinion.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Byte> DATA_ATTACK_ID = SynchedEntityData.defineId(OtherlyMinion.class, EntityDataSerializers.BYTE);

    private int attackCooldown = 20;
    private int spellCastCooldown;
    private int projectileCooldown;
    private int meleeCooldown;

    private final ServerBossEvent bossBar;

    public enum OtherlyMinionArmPose {
        MELEE_ATTACKING,
        RANGED_ATTACKING,
        SPELLCASTING,
        IDLE
    }

    public OtherlyMinion(EntityType<? extends OtherlyMinion> type, Level level) {
        super(type, level);
        this.bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    }

    @Override
    public void setCustomName(@Nullable Component p_20053_) {
        super.setCustomName(p_20053_);
        this.bossBar.setName(this.getDisplayName());
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 20.0D)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    public OtherlyMinionArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return OtherlyMinionArmPose.SPELLCASTING;
        }
        else if (this.isThrowingProjectile()) {
            return OtherlyMinionArmPose.RANGED_ATTACKING;
        }
        else if (this.isMeleeAttacking()) {
            return OtherlyMinionArmPose.MELEE_ATTACKING;
        }
        return OtherlyMinionArmPose.IDLE;
    }

    public boolean isCastingSpell() {
        return this.entityData.get(DATA_ATTACK_ID) == 1;
    }

    public boolean isThrowingProjectile() {
        return this.entityData.get(DATA_ATTACK_ID) == 2;
    }

    public boolean isMeleeAttacking() {
        return this.entityData.get(DATA_ATTACK_ID) == 3;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level.isClientSide()) {
            if (attackCooldown > 0) --attackCooldown;
            if (spellCastCooldown > 0) {
                --spellCastCooldown;
                if (spellCastCooldown <= 0) {
                    this.entityData.set(DATA_ATTACK_ID, (byte)0);
                }
            }
            if (projectileCooldown > 0) {
                --projectileCooldown;
                if (projectileCooldown <= 0) {
                    this.entityData.set(DATA_ATTACK_ID, (byte)0);
                }
            }
            if (meleeCooldown > 0) {
                --meleeCooldown;
                if (meleeCooldown <= 0) {
                    this.entityData.set(DATA_ATTACK_ID, (byte)0);
                }
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (getSummoner() != null) {
            if (getTarget() == null || !getTarget().getUUID().equals(getSummoner())) {
                Player player = this.level.getPlayerByUUID(getSummoner());
                if (player != null && player.isAlive()) {
                    if (!bossBar.getPlayers().contains((ServerPlayer) player)) {
                        bossBar.addPlayer((ServerPlayer) player);
                        bossBar.setVisible(true);
                    }
                    if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                        this.setTarget(player);
                        this.setAggressive(true);
                    }
                }
            }
            else if (getTarget() != null && getTarget().isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(getTarget())) {
                if (getTarget().distanceToSqr(this) < 240.0D) {
                    if (getTarget().distanceToSqr(this) > 45.0D) this.getNavigation().moveTo(getTarget(), 1.0D);
                    this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
                    this.chooseAttack(this.getTarget());
                }
                else if (getTarget().isOnGround()) {
                    Vec3 vec3 = getTarget().blockPosition().offset(random.nextInt(1, 4), 0, random.nextInt(1, 4)).getCenter();
                    this.teleportTo(vec3.x, vec3.y, vec3.z);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT);
                }
            }
            else {
                this.setTarget(null);
                this.setAggressive(false);
            }
        }
    }

    public boolean isMale() {
        return this.entityData.get(MALE);
    }

    protected void chooseAttack(LivingEntity target) {
        if (attackCooldown <= 0) {
            if (ActionUtils.meleeAttack(this, target, 3.0F)) {
                this.meleeCooldown = 20;
                this.attackCooldown = meleeCooldown * 2;
                this.entityData.set(DATA_ATTACK_ID, (byte)3);
            }
            else if (spellCastCooldown <= 0 && random.nextBoolean() && random.nextBoolean()) {
                this.castSpell(target);
                this.attackCooldown = 20;
            } else if (projectileCooldown <= 0 && random.nextBoolean()) {
                this.performRangedAttack(target, 1.0F);
                this.attackCooldown = projectileCooldown * 4;
            }
        }
    }

    protected void castSpell(LivingEntity target) {
        MobEffect spell = SPELLS.get(random.nextInt(SPELLS.size()));
        this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
        this.playSound(SoundEvents.EVOKER_CAST_SPELL);
        target.addEffect(new MobEffectInstance(spell, 200, 1));
        this.spellCastCooldown = 40;
        this.entityData.set(DATA_ATTACK_ID, (byte)1);
    }

    @Nullable
    public UUID getSummoner() {
        return this.entityData.get(SUMMONER).orElse(null);
    }

    public void setSummoner(@Nullable Player player) {
        if (player != null) this.entityData.set(SUMMONER, Optional.of(player.getUUID()));
        else this.entityData.set(SUMMONER, Optional.empty());
    }

    public void setMale(boolean male) {
        this.entityData.set(MALE, male);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MALE,false);
        this.entityData.define(SUMMONER, Optional.empty());
        this.entityData.define(DATA_ATTACK_ID, (byte)0);
    }

    @Override
    public boolean hurt(DamageSource source, float amt) {
        if (source.getEntity() != null && source.getEntity().getUUID().equals(getSummoner())) return super.hurt(source, amt);
        else if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        return super.hurt(source, amt);
    }

    @Override
    public void die(DamageSource source) {
        bossBar.removeAllPlayers();
        bossBar.setVisible(false);
        if (source.getEntity() != null && source.getEntity().getUUID().equals(getSummoner())) {
            this.spawnAtLocation(OtherworldItems.OTHERWORLD_TOTEM.get().getDefaultInstance());
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Male", isMale());
        UUID summoner = getSummoner();
        if (summoner != null) {
            tag.putUUID("Summoner", summoner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Male")) setMale(tag.getBoolean("Male"));
        if (tag.hasUUID("Summoner")) this.entityData.set(SUMMONER, Optional.of(tag.getUUID("Summoner")));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        this.setMale(this.random.nextBoolean());
        return super.finalizeSpawn(levelAccessor, instance, spawnType, groupData, tag);
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float power) {
        this.projectileCooldown = 20;
        this.entityData.set(DATA_ATTACK_ID, (byte)2);
        if (this.level instanceof ServerLevel sLevel) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(sLevel);
            lightningBolt.moveTo(entity.blockPosition().offset(random.nextInt(0, 3), 0, random.nextInt(0, 3)).getCenter());
            lightningBolt.setDamage(3.25F);
            sLevel.addFreshEntity(lightningBolt);
        }
    }
}
