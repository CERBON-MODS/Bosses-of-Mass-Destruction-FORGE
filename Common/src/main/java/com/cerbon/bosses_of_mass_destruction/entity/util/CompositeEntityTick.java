package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Arrays;

public class CompositeEntityTick<T extends Level> implements IEntityTick<T> {
    private final List<IEntityTick<T>> tickList;

    @SafeVarargs
    public CompositeEntityTick(IEntityTick<T>... tickHandlers) {
        this.tickList = Arrays.asList(tickHandlers);
    }

    @Override
    public void tick(T level) {
        for (IEntityTick<T> tickHandler : tickList)
            tickHandler.tick(level);
    }
}

