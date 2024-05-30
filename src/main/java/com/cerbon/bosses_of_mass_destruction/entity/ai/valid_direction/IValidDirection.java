package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import net.minecraft.util.math.vector.Vector3d;

public interface IValidDirection {
    boolean isValidDirection(Vector3d normedDirection);
}
