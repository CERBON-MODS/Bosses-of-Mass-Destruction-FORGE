package com.cerbon.bosses_of_mass_destruction.entity.util;

import net.minecraft.network.syncher.EntityDataAccessor;

import java.util.List;
import java.util.Arrays;

public class CompositeDataAccessorHandler implements IDataAccessorHandler {
    private final List<IDataAccessorHandler> handlerList;

    public CompositeDataAccessorHandler(IDataAccessorHandler... dataHandlers) {
        this.handlerList = Arrays.asList(dataHandlers);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        for (IDataAccessorHandler handler : handlerList) {
            handler.onSyncedDataUpdated(data);
        }
    }
}
