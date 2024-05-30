package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import com.cerbon.bosses_of_mass_destruction.entity.ai.ISteering;
import com.cerbon.bosses_of_mass_destruction.entity.ai.ITargetSelector;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.function.Consumer;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class VelocityGoal extends Goal {
    private final Consumer<Vector3d> onTargetSelected;
    private final ISteering steering;
    private final ITargetSelector targetSelector;

    public VelocityGoal(Consumer<Vector3d> onTargetSelected, ISteering steering, ITargetSelector targetSelector) {
        this.onTargetSelected = onTargetSelected;
        this.steering = steering;
        this.targetSelector = targetSelector;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        Vector3d target = targetSelector.getTarget();
        Vector3d velocity = steering.accelerateTo(target);
        onTargetSelected.accept(velocity);
        super.tick();
    }
}
