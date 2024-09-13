package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.common.blocks.PortalBlock;
import net.kitcaitie.otherworld.common.story.events.EventTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class PortalTotemItem extends Item {
    private final List<ResourceKey<Level>> dimensions;
    private final Supplier<Block> portalFrameBlock;
    private final Supplier<Block> portalBlock;
    private final EventTrigger trigger;

    public PortalTotemItem(Properties properties, List<ResourceKey<Level>> dimensions, Supplier<Block> portalFrameBlock, Supplier<Block> portalBlock, @Nullable EventTrigger trigger) {
        super(properties);
        this.dimensions = dimensions;
        this.portalFrameBlock = portalFrameBlock;
        this.portalBlock = portalBlock;
        this.trigger = trigger;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        ItemStack stack = useOnContext.getItemInHand();
        BlockPos blockPos = useOnContext.getClickedPos();
        if (dimensions.contains(level.dimension())) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                BlockPos framePos = blockPos.relative(direction);
                if (trySpawnPortal(level, framePos)) {
                    Minecraft.getInstance().gameRenderer.displayItemActivation(stack.copy());
                    if (trigger != null) {
                        trigger.of(framePos).trigger(player, null, null);
                    }
                    level.playSound(player, framePos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    stack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    public boolean trySpawnPortal(LevelAccessor level, BlockPos pos) {
        Size size = this.isPortal(level, pos);
        if (size != null) {
            size.placePortalBlocks();
            return true;
        }
        return false;
    }

    @Nullable
    private Size isPortal(LevelAccessor level, BlockPos pos) {
        Size size = new Size(level, pos, Direction.Axis.X, portalBlock, portalFrameBlock);
        if (size.isValid() && size.portalBlockCount == 0) {
            return size;
        }
        else {
            size = new Size(level, pos, Direction.Axis.Z, portalBlock, portalFrameBlock);
            return size.isValid() && size.portalBlockCount == 0 ? size : null;
        }
    }

    public static class Size {
        private final LevelAccessor level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private final BlockBehaviour.StatePredicate CAN_CONNECT;
        private final Supplier<Block> portalBlock;
        private final Supplier<Block> portalFrameBlock;
        private int portalBlockCount;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(LevelAccessor level, BlockPos pos, Direction.Axis axis, Supplier<Block> portalBlock, Supplier<Block> portalFrameBlock) {
            this.level = level;
            this.axis = axis;
            this.portalBlock = portalBlock;
            this.portalFrameBlock = portalFrameBlock;
            CAN_CONNECT = (bs, bg, bp) -> bs.isAir() || bs.is(this.portalBlock.get());
            if (axis == Direction.Axis.X) {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            for(BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && CAN_CONNECT.test(level.getBlockState(pos.below()), level, pos.below()); pos = pos.below()) {
            }

            int i = getDistanceUntilEdge(pos, this.leftDir) - 1;
            if (i >= 0) {
                this.bottomLeft = pos.relative(this.leftDir, i);
                this.width = getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = calculatePortalHeight();
            }
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
            int i;
            for(i = 0; i < 22; ++i) {
                BlockPos blockpos = pos.relative(directionIn, i);
                if(!CAN_CONNECT.test(level.getBlockState(blockpos), this.level, blockpos) ||
                        !(this.level.getBlockState(blockpos.below()).is(portalFrameBlock.get()))) {
                    break;
                }
            }

            BlockPos framePos = pos.relative(directionIn, i);
            return this.level.getBlockState(framePos).is(portalFrameBlock.get()) ? i : 0;
        }

        protected int calculatePortalHeight() {
            label56:
            for(this.height = 0; this.height < 21; ++this.height) {
                for(int i = 0; i < this.width; ++i) {
                    BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    if (!CAN_CONNECT.test(blockstate, this.level, blockpos)) {
                        break label56;
                    }

                    if (blockstate.is(portalBlock.get())) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        BlockPos framePos = blockpos.relative(this.leftDir);
                        if (!(this.level.getBlockState(framePos).is(portalFrameBlock.get()))) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        BlockPos framePos = blockpos.relative(this.rightDir);
                        if (!(this.level.getBlockState(framePos).is(portalFrameBlock.get()))) {
                            break label56;
                        }
                    }
                }
            }

            for(int j = 0; j < this.width; ++j) {
                BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
                if (!(this.level.getBlockState(framePos).is(portalFrameBlock.get()))) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks() {
            for(int i = 0; i < this.width; ++i) {
                BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i);

                for(int j = 0; j < this.height; ++j) {
                    this.level.setBlock(blockpos.above(j), portalBlock.get().defaultBlockState().setValue(PortalBlock.AXIS, this.axis), 18);
                }
            }
        }

        private boolean isPortalCountValidForSize() {
            return this.portalBlockCount >= this.width * this.height;
        }

        public boolean validatePortal() {
            return this.isValid() && this.isPortalCountValidForSize();
        }

    }
}
