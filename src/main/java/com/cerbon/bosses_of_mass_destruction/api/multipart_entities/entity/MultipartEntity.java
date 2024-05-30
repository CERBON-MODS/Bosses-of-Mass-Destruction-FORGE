package com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.util.CompoundOrientedBox;
import net.minecraft.util.math.AxisAlignedBB;

public interface MultipartEntity {
    CompoundOrientedBox getCompoundBoundingBox(AxisAlignedBB bounds);
}
