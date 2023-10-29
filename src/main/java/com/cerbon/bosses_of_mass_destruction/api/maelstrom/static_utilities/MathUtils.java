package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.math.ReferencedAxisRotator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
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

    // https://www.geeksforgeeks.org/bresenhams-algorithm-for-3-d-line-drawing/
    public static List<BlockPos> getBlocksInLine(BlockPos startPos, BlockPos endPos) {
        int x1 = startPos.getX();
        int y1 = startPos.getY();
        int z1 = startPos.getZ();
        int x2 = endPos.getX();
        int y2 = endPos.getY();
        int z2 = endPos.getZ();
        List<BlockPos> points = new ArrayList<>();
        points.add(startPos);
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int xs = (x2 > x1) ? 1 : -1;
        int ys = (y2 > y1) ? 1 : -1;
        int zs = (z2 > z1) ? 1 : -1;

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                points.add(new BlockPos(x1, y1, z1));
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                points.add(new BlockPos(x1, y1, z1));
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                points.add(new BlockPos(x1, y1, z1));
            }
        }
        return points;
    }

}
