package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MutableBox;
import com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities.InvokerArrayVoxelShape;
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class CompoundOrientedBox extends AABB implements Iterable<OrientedBox> {
    private final Collection<OrientedBox> boxes;
    private VoxelShape cached;
    private final @Nullable MutableBox overrideBox;

    public CompoundOrientedBox(final AABB bounds, final Collection<OrientedBox> boxes, MutableBox overrideBox) {
        this(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ, boxes, overrideBox);
    }

    public CompoundOrientedBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final Collection<OrientedBox> boxes, @Nullable MutableBox overrideBox) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.boxes = boxes;
        this.overrideBox = overrideBox;
    }

    private CompoundOrientedBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final Collection<OrientedBox> boxes, final VoxelShape cached, @Nullable MutableBox overrideBox) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.boxes = boxes;
        this.cached = cached;
        this.overrideBox = overrideBox;
    }

    @Override
    public @NotNull AABB inflate(final double x, final double y, final double z) {
        final List<OrientedBox> orientedBoxes = new ObjectArrayList<>(boxes.size());
        for (final OrientedBox box : boxes)
            orientedBoxes.add(box.expand(x,y,z));

        MutableBox overrideBox = null;
        if(this.overrideBox != null)
            overrideBox = new MutableBox(this.overrideBox.getBox().inflate(x, y, z));

        if (cached != null) {
            return new CompoundOrientedBox(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z, orientedBoxes, cached.move(x, y, z), overrideBox);
        }
        return new CompoundOrientedBox(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z, orientedBoxes, overrideBox);
    }

    @Override
    public @NotNull AABB move(final double x, final double y, final double z) {
        final List<OrientedBox> orientedBoxes = new ObjectArrayList<>(boxes.size());
        for (final OrientedBox box : boxes)
            orientedBoxes.add(box.offset(x, y, z));

        MutableBox overrideBox = null;
        if(this.overrideBox != null)
            overrideBox = new MutableBox(this.overrideBox.getBox().move(x, y, z));

        if (cached != null) {
            return new CompoundOrientedBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z, orientedBoxes, cached.move(x, y, z), overrideBox);
        }
        return new CompoundOrientedBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z, orientedBoxes, overrideBox);
    }

    @Override
    public @NotNull AABB move(final BlockPos blockPos) {
        return move(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public @NotNull Optional<Vec3> clip(final @NotNull Vec3 min, final @NotNull Vec3 max) {
        double t = Double.MAX_VALUE;
        for (final OrientedBox box : boxes) {
            final double tmp = box.raycast(min, max);
            if (tmp != -1)
                t = Math.min(t, tmp);
        }
        if (t != Double.MAX_VALUE) {
            final double d = max.x - min.x;
            final double e = max.y - min.y;
            final double f = max.z - min.z;
            return Optional.of(min.add(t * d, t * e, t * f));
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Iterator<OrientedBox> iterator() {
        return Iterators.unmodifiableIterator(boxes.iterator());
    }

    @Override
    public boolean intersects(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        return intersects(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    @Override
    public boolean intersects(final @NotNull AABB box) {
        final Vec3[] vertices = OrientedBox.getVertices(box);
        for (final OrientedBox orientedBox : boxes) {
            if (orientedBox.intersects(vertices))
                return true;
        }
        return false;
    }

    public VoxelShape toVoxelShape() {
        if (cached != null) {
            return cached;
        }
        if(overrideBox != null) {
            cached = Shapes.create(overrideBox.getBox());
            return cached;
        }
        final double minX = min(Direction.Axis.X) + 0.0001;
        final double minY = min(Direction.Axis.Y) + 0.0001;
        final double minZ = min(Direction.Axis.Z) + 0.0001;

        final double deltaX = max(Direction.Axis.X) - minX;
        final double deltaY = max(Direction.Axis.Y) - minY;
        final double deltaZ = max(Direction.Axis.Z) - minZ;
        double resolution = 4.0;
        final int xResolution = (int) Math.ceil(deltaX * resolution + 0.0001);
        final int yResolution = (int) Math.ceil(deltaY * resolution + 0.0001);
        final int zResolution = (int) Math.ceil(deltaZ * resolution + 0.0001);

        final BitSetDiscreteVoxelShape bitSet = new BitSetDiscreteVoxelShape(xResolution, yResolution, zResolution);
        for (int i = 0; i < xResolution; i++) {
            final double x = minX + i / resolution;
            for (int j = 0; j < zResolution; j++) {
                final double z = minZ + j / resolution;
                for (int k = 0; k < yResolution; k++) {
                    final double y = minY + k / resolution;
                    final AABB box = new AABB(x, y, z, x + 0.9999 / xResolution, y + 0.9999 / yResolution, z + 0.9999 / zResolution);
                    if (intersects(box))
                        bitSet.fill(i, k, j);
                }
            }
        }
        final DoubleList xPoints = new DoubleArrayList(xResolution + 1);
        for (int i = 0; i < xResolution + 1; i++)
            xPoints.add(minX + i / resolution);

        final DoubleList yPoints = new DoubleArrayList(yResolution + 1);
        for (int i = 0; i < yResolution + 1; i++)
            yPoints.add(minY + i / resolution);

        final DoubleList zPoints = new DoubleArrayList(zResolution + 1);
        for (int i = 0; i < zResolution + 1; i++)
            zPoints.add(minZ + i / resolution);

        return cached = InvokerArrayVoxelShape.init(bitSet, xPoints, yPoints, zPoints);
    }

    @Override
    public boolean contains(final double x, final double y, final double z) {
        for (final OrientedBox box : boxes) {
            if (box.contains(x, y, z))
                return true;
        }
        return false;
    }

    public CompoundOrientedBox withBounds(final AABB bounds) {
        return new CompoundOrientedBox(bounds, new ObjectArrayList<>(boxes), overrideBox);
    }

    public double calculateMaxDistance(final Direction.Axis axis, final VoxelShape voxelShape, double maxDist) {
        for (final AABB boundingBox : toVoxelShape().toAabbs()) {
            maxDist = voxelShape.collide(axis, boundingBox, maxDist);
            if (Math.abs(maxDist) < 0.0001)
                return 0;
        }
        return maxDist;
    }
}
