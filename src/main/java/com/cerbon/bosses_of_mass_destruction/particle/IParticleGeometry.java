package com.cerbon.bosses_of_mass_destruction.particle;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;

@FunctionalInterface
public interface IParticleGeometry {
    Vector3f[] getGeometry(
            Camera camera,
            float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    );
}

