package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event;

import java.util.function.Supplier;

public class TimedEvent implements IEvent {
    private final Runnable callback;
    private final int delay;
    private final int duration;
    private final Supplier<Boolean> shouldCancel;
    private int age;

    public TimedEvent(Runnable callback, int delay, int duration, Supplier<Boolean> shouldCancel) {
        this.callback = callback;
        this.delay = delay;
        this.duration = duration;
        this.shouldCancel = shouldCancel;
        this.age = 0;
    }

    public TimedEvent(Runnable callback, int delay) {
        this(callback, delay, 1, () -> false);
    }

    @Override
    public boolean shouldDoEvent() {
        return age++ >= delay && !shouldCancel.get();
    }

    @Override
    public void doEvent() {
        callback.run();
    }

    @Override
    public boolean shouldRemoveEvent() {
        return shouldCancel.get() || age >= delay + duration;
    }

    @Override
    public int tickSize() {
        return 1;
    }
}

