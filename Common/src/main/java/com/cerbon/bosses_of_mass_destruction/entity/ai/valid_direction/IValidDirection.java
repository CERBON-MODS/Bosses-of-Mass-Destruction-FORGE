package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import net.minecraft.world.phys.Vec3;

public interface IValidDirection {
    boolean isValidDirection(Vec3 normedDirection);
}
