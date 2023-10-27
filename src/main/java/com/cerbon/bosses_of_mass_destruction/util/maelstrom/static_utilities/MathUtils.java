package com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.general.math.ReferencedAxisRotator;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MathUtils {
    public static Vec3 lerpVec(float partialTicks, Vec3 vec1, Vec3 vec2) {
        double x = Mth.lerp(partialTicks, vec1.x, vec2.x);
        double y = Mth.lerp(partialTicks, vec1.y, vec2.y);
        double z = Mth.lerp(partialTicks, vec1.z, vec2.z);
        return new Vec3(x, y, z);
    }

    public static void circleCallback(double radius, int points, Vec3 axis, Consumer<Vec3> callback) {
        double degrees = Math.PI * 2 / points;
        double axisYaw = directionToYaw(axis);
        ReferencedAxisRotator rotator = new ReferencedAxisRotator(VecUtils.yAxis, axis);
        for (int i = 0; i < points; i++) {
            double radians = i * degrees;
            Vec3 offset = VecUtils.rotateVector(new Vec3(Math.sin(radians), 0.0, Math.cos(radians))
                    .multiply(radius, radius, radius), VecUtils.yAxis, -axisYaw);
            Vec3 rotated = rotator.rotate(offset);
            callback.accept(rotated);
        }
    }

    public static double directionToYaw(Vec3 direction) {
        double x = direction.x();
        double z = direction.z();

        return Math.toDegrees(Mth.atan2(z, x));
    }

    public static float roundedStep(float n, List<Float> steps, boolean floor) {
        if (floor) {
            steps.sort(Collections.reverseOrder());
            for (Float step : steps) {
                if (step <= n) {
                    return step;
                }
            }
            return steps.get(0);
        } else {
            Collections.sort(steps);
            for (Float step : steps) {
                if (step > n) {
                    return step;
                }
            }
            return steps.get(steps.size() - 1);
        }
    }


}
