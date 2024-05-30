package com.cerbon.bosses_of_mass_destruction.entity.ai;

import net.minecraft.util.math.vector.Vector3d;

public interface ISteering {
    Vector3d accelerateTo(Vector3d target);
}
