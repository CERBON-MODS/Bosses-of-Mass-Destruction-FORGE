package com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities;

import net.minecraft.world.phys.Vec3;

public class VecUtils {
    public static final Vec3 xAxis = new Vec3(1.0, 0.0, 0.0);
    public static final Vec3 yAxis = new Vec3(0.0, 1.0, 0.0);
    public static final Vec3 zAxis = new Vec3(0.0, 0.0, 1.0);
    public static final Vec3 unit = new Vec3(1.0, 1.0, 1.0);

    public static Vec3 coerceAtLeast(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.max(vec1.x(), vec2.x()), Math.max(vec1.y(), vec2.y()), Math.max(vec1.z(), vec2.z()));
    }

    public static Vec3 coerceAtMost(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.min(vec1.x(), vec2.x()), Math.min(vec1.y(), vec2.y()), Math.min(vec1.z(), vec2.z()));
    }
}
