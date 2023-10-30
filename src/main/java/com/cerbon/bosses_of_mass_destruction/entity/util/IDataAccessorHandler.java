package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface IDataAccessorHandler {
    void onTrackedDataSet(EntityDataAccessor<?> data);
}
