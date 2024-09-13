package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.blocks.LockableDoorBlock;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class UnlockOrLockPrisonDoor extends Action {
    private final Predicate<Player> IS_BEING_RELEASED;
    protected BlockPos doorPos;
    protected ItemStack key = ItemStack.EMPTY;
    protected boolean isLockingDoor;
    protected boolean unlockedDoor = false;

    public UnlockOrLockPrisonDoor(AbstractPerson person) {
        super(person);
        this.IS_BEING_RELEASED = (plr) -> {
            PlayerCharacter chr = PowerUtils.accessPlayerCharacter(plr);
            return chr.isCriminal() && (chr.getPrisonTime() == 0 || !chr.isImprisoned());
        };
        this.setFlags(EnumSet.of(Flags.LOOKING, Flags.MOVING, Flags.USE_BLOCK, Flags.INTERACTING, Flags.WAR));
    }

    @Override
    public boolean canStart() {
        BlockPos blockPos;
        if (person.getInvolvedWar() != null) {
            if (Set.of(person.getInvolvedWar().invader).contains(person.getRace()) && person.getLastHurtByMob() == null) {
                List<Player> players = this.person.level.getEntitiesOfClass(Player.class, this.person.getBoundingBox().inflate(15.0D));

                if (!players.isEmpty()) {
                    blockPos = ActionUtils.findBlock(person, (block) -> block.getBlock() instanceof LockableDoorBlock && block.getValue(LockableDoorBlock.LOCKED), players.get(0).blockPosition(), 10, 2);
                } else {
                    blockPos = ActionUtils.findBlock(person, (block) -> block.getBlock() instanceof LockableDoorBlock && block.getValue(LockableDoorBlock.LOCKED), person.blockPosition(), 20, 4);
                }

                if (blockPos != null && person.level.getBlockState(blockPos).getBlock() instanceof LockableDoorBlock doorBlock) {
                    ItemStack stack = person.getItemInInventory((i) -> i.is(doorBlock.getKey()));
                    if (stack != null) {
                        this.doorPos = blockPos;
                        this.key = stack;
                        this.isLockingDoor = false;
                        return true;
                    }
                }
            }
        }
        else if (person.getRandom().nextInt(60) == 0) {
            if (person.isSoldier() && person.getSpawnType() != MobSpawnType.NATURAL) return false;
            blockPos = ActionUtils.findBlock(person, (block) -> block.getBlock() instanceof LockableDoorBlock && !block.getValue(LockableDoorBlock.LOCKED), person.blockPosition(), 20, 4);
            if (blockPos != null && person.level.getBlockState(blockPos).getBlock() instanceof LockableDoorBlock doorBlock) {
                ItemStack stack = person.getItemInInventory((i) -> i.is(doorBlock.getKey()));
                if (stack != null) {
                    List<Player> players = person.level.getEntitiesOfClass(Player.class, new AABB(blockPos).inflate(20.0D), IS_BEING_RELEASED);
                    if (players.isEmpty()) {
                        this.doorPos = blockPos;
                        this.key = stack;
                        this.isLockingDoor = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return !unlockedDoor && person.level.getBlockState(doorPos).getBlock() instanceof LockableDoorBlock doorBlock && key.is(doorBlock.getKey()) && person.level.getBlockState(doorPos).getValue(LockableDoorBlock.LOCKED) != isLockingDoor;
    }

    @Override
    public void tick() {
        if (!person.getItemInHand(InteractionHand.MAIN_HAND).is(key.getItem())) person.setItemInHand(InteractionHand.MAIN_HAND, key);
        if (doorPos.closerToCenterThan(person.position(), 2.0D)) {
            BlockState state = person.level.getBlockState(doorPos);
            if (state.getBlock() instanceof LockableDoorBlock) {
                this.lockDoor(isLockingDoor);
            }
        }
        else {
            ActionUtils.lookAt(person, doorPos);
            ActionUtils.moveTo(person, doorPos, !this.isLockingDoor ? 1.2D : 1.0D);
        }
    }

    protected void lockDoor(boolean lock) {
        BlockState state = person.level.getBlockState(doorPos);
        if (state.getBlock() instanceof LockableDoorBlock) {
            person.swing(InteractionHand.MAIN_HAND);
            if (lock && state.getValue(DoorBlock.OPEN)) LockableDoorBlock.useDoor(state, person.level, doorPos, person,null);
            else {
                LockableDoorBlock.unlockOrLockDoor(state, person.level, doorPos, key, lock);
                this.unlockedDoor = true;
            }
        }
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    @Override
    public Priority getPriority() {
        return Priority.P2;
    }

    @Override
    public void stop() {
        this.key = ItemStack.EMPTY;
        this.person.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.doorPos = null;
        this.person.getNavigation().stop();
        super.stop();
        this.unlockedDoor = false;
        this.isLockingDoor = false;
    }
}
