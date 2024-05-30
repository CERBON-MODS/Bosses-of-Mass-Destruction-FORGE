package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class ModRandom implements IRandom {
    @Override
    public double getDouble() {
        return new Random().nextDouble();
    }

    @Override
    public Vector3d getVector() {
        return RandomUtils.randVec();
    }
}
