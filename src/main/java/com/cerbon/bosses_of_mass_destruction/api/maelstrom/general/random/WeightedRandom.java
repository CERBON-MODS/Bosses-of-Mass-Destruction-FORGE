package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random;

import com.mojang.datafixers.util.Pair;

import java.util.*;

// https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
public class WeightedRandom<E> {
    private final Random random;
    private final NavigableMap<Double, E> map;
    private double total;

    public WeightedRandom() {
        this(new Random());
    }

    public WeightedRandom(Random random) {
        this.random = random;
        this.map = new TreeMap<>();
        this.total = 0.0;
    }

    public WeightedRandom<E> add(double weight, E result) {
        if (weight <= 0) return this;
        if (Double.isNaN(weight) || Double.isInfinite(weight)) {
            throw new IllegalArgumentException("The weight for random collection is invalid: " + weight);
        }
        total += weight;
        map.put(total, result);
        return this;
    }

    public void addAll(Collection<Pair<Double, E>> collection) {
        for (Pair<Double, E> pair : collection) {
            add(pair.getFirst(), pair.getSecond());
        }
    }

    public E next() {
        double value = random.nextDouble() * total;
        Map.Entry<Double, E> entry = map.higherEntry(value);
        return entry != null ? entry.getValue() : null;
    }
}
