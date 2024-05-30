package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class VecUtils {
    public static final Vector3d xAxis = new Vector3d(1.0, 0.0, 0.0);
    public static final Vector3d yAxis = new Vector3d(0.0, 1.0, 0.0);
    public static final Vector3d zAxis = new Vector3d(0.0, 0.0, 1.0);
    public static final Vector3d unit = new Vector3d(1.0, 1.0, 1.0);

    public static Vector3d yOffset(Vector3d vec, double i) {
        return vec.add(0.0, i, 0.0);
    }

    public static Vector3d planeProject(Vector3d vec, Vector3d planeVector) {
        return vec.subtract(planeVector.multiply(vec.dot(planeVector), vec.dot(planeVector), vec.dot(planeVector)));
    }

    public static Vector3d asVec3(BlockPos blockPos) {
        return new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vector3d negateServer(Vector3d vec) {
        return vec.multiply(-1.0, -1.0, -1.0);
    }

    public static Vector3d coerceAtLeast(Vector3d vec1, Vector3d vec2) {
        return new Vector3d(Math.max(vec1.x(), vec2.x()), Math.max(vec1.y(), vec2.y()), Math.max(vec1.z(), vec2.z()));
    }

    public static Vector3d coerceAtMost(Vector3d vec1, Vector3d vec2) {
        return new Vector3d(Math.min(vec1.x(), vec2.x()), Math.min(vec1.y(), vec2.y()), Math.min(vec1.z(), vec2.z()));
    }

    public static double unsignedAngle(Vector3d vec, Vector3d b) {
        double dot = vec.dot(b);
        double lengths = vec.length() * b.length();

        if (lengths == 0.0)
            return 0.0;

        double cos = Math.max(-1.0, Math.min(1.0, dot / lengths));
        return Math.toDegrees(Math.acos(cos));
    }

    public static Vector3d rotateVector(Vector3d vec, Vector3d axis, double degrees) {
        double theta = Math.toRadians(degrees);
        Vector3d normedAxis = axis.normalize();
        double x = vec.x();
        double y = vec.y();
        double z = vec.z();
        double u = normedAxis.x();
        double v = normedAxis.y();
        double w = normedAxis.z();
        double v1 = u * x + v * y + w * z;
        double xPrime = u * v1 * (1.0 - Math.cos(theta)) + x * Math.cos(theta) + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * v1 * (1.0 - Math.cos(theta)) + y * Math.cos(theta) + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * v1 * (1.0 - Math.cos(theta)) + z * Math.cos(theta) + (-v * x + u * y) * Math.sin(theta);
        return new Vector3d(xPrime, yPrime, zPrime);
    }
}
