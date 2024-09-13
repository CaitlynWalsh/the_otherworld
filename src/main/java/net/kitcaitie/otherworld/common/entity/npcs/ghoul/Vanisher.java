package net.kitcaitie.otherworld.common.entity.npcs.ghoul;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.AIBrain;
import net.kitcaitie.otherworld.common.entity.npcs.ai.brain.VanisherBrain;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Vanisher extends Ghoul {
    private boolean hasBook;
    public Vanisher(EntityType<? extends Vanisher> type, Level level) {
        super(type, level);
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
        return new VanisherBrain(this, level.getProfilerSupplier());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        this.setItemInHand(InteractionHand.MAIN_HAND, Items.BOW.getDefaultInstance());
    }

    @Override
    public boolean isMale() {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData retVal = super.finalizeSpawn(accessor, instance, type, data, tag);
        this.hasBook = true;
        this.despawnTimer = 2400;
        return retVal;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (PowerUtils.accessPlayerCharacter(player).isGhoul()) {
            this.sayTo(player, "vanisher");
            return InteractionResult.SUCCESS;
        }
        else if (this.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get())) {
            if (this.hasBook) {
                this.spawnAtLocation(OtherworldItems.OMINOUS_BOOK.get());
                this.hasBook = false;
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("hasBook", this.hasBook);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hasBook = tag.getBoolean("hasBook");
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public void setAge(int p_146763_) {
    }

    @Override
    public PersonData getPersonData() {
        return new PersonData();
    }

    @Override
    public void setPersonData(PersonData personData) {
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
    public ItemStack getShootItem() {
        ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
        PotionUtils.setPotion(itemstack, Potions.SLOWNESS);
        PotionUtils.setCustomEffects(itemstack, List.of(new MobEffectInstance(MobEffects.WITHER, 240, 0)));
        return itemstack;
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float v) {
        ItemStack itemstack = getShootItem();
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, v);
        if (abstractarrow instanceof Arrow) ((Arrow) abstractarrow).setEffectsFromItem(itemstack);
        if (this.getMainHandItem().getItem() instanceof BowItem)
            abstractarrow = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow);
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = entity.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrow);
    }

    public static boolean checkVanisherSpawnRules(EntityType<? extends Vanisher> type, ServerLevelAccessor accessor, MobSpawnType spawnType, BlockPos pos, RandomSource source) {
        return accessor.getLevel().isNight() && accessor.getLevel().getDayTime() >= 14000 && accessor.getMaxLocalRawBrightness(pos) == 0 && accessor.getBlockState(pos.below()).is(BlockTags.DIRT) && accessor.getLevel().getEntitiesOfClass(AbstractPerson.class, type.getAABB(pos.getX(), pos.getY(), pos.getZ()).inflate(50.0D)).isEmpty();
    }

    @Override
    public boolean canHaveName() {
        return false;
    }
}
