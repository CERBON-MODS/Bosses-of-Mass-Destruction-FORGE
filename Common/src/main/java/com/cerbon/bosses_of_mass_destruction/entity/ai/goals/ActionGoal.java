package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IAction;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionStop;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Supplier;

public class ActionGoal extends Goal {
    private final Supplier<Boolean> hasTarget;
    private final Supplier<Boolean> canContinue;
    private IAction tickAction = () -> {};
    private IAction startAction = () -> {};
    private IActionStop endAction = () -> {};

    public ActionGoal(Supplier<Boolean> hasTarget, Supplier<Boolean> canContinue, IAction tickAction, IAction startAction, IActionStop endAction) {
        this.hasTarget = hasTarget;
        this.canContinue = canContinue != null ? canContinue : hasTarget;
        this.tickAction = tickAction != null ? tickAction : this.tickAction;
        this.startAction = startAction != null ? startAction : this.startAction;
        this.endAction = endAction != null ? endAction : this.endAction;
    }

    @Override
    public boolean canUse() {
        return hasTarget.get();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canContinue.get();
    }

    @Override
    public void start() {
        startAction.perform();
    }

    @Override
    public void tick() {
        tickAction.perform();
    }

    @Override
    public void stop() {
        endAction.stop();
    }
}
