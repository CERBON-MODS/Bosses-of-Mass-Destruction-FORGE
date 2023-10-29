package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data;

import java.util.Collection;

public interface IHistoricalData<T> {
    void set(T value);
    T get(int past);
    Collection<T> getAll();
}

