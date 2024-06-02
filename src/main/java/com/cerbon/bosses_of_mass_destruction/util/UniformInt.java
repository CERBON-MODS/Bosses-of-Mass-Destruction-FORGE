package com.cerbon.bosses_of_mass_destruction.util;

import java.util.Random;

public class UniformInt {

    private final int minInclusive;
    private final int maxInclusive;

    private UniformInt(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static UniformInt of(int minInclusive, int maxInclusive) {
        return new UniformInt(minInclusive, maxInclusive);
    }

    public int sample(Random random) {
        return randomBetweenInclusive(random, this.minInclusive, this.maxInclusive);
    }

    public int getMinValue() {
        return this.minInclusive;
    }

    public int getMaxValue() {
        return this.maxInclusive;
    }

    public static int randomBetweenInclusive(Random random, int minInclusive, int maxInclusive) {
        return random.nextInt(maxInclusive - minInclusive + 1) + minInclusive;
    }
}
