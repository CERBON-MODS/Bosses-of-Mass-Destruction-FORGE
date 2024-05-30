package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;

public interface IRenderLight<T extends Entity> {
    int getBlockLight(T entity, BlockPos blockPos);
}

