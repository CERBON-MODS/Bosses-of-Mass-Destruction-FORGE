package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.level.Level;

public interface IEntityTick<T extends Level> {
    void tick(T level);
}
