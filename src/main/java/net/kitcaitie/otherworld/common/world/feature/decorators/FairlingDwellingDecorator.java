package net.kitcaitie.otherworld.common.world.feature.decorators;

import com.mojang.serialization.Codec;
import net.kitcaitie.otherworld.common.blocks.EntityNestBlock;
import net.kitcaitie.otherworld.common.entity.Fairling;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.kitcaitie.otherworld.registry.OtherworldEntities;
import net.kitcaitie.otherworld.registry.OtherworldFeatures;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class FairlingDwellingDecorator extends TreeDecorator {
    public static final Codec<FairlingDwellingDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(FairlingDwellingDecorator::new, (p_69971_) -> {
        return p_69971_.probability;
    }).codec();
    private final float probability;

    public FairlingDwellingDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return OtherworldFeatures.FAIRLING_DWELLINGS.get();
    }

    public void place(TreeDecorator.Context context) {
        RandomSource randomsource = context.random();
        if (!(randomsource.nextFloat() >= this.probability)) {
            List<BlockPos> list = context.logs();
            list.removeIf((blockPos) -> !context.level().isStateAtPosition(blockPos.below(), BlockState::isAir));
            Direction direction =  Direction.Plane.HORIZONTAL.getRandomDirection(randomsource);
            for (BlockPos blockPos : context.logs()) {
                if (context.level().isStateAtPosition(blockPos.relative(direction), BlockState::isAir)) {
                    context.setBlock(blockPos, OtherworldBlocks.FAIRLING_DWELLING.get().defaultBlockState().setValue(EntityNestBlock.FACING, direction));
                    context.level().getBlockEntity(blockPos, OtherworldBlocks.ENTITY_NEST_BLOCK_ENTITY.get()).ifPresent((entity) -> {
                        int j = randomsource.nextInt(1, 3);

                        for (int k = 0; k < j; ++k) {
                            CompoundTag compoundtag = new CompoundTag();
                            compoundtag.putString("id", ForgeRegistries.ENTITY_TYPES.getKey(OtherworldEntities.FAIRLING.get()).toString());
                            if (randomsource.nextInt(8) == 0) {
                                ItemStack stack = Fairling.SPAWN_ITEMS.get(randomsource.nextInt(Fairling.SPAWN_ITEMS.size())).getDefaultInstance();
                                ListTag listTag = new ListTag();
                                if (randomsource.nextInt(1000) == 0) {
                                    stack = OtherworldItems.OTHERWORLD_TOTEM.get().getDefaultInstance();
                                }
                                else if (randomsource.nextInt(100) == 0) {
                                    stack = OtherworldItems.OTHERAN_EYE.get().getDefaultInstance();
                                }
                                listTag.add(stack.save(new CompoundTag()));
                                compoundtag.put("HandItems", listTag);
                            }
                            else if (randomsource.nextInt(8) == 0) {
                                compoundtag.putInt("Age", -24000);
                            }
                            compoundtag.putBoolean("Reproduce", randomsource.nextInt(4) == 0);
                            entity.storeEntity(compoundtag, randomsource.nextInt(599));
                        }
                    });
                    break;
                }
            }
        }
    }
}
