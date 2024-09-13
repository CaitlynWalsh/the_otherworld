package net.kitcaitie.otherworld.common.entity.npcs.ai.brain;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.ai.actions.*;
import net.kitcaitie.otherworld.registry.OtherworldMobEffects;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;
import java.util.function.Supplier;

public abstract class AIBrain {

    public enum AIStates {
        CORE,
        FORCED,
        WAR
    }

    protected final AbstractPerson person;
    protected final Supplier<ProfilerFiller> profiler;
    public Map<AIStates, List<Action>> actionMap = new HashMap<>();

    public List<Action> coreActions;

    public AIStates currentState = AIStates.CORE;
    public AIStates lastState;

    public EnumSet<Action.Flags> blacklisted = EnumSet.noneOf(Action.Flags.class);
    boolean sleeping;
    boolean trading;
    boolean freeze;
    boolean mindControlling;
    AbstractPerson mindController;
    boolean mindControlDisabled;
    private List<Action> runningActions = new ArrayList<>();

    public AIBrain(AbstractPerson person, Supplier<ProfilerFiller> profiler) {
        this.person = person;
        this.profiler = profiler;
        this.coreActions = List.of(
                new EatAction(person),
                new PickUpItemAction(person),
                new CraftingAction(person),
                new FloatInWaterAction(person),
                new DoorInteractAction(person)
        );
    }

    public void initAI() {
        initActionMap();
        postInit();
    }

    public void tickAI() {
        ProfilerFiller profilerFiller = profiler.get();
        profilerFiller.push("AiBrainTick");
        tick();
        profilerFiller.pop();
    }

    public void switchStates(AIStates newState) {
        if (currentState != newState) {
            this.lastState = currentState;
            this.currentState = newState;
            runningActions = new ArrayList<>();
        }
    }

    protected abstract void initActionMap();

    protected void postInit() {
    }

    protected void tick() {
        ProfilerFiller profilerFiller = profiler.get();

        if (this.shouldFreeze()) {
            if (!freeze) {
                this.stopAllActions();
                this.freeze = true;
            }
            return;
        }
        this.freeze = false;

        if (lastState != null && lastState != currentState) {
            List<Action> lastActions = actionMap.get(lastState);
            profilerFiller.push("AiBrainLastStateCleanup");
            for (Action action : lastActions) {
                if (action.isStarted()) {
                    action.stop();
                }
            }
            profilerFiller.pop();
        }

        Action.Priority priority = null;
        EnumSet<Action.Flags> disabled = EnumSet.noneOf(Action.Flags.class);

        profilerFiller.push("AiBrainCoreUpdate");
        actionUpdate(coreActions, disabled, priority);
        profilerFiller.pop();

        if (this.isMindControlled() && !isMindControlDisabled()) return;

        if (runningActions.isEmpty()) {
           runningActions = actionMap.get(currentState).stream().sorted(Comparator.comparing((Action::getPriority))).toList();
        }

        profilerFiller.push("AiBrainStateUpdate");
        actionUpdate(runningActions, disabled, priority);
        profilerFiller.pop();

    }

    private boolean shouldFreeze() {
        return person.isPassenger() || person.isSleeping() || person.hasEffect(OtherworldMobEffects.UNCONSCIOUS.get());
    }

    protected void actionUpdate(List<Action> actions, EnumSet<Action.Flags> disabled, Action.Priority priority) {
        ProfilerFiller profilerFiller = profiler.get();
        if (actions != null && !actions.isEmpty()) {
            profilerFiller.push("AiBrainActionStart");
            for (Action action : actions) {
                if (!action.isStarted() && !isActionDisabled(action, disabled, priority) && action.canStart()) {
                    boolean checkFlag = priority == null || priority.ordinal() > action.getPriority().ordinal();
                    action.start();
                    if (action.stopLowerPriorities() && checkFlag) {
                        priority = action.getPriority();
                    }
                    if (!action.disabledFlags().isEmpty()) {
                        disabled.addAll(action.disabledFlags());
                    }
                }
            }

            profilerFiller.push("AiBrainActionTick");
            for (Action action : actions) {
                if (action.isStarted() && !isActionDisabled(action, disabled, priority) && action.canContinue()) {
                    action.tick();
                }
            }
            profilerFiller.pop();

            profilerFiller.pop();
            profilerFiller.push("AiBrainActionCleanup");
            for (Action action : actions) {
                if (action.isStarted() && (isActionDisabled(action, disabled, priority) || !action.canContinue())) {
                    disabled.removeAll(action.disabledFlags());
                    action.stop();
                }
            }
            profilerFiller.pop();
        }
    }

    protected boolean actionContainsAnyBlacklistedFlags(Action action) {
        if (blacklisted.isEmpty()) return false;
        for (Action.Flags flags : action.getFlags()) {
            if (blacklisted.contains(flags)) return true;
        }
        return false;
    }

    public final void stopAllActions() {
        for (AIStates states : actionMap.keySet()) {
            actionMap.get(states).stream().filter(Action::isStarted).forEach(Action::stop);
        }
    }

    private boolean isActionDisabled(Action action, EnumSet<Action.Flags> flags, Action.Priority priority) {
        if (actionContainsAnyBlacklistedFlags(action)) return true;
        if (flags != null && !flags.isEmpty()) {
            for (Action.Flags f : action.getFlags()) {
                if (flags.contains(f)) {
                    return true;
                }
            }
        }
        if (priority == null) return false;
        return action.getPriority().ordinal() > priority.ordinal();
    }

    public void startMindControl(AbstractPerson person) {
        if (person.getAi() != null) {
            this.enableMindControl();
            this.stopAllActions();
            this.mindController = person;
            person.getAi().mindControlling = true;
            person.getAi().enableMindControl();
            person.getNavigation().stop();
        }
    }

    public void stopMindControl() {
        this.mindController = null;
        this.mindControlling = false;
        this.disableMindControl();
    }

    public AbstractPerson getMindController() {
        return this.mindController;
    }

    public boolean isMindControlled() {
        return this.mindController != null;
    }

    public boolean isMindControlling() {
        return this.mindControlling;
    }

    public void disableMindControl() {
        this.mindControlDisabled = true;
    }

    public void enableMindControl() {
        this.mindControlDisabled = false;
    }

    public boolean isMindControlDisabled() {
        return this.mindControlDisabled;
    }

}
