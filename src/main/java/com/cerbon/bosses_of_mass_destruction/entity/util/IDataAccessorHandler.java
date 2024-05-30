package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.network.datasync.DataParameter;

public interface IDataAccessorHandler {
    void onSyncedDataUpdated(DataParameter<?> data);
}
