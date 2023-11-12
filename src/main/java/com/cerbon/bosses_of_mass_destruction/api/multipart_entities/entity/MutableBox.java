package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import net.minecraft.world.phys.AABB;

public final class MutableBox {
    private AABB box;

    public MutableBox(AABB box) {
        this.box = box;
    }

    public AABB getBox() {
        return box;
    }

    public void setBox(AABB box) {
        this.box = box;
    }
}
