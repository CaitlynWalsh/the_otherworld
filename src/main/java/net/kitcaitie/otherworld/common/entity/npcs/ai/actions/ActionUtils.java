package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.client.DialogueEvent;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.PersonMoveControl;
import net.kitcaitie.otherworld.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ActionUtils {

    public static void lookAt(PathfinderMob entity, double x, double y, double z) {
        entity.getLookControl().setLookAt(x, y, z);
    }

    public static void lookAt(PathfinderMob entity, BlockPos blockPos) {
        entity.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }

    public static void lookAt(PathfinderMob entity, Entity lookAt) {
        entity.getLookControl().setLookAt(lookAt);
    }

    public static void lockEyes(PathfinderMob entity1, PathfinderMob entity2) {
        lookAt(entity1, entity2);
        lookAt(entity2, entity1);
    }

    public static void moveTo(PathfinderMob entity, Vec3 vec3, double speed) {
        if (vec3 != null && (!entity.isPathFinding() || !Objects.equals(entity.getNavigation().getTargetPos(), Utils.vecToBpos(vec3)))) {
            entity.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, speed);
        }
    }

    public static void moveTo(PathfinderMob entity, BlockPos blockPos, double speed) {
        if (blockPos != null && (!entity.isPathFinding() || !Objects.equals(entity.getNavigation().getTargetPos(), blockPos))) {
            entity.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), speed);
        }
    }

    public static void moveTo(PathfinderMob entity, Entity moveTo, double speed) {
        if (moveTo != null && (!entity.isPathFinding() || !Objects.equals(entity.getNavigation().getTargetPos(), moveTo.blockPosition()))) {
            entity.getNavigation().moveTo(moveTo, speed);
        }
    }

    public static void maybeMoveTo(PathfinderMob entity, Vec3 vec3, double speed) {
        if (vec3 != null && !entity.isPathFinding()) {
            entity.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, speed);
        }
    }

    public static void maybeMoveTo(PathfinderMob entity, BlockPos blockPos, double speed) {
        if (blockPos != null && !entity.isPathFinding()) {
            entity.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), speed);
        }
    }

    public static void maybeMoveTo(PathfinderMob entity, Entity moveTo, double speed) {
        if (moveTo != null && !entity.isPathFinding()) {
            entity.getNavigation().moveTo(moveTo, speed);
        }
    }

    public static void stopMoving(PathfinderMob mob) {
        mob.getNavigation().stop();
        if (mob instanceof AbstractPerson person) {
            ((PersonMoveControl)person.getMoveControl()).stopMoving();
        }
    }

    public static void lookAndMoveTo(PathfinderMob mob, LivingEntity moveTo, double speed) {
        lookAt(mob, moveTo);
        moveTo(mob, moveTo, speed);
    }

    public static void strafeAwayFrom(PathfinderMob mob, Entity target, float dist) {
        mob.getNavigation().stop();
        lookAndTurnTo(mob, target);
        mob.getMoveControl().strafe(-dist, 0);
    }

    public static void lookAndTurnTo(PathfinderMob mob, Entity target) {
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        mob.setYRot(Mth.rotateIfNecessary(mob.getYRot(), mob.getYHeadRot(), 0.0F));
    }

    public static BlockPos findBlock(PathfinderMob entity, Predicate<BlockState> predicate, BlockPos startPos, int range, int yRange) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for(int k = 0; k <= yRange; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < range; ++l) {
                for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        mutable.setWithOffset(startPos, i1, k - 1, j1);
                        BlockState blockState = entity.level.getBlockState(mutable);
                        if (predicate.test(blockState)) {
                            return mutable;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void changeBlock(PathfinderMob mob, BlockState blockState, BlockPos pos) {
        mob.level.setBlock(pos, blockState, 11);
        mob.level.gameEvent(mob, GameEvent.BLOCK_CHANGE, pos);
    }

    public static void placeBlock(PathfinderMob mob, BlockState blockToPlace, BlockPos pos) {
        mob.level.playSound(null, pos, blockToPlace.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        mob.level.setBlock(pos, blockToPlace, 11);
        mob.level.gameEvent(mob, GameEvent.BLOCK_PLACE, pos);
        ForgeEventFactory.onBlockPlace(mob, BlockSnapshot.create(mob.level.dimension(), mob.level, pos), Direction.UP);
    }

    public static void breakBlock(PathfinderMob mob, BlockPos blockToBreak, boolean drop) {
        mob.level.destroyBlock(blockToBreak, drop, mob);
        mob.level.gameEvent(mob, GameEvent.BLOCK_DESTROY, blockToBreak);
    }

    public static void bonemealBlock(PathfinderMob mob, BlockPos blockPos) {
        BlockState block = mob.level.getBlockState(blockPos);
        if (block.getBlock() instanceof BonemealableBlock bonemealableBlock && mob.level instanceof ServerLevel serverLevel) {
            if (bonemealableBlock.isBonemealSuccess(serverLevel, serverLevel.random, blockPos, block)) {
                bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, blockPos, block);
                serverLevel.levelEvent(1505, blockPos, 0);
            }
        }
    }

    public static List<AbstractPerson> findGroupOfPeople(AbstractPerson person, Predicate<AbstractPerson> predicate, double range) {
        return person.level.getEntitiesOfClass(AbstractPerson.class, person.getBoundingBox().inflate(range), predicate);
    }

    public static List<LivingEntity> findGroupOfEntities(AbstractPerson person, Predicate<LivingEntity> predicate, double range) {
        return person.level.getEntitiesOfClass(LivingEntity.class, person.getBoundingBox().inflate(range), predicate);
    }

    public static boolean meleeAttack(PathfinderMob mob, LivingEntity target, float range) {
        if (mob.distanceTo(target) <= range) {
            mob.swing(InteractionHand.MAIN_HAND);
            return mob.doHurtTarget(target);
        }
        return false;
    }

    public static boolean rangedAttack(AbstractPerson person, LivingEntity target) {
        if (person.hasLineOfSight(target)) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(person, (i) -> i instanceof BowItem);
            ItemStack stack = person.getItemInHand(hand);
            if (stack.getItem() instanceof BowItem) {
                if (person.isUsingItem()) {
                    int i = person.getTicksUsingItem();
                    if (i > 20) {
                        person.performRangedAttack(target, BowItem.getPowerForTime(i));
                        person.stopUsingItem();
                        person.setItemInHand(hand, stack);
                        return true;
                    }
                }
                else {
                    person.startUsingItem(hand);
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean giveFoodToPersonTick(AbstractPerson person1, LivingEntity person2, ItemStack food) {
        if (!person1.getMainHandItem().equals(food)) person1.setItemInHand(InteractionHand.MAIN_HAND, food);
        if (person1.distanceToSqr(person2) > 2.5D) {
            ActionUtils.lookAndMoveTo(person1, person2, 1.0D);
            return false;
        }
        else {
            if (person2 instanceof AbstractPerson abstractPerson) {
                ActionUtils.lockEyes(person1, abstractPerson);
                abstractPerson.getNavigation().stop();
            }

            person1.swing(InteractionHand.MAIN_HAND);
            person1.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            if (person2 instanceof AbstractPerson) {
                ((AbstractPerson) person2).getInventory().addItem(food);
                person2.swing(InteractionHand.MAIN_HAND);
            }
            else if (person2 instanceof Player player) {
                player.addItem(food);
                person1.sayTo(player, DialogueEvent.GIFTING_FOOD.getString());
            }
            return true;
        }
    }

}
