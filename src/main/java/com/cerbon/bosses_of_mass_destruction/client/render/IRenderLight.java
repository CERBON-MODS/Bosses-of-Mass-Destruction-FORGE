package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface IRenderLight<T extends Entity> {
    int getBlockLight(T entity, BlockPos blockPos);
}

