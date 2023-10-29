package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class VecUtils {
    public static final Vec3 xAxis = new Vec3(1.0, 0.0, 0.0);
    public static final Vec3 yAxis = new Vec3(0.0, 1.0, 0.0);
    public static final Vec3 zAxis = new Vec3(0.0, 0.0, 1.0);
    public static final Vec3 unit = new Vec3(1.0, 1.0, 1.0);

    public static Vec3 yOffset(Vec3 vec, double i) {
        return vec.add(0.0, i, 0.0);
    }

    public static Vec3 planeProject(Vec3 vec, Vec3 planeVector) {
        return vec.subtract(planeVector.multiply(vec.dot(planeVector), vec.dot(planeVector), vec.dot(planeVector)));
    }

    public static Vec3 newVec3(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static Vec3 newVec3d() {
        return new Vec3(0.0, 0.0, 0.0);
    }

    public static Vec3 asVec3(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static Vec3 negateServer(Vec3 vec) {
        return vec.multiply(-1.0, -1.0, -1.0);
    }

    public static Vec3 coerceAtLeast(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.max(vec1.x(), vec2.x()), Math.max(vec1.y(), vec2.y()), Math.max(vec1.z(), vec2.z()));
    }

    public static Vec3 coerceAtMost(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.min(vec1.x(), vec2.x()), Math.min(vec1.y(), vec2.y()), Math.min(vec1.z(), vec2.z()));
    }

    public static double unsignedAngle(Vec3 vec, Vec3 b) {
        double dot = vec.dot(b);
        double lengths = vec.length() * b.length();

        if (lengths == 0.0)
            return 0.0;

        double cos = Math.max(-1.0, Math.min(1.0, dot / lengths));
        return Math.toDegrees(Math.acos(cos));
    }

    public static Vec3 rotateVector(Vec3 vec, Vec3 axis, double degrees) {
        double theta = Math.toRadians(degrees);
        Vec3 normedAxis = axis.normalize();
        double x = vec.x();
        double y = vec.y();
        double z = vec.z();
        double u = normedAxis.x();
        double v = normedAxis.y();
        double w = normedAxis.z();
        double xPrime = u * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + x * Math.cos(theta) + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + y * Math.cos(theta) + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + z * Math.cos(theta) + (-v * x + u * y) * Math.sin(theta);
        return new Vec3(xPrime, yPrime, zPrime);
    }
}
