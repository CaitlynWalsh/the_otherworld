package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.blocks.LockableDoorBlock;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.IOtherworldPlayer;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;
import java.util.List;

public class ReleasePrisonerAction extends Action {
    protected Player prisoner;
    protected BlockPos doorPos;
    protected ItemStack key = ItemStack.EMPTY;
    protected boolean unlockedDoor = false;
    protected boolean relockedDoor = false;
    private StopCheck stopCheck;

    public ReleasePrisonerAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.MOVING, Flags.LOOKING, Flags.USE_BLOCK, Flags.INTERACTING));
    }

    @Override
    public boolean canStart() {
        if (person.getInvolvedWar() != null) return false;
        if (person.isAggressive() || person.getCombatTracker().isInCombat()) return false;
        ItemStack stack = getKey();
        if (!stack.isEmpty()) {
            List<Player> players = person.level.getEntitiesOfClass(Player.class, person.getBoundingBox().inflate(50.0D), (plr) -> PowerUtils.accessPlayerCharacter(plr).isImprisoned());
            if (!players.isEmpty()) {
                Player player = players.stream().filter(ReleasePrisonerAction::isPlayerInPrison).findFirst().orElse(null);
                if (player != null) {
                    BlockPos pos = ActionUtils.findBlock(person, (block) -> block.getBlock() instanceof LockableDoorBlock doorBlock && stack.is(doorBlock.getKey()), player.blockPosition(), 6, 1);
                    if (pos != null) {
                        person.passiveTargets.addAll(players);
                        prisoner = player;
                        doorPos = pos;
                        key = stack;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(prisoner);
        character.setImprisoned(false);
        character.sendPacket(prisoner);
        this.stopCheck = new StopCheck();
        stopCheck.start();
    }

    @Override
    public boolean canContinue() {
        return !this.relockedDoor && person.getInvolvedWar() == null && person.getLastHurtByMob() == null && prisoner != null && prisoner.isAlive() && !prisoner.isRemoved() && person.level.getBlockState(doorPos).getBlock() instanceof LockableDoorBlock doorBlock && key.is(doorBlock.getKey());
    }

    @Override
    public void tick() {
        if (!person.getItemInHand(InteractionHand.MAIN_HAND).is(key.getItem())) person.setItemInHand(InteractionHand.MAIN_HAND, key);
        if (doorPos.closerToCenterThan(person.position(), 2.0D)) {
            BlockState state = person.level.getBlockState(doorPos);
            if (state.getBlock() instanceof LockableDoorBlock) {
                if (unlockedDoor && !state.getValue(LockableDoorBlock.LOCKED)) {
                    ActionUtils.lookAt(person, prisoner);
                    ActionUtils.stopMoving(person);
                    if (!state.getValue(DoorBlock.OPEN)) {
                        LockableDoorBlock.useDoor(state, person.level, doorPos, person, null);
                    }
                    if (person.level.canSeeSky(prisoner.blockPosition())) {
                        LockableDoorBlock.useDoor(state, person.level, doorPos, person, null);
                        PlayerCharacter character = PowerUtils.accessPlayerCharacter(prisoner);
                        character.releaseFromJail((ServerPlayer) prisoner);
                        character.sendPacket(prisoner);
                        this.relockedDoor = true;
                    }
                } else {
                    if (lockDoor(false)) {
                        this.unlockedDoor = true;
                        person.sayTo(prisoner, DialogueEvent.PRISON_RELEASE.getString());
                    }
                }
            }
        }
        else {
            ActionUtils.lookAt(person, doorPos);
            ActionUtils.moveTo(person, doorPos, 0.9D);
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P1;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    @Override
    public void stop() {
        if (this.person != null && this.person.isAlive()) {
            this.stopCheck.checkReset();
            person.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            person.passiveTargets.clear();
        }
        this.prisoner = null;
        this.doorPos = null;
        this.key = null;
        super.stop();
        this.unlockedDoor = false;
        this.relockedDoor = false;
        stopCheck.stop();
        stopCheck = null;
    }

    protected boolean lockDoor(boolean lock) {
        BlockState state = person.level.getBlockState(doorPos);
        if (state.getBlock() instanceof LockableDoorBlock) {
            person.swing(InteractionHand.MAIN_HAND);
            if (lock && state.getValue(DoorBlock.OPEN)) {
                LockableDoorBlock.useDoor(state, person.level, doorPos, person,null);
                return false;
            }
            LockableDoorBlock.unlockOrLockDoor(state, person.level, doorPos, key, lock);
            return true;
        }
        return false;
    }

    protected ItemStack getKey() {
        return person.getItemInInventory((item) -> {
            switch (person.getRace()) {
                case ONI -> {
                    return item.is(OtherworldItems.IRON_KEY.get());
                }
                case ROSEIAN -> {
                    return item.is(OtherworldItems.ROSEGOLD_KEY.get());
                }
                case FAIRIE -> {
                    return item.is(OtherworldItems.OPAL_KEY.get());
                }
                case EMBERIAN -> {
                    return item.is(OtherworldItems.TOPAZ_KEY.get());
                }
                case ICEIAN -> {
                    return item.is(OtherworldItems.SAPPHIRE_KEY.get());
                }
                default -> {
                    return false;
                }
            }
        });
    }

    public static boolean isPlayerInPrison(Player player) {
        PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
        return character.canBeReleasedFromJail() && character.isImprisoned();
    }

    private class StopCheck {
        public void start() {
            //System.out.println("CheckStopping registered!");
            MinecraftForge.EVENT_BUS.register(this);
        }

        public void checkReset() {
            if (!ReleasePrisonerAction.this.lockDoor(true)) {
                ReleasePrisonerAction.this.lockDoor(true);
            }

            PlayerCharacter character = PowerUtils.accessPlayerCharacter(prisoner);

            if (character.isCriminal() && !ReleasePrisonerAction.this.relockedDoor) {
                //System.out.println("CheckStopping is resetting player!");
                character.setImprisoned(true);
                character.sendPacket(prisoner);
            }
        }

        @SubscribeEvent
        public void livingDeathEvent(LivingDeathEvent event) {
            if (event.getEntity().is(ReleasePrisonerAction.this.person)) {
                if (ReleasePrisonerAction.this.prisoner != null) {
                    //System.out.println("CheckStopping is active!");
                    if (event.getSource().getEntity() != null && event.getSource().getEntity().is(ReleasePrisonerAction.this.prisoner)) return;
                    checkReset();
                }
                this.stop();
            }
        }

        @SubscribeEvent
        public void playerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity().is(ReleasePrisonerAction.this.prisoner)) {
                //System.out.println("CheckStopping is active!");

                if (!ReleasePrisonerAction.this.lockDoor(true)) {
                    ReleasePrisonerAction.this.lockDoor(true);
                }

                PlayerCharacter character = ((IOtherworldPlayer) ReleasePrisonerAction.this.prisoner).getPlayerCharacter();

                if (character.isCriminal() && !ReleasePrisonerAction.this.relockedDoor) {
                    //System.out.println("CheckStopping is resetting player!");
                    character.setImprisoned(true);
                    ((IOtherworldPlayer) ReleasePrisonerAction.this.prisoner).setPlayerCharacterTag(character.writeNBT());

                }
                this.stop();
            }
        }

        public void stop() {
            //System.out.println("CheckStopping unregistered!");
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

}
