package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.phys.Vec3;

public interface IEntity {
    Vec3 getVel();
    Vec3 getPos();
    Vec3 getEyePos();
    Vec3 getRotationVector();
    int getAge();
    boolean isAlive();
    IEntity target();
}

