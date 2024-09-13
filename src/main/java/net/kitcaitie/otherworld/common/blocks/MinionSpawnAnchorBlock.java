package net.kitcaitie.otherworld.common.blocks;

import net.kitcaitie.otherworld.common.story.events.EventTrigger;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MinionSpawnAnchorBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    protected final EventTrigger minionSpawner;
    private final int time;

    public MinionSpawnAnchorBlock(Properties properties, EventTrigger minionSpawner, int time) {
        super(properties);
        this.minionSpawner = minionSpawner;
        this.time = time;
        this.registerDefaultState(this.getStateDefinition().any().setValue(CHARGED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (state.getValue(CHARGED) || !stack.is(OtherworldTags.OTHERLY_MINION_SUMMON_ITEMS)) {
            return InteractionResult.PASS;
        } else {
            BlockState state1 = state.setValue(CHARGED, true);
            level.setBlock(pos, state1, 3);
            stack.shrink(1);
            if (level instanceof ServerLevel) {
                level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);

                new Object() {
                    int time;
                    void start(int timer) {
                        this.time = timer;
                        MinecraftForge.EVENT_BUS.register(this);
                    }

                    @SubscribeEvent
                    void update(TickEvent.ServerTickEvent event) {
                        --time;
                        if (time < 0) {
                            minionSpawner.of(pos).trigger(player, null, null);
                            MinecraftForge.EVENT_BUS.unregister(this);
                        }
                    }

                }.start(time);

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }
}
