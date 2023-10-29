package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event;

public interface IEvent {
    boolean shouldDoEvent();
    void doEvent();
    boolean shouldRemoveEvent();
    int tickSize();
}
