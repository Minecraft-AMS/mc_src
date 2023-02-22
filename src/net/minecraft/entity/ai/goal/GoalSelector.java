/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final PrioritizedGoal REPLACEABLE_GOAL = new PrioritizedGoal(Integer.MAX_VALUE, new Goal(){

        @Override
        public boolean canStart() {
            return false;
        }
    }){

        @Override
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Control, PrioritizedGoal> goalsByControl = new EnumMap<Goal.Control, PrioritizedGoal>(Goal.Control.class);
    private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
    private final Supplier<Profiler> profiler;
    private final EnumSet<Goal.Control> disabledControls = EnumSet.noneOf(Goal.Control.class);
    private int field_30212;
    private int timeInterval = 3;

    public GoalSelector(Supplier<Profiler> profiler) {
        this.profiler = profiler;
    }

    public void add(int priority, Goal goal) {
        this.goals.add(new PrioritizedGoal(priority, goal));
    }

    @VisibleForTesting
    public void clear() {
        this.goals.clear();
    }

    public void remove(Goal goal) {
        this.goals.stream().filter(prioritizedGoal -> prioritizedGoal.getGoal() == goal).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::stop);
        this.goals.removeIf(prioritizedGoal -> prioritizedGoal.getGoal() == goal);
    }

    private static boolean usesAny(PrioritizedGoal goal, EnumSet<Goal.Control> controls) {
        for (Goal.Control control : goal.getControls()) {
            if (!controls.contains((Object)control)) continue;
            return true;
        }
        return false;
    }

    private static boolean canReplaceAll(PrioritizedGoal goal, Map<Goal.Control, PrioritizedGoal> goalsByControl) {
        for (Goal.Control control : goal.getControls()) {
            if (goalsByControl.getOrDefault((Object)control, REPLACEABLE_GOAL).canBeReplacedBy(goal)) continue;
            return false;
        }
        return true;
    }

    public void tick() {
        Profiler profiler = this.profiler.get();
        profiler.push("goalCleanup");
        for (PrioritizedGoal prioritizedGoal : this.goals) {
            if (!prioritizedGoal.isRunning() || !GoalSelector.usesAny(prioritizedGoal, this.disabledControls) && prioritizedGoal.shouldContinue()) continue;
            prioritizedGoal.stop();
        }
        Iterator<Map.Entry<Goal.Control, PrioritizedGoal>> iterator = this.goalsByControl.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Goal.Control, PrioritizedGoal> entry = iterator.next();
            if (entry.getValue().isRunning()) continue;
            iterator.remove();
        }
        profiler.pop();
        profiler.push("goalUpdate");
        for (PrioritizedGoal prioritizedGoal : this.goals) {
            if (prioritizedGoal.isRunning() || GoalSelector.usesAny(prioritizedGoal, this.disabledControls) || !GoalSelector.canReplaceAll(prioritizedGoal, this.goalsByControl) || !prioritizedGoal.canStart()) continue;
            for (Goal.Control control : prioritizedGoal.getControls()) {
                PrioritizedGoal prioritizedGoal2 = this.goalsByControl.getOrDefault((Object)control, REPLACEABLE_GOAL);
                prioritizedGoal2.stop();
                this.goalsByControl.put(control, prioritizedGoal);
            }
            prioritizedGoal.start();
        }
        profiler.pop();
        this.tickGoals(true);
    }

    public void tickGoals(boolean tickAll) {
        Profiler profiler = this.profiler.get();
        profiler.push("goalTick");
        for (PrioritizedGoal prioritizedGoal : this.goals) {
            if (!prioritizedGoal.isRunning() || !tickAll && !prioritizedGoal.shouldRunEveryTick()) continue;
            prioritizedGoal.tick();
        }
        profiler.pop();
    }

    public Set<PrioritizedGoal> getGoals() {
        return this.goals;
    }

    public Stream<PrioritizedGoal> getRunningGoals() {
        return this.goals.stream().filter(PrioritizedGoal::isRunning);
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void disableControl(Goal.Control control) {
        this.disabledControls.add(control);
    }

    public void enableControl(Goal.Control control) {
        this.disabledControls.remove((Object)control);
    }

    public void setControlEnabled(Goal.Control control, boolean enabled) {
        if (enabled) {
            this.enableControl(control);
        } else {
            this.disableControl(control);
        }
    }
}

