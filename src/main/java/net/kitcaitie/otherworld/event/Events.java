package net.kitcaitie.otherworld.event;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.blocks.OtheranBlock;
import net.kitcaitie.otherworld.common.world.BlockData;
import net.kitcaitie.otherworld.common.world.OtherworldServerLevel;
import net.kitcaitie.otherworld.registry.OtherworldBlocks;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Otherworld.MODID)
public class Events {

    @SubscribeEvent
    public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof Player && !(event.getEntity() instanceof FakePlayer)) {
            if (!event.isCanceled() && event.getResult() != Event.Result.DENY) {
                if (useHoe(event)) {
                    event.getEntity().swing(event.getHand());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() != null) {
            if (event.getEntity().level instanceof ServerLevel serverLevel) {
                if (BlockData.IS_VALID_BLOCK.test(event.getPlacedBlock())) {
                    StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(event.getPos(), OtherworldTags.OTHERWORLD_UNBREAKABLE_STRUCTURES);
                    if (structureStart.isValid() && serverLevel.structureManager().structureHasPieceAt(event.getPos(), structureStart)) {
                        ((OtherworldServerLevel) serverLevel).getBlockData().addBlock(event.getPos());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreaking(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof FakePlayer)) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                if (!serverLevel.dimension().equals(Otherworld.OTHERWORLD)) return;
                boolean allowed = event.getPlayer().hasPermissions(OtherworldConfigs.SERVER.permissionLevelToBreakStructures.get());
                if (serverLevel.getServer().isDedicatedServer()) {
                    if (allowed && serverLevel.getServer().getPlayerList().getOps().get(event.getPlayer().getGameProfile()) != null) {
                        ((OtherworldServerLevel)serverLevel).getBlockData().removeBlock(event.getPos());
                        return;
                    }
                } else if (allowed) {
                    ((OtherworldServerLevel)serverLevel).getBlockData().removeBlock(event.getPos());
                    return;
                }
                StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(event.getPos(), OtherworldTags.OTHERWORLD_UNBREAKABLE_STRUCTURES);
                if (structureStart.isValid() && serverLevel.structureManager().structureHasPieceAt(event.getPos(), structureStart)) {
                    boolean canceled = BlockData.IS_VALID_BLOCK.test(event.getState()) && !((OtherworldServerLevel)serverLevel).getBlockData().containsBlock(event.getPos());
                    if (!canceled) {
                        ((OtherworldServerLevel)serverLevel).getBlockData().removeBlock(event.getPos());
                    }
                    event.setCanceled(canceled);
                }
            }
        }
    }

    public static boolean useHoe(PlayerInteractEvent.RightClickBlock event) {
        ItemStack hoe = event.getItemStack();
        if (!hoe.isEmpty() && hoe.getItem() instanceof HoeItem) {
            Level level = event.getLevel();
            BlockState blockState = level.getBlockState(event.getPos());
            if (blockState.getBlock() instanceof OtheranBlock && blockState.is(BlockTags.DIRT)) {
                if (!level.isClientSide) {
                    switch (((OtheranBlock) blockState.getBlock()).getWorldType()) {
                        case EMBERIA -> level.setBlockAndUpdate(event.getPos(), OtherworldBlocks.SOOT_BLOCK.get().defaultBlockState());
                        case ENCHANTIA -> level.setBlockAndUpdate(event.getPos(), OtherworldBlocks.ENCHANTED_FARMLAND.get().defaultBlockState());
                        case GLACEIA -> level.setBlockAndUpdate(event.getPos(), OtherworldBlocks.GRANULAR_ICE.get().defaultBlockState());
                        case ROSEIA -> level.setBlockAndUpdate(event.getPos(), OtherworldBlocks.ROSE_QUARTZ_FARMLAND.get().defaultBlockState());
                    }
                    level.playSound(null, event.getPos(), SoundEvents.HOE_TILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                    hoe.hurtAndBreak(1, event.getEntity(), player -> {
                        player.broadcastBreakEvent(event.getHand());
                        ForgeEventFactory.onPlayerDestroyItem(event.getEntity(), hoe, event.getHand());
                    });
                }
                return true;
            }
        }
        return false;
    }
}
