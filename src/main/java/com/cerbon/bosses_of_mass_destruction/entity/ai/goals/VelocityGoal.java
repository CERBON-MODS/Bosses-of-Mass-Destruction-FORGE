package com.cerbon.bosses_of_mass_destruction.entity.ai.goals;

import com.cerbon.bosses_of_mass_destruction.entity.ai.ISteering;
import com.cerbon.bosses_of_mass_destruction.entity.ai.ITargetSelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Consumer;

public class VelocityGoal extends Goal {
    private final Consumer<Vec3> onTargetSelected;
    private final ISteering steering;
    private final ITargetSelector targetSelector;

    public VelocityGoal(Consumer<Vec3> onTargetSelected, ISteering steering, ITargetSelector targetSelector) {
        this.onTargetSelected = onTargetSelected;
        this.steering = steering;
        this.targetSelector = targetSelector;
        EnumSet<Flag> controls = EnumSet.of(Flag.MOVE, Flag.LOOK);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        Vec3 target = targetSelector.getTarget();
        Vec3 velocity = steering.accelerateTo(target);
        onTargetSelected.accept(velocity);
        super.tick();
    }
}
