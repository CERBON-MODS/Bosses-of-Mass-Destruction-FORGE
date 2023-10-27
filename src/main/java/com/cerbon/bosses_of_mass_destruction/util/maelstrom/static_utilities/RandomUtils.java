package com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities;

import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Supplier;

public class RandomUtils {

    public static int range(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum is greater than maximum");
        }
        int range = max - min;
        return min + new Random().nextInt(range);
    }

    public static double range(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum is greater than maximum");
        }
        double range = max - min;
        return min + Math.random() * range;
    }



    public static Vec3 randVec(Supplier<Double> rand) {
        if (rand == null)
            rand = () -> Math.random() - 0.5;

        return new Vec3(rand.get(), rand.get(), rand.get());
    }

    public static int randSign() {
        return (new Random().nextInt(2) == 0) ? 1 : -1;
    }
}
