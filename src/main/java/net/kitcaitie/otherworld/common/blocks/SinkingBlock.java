package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.registry.OtherworldDamage;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class SinkingBlock extends Block implements BucketPickup {
    private final ResourceKey<DamageType> damageType;
    @Nullable private final SoundEvent sinkSound;
    private final Supplier<Item> bucket;

    public SinkingBlock(Properties properties, ResourceKey<DamageType> damageType, @Nullable SoundEvent sinkSound, Supplier<Item> bucket) {
        super(properties.noCollission().isSuffocating((bs, bg, bp) -> false).isValidSpawn((bs, bg, bp, e) -> false));
        this.sinkSound = sinkSound;
        this.damageType = damageType;
        this.bucket = bucket;
    }

    public VoxelShape getBlockSupportShape(BlockState p_54456_, BlockGetter p_54457_, BlockPos p_54458_) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
        return Shapes.block();
    }

    @Nullable
    public SoundEvent getSinkSound() {
        return sinkSound;
    }

    public ResourceKey<DamageType> getDamageType() {
        return damageType;
    }

    public Item getBucket() {
        return bucket.get();
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter p_49823_, BlockPos p_49824_, BlockState p_49825_) {
        return getBucket().getDefaultInstance();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        boolean living = entity instanceof LivingEntity;
        boolean flag = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
        RandomSource source = level.getRandom();
        entity.makeStuckInBlock(state, new Vec3(0.25D, 1.0D, 0.25D));
        if (living && level.isClientSide()) {
            if (flag && source.nextBoolean()) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), entity.getX(), (double) (entity.getY() + 1), entity.getZ(), (double) (Mth.randomBetween(source, -1.0F, 1.0F) * 0.083333336F), (double) 0.05F, (double) (Mth.randomBetween(source, -1.0F, 1.0F) * 0.083333336F));
            }
        }
        if (living && !level.isClientSide()) {
            if (source.nextInt(10) == 0 && getSinkSound() != null) {
                level.playSound(null, pos, getSinkSound(), SoundSource.BLOCKS);
            }
            if (level.getBlockState(Utils.vecToBpos(entity.getEyePosition())).is(this)) {
                if (source.nextBoolean()) {
                    entity.hurt(OtherworldDamage.source(level.registryAccess(), getDamageType(), null, null), 1.0F);
                }
            }
        }
    }

    public boolean isPathfindable(BlockState p_154258_, BlockGetter p_154259_, BlockPos p_154260_, PathComputationType p_154261_) {
        return true;
    }

    public float getShadeBrightness(BlockState p_222462_, BlockGetter p_222463_, BlockPos p_222464_) {
        return 0.2F;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (level.isClientSide()) {
            RandomSource source = level.getRandom();
            for (int i=0; i<source.nextInt(5, 8); i++) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX(), (double) (pos.getY() + 1), pos.getZ(), (double) (Mth.randomBetween(source, -1.0F, 1.0F) * 0.083333336F), (double) 0.05F, (double) (Mth.randomBetween(source, -1.0F, 1.0F) * 0.083333336F));
            }
        }

        if (!level.isClientSide()) {
            if (getSinkSound() != null) {
                level.playSound(null, pos, getSinkSound(), SoundSource.BLOCKS);
            }
            level.levelEvent(2001, pos, Block.getId(state));
        }
        return getBucket().getDefaultInstance();
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.ofNullable(getSinkSound());
    }
}
