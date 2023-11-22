package com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.math.ReferencedAxisRotator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MathUtils {
    public static boolean withinDistance(Vec3 pos1, Vec3 pos2, double distance) {
        if (distance < 0) throw new IllegalArgumentException("Distance cannot be negative");
        return pos1.distanceToSqr(pos2) < Math.pow(distance, 2.0);
    }

    public static boolean movingTowards(Vec3 center, Vec3 pos, Vec3 direction) {
        Vec3 directionTo = unNormedDirection(pos, center);
        return direction.dot(directionTo) > 0;
    }

    public static Vec3 unNormedDirection(Vec3 source, Vec3 target) {
        return target.subtract(source);
    }

    /**
     * Calls a function that linearly interpolates between two points. Includes both ends of the line
     * <p>
     * Callback returns the position and the point number from 1 to points
     */
    public static void lineCallback(Vec3 start, Vec3 end, int points, java.util.function.BiConsumer<Vec3, Integer> callback) {
        Vec3 dir = end.subtract(start).scale(1.0 / (points - 1));
        Vec3 pos = start;
        for (int i = 0; i < points; i++) {
            callback.accept(pos, i);
            pos = pos.add(dir);
        }
    }

    public static void circleCallback(double radius, int points, Vec3 axis, Consumer<Vec3> callback) {
        double degrees = Math.PI * 2 / points;
        double axisYaw = directionToYaw(axis);
        ReferencedAxisRotator rotator = new ReferencedAxisRotator(VecUtils.yAxis, axis);
        for (int i = 0; i < points; i++) {
            double radians = i * degrees;
            Vec3 offset = VecUtils.rotateVector(new Vec3(Math.sin(radians), 0.0, Math.cos(radians)).scale(radius), VecUtils.yAxis, -axisYaw);
            Vec3 rotated = rotator.rotate(offset);
            callback.accept(rotated);
        }
    }

    public static Collection<Vec3> circlePoints(double radius, int points, Vec3 axis) {
        Collection<Vec3> vectors = new ArrayList<>();
        circleCallback(radius, points, axis, vectors::add);
        return vectors;
    }

    public static boolean willAABBFit(AABB aabb, Vec3 movement, Predicate<AABB> collision) {
        AtomicBoolean collided = new AtomicBoolean(false);
        int points = (int) Math.ceil(movement.length() / aabb.getSize());
        lineCallback(Vec3.ZERO, movement, points, (vec3, integer) -> {
            if (collision.test(aabb.move(vec3)))
                collided.set(true);
        });
        return !collided.get();
    }

    public static float directionToPitch(Vec3 direction) {
        double x = direction.x;
        double z = direction.z;
        double y = direction.y;

        double h = Math.sqrt(x * x + z * z);
        return (float) Math.toDegrees(-Mth.atan2(y, h));
    }

    public static double directionToYaw(Vec3 direction) {
        double x = direction.x();
        double z = direction.z();

        return Math.toDegrees(Mth.atan2(z, x));
    }

    public static Vec3 lerpVec(float partialTicks, Vec3 vec1, Vec3 vec2) {
        double x = Mth.lerp(partialTicks, vec1.x, vec2.x);
        double y = Mth.lerp(partialTicks, vec1.y, vec2.y);
        double z = Mth.lerp(partialTicks, vec1.z, vec2.z);
        return new Vec3(x, y, z);
    }

    public static Vec3 axisOffset(Vec3 direction, Vec3 offset) {
        Vec3 forward = direction.normalize();
        Vec3 side = forward.cross(VecUtils.yAxis).normalize();
        Vec3 up = side.cross(forward).normalize();
        return forward.scale(offset.x).add(side.scale(offset.z)).add(up.scale(offset.y));
    }

    public static boolean facingSameDirection(Vec3 direction1, Vec3 direction2) {
        return direction1.dot(direction2) > 0;
    }

    // https://www.wikihow.com/Add-Consecutive-Integers-from-1-to-100
    public static int consecutiveSum(int firstNumber, int lastNumber) {
        return ((lastNumber - firstNumber + 1) * (firstNumber + lastNumber) / 2);
    }

    public static float roundedStep(float n, List<Float> steps, boolean floor) {
        List<Float> sortableSteps = new ArrayList<>(steps);
        if (floor) {
            sortableSteps.sort(Collections.reverseOrder());
            for (Float step : sortableSteps) {
                if (step <= n)
                    return step;
            }
            return sortableSteps.get(0);
        } else {
            Collections.sort(sortableSteps);
            for (Float step : sortableSteps) {
                if (step > n)
                    return step;
            }
            return sortableSteps.get(sortableSteps.size() - 1);
        }
    }

    public static List<Vec3> buildBlockCircle(double radius) {
        int intRadius = (int) radius;
        double radiusSq = radius * radius;
        List<Vec3> points = new ArrayList<>();
        for (int x = -intRadius; x <= intRadius; x++) {
            for (int z = -intRadius; z <= intRadius; z++) {
                Vec3 pos = new Vec3(x, 0.0, z);
                if (pos.lengthSqr() <= radiusSq)
                    points.add(pos);
            }
        }
        return points;
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

    public static float ratioLerp(float time, float ratio, float maxAge, float partialTicks) {
        assert ratio <= 1;
        assert ratio >= 0;
        assert maxAge > 0;

        float currentTime = Mth.clamp((time + partialTicks) / maxAge, 0f, 1f);
        return Math.max(0f, currentTime - ratio) / (1 - ratio);
    }

}
