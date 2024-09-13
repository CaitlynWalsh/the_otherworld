package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import java.util.EnumSet;

public class FarmerWorkAction extends Action {
    private static final int COOLDOWN_TICKS = 10;
    protected final int range;
    protected final ObjectArrayList<BlockPos> farmland = new ObjectArrayList<>();
    protected BlockPos waterPos;
    protected BlockPos currentFarmingPos;
    protected ItemStack hoe = ItemStack.EMPTY;
    protected ItemStack seeds = ItemStack.EMPTY;
    protected ItemStack bonemeal = ItemStack.EMPTY;
    protected int farmingCooldown = COOLDOWN_TICKS;

    public FarmerWorkAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.LOOKING, Flags.USE_BLOCK, Flags.USE_ITEM, Flags.PLACE_BLOCK, Flags.DESTROY_BLOCK));
        this.range = 3;
    }

    @Override
    public boolean canStart() {
        if (person.isBaby() || !person.isVillager()) return false;
        if (person.getJobType() != IOccupation.VillagerType.FARMER) return false;
        else if (person.getWorkPos() != BlockPos.ZERO && person.isVillagerWorkingHours()) {
            getFarmingItems();
            if (waterPos == null || (needsWater() && !isWaterOrLava(person.level.getBlockState(waterPos)))) {
                this.waterPos = needsWater() ? ActionUtils.findBlock(person, this::isWaterOrLava,
                        person.getWorkPos(), 3, 4) : person.getWorkPos().below();
                if (waterPos == null) {
                    return false;
                }
            }
            getFarmingBlocks();
            return !farmland.isEmpty();
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        if (person.tickCount % 60 == 1 && (this.seeds.isEmpty() || this.hoe.isEmpty() || this.bonemeal.isEmpty())) getFarmingItems();
        return !farmland.isEmpty() && person.getWorkPos() != BlockPos.ZERO && person.getJobType() == IOccupation.VillagerType.FARMER && (!needsWater() || isWaterOrLava(person.level.getBlockState(waterPos)));
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.farmingCooldown > 0) return;
        if (!farmland.isEmpty()) {
            this.currentFarmingPos = farmland.get(0);
            if (shouldHarvest(this.currentFarmingPos)) {
                if (currentFarmingPos.closerToCenterThan(person.position(), 2.0D)) {
                    person.swing(InteractionHand.MAIN_HAND);
                    ActionUtils.breakBlock(person, this.currentFarmingPos.above(), true);
                    this.farmland.remove(this.currentFarmingPos);
                    this.farmingCooldown = COOLDOWN_TICKS;
                }
                ActionUtils.lookAt(person, this.currentFarmingPos);
                ActionUtils.moveTo(person, this.currentFarmingPos, 0.85D);
                return;
            }
            if (shouldBoneMeal(currentFarmingPos)) {
                if (person.getItemInHand(InteractionHand.MAIN_HAND) != this.bonemeal)
                    person.setItemInHand(InteractionHand.MAIN_HAND, this.bonemeal);
                if (currentFarmingPos.closerToCenterThan(person.position(), 2.0D)) {
                    person.swing(InteractionHand.MAIN_HAND);
                    this.bonemeal.shrink(1);
                    ActionUtils.bonemealBlock(person, currentFarmingPos.above());
                    if (this.bonemeal.isEmpty() || shouldHarvest(currentFarmingPos)) {
                        this.farmland.remove(this.currentFarmingPos);
                    }
                    this.farmingCooldown = COOLDOWN_TICKS;
                }
                ActionUtils.lookAt(person, this.currentFarmingPos);
                ActionUtils.moveTo(person, this.currentFarmingPos, 0.85D);
                return;
            }
            if (shouldTill(currentFarmingPos)) {
                if (person.getItemInHand(InteractionHand.MAIN_HAND) != this.hoe)
                    person.setItemInHand(InteractionHand.MAIN_HAND, this.hoe);
                if (currentFarmingPos.closerToCenterThan(person.position(), 2.0D)) {
                    BlockState state = geTilledBlockState(currentFarmingPos);
                    if (state != person.level.getBlockState(currentFarmingPos)) {
                        person.swing(InteractionHand.MAIN_HAND);
                        person.level.playSound(null, currentFarmingPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ActionUtils.changeBlock(person, state, currentFarmingPos);
                        this.farmingCooldown = COOLDOWN_TICKS;
                    } else {
                        this.farmland.remove(currentFarmingPos);
                        return;
                    }
                }
                ActionUtils.lookAt(person, this.currentFarmingPos);
                ActionUtils.moveTo(person, this.currentFarmingPos, 0.85D);
                return;
            }
            if (shouldPlantOn(currentFarmingPos) && !seeds.isEmpty()) {
                if (person.getItemInHand(InteractionHand.MAIN_HAND) != this.seeds)
                    person.setItemInHand(InteractionHand.MAIN_HAND, this.seeds);
                if (currentFarmingPos.closerToCenterThan(person.position(), 2.0D)) {
                    BlockState state = ((ItemNameBlockItem) this.seeds.getItem()).getBlock().defaultBlockState();
                    person.swing(InteractionHand.MAIN_HAND);
                    seeds.shrink(1);
                    ActionUtils.placeBlock(person, state, currentFarmingPos.above());
                    this.farmingCooldown = COOLDOWN_TICKS;
                    this.farmland.remove(this.currentFarmingPos);
                    return;
                }
                ActionUtils.lookAt(person, this.currentFarmingPos);
                ActionUtils.moveTo(person, this.currentFarmingPos, 0.85D);
                return;
            }
            this.farmland.remove(this.currentFarmingPos);
        }
    }

    @Override
    public EnumSet<Flags> disabledFlags() {
        return EnumSet.of(Flags.MOVING_WANDER);
    }

    @Override
    public void stop() {
        super.stop();
        person.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.farmland.clear();
        this.farmingCooldown = COOLDOWN_TICKS;
        this.currentFarmingPos = null;
        this.seeds = ItemStack.EMPTY;
        this.hoe = ItemStack.EMPTY;
        this.bonemeal = ItemStack.EMPTY;
    }

    protected BlockState geTilledBlockState(BlockPos pos) {
        BlockState state = person.level.getBlockState(pos);
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)) return Blocks.FARMLAND.defaultBlockState();
        else if (state.is(OtherworldTags.TILLABLE_BLOCKS) && state.getBlock() instanceof IWorlds world) {
            switch (world.getWorldType()) {
                case ROSEIA -> {
                    return OtherworldBlocks.ROSE_QUARTZ_FARMLAND.get().defaultBlockState();
                }
                case ENCHANTIA -> {
                    return OtherworldBlocks.ENCHANTED_FARMLAND.get().defaultBlockState();
                }
                case EMBERIA -> {
                    return OtherworldBlocks.SOOT_BLOCK.get().defaultBlockState();
                }
                case GLACEIA -> {
                    return OtherworldBlocks.GRANULAR_ICE.get().defaultBlockState();
                }
            }
        }
        return state;
    }

    public boolean isWaterOrLava(BlockState bs) {
        return person.isEmberian() ? bs.getFluidState().getType() == Fluids.LAVA : ((bs.hasProperty(BlockStateProperties.WATERLOGGED) && bs.getValue(BlockStateProperties.WATERLOGGED)) || bs.getFluidState().getType() == Fluids.WATER);
    }

    public boolean needsWater() {
        return !person.isIceian() && !person.isGhoul();
    }

    protected void getFarmingBlocks() {
        this.farmland.clear();
        for (int i=-range; i < range; i++) {
            for (int j=-range; j < range; j++) {
                BlockPos blockPos = this.waterPos.mutable().move(i, 0, j);
                BlockState blockState = person.level.getBlockState(blockPos);
                if (blockState.is(OtherworldTags.TILLABLE_BLOCKS) || blockState.is(OtherworldTags.FARMLAND)) {
                    this.farmland.add(blockPos);
                }
            }
        }
    }

    protected void getFarmingItems() {
        this.seeds = person.getItemInInventory((stack) -> stack.getItem() instanceof ItemNameBlockItem blockItem && blockItem.getBlock() instanceof CropBlock);
        this.hoe = person.getItemInInventory((stack) -> stack.getItem() instanceof HoeItem);
        this.bonemeal = person.getItemInInventory((item) -> item.is(Items.BONE_MEAL));
    }

    protected boolean shouldPlantOn(BlockPos pos) {
        if (seeds.isEmpty() || !person.level.isEmptyBlock(pos.above())) return false;
        return person.level.getBlockState(pos).is(OtherworldTags.FARMLAND) && ((CropBlock)((ItemNameBlockItem)seeds.getItem()).getBlock()).canSurvive(person.level.getBlockState(pos.above()), person.level, pos.above());
    }

    protected boolean shouldBoneMeal(BlockPos pos) {
        if (bonemeal.isEmpty()) return false;
        BlockState crop = person.level.getBlockState(pos.above());
        if (!(crop.getBlock() instanceof CropBlock)) return false;
        return !(((CropBlock)crop.getBlock()).isMaxAge(crop));
    }

    protected boolean shouldHarvest(BlockPos pos) {
        BlockState crop = person.level.getBlockState(pos.above());
        if (!(crop.getBlock() instanceof CropBlock)) return false;
        return ((CropBlock)crop.getBlock()).isMaxAge(crop);
    }

    protected boolean shouldTill(BlockPos pos) {
        if (this.hoe.isEmpty()) return false;
        if (!person.level.getBlockState(pos.above()).getMaterial().isReplaceable()) return false;
        return person.level.getBlockState(pos).is(OtherworldTags.TILLABLE_BLOCKS);
    }

}
