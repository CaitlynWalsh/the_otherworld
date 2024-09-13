package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class BabyPlayWithOthersAction extends Action {
    public enum State {
        JUMP_ON_BED,
        TAG,
        SNOWBALL_FIGHT
    }
    protected BlockPos bedPos;
    protected Projectile projectile;
    protected List<AbstractPerson> friends = new ArrayList<>();
    protected AbstractPerson friend;
    protected AbstractPerson chaser;
    protected AbstractPerson fleeFromChaser;
    protected State state;
    protected int cooldown = 0;
    protected int playCooldown = 0;
    protected Vec3 position;

    public BabyPlayWithOthersAction(AbstractPerson person) {
        super(person);
        this.setFlags(EnumSet.of(Flags.PLAYING, Flags.SOCIAL, Flags.JUMPING, Flags.MOVING, Flags.LOOKING));
    }

    @Override
    public boolean canStart() {
        if (person.isGhoul()) return false;
        if (this.playCooldown > 0) {
            this.playCooldown --;
            return false;
        }
        if (person.isPassenger()) return false;
        if (person.hasValidHomePos() && !person.isWithinRestriction()) return false;
        if (person.level.isDay() && person.isBaby() && person.getInvolvedWar() == null) {
            this.state = State.values()[person.getRandom().nextInt(State.values().length)];
            switch (state) {
                case JUMP_ON_BED -> {
                    if (person.isOni() || person.isHuman() || person.getRace() == IRaces.Race.ONIMAN) return false;
                    if (this.person.hasValidHomePos() && this.bedPos == null) {
                        this.bedPos = ActionUtils.findBlock(person, (state) -> state.getBlock() instanceof BedBlock, person.getHomePos(), 10, 2);
                    }
                    return bedPos != null;
                }
                case TAG -> {
                    this.friends = ActionUtils.findGroupOfPeople(person, isValidPlayTarget(this.person), 16.0D);
                    if (!this.friends.isEmpty()) {
                        this.friend = findFriend();
                        return this.friend != null;
                    }
                }
                case SNOWBALL_FIGHT -> {
                    this.projectile = chooseProjectile(this.person);
                    if (projectile != null) {
                        this.friends = ActionUtils.findGroupOfPeople(person, isValidPlayTarget(this.person), 16.0D);
                        if (!this.friends.isEmpty()) {
                            this.friend = findFriend();
                            return this.friend != null;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        if (!person.isBaby()) return false;
        if (this.playCooldown > 0) return false;
        if (!person.level.isDay()) return false;
        if (person.getInvolvedWar() != null) return false;
        switch (state) {
            case JUMP_ON_BED -> {
                return person.level.getBlockState(bedPos).getBlock() instanceof BedBlock;
            }
            case TAG -> {
                if (this.friend == null || !this.friend.isAlive()) return false;
                return isValidPlayTarget(this.person).test(this.friend);
            }
            case SNOWBALL_FIGHT -> {
                if (this.friend == null || !this.friend.isAlive()) return false;
                if (!isValidPlayTarget(this.person).test(this.friend)) return false;
                return projectile != null;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.person.getNavigation().stop();
        switch (state) {
            case JUMP_ON_BED -> {
            }
            case TAG, SNOWBALL_FIGHT -> {
                this.friend.getNavigation().stop();

                this.friend.getAi().startMindControl(this.person);

                this.chaser = this.person;
                this.fleeFromChaser = this.friend;
            }
        }

        super.start();
    }

    protected Projectile chooseProjectile(AbstractPerson thrower) {
        switch (thrower.getRace()) {
            case EMBERIAN -> {
                return new SmallFireball(thrower.level, thrower, 0.0D, 0.0D, 0.0D);
            }
            case ICEIAN -> {
                return new Snowball(thrower.level, thrower);
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (this.cooldown > 0) {
            --this.cooldown;
        }

        switch (state) {
            case JUMP_ON_BED -> {
                if (person.getBlockStateOn().getBlock() instanceof BedBlock) {
                    if (this.cooldown <= 0) {
                        person.getJumpControl().jump();
                        this.cooldown = this.person.getRandom().nextInt(20, 40);
                    }
                }
                else if (person.isOnGround()) {
                    ActionUtils.lookAt(person, bedPos);
                    ActionUtils.maybeMoveTo(person, bedPos, 1.0D);
                }
            }
            case TAG -> {
                this.person.getLookControl().setLookAt(this.friend);
                this.friend.getLookControl().setLookAt(this.person);

                if (cooldown <= 0) {
                    if (this.chaser.distanceTo(this.fleeFromChaser) > 2.0D) {
                        ActionUtils.maybeMoveTo(this.chaser, this.fleeFromChaser, 1.03D);
                        position = DefaultRandomPos.getPosAway(fleeFromChaser, 8, 6, chaser.position());
                        ActionUtils.maybeMoveTo(this.fleeFromChaser, position, 1.0D);
                    } else {
                        this.person.getNavigation().stop();
                        this.friend.getNavigation().stop();
                        this.chaser.swing(InteractionHand.MAIN_HAND);
                        AbstractPerson oldChaser = this.chaser;
                        this.chaser = this.fleeFromChaser;
                        this.fleeFromChaser = oldChaser;
                        position = DefaultRandomPos.getPosAway(fleeFromChaser, 8, 6, chaser.position());
                        ActionUtils.maybeMoveTo(this.fleeFromChaser, position, 1.0D);
                        this.cooldown = person.getRandom().nextInt(60, 80);
                    }
                }
            }
            case SNOWBALL_FIGHT -> {
                this.person.getLookControl().setLookAt(this.friend);
                this.friend.getLookControl().setLookAt(this.person);

                if (this.chaser.distanceTo(this.fleeFromChaser) > 2.0D) {
                    position = DefaultRandomPos.getPosAway(fleeFromChaser, 8, 6, chaser.position());
                    ActionUtils.maybeMoveTo(this.fleeFromChaser, position, 1.0D);
                    ActionUtils.moveTo(this.chaser, this.fleeFromChaser, 1.03D);
                }

                if (cooldown <= 0 && this.chaser.hasLineOfSight(this.fleeFromChaser)) {
                    if (this.projectile != null && person.level instanceof ServerLevel level) {
                        this.chaser.getNavigation().stop();
                        ActionUtils.lookAndTurnTo(this.chaser, this.fleeFromChaser);
                        this.chaser.swing(InteractionHand.MAIN_HAND);
                        this.projectile.setPos(this.chaser.getX() - (double)(this.chaser.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(chaser.yBodyRot * ((float)Math.PI / 180F)), chaser.getEyeY() - (double)0.1F, chaser.getZ() + (double)(chaser.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(chaser.yBodyRot * ((float)Math.PI / 180F)));
                        this.projectile.shoot(this.fleeFromChaser.getX() - chaser.getX(), fleeFromChaser.getY(0.3D) - projectile.getY(), this.fleeFromChaser.getZ() - chaser.getZ(), 1.0F, 1.0F);
                        level.addFreshEntity(this.projectile);

                        this.cooldown = person.getRandom().nextInt(80, 100);

                        AbstractPerson oldChaser = this.chaser;
                        this.chaser = this.fleeFromChaser;
                        this.fleeFromChaser = oldChaser;

                        this.projectile = this.chooseProjectile(this.chaser);

                        position = DefaultRandomPos.getPosAway(fleeFromChaser, 8, 6, chaser.position());
                        ActionUtils.maybeMoveTo(this.fleeFromChaser, position, 1.0D);
                    }
                }
            }
        }

        if (person.getRandom().nextFloat() < 0.001) {
            this.playCooldown = 600;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.P5;
    }

    @Override
    public boolean stopLowerPriorities() {
        return state != State.JUMP_ON_BED;
    }

    @Override
    public EnumSet<Flags> disabledFlags() {
        return EnumSet.of(Flags.FOLLOW);
    }

    @Override
    public void stop() {
        switch (state) {
            case JUMP_ON_BED -> {
                if (this.bedPos != null && !(this.person.level.getBlockState(bedPos).getBlock() instanceof BedBlock)) {
                    this.bedPos = null;
                }
            }
            case TAG, SNOWBALL_FIGHT -> {
                this.person.getNavigation().stop();
                this.person.getAi().stopMindControl();
                if (this.friend != null) this.friend.getAi().stopMindControl();
                this.friends.forEach((f) -> f.getAi().stopMindControl());
                this.friends.clear();
                this.friend = null;
            }
        }
        this.projectile = null;
        super.stop();
    }

    public static Predicate<AbstractPerson> isValidPlayTarget(AbstractPerson person) {
        return (person1) -> !person1.is(person) && !person1.isPassenger() && person1.distanceToSqr(person) < 35.0D && !person1.isGhoul() && person1.getAi() != null && (!person1.getAi().isMindControlled() || person1.getAi().getMindController().is(person)) && !person1.getAi().isMindControlling() && person1.isBaby() && !person1.isSleeping() && person1.isAlive() && person1.getInvolvedWar() == null;
    }

    private AbstractPerson findFriend() {
        if (this.friends.isEmpty()) return null;
        AbstractPerson f = this.friends.get(person.getRandom().nextInt(Integer.min(4, friends.size())));
        if (isValidPlayTarget(person).test(f)) {
            return f;
        }
        return null;
    }
}
