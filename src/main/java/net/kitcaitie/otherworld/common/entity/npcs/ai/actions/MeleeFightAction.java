package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.entity.AggressiveAnimal;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.MonsterTargetable;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.event.CoreEvents;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public abstract class MeleeFightAction extends Action {
    protected static final Predicate<LivingEntity> MONSTERS = (mob) -> (mob instanceof Mob && ((mob instanceof Creeper creeper && creeper.getSwellDir() <= 0) || ((mob instanceof Enemy || mob instanceof AggressiveAnimal) && !CoreEvents.NO_ADDED_AGGRO_TO_HUMANS.contains(mob.getType()))));
    protected static final Predicate<LivingEntity> FLEE_FROM = (mob) -> (mob instanceof Mob && mob instanceof Enemy && ((mob instanceof Creeper creeper && creeper.getSwellDir() >= 1))) || (CoreEvents.NO_ADDED_AGGRO_TO_HUMANS.contains(mob.getType()));
    private final Predicate<LivingEntity> SHOULD_APPLY_SEDATIVE;
    protected final Predicate<LivingEntity> SHOULD_ATTACK;
    protected final Predicate<LivingEntity> SHOULD_ARREST;
    protected final TargetingConditions flee_targeting;
    protected final TargetingConditions soldier_targeting;
    protected final Predicate<LivingEntity> targeting;

    protected LivingEntity target;
    protected Mob fleeFrom;
    protected Vec3 fleeTo;
    protected int ticksUntilNextAttack;
    protected final int attackInterval = 18;
    protected boolean fleeing;

    public MeleeFightAction(AbstractPerson person) {
       super(person);
       this.SHOULD_ATTACK = (entity) -> {
            if (person.isSoldier()) {
                Story story = Otherworld.getStoryline((ServerLevel) person.level).getStory();
                if (entity instanceof ServerPlayer player) {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                    if (story.areRacesAtWar(character.getRace(), person.getRace())) {
                        return !character.isWantedCriminal() && (!character.isSpy() || character.isCaughtSpying());
                    }
                    else if (character.getRace() == person.getRace() || story.areRacesAllied(character.getRace(), person.getRace())) {
                        return character.isCriminal() && (character.isImprisoned() || !character.canBeReleasedFromJail());
                    }
                }
                else if (entity instanceof AbstractPerson person1) {
                    if (story.areRacesAtWar(person1.getRace(), person.getRace())) {
                        return person.isOni() || person.isEmberian() || person1.isWarrior();
                    }
                    else if (person1.getRace() == person.getRace() || story.areRacesAllied(person1.getRace(), person.getRace())) {
                        return person1.isCriminal();
                    }
                }
            }
            return false;
       };
       this.SHOULD_ARREST = (entity) -> {
           if (entity instanceof ServerPlayer player) {
               if (OtherworldConfigs.SERVER.allowSoldierArrests.get()) {
                   PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                   Story story = Otherworld.getStoryline(player.getLevel()).getStory();
                   if (story.areRacesAtWar(character.getRace(), person.getRace())) {
                       return OtherworldConfigs.SERVER.allowWarPrisonerArrests.get();
                   }
                   return true;
               }
           }
           return false;
       };
       this.SHOULD_APPLY_SEDATIVE = (this.SHOULD_ARREST.and(this.SHOULD_ATTACK).and((mob) -> {
           if (mob instanceof ServerPlayer player) {
               if (!person.isMeleeWeapon(person.getMainHandItem())) return false;
               if (!person.isSoldier()) return false;
               if (person.getInvolvedWar() != null) return false;
               return !player.hasEffect(OtherworldMobEffects.CAPTURED.get());
           }
           return false;
       }));
       this.flee_targeting = TargetingConditions.forNonCombat().range(8.0D).selector(FLEE_FROM.and((mob) -> {
           if (mob instanceof AggressiveAnimal) {
               return !person.isSoldier() && !person.isHuman();
           }
           return person instanceof MonsterTargetable;
       }));
       this.targeting = MONSTERS.and(((e) -> (e instanceof AggressiveAnimal && (person.isSoldier() || person.isHuman())) || (this.person instanceof MonsterTargetable trg && trg.canTarget((Mob) e))));
       this.soldier_targeting = TargetingConditions.forCombat().range(16.0D).selector(this::shouldSoldierAttack);
       this.setFlags(EnumSet.of(Flags.ATTACKING, Flags.MOVING_COMBAT, Flags.LOOKING_COMBAT));
    }

    @Override
    public boolean canStart() {
        return !person.isBlind() && !person.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get());
    }

    @Override
    public boolean canContinue() {
        return !person.isBlind() && !person.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get());
    }

    @Override
    public void start() {
        super.start();
        person.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (ticksUntilNextAttack > 0) --ticksUntilNextAttack;
        if (this.fleeFrom != null && !fleeFrom.isRemoved() && this.fleeing) {
            if (!flee_targeting.test(person, fleeFrom)) {
                this.fleeFrom = null;
                this.fleeTo = null;
                this.fleeing = false;
                ActionUtils.stopMoving(person);
                return;
            }
            if (this.fleeFrom instanceof Creeper creeper) {
                this.doMeleeAttack(creeper);
            }
            if (this.fleeFrom != null && !fleeFrom.isRemoved() && !this.fleeFrom.isDeadOrDying()) {
                if (fleeTo == null) {
                    fleeTo = DefaultRandomPos.getPosAway(this.person, 8, 6, this.fleeFrom.position());
                    ActionUtils.moveTo(person, fleeTo, 1.2D);
                }
                if (person.getNavigation().isDone()) fleeTo = null;
                return;
            }
            this.fleeFrom = null;
            this.fleeTo = null;
            this.fleeing = false;
            ActionUtils.stopMoving(person);
            return;
        }
        List<Mob> list = this.person.level.getEntitiesOfClass(Mob.class, person.getBoundingBox().inflate(12.0D), (e) -> flee_targeting.test(person, e));
        if (!list.isEmpty()) {
            this.fleeFrom = list.get(0);
            this.fleeTo = null;
            this.fleeing = true;
            return;
        }
        this.fleeFrom = null;
        this.fleeTo = null;
        this.fleeing = false;
        if (this.target != null) {
            if (!canAttack(false).test(target)) {
                this.target = null;
                person.setTarget(null);
                return;
            }
            else if (person.isSoldier() && !shouldSoldierAttack(target)) {
                this.target = null;
                person.setTarget(null);
                return;
            }
            if (person.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                person.setItemInHand(InteractionHand.MAIN_HAND, person.getMeleeWeaponInInventory());
                return;
            }
        }
        else {
            LivingEntity entity = this.person.getLastHurtByMob();
            if (entity != null && canAttack(true).test(entity)) {
                this.target = entity;
                return;
            }
            else {
                entity = this.person.getTarget();
                if (entity != null && canAttack(true).test(entity)) {
                    this.target = this.person.getTarget();
                    return;
                }
            }

            list = this.person.level.getEntitiesOfClass(Mob.class, person.getBoundingBox().inflate(8.0D), targeting);
            if (!list.isEmpty()) this.target = list.get(0);
            else if (person.isSoldier() || person.isHuman()) {
                List<LivingEntity> list1 = ActionUtils.findGroupOfEntities(person, this::shouldSoldierAttack, 16.0D);
                if (!list1.isEmpty()) this.target = list1.get(person.getRandom().nextInt(list1.size()));
            }
            return;
        }
        super.tick();
    }

    protected boolean shouldSoldierAttack(LivingEntity entity) {
        return targeting.test(entity) || SHOULD_ATTACK.test(entity) || SHOULD_APPLY_SEDATIVE.test(entity);
    }

    public Predicate<LivingEntity> canAttack(boolean ignoreSight) {
        return (target) -> {
            if (target != null) {
                if (!target.isAlive() || target.isRemoved() || target.isDeadOrDying()) return false;
                if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) return false;
                if (person.passiveTargets.contains(target)) return false;
                if (this.person.isAlliedTo(target)) return false;
                if (!this.person.canAttack(target)) return false;
                if (target.hasEffect(OtherworldMobEffects.CAPTURED.get())) return false;
                Team team = this.person.getTeam();
                Team team1 = target.getTeam();
                if (team != null && team == team1) return false;
                else {
                    double d0 = person.getVisibilityPercent(target);
                    double d1 = Math.max(person.getAttributeValue(Attributes.FOLLOW_RANGE) * d0, 2.0D);
                    double d2 = target.distanceToSqr(person.getX(), person.getY(), person.getZ());
                    if (d2 > d1 * d1) {
                        return false;
                    }

                    if (!ignoreSight) return this.person.hasLineOfSight(target);

                    return true;
                }
            }
            return false;
        };
    }

    @Override
    public void stop() {
        super.stop();
        this.target = null;
        person.setTarget(null);
        this.fleeing = false;
        this.fleeTo = null;
        this.fleeFrom = null;
        ActionUtils.stopMoving(person);
    }

    public void doMeleeAttack(LivingEntity target) {
        if (person.distanceTo(target) > 2.15F) return;
        if (this.ticksUntilNextAttack <= 0) {
            if (!target.isBlocking() && target.getHealth() <= 10.0D && SHOULD_APPLY_SEDATIVE.test(target)) {
                ActionUtils.stopMoving(person);
                target.addEffect(new MobEffectInstance(OtherworldMobEffects.CAPTURED.get(), 300, 2));
                if (target.level instanceof ServerLevel) {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter((Player) target);
                    character.setPrisoner((ServerPlayer) target, (ServerLevel) target.level, false, false);
                    character.sendPacket((Player) target);
                }
            }
            else if (ActionUtils.meleeAttack(this.person, target, 2.15F)) {
                ActionUtils.stopMoving(person);
            }
            this.ticksUntilNextAttack = attackInterval;
        }
    }

}
