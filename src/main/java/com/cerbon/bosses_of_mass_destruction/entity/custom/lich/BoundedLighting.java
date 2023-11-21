package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderLight;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class BoundedLighting<T extends Entity> implements IRenderLight<T> {
    private final int minimumValue;

    public BoundedLighting(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    @Override
    public int getBlockLight(T entity, BlockPos blockPos) {
        return Math.max(VanillaCopiesServer.getBlockLight(entity, blockPos), minimumValue);
    }
}

