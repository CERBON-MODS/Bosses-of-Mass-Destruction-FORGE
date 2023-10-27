package com.cerbon.bosses_of_mass_destruction.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VanillaCopies {
    public static Vector3f[] buildBillBoardGeometry(
            @NotNull Camera camera, float tickDelta,
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

    public static Vector3f @NotNull [] buildFlatGeometry(
            @NotNull Camera camera, float tickDelta,
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

    public static void renderBillboard(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int i,
            EntityRenderDispatcher dispatcher,
            RenderType type,
            Quaternionf rotation
    ) {
        poseStack.pushPose();
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
        poseStack.mulPose(rotation);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        VertexConsumer vertexConsumer = buffer.getBuffer(type);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        poseStack.popPose();
    }

    private static void produceVertex(
            VertexConsumer vertexConsumer,
            Matrix4f modelMatrix,
            Matrix3f normalMatrix,
            int light,
            float x,
            int y,
            int textureU,
            int textureV
    ) {
        vertexConsumer.vertex(modelMatrix, x - 0.5f, (float) y - 0.25f, 0.0f)
                .color(255, 255, 255, 255)
                .uv((float) textureU, (float) textureV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, 0.0f, 1.0f, 0.0f)
                .endVertex();
    }

}
