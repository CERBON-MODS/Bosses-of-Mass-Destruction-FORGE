package com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtils {
    public static Vec3 lerpVec(float partialTicks, Vec3 vec1, Vec3 vec2) {
        double x = Mth.lerp(partialTicks, vec1.x, vec2.x);
        double y = Mth.lerp(partialTicks, vec1.y, vec2.y);
        double z = Mth.lerp(partialTicks, vec1.z, vec2.z);
        return new Vec3(x, y, z);
    }

}
