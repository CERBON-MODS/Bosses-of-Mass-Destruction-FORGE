package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.util.math.vector.Vector3d;

public interface IEntity {
    Vector3d getDeltaMovement();
    Vector3d getPos();
    Vector3d getEyePos();
    Vector3d getLookAngle();
    int getTickCount();
    boolean isAlive();
    IEntity target();
}

