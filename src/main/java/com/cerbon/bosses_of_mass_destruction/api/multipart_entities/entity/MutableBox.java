package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import net.minecraft.util.math.AxisAlignedBB;

public final class MutableBox {
    private AxisAlignedBB box;

    public MutableBox(AxisAlignedBB box) {
        this.box = box;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public void setBox(AxisAlignedBB box) {
        this.box = box;
    }
}
