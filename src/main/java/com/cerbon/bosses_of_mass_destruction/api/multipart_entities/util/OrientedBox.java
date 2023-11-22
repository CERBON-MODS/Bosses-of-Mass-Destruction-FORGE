package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class OrientedBox {
    private final Vec3 center;
    private final Vec3 halfExtents;
    private final QuaternionD rotation;
    private AABB extents;
    private Matrix3d matrix;
    private Matrix3d inverse;
    private Vec3[] vertices;
    private Vec3[] basis;

    public OrientedBox(final AABB box) {
        center = box.getCenter();
        halfExtents = new Vec3(box.getXsize() / 2, box.getYsize() / 2, box.getZsize() / 2);
        rotation = QuaternionD.IDENTITY;
    }

    public OrientedBox(final Vec3 center, final Vec3 halfExtents, final QuaternionD rotation) {
        this.center = center;
        this.halfExtents = halfExtents;
        this.rotation = rotation;
    }

    public OrientedBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final QuaternionD rotation) {
        center = new Vec3((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
        halfExtents = new Vec3((maxX - minX) / 2, (maxY - minY) / 2, (maxZ - minZ) / 2);
        this.rotation = rotation;
    }

    private OrientedBox(final Vec3 center, final Vec3 halfExtents, final QuaternionD rotation, final Matrix3d matrix, final Matrix3d inverse, final Vec3[] basis) {
        this.center = center;
        this.halfExtents = halfExtents;
        this.rotation = rotation;
        this.matrix = matrix;
        this.inverse = inverse;
        this.basis = basis;
    }

    public Matrix3d getMatrix() {
        if (matrix == null) {
            matrix = new Matrix3d(rotation);
        }
        return matrix;
    }

    public Matrix3d getInverse() {
        if (inverse == null) {
            inverse = getMatrix().invert();
        }
        return inverse;
    }

    public AABB getExtents() {
        if (extents == null) {
            extents = new AABB(halfExtents.multiply(-1, -1, -1), halfExtents);
        }
        return extents;
    }

    public Vec3[] getBasis() {
        if (basis == null) {
            basis = matrix.getBasis();
        }
        return basis;
    }

    public OrientedBox rotate(final QuaternionD quaternion) {
        if (QuaternionD.IDENTITY.equals(quaternion)) {
            return this;
        }
        return new OrientedBox(center, halfExtents, rotation.hamiltonProduct(quaternion));
    }

    public OrientedBox translate(final double x, final double y, final double z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        final Matrix3d matrix = getMatrix();
        final double transX = matrix.transformX(x, y, z);
        final double transY = matrix.transformY(x, y, z);
        final double transZ = matrix.transformZ(x, y, z);
        return new OrientedBox(center.add(transX, transY, transZ), halfExtents, rotation, matrix, inverse, basis);
    }

    public OrientedBox transform(final double x, final double y, final double z, final double pivotX, final double pivotY, final double pivotZ, final QuaternionD quaternion) {
        final Vec3 vec = getMatrix().transform(x - pivotX, y - pivotY, z - pivotZ);
        final boolean bl = quaternion.equals(QuaternionD.IDENTITY);
        return new OrientedBox(center.add(vec), halfExtents, rotation.hamiltonProduct(quaternion), bl ? matrix : null, bl ? inverse : null, bl ? basis : null).translate(pivotX, pivotY, pivotZ);
    }

    public QuaternionD getRotation() {
        return rotation;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Vec3 getHalfExtents() {
        return halfExtents;
    }

    public OrientedBox offset(final double x, final double y, final double z) {
        return new OrientedBox(center.add(x, y, z), halfExtents, rotation, matrix, inverse, basis);
    }

    private void computeVertices() {
        final AABB box = getExtents();
        final Vec3[] vertices = getVertices(box);
        this.vertices = new Vec3[8];
        final Matrix3d matrix = getMatrix();
        for (int i = 0; i < vertices.length; i++) {
            this.vertices[i] = matrix.transform(vertices[i]).add(center);
        }
    }

    public static Vec3[] getVertices(final AABB box) {
        final Vec3[] vertices = new Vec3[8];
        int index = 0;
        final Direction.AxisDirection[] axisDirections = Direction.AxisDirection.values();
        for (final Direction.AxisDirection x : axisDirections) {
            for (final Direction.AxisDirection y : axisDirections) {
                for (final Direction.AxisDirection z : axisDirections) {
                    vertices[index++] = new Vec3(getPoint(box, x, Direction.Axis.X), getPoint(box, y, Direction.Axis.Y), getPoint(box, z, Direction.Axis.Z));
                }
            }
        }
        return vertices;
    }

    private static double getPoint(final AABB box, final Direction.AxisDirection direction, final Direction.Axis axis) {
        return direction == Direction.AxisDirection.NEGATIVE ? box.min(axis) : box.max(axis);
    }

    public boolean intersects(final AABB other) {
        return intersects(getVertices(other));
    }

    public boolean intersects(final Vec3[] otherVertices) {
        if (vertices == null)
            computeVertices();

        final Vec3[] vertices1 = vertices;
        final Vec3[] normals1 = getBasis();
        for (final Vec3 normal : normals1) {
            if (!sat(normal, vertices1, otherVertices))
                return false;
        }
        final Vec3[] normals2 = Matrix3d.IDENTITY_BASIS;
        for (final Vec3 normal : normals2) {
            if (!sat(normal, vertices1, otherVertices))
                return false;
        }
        for (int i = 0; i < normals1.length; i++) {
            for (int j = i; j < normals2.length; j++) {
                final Vec3 normal = cross(normals1[i], normals2[j]);
                if (!sat(normal, vertices1, otherVertices))
                    return false;
            }
        }
        return true;
    }


    private static Vec3 cross(final Vec3 first, final Vec3 second) {
        return new Vec3(first.y * second.z - first.z * second.y, first.z * second.x - first.x * second.z, first.x * second.y - first.y * second.x);
    }

    private static boolean sat(final Vec3 normal, final Vec3[] vertices1, final Vec3[] vertices2) {
        double min1 = Double.MAX_VALUE;
        double max1 = -Double.MAX_VALUE;
        for (final Vec3 d : vertices1) {
            final double v = d.dot(normal);
            min1 = Math.min(min1, v);
            max1 = Math.max(max1, v);
        }
        double min2 = Double.MAX_VALUE;
        double max2 = -Double.MAX_VALUE;
        for (final Vec3 vec3d : vertices2) {
            final double v = vec3d.dot(normal);
            min2 = Math.min(min2, v);
            max2 = Math.max(max2, v);
        }
        return min1 <= min2 && min2 <= max1 || min2 <= min1 && min1 <= max2;
    }

    public double raycast(final Vec3 start, final Vec3 end) {
        final Matrix3d inverse = getInverse();
        final Vec3 d = inverse.transform(start.x - center.x, start.y - center.y, start.z - center.z);
        final Vec3 e = inverse.transform(end.x - center.x, end.y - center.y, end.z - center.z);
        return raycast0(d, e);
    }

    private double raycast0(final Vec3 start, final Vec3 end) {
        final double d = end.x - start.x;
        final double e = end.y - start.y;
        final double f = end.z - start.z;
        final double[] t = new double[]{1};
        final Direction direction = traceCollisionSide(getExtents(), start, t, d, e, f);
        if (direction != null) {
            return t[0];
        }
        return -1;
    }

    @Nullable
    private static Direction traceCollisionSide(final AABB box, final Vec3 intersectingVector, final double[] traceDistanceResult, final double xDelta, final double yDelta, final double zDelta) {
        Direction approachDirection = null;
        if (xDelta > 1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, xDelta, yDelta, zDelta, box.minX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.WEST, intersectingVector.x, intersectingVector.y, intersectingVector.z);
        else if (xDelta < -1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, xDelta, yDelta, zDelta, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.EAST, intersectingVector.x, intersectingVector.y, intersectingVector.z);

        if (yDelta > 1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, yDelta, zDelta, xDelta, box.minY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.DOWN, intersectingVector.y, intersectingVector.z, intersectingVector.x);
        else if (yDelta < -1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, yDelta, zDelta, xDelta, box.maxY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.UP, intersectingVector.y, intersectingVector.z, intersectingVector.x);

        if (zDelta > 1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, zDelta, xDelta, yDelta, box.minZ, box.minX, box.maxX, box.minY, box.maxY, Direction.NORTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);
        else if (zDelta < -1.0E-7D)
            approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, zDelta, xDelta, yDelta, box.maxZ, box.minX, box.maxX, box.minY, box.maxY, Direction.SOUTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);

        return approachDirection;
    }

    @Nullable
    private static Direction traceCollisionSide(final double[] traceDistanceResult, final Direction approachDirection, final double xDelta, final double yDelta, final double zDelta, final double begin, final double minX, final double maxX, final double minZ, final double maxZ, final Direction resultDirection, final double startX, final double startY, final double startZ) {
        final double d = (begin - startX) / xDelta;
        final double e = startY + d * yDelta;
        final double f = startZ + d * zDelta;
        if (0.0D < d && d < traceDistanceResult[0] && minX - 1.0E-7D < e && e < maxX + 1.0E-7D && minZ - 1.0E-7D < f && f < maxZ + 1.0E-7D) {
            traceDistanceResult[0] = d;
            return resultDirection;
        } else
            return approachDirection;
    }

    public boolean contains(double x, double y, double z) {
        x -= center.x;
        y -= center.y;
        z -= center.z;
        final double transX = getMatrix().transformX(x, y, z);
        final double transY = getMatrix().transformY(x, y, z);
        final double transZ = getMatrix().transformZ(x, y, z);
        return getExtents().contains(transX, transY, transZ);
    }

    public double getMax(final Direction.Axis axis) {
        final Matrix3d matrix = getMatrix();
        return switch (axis) {
            case X -> Math.max(matrix.m00, Math.max(matrix.m01, matrix.m02)) * halfExtents.x + center.x;
            case Y -> Math.max(matrix.m10, Math.max(matrix.m11, matrix.m12)) * halfExtents.y + center.y;
            case Z -> Math.max(matrix.m20, Math.max(matrix.m21, matrix.m22)) * halfExtents.z + center.z;
        };
    }

    public double getMin(final Direction.Axis axis) {
        final Matrix3d matrix = getMatrix();
        return switch (axis) {
            case X -> Math.min(matrix.m00, Math.min(matrix.m01, matrix.m02)) * halfExtents.x + center.x;
            case Y -> Math.min(matrix.m10, Math.min(matrix.m11, matrix.m12)) * halfExtents.y + center.y;
            case Z -> Math.min(matrix.m20, Math.min(matrix.m21, matrix.m22)) * halfExtents.z + center.z;
        };
    }

    public OrientedBox expand(double x, double y, double z) {
        if(x==0&&y==0&&z==0) {
            return this;
        }
        return new OrientedBox(center, halfExtents.add(x/2,y/2,z/2), rotation, matrix, inverse, basis);
    }
}
