package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event;

import java.util.Iterator;
import java.util.List;

public class EventSeries implements IEvent {
    private final Iterator<IEvent> iterator;
    private IEvent currentEvent;

    public EventSeries(IEvent... events) {
        if (events.length < 1) throw new IllegalArgumentException("Must have at least one event");
        this.iterator = List.of(events).iterator();
        this.currentEvent = iterator.next();
    }

    @Override
    public boolean shouldDoEvent() {
        return currentEvent.shouldDoEvent();
    }

    @Override
    public void doEvent() {
        currentEvent.doEvent();
    }

    @Override
    public boolean shouldRemoveEvent() {
        while (currentEvent.shouldRemoveEvent()) {
            if (iterator.hasNext())
                currentEvent = iterator.next();
            else
                return true;
        }
        return false;
    }

    @Override
    public int tickSize() {
        return currentEvent.tickSize();
    }
}
