package com.cerbon.bosses_of_mass_destruction.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BMDUtils {
    public static float randomPitch(@NotNull RandomSource random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }

    public static void spawnParticle(ServerLevel level, ParticleOptions particleType, Vec3 pos, Vec3 velOrOffset, int count, double speed) {
        level.sendParticles(
                particleType,
                pos.x,
                pos.y,
                pos.z,
                count,
                velOrOffset.x,
                velOrOffset.y,
                velOrOffset.z,
                speed
        );
    }
}
