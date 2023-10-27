package com.cerbon.bosses_of_mass_destruction.util;

import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class VanillaCopies {
    public static Vector3f[] buildBillBoardGeometry(Camera camera, float tickDelta, double prevPosX, double prevPosY, double prevPosZ, double x, double y, double z, float scale, float rotation){
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());
        Quaternionf quaternion2 = camera.rotation();

        Vector3f[] vector3fs = {
                new Vector3f(-1.0f, -1.0f, 0.0f),
                new Vector3f(-1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, -1.0f, 0.0f)
        };

        for (int k = 0; k <= 3; k++){
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(Axis.ZP.rotationDegrees(rotation));
            vector3f2.rotate(quaternion2);
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

    public static Vector3f[] buildFlatGeometry(
            Camera camera, float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    ) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (Mth.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (Mth.lerp(tickDelta, prevPosZ, z) - vec3.z());

        Vector3f[] vector3fs = new Vector3f[]{
                new Vector3f(-1.0f, 0.0f, -1.0f),
                new Vector3f(-1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, -1.0f)
        };

        for (int k = 0; k <= 3; k++) {
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.rotate(Axis.YP.rotationDegrees(rotation));
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

}
