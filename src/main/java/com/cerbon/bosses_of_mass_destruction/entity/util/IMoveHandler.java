package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.entity.MoverType;
import net.minecraft.util.math.vector.Vector3d;

public interface IMoveHandler {
    boolean canMove(MoverType type, Vector3d movement);
}
