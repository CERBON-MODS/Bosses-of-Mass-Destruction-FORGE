package com.cerbon.bosses_of_mass_destruction.entity.ai.action;

import com.cerbon.bosses_of_mass_destruction.entity.util.ProjectileThrower;
import net.minecraft.entity.CreatureEntity;

public class ThrowProjectileAction implements IAction {
    private final CreatureEntity entity;
    private final ProjectileThrower projectileThrower;

    public ThrowProjectileAction(CreatureEntity entity, ProjectileThrower projectileThrower) {
        this.entity = entity;
        this.projectileThrower = projectileThrower;
    }

    @Override
    public void perform() {
        if (entity.getTarget() != null)
            projectileThrower.throwProjectile(entity.getTarget().getBoundingBox().getCenter());
    }
}
