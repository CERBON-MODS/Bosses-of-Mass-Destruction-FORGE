package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import net.minecraft.world.phys.AABB;

public interface MultipartEntity {
    CompoundOrientedBox getCompoundBoundingBox(AABB bounds);
}
