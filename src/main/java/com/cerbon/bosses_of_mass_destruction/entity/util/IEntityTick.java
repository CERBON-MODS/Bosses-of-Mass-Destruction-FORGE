package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.World;

public interface IEntityTick<T extends World> {
    void tick(T level);
}
