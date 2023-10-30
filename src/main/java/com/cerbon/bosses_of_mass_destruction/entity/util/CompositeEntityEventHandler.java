package com.cerbon.bosses_of_mass_destruction.entity.util;

import java.util.List;
import java.util.Arrays;

public class CompositeEntityEventHandler implements IEntityEventHandler {
    private final List<IEntityEventHandler> entityEventHandlerList;

    public CompositeEntityEventHandler(IEntityEventHandler... statusHandlers) {
        this.entityEventHandlerList = Arrays.asList(statusHandlers);
    }

    @Override
    public void handleEntityEvent(byte id) {
        for (IEntityEventHandler entityEventHandler : entityEventHandlerList) {
            entityEventHandler.handleEntityEvent(id);
        }
    }
}
