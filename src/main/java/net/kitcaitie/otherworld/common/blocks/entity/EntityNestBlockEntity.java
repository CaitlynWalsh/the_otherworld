package net.kitcaitie.otherworld.common.blocks.entity;

import net.kitcaitie.otherworld.common.blocks.EntityNestBlock;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityNestBlockEntity extends BlockEntity {
    private static final List<String> IGNORED_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "DeathTime", "FallDistance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation");
    @Nullable
    protected EntityType<? extends LivingEntity> entityType;
    protected final List<StoredData> stored = new ArrayList<>();
    protected int maxOccupants;
    protected @Nullable SoundEvent enterNestSound;
    protected @Nullable SoundEvent leaveNestSound;
    protected final SimpleContainer inventory = new SimpleContainer(1);

    public EntityNestBlockEntity(BlockPos pos, BlockState state) {
        super(OtherworldBlocks.ENTITY_NEST_BLOCK_ENTITY.get(), pos, state);
        if (state.getBlock() instanceof EntityNestBlock) {
            this.setData((EntityNestBlock) state.getBlock());
        }
    }

    public void setData(EntityNestBlock nestBlock) {
        this.entityType = nestBlock.getEntityType();
        this.maxOccupants = nestBlock.getMaxOccupants();
        this.enterNestSound = nestBlock.getEnterNestSound();
        this.leaveNestSound = nestBlock.getLeaveNestSound();
    }

    public boolean releaseAllInhabitors(@Nullable Player player, BlockState blockState, boolean isEmergency) {
        List<LivingEntity> list = new ArrayList<>();
        if (level == null) return false;
        this.stored.removeIf((data) -> releaseEntityFromNest(this.level, this.worldPosition, blockState, data, list, player, isEmergency));
        if (!list.isEmpty()) {
            setChanged(this.level, this.worldPosition, blockState);
        }
        return true;
    }

    @Nullable
    public EntityType<? extends LivingEntity> getEntityType(LevelAccessor accessor, BlockPos pos) {
        if (this.entityType == null) {
            Block block = accessor.getBlockState(pos).getBlock();
            if (block instanceof EntityNestBlock) {
                this.setData((EntityNestBlock) block);
            }
        }
        return this.entityType;
    }

    public void addOccupant(LivingEntity entity) {
        this.addOccupantWithPresetTicks(entity, 0);
    }

    public boolean addItemToNest(ItemStack stack) {
        if (!this.inventory.canAddItem(stack)) {
            ItemStack item = this.inventory.getItem(0);
            if (this.level != null && item.getCount() >= item.getMaxStackSize()) {
                BlockState state = level.getBlockState(getBlockPos());
                if (!state.getValue(EntityNestBlock.FULL)) {
                    state.setValue(EntityNestBlock.FULL, true);
                    this.level.setBlock(getBlockPos(), state, 3);
                }
            }
            return false;
        }
        this.inventory.addItem(stack);
        return true;
    }

    public ItemStack getContents() {
        return this.inventory.getItem(0);
    }

    public void addOccupantWithPresetTicks(LivingEntity entity, int p_58747_) {
        if (this.stored.size() < maxOccupants && Objects.equals(entity.getType(), this.entityType) && entity instanceof EntityNestBlock.NestInhabitor inhabitor) {
            entity.stopRiding();
            entity.ejectPassengers();
            inhabitor.setNestPos(this.getBlockPos());
            inhabitor.onEnterNest(entity, getBlockPos(), this.level.getBlockState(getBlockPos()), this);
            CompoundTag compoundtag = new CompoundTag();
            entity.save(compoundtag);
            this.storeEntity(compoundtag, p_58747_);
            if (this.level != null) {
                BlockPos blockpos = this.getBlockPos();
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(entity, this.getBlockState()));
                if (this.enterNestSound != null) {
                    this.level.playSound(null, blockpos, this.enterNestSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }

            entity.discard();
            super.setChanged();
        }
    }

    public void storeEntity(CompoundTag tag, int ticks) {
        this.stored.add(new StoredData(tag, ticks, 300));
    }

    private static boolean releaseEntityFromNest(Level level, BlockPos blockPos, BlockState state, StoredData data, @Nullable List<LivingEntity> entities, @Nullable Player player, boolean isEmergency) {
        EntityNestBlockEntity blockEntity = (EntityNestBlockEntity) level.getBlockEntity(blockPos);
        if ((level.isNight() || level.isRaining()) && !isEmergency) {
            return false;
        }
        else {
            CompoundTag tag = data.entityData.copy();
            IGNORED_TAGS.forEach(tag::remove);
            tag.putLong("HomePos", blockPos.asLong());
            Direction direction = state.getValue(EntityNestBlock.FACING);
            BlockPos pos1 = blockPos.relative(direction);
            boolean flag = !level.getBlockState(pos1).getCollisionShape(level, pos1).isEmpty();
            if (flag && !isEmergency) {
                return false;
            }
            else {
                Entity entity = EntityType.loadEntityRecursive(tag, level, (entity1) -> entity1);
                if (entity instanceof LivingEntity) {
                    if (!Objects.equals(entity.getType(), blockEntity.getEntityType(level, blockPos))) {
                        return false;
                    }
                    else {
                        if (entities != null) {
                            entities.add((LivingEntity) entity);
                        }
                        float f = entity.getBbWidth();
                        double d3 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
                        double d0 = (double)blockPos.getX() + 0.5D + d3 * (double)direction.getStepX();
                        double d1 = (double)blockPos.getY() + 0.5D - (double)(entity.getBbHeight() / 2.0F);
                        double d2 = (double)blockPos.getZ() + 0.5D + d3 * (double)direction.getStepZ();
                        entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                    }
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, level.getBlockState(blockPos)));
                    if (blockEntity.leaveNestSound != null) {
                        level.playSound(null, blockPos, blockEntity.leaveNestSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    if (level.addFreshEntity(entity)) {
                        if (entity instanceof EntityNestBlock.NestInhabitor inhabitor) {
                            inhabitor.onLeaveNest(entity, blockPos, blockEntity, data.ticksInNest, player, isEmergency);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getMaxOccupants() {
        return maxOccupants;
    }

    public int getNumberOfInhabitants() {
        return this.stored.size();
    }

    private static void tickInhabitors(Level level, BlockPos blockPos, BlockState blockState, List<StoredData> storedData) {
        boolean flag = false;
        StoredData stored;

        for (Iterator<StoredData> storedDataIterator = storedData.iterator(); storedDataIterator.hasNext(); ++stored.ticksInNest) {
            stored = storedDataIterator.next();
            if (stored.ticksInNest > stored.minOccupationTicks) {
                if (releaseEntityFromNest(level, blockPos, blockState, stored, null, null, false)) {
                    flag = true;
                    storedDataIterator.remove();
                }
            }
        }

        if (flag) {
            setChanged(level, blockPos, blockState);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EntityNestBlockEntity entity) {
        tickInhabitors(level, pos, state, entity.stored);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.stored.clear();
        ListTag listTag = tag.getList("Inhabitors", 10);
        for(int i = 0; i < listTag.size(); ++i) {
            CompoundTag nbt = listTag.getCompound(i);
            StoredData storedData = new StoredData(nbt.getCompound("Entity"), nbt.getInt("TicksInNest"), nbt.getInt("MinOccupationTicks"));
            this.stored.add(storedData);
        }
        this.inventory.fromTag(tag.getList("Inventory", 10));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inhabitors", this.writeInhabitors());
        tag.put("Inventory", this.inventory.createTag());
    }

    @Override
    public void saveToItem(ItemStack stack) {
        CompoundTag tag = this.saveWithoutMetadata();
        tag.put("Inhabitors", this.writeInhabitors());
        tag.put("Inventory", this.inventory.createTag());
        BlockItem.setBlockEntityData(stack, this.getType(), tag);
    }

    protected ListTag writeInhabitors() {
        ListTag listTag = new ListTag();

        for (StoredData data : this.stored) {
            CompoundTag entityTag = data.entityData.copy();
            IGNORED_TAGS.forEach(entityTag::remove);
            CompoundTag nbt = new CompoundTag();
            nbt.put("Entity", entityTag);
            nbt.putInt("TicksInNest", data.ticksInNest);
            nbt.putInt("MinOccupationTicks", data.minOccupationTicks);
            listTag.add(nbt);
        }

        return listTag;
    }

    static class StoredData {
        final CompoundTag entityData;
        int ticksInNest;
        final int minOccupationTicks;

        public StoredData(CompoundTag tag, int ticksInNest, int minOccupationTicks) {
            IGNORED_TAGS.forEach(tag::remove);
            this.entityData = tag;
            this.ticksInNest = ticksInNest;
            this.minOccupationTicks = minOccupationTicks;
        }
    }

}
