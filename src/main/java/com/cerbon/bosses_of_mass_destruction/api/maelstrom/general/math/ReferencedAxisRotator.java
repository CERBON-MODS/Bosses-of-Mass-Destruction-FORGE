package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.math;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import net.minecraft.util.math.vector.Vector3d;

public class ReferencedAxisRotator {
    private final double angleBetween;
    private final Vector3d rotationAxis;

    public ReferencedAxisRotator(Vector3d originalAxis, Vector3d newAxis) {
        this.angleBetween = VecUtils.unsignedAngle(originalAxis, newAxis);
        this.rotationAxis = originalAxis.cross(newAxis);
    }

    public Vector3d rotate(Vector3d vec) {
        return VecUtils.rotateVector(vec, rotationAxis, angleBetween);
    }
}

