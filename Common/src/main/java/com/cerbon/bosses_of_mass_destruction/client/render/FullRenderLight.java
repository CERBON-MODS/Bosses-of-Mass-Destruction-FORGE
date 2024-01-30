package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class FullRenderLight<T extends Entity> implements IRenderLight<T> {

    @Override
    public int getBlockLight(T entity, BlockPos blockPos) {
        return 15;
    }
}

