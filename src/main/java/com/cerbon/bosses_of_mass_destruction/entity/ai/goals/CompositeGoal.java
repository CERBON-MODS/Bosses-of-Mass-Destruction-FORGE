package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import net.minecraft.entity.ai.goal.Goal;

import java.util.Arrays;
import java.util.List;

public class CompositeGoal extends Goal {
    private final List<Goal> goals;

    public CompositeGoal(Goal... goals) {
        this.goals = Arrays.asList(goals);
        for (Goal goal : goals)
            this.getFlags().addAll(goal.getFlags());
    }

    @Override
    public boolean canUse() {
        return goals.stream().allMatch(Goal::canUse);
    }

    @Override
    public boolean isInterruptable() {
        return goals.stream().allMatch(Goal::isInterruptable);
    }

//    @Override
//    public boolean requiresUpdateEveryTick() {
//        return true;
//    }

    @Override
    public void tick() {
        goals.forEach(Goal::tick);
    }

    @Override
    public void stop() {
        goals.forEach(Goal::stop);
    }

    @Override
    public void start() {
        goals.forEach(Goal::start);
    }
}

