package com.cerbon.bosses_of_mass_destruction.entity.ai;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.IRandom;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.IValidDirection;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntity;
import net.minecraft.util.math.vector.Vector3d;

public class ValidatedTargetSelector implements ITargetSelector {
    private final IEntity entity;
    private final IValidDirection validator;
    private final IRandom random;
    private Vector3d previousDirection;

    public ValidatedTargetSelector(IEntity entity, IValidDirection validator, IRandom random) {
        this.entity = entity;
        this.validator = validator;
        this.random = random;
        this.previousDirection = random.getVector();
    }

    @Override
    public Vector3d getTarget() {
        Vector3d pos = entity.getPos();
        for (int i = 5; i < 200; i += 20) {
            Vector3d newDirection = VecUtils.rotateVector(previousDirection,random.getVector(), random.getDouble() * i);

            if (validator.isValidDirection(newDirection.normalize())) {
                previousDirection = newDirection;
                return pos.add(newDirection);
            }
        }

        previousDirection = VecUtils.negateServer(previousDirection);
        return pos.add(previousDirection);
    }
}
