package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.OrientedBox;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A complete entity hit box hierarchy
 */
public final class EntityBounds {
    private CompoundOrientedBox cache;
    private final Map<String, EntityPart> partMap;
    private @Nullable
    final MutableBox overrideBox;

    EntityBounds(final Map<String, EntityPart> partMap, @Nullable AABB overrideBox) {
        this.partMap = partMap;
        this.overrideBox = new MutableBox(overrideBox);
    }

    public @Nullable MutableBox getOverrideBox() {
        return overrideBox;
    }

    public boolean hasPart(final String name) {
        return partMap.get(name) != null;
    }

    /**
     * @param name Name of the part
     * @return The part with name, or null if it doesn't exist
     */
    public EntityPart getPart(final String name) {
        return partMap.get(name);
    }

    /**
     * @param start Starting position of raycast
     * @param end   Ending position of raycast
     * @return Part name the ray first intersects, or null if no part was intersected
     */
    public @Nullable String raycast(final Vec3 start, final Vec3 end) {
        double t = 1.00001;
        String result = null;
        for (final Map.Entry<String, EntityPart> entry : partMap.entrySet()) {
            final double tmp = entry.getValue().getBox().raycast(start, end);
            if (tmp != -1 && tmp < t) {
                t = tmp;
                result = entry.getKey();
            }
        }
        return result;
    }

    /**
     * @param bounds Bounding box outside which no collisions will be processed, should be the normal bounding box of the entity
     * @return Box compatible hierarchy of all the EntityParts
     */
    public CompoundOrientedBox getBox(final AABB bounds) {
        boolean changed = cache == null;
        for (final EntityPart value : partMap.values()) {
            if (value.isChanged()) {
                changed = true;
                value.setChanged(false);
            }
        }
        if (changed) {
            final List<OrientedBox> parts = new ObjectArrayList<>(partMap.size());
            for (final EntityPart value : partMap.values()) {
                parts.add(value.getBox());
            }
            cache = new CompoundOrientedBox(bounds, parts, overrideBox);
        }
        return cache.withBounds(bounds);
    }

    /**
     * @return A new builder
     */
    public static EntityBoundsBuilder builder() {
        return new EntityBoundsBuilder();
    }

    public interface Factory {
        /**
         * @return A new entity bounds instance, should only be called once per entity at entity creation
         */
        EntityBounds create();
    }

    public static final class EntityBoundsBuilder {
        private final Map<String, EntityPartInfo> partInfos = new Object2ObjectLinkedOpenHashMap<>();
        private AABB overrideBox = null;

        EntityBoundsBuilder() {
        }

        EntityBoundsBuilder addInfo(final EntityPartInfo info) {
            if (info.parent != null && !partInfos.containsKey(info.parent)) {
                throw new RuntimeException("Unknown part: " + info.parent + ", did you register a child before a parent");
            }
            partInfos.put(info.name, info);
            return this;
        }

        public EntityBoundsBuilder overrideCollisionBox(AABB box) {
            this.overrideBox = box;
            return this;
        }

        /**
         * Adds a new hit box to the builder
         *
         * @param name The name of the part, duplicates not allowed
         * @return The hit box builder
         */
        public EntityPartInfoBuilder add(final String name) {
            if (partInfos.containsKey(name)) {
                throw new RuntimeException("Duplicate part: " + name);
            }
            return new EntityPartInfoBuilder(this, name);
        }

        public Factory getFactory() {
            //defensive copy
            final Map<String, EntityPartInfo> copy = new Object2ObjectLinkedOpenHashMap<>(partInfos);
            return () -> {
                final Map<String, EntityPart> partMap = new Object2ObjectOpenHashMap<>();
                for (final Map.Entry<String, EntityPartInfo> entry : copy.entrySet()) {
                    final EntityPartInfo info = entry.getValue();
                    final EntityPart entityPart = new EntityPart(info.parent != null ? partMap.get(info.parent) : null, info.bounds, false, info.x, info.y, info.z);
                    entityPart.setPivotX(info.px);
                    entityPart.setPivotY(info.py);
                    entityPart.setPivotZ(info.pz);
                    partMap.put(entry.getKey(), entityPart);
                }
                return new EntityBounds(partMap, overrideBox);
            };
        }
    }

    public static final class EntityPartInfoBuilder {
        final EntityBoundsBuilder builder;
        @Nullable String parent;
        final String name;
        double x, y, z;
        double px, py, pz;
        AABB bounds;

        EntityPartInfoBuilder(final EntityBoundsBuilder builder, final String name) {
            this.builder = builder;
            this.name = name;
        }


        /**
         * Set position relative to parent, or absolute position if parent is null
         *
         * @param x X pos
         * @param y Y Pos
         * @param z Z pos
         * @return this
         */
        public EntityPartInfoBuilder setOffset(final double x, final double y, final double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        /**
         * Set pivot position around which this will be rotated
         *
         * @param x X pivot pos
         * @param y Y pivot pos
         * @param z Z pivot pos
         * @return this
         */
        public EntityPartInfoBuilder setPivot(final double x, final double y, final double z) {
            px = x;
            py = y;
            pz = z;
            return this;
        }

        /**
         * @param parent The name of the parent part, the parent part needs to be built first
         * @return this
         */
        public EntityPartInfoBuilder setParent(@Nullable final String parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Set bounding box, you should probably use {@link #setBounds(double, double, double)} instead unless you know hat you are doing
         *
         * @param bounds Bounding box
         * @return this
         */
        public EntityPartInfoBuilder setBounds(final AABB bounds) {
            this.bounds = bounds;
            return this;
        }

        /**
         * Set dimensions of the box, you should prefer this to {@link #setBounds(AABB)}
         *
         * @param xLength width
         * @param yLength height
         * @param zLength depth
         * @return this
         */
        public EntityPartInfoBuilder setBounds(final double xLength, final double yLength, final double zLength) {
            bounds = new AABB(-xLength / 2, -yLength / 2, -zLength / 2, xLength / 2, yLength / 2, zLength / 2);
            return this;
        }

        /**
         * @return Builds this box and returns the parent {@link EntityBounds.EntityBoundsBuilder}
         */
        public EntityBoundsBuilder build() {
            return builder.addInfo(new EntityPartInfo(parent, name, x, y, z, px, py, pz, bounds));
        }
    }

    private record EntityPartInfo(@Nullable String parent, String name, double x, double y, double z, double px,
                                  double py, double pz, AABB bounds) {
    }
}
