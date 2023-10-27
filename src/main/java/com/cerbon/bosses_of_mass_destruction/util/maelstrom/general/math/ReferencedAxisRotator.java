package com.cerbon.bosses_of_mass_destruction.util.maelstrom.general.math;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.VecUtils;
import net.minecraft.world.phys.Vec3;

public class ReferencedAxisRotator {
    private final double angleBetween;
    private final Vec3 rotationAxis;

    public ReferencedAxisRotator(Vec3 originalAxis, Vec3 newAxis) {
        this.angleBetween = VecUtils.unsignedAngle(originalAxis, newAxis);
        this.rotationAxis = originalAxis.cross(newAxis);
    }

    public Vec3 rotate(Vec3 vec) {
        return VecUtils.rotateVector(vec, rotationAxis, angleBetween);
    }
}

