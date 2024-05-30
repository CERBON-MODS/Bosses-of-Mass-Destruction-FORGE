package com.cerbon.bosses_of_mass_destruction.client.render;

import net.minecraft.util.math.vector.Vector4f;
import software.bernie.geckolib3.geo.render.built.GeoBone;

@FunctionalInterface
public interface IBoneLight {
    int getLightForBone(GeoBone bone, int packedLight);

    default Vector4f getColorForBone(GeoBone bone, Vector4f rgbaColor) {
        return rgbaColor;
    }

    int fullbright = 15728880;
}

