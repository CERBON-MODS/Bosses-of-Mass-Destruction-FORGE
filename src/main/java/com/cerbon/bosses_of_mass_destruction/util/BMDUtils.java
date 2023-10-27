package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class BMDUtils {
    public static float randomPitch(@NotNull RandomSource random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }
}
