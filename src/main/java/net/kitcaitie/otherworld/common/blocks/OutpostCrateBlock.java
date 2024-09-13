package net.kitcaitie.otherworld.common.blocks;

import io.netty.buffer.Unpooled;
import net.kitcaitie.otherworld.client.gui.menu.FakeInventoryMenu;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class OutpostCrateBlock extends Block {
    public OutpostCrateBlock(Properties properties) {
        super(properties);
    }

    public InteractionResult use(BlockState p_51531_, Level p_51532_, BlockPos p_51533_, Player p_51534_, InteractionHand p_51535_, BlockHitResult p_51536_) {
        if (p_51532_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuprovider = this.getMenuProvider(p_51531_, p_51532_, p_51533_);
            if (menuprovider != null) {
                p_51534_.openMenu(menuprovider);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.otherworld.crate");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put("i", PowerUtils.accessPlayerCharacter(player).fakeInventoryTag);
                return new FakeInventoryMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeNbt(compoundTag));
            }
        };
    }
}
