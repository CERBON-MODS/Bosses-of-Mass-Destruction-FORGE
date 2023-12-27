package com.cerbon.bosses_of_mass_destruction.entity.ai;

import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.IValidDirection;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntity;
import com.cerbon.cerbons_api.api.static_utilities.RandomUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.world.phys.Vec3;

public class ValidatedTargetSelector implements ITargetSelector {
    private final IEntity entity;
    private final IValidDirection validator;
    private Vec3 previousDirection;

    public ValidatedTargetSelector(IEntity entity, IValidDirection validator) {
        this.entity = entity;
        this.validator = validator;
        this.previousDirection = RandomUtils.randVec();
    }

    @Override
    public Vec3 getTarget() {
        Vec3 pos = entity.getPos();
        for (int i = 5; i < 200; i += 20) {
            Vec3 newDirection = VecUtils.rotateVector(previousDirection, RandomUtils.randVec(), RandomUtils.randDouble() * i);

            if (validator.isValidDirection(newDirection.normalize())) {
                previousDirection = newDirection;
                return pos.add(newDirection);
            }
        }

        previousDirection = VecUtils.negateServer(previousDirection);
        return pos.add(previousDirection);
    }
}
