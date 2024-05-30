package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.OrientedBox;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.QuaternionD;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

/**
 * Represents a hit box of an entity
 */
public final class EntityPart {
    private boolean changed = true;
    private double offX,offY,offZ;
    private double x, y, z;
    private final AxisAlignedBB box;
    private double px, py, pz;
    private QuaternionD rotation;
    private @Nullable EntityPart parent;

    EntityPart(final @Nullable EntityPart parent, AxisAlignedBB box, final boolean center, double offX, double offY, double offZ) {
        this.parent = parent;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        rotation = QuaternionD.IDENTITY;
        if (center) box = box.move(-box.minX - box.getXsize() / 2, -box.minY - box.getXsize() / 2, -box.minZ - box.getXsize() / 2);
        this.box = box;
        setX(0.0);
        setY(0.0);
        setZ(0.0);
    }

    void setParent(@Nullable final EntityPart parent) {
        this.parent = parent;
    }

    public void setOffX(double offX) {
        this.offX = offX;
        changed = true;
    }

    public void setOffY(double offY) {
        this.offY = offY;
        changed = true;
    }

    public void setOffZ(double offZ) {
        this.offZ = offZ;
        changed = true;
    }

    /**
     * @param x X coordinate relative to parent
     */
    public void setX(final double x) {
        this.x = x+offX;
        changed = true;
    }

    /**
     * @param y Y coordinate relative to parent
     */
    public void setY(final double y) {
        this.y = y+offY;
        changed = true;
    }

    /**
     * @param z Z coordinate relative to parent
     */
    public void setZ(final double z) {
        this.z = z+offZ;
        changed = true;
    }

    /**
     * @param px X coordinate of point this part should be rotated around
     */
    public void setPivotX(final double px) {
        this.px = px;
        changed = true;
    }

    /**
     * @param py X coordinate of point this part should be rotated around
     */
    public void setPivotY(final double py) {
        this.py = py;
        changed = true;
    }

    /**
     * @param pz X coordinate of point this part should be rotated around
     */
    public void setPivotZ(final double pz) {
        this.pz = pz;
        changed = true;
    }

    public void setRotation(final QuaternionD rotation) {
        this.rotation = rotation;
        changed = true;
    }

    public void rotate(final QuaternionD quaternion) {
        rotation = rotation.hamiltonProduct(quaternion);
        changed = true;
    }

    public void rotate(final double pitch, final double yaw, final double roll, final boolean degrees) {
        rotation = rotation.hamiltonProduct(new QuaternionD(pitch, yaw, roll, degrees));
        changed = true;
    }

    public void setRotation(final double pitch, final double yaw, final double roll, final boolean degrees) {
        rotation = new QuaternionD(pitch, yaw, roll, degrees);
        changed = true;
    }

    void setChanged(final boolean changed) {
        this.changed = changed;
    }

    boolean isChanged() {
        return changed;
    }

    /**
     * @return Oriented box represented by this EntityPart after all transformations have been applied
     */
    public OrientedBox getBox() {
        final OrientedBox orientedBox = new OrientedBox(box);
        return transformChild(orientedBox);
    }

    private OrientedBox transformChild(OrientedBox orientedBox) {
        if (parent != null)
            orientedBox = parent.transformChild(orientedBox);

        return orientedBox.transform(x, y, z, px, py, pz, rotation);
    }
}
