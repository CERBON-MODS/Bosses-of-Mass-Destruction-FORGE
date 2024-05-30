package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class ValidDirectionOr implements IValidDirection {
    private final List<IValidDirection> validators;

    public ValidDirectionOr(List<IValidDirection> validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValidDirection(Vector3d normedDirection) {
        return validators.stream().anyMatch(validator -> validator.isValidDirection(normedDirection));
    }
}

