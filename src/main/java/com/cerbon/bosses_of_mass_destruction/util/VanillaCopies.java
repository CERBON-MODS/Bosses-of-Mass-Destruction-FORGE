package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;

import javax.annotation.Nonnull;

public class VanillaCopies {
    public static void renderBillboard(
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int i,
            EntityRendererManager dispatcher,
            RenderType type,
            Quaternion rotation
    ) {
        poseStack.pushPose();
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) Math.toRadians(180)));
        poseStack.mulPose(rotation);
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        IVertexBuilder vertexConsumer = buffer.getBuffer(type);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        poseStack.popPose();
    }

    public static void produceVertex(
            IVertexBuilder vertexConsumer,
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

    public static Vector3f[] buildFlatGeometry(
            @Nonnull ActiveRenderInfo camera, float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    ) {
        Vector3d vec3 = camera.getPosition();
        float f = (float) (MathHelper.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (MathHelper.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (MathHelper.lerp(tickDelta, prevPosZ, z) - vec3.z());

        Vector3f[] vector3fs = new Vector3f[]{
                new Vector3f(-1.0f, 0.0f, -1.0f),
                new Vector3f(-1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, 1.0f),
                new Vector3f(1.0f, 0.0f, -1.0f)
        };

        for (int k = 0; k <= 3; k++) {
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.transform(Vector3f.YP.rotationDegrees(rotation));
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

    public static Vector3f[] buildBillBoardGeometry(
            @Nonnull ActiveRenderInfo camera, float tickDelta,
            double prevPosX,
            double prevPosY,
            double prevPosZ,
            double x,
            double y,
            double z,
            float scale,
            float rotation
    ) {
        Vector3d vec3 = camera.getPosition();
        float f = (float) (MathHelper.lerp(tickDelta, prevPosX, x) - vec3.x());
        float g = (float) (MathHelper.lerp(tickDelta, prevPosY, y) - vec3.y());
        float h = (float) (MathHelper.lerp(tickDelta, prevPosZ, z) - vec3.z());
        Quaternion quaternion2 = camera.rotation();

        Vector3f[] vector3fs = {
                new Vector3f(-1.0f, -1.0f, 0.0f),
                new Vector3f(-1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, -1.0f, 0.0f)
        };

        for (int k = 0; k <= 3; k++){
            Vector3f vector3f2 = vector3fs[k];
            vector3f2.transform(Vector3f.ZP.rotationDegrees(rotation));
            vector3f2.transform(quaternion2);
            vector3f2.mul(scale);
            vector3f2.add(f, g, h);
        }

        return vector3fs;
    }

    public static void renderBeam(LivingEntity actor, Vector3d target, Vector3d prevTarget, float partialTicks, Vector3d color, MatrixStack poseStack, IRenderTypeBuffer buffer, RenderType renderType){
        float j = actor.level.getGameTime() + partialTicks;
        float k = j % 1.0F;
        float l = actor.getEyeHeight();
        poseStack.pushPose();
        poseStack.translate(0.0, l, 0.0);
        Vector3d vec3 = MathUtils.lerpVec(partialTicks, prevTarget, target);
        Vector3d vec32 = fromLerpedPosition(actor, l, partialTicks);
        Vector3d vec33 = vec3.subtract(vec32);
        float m = (float) vec33.length();
        vec33 = vec33.normalize();
        float n = (float) Math.acos(vec33.y);
        float o = (float) Math.atan2(vec33.z, vec33.x);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((1.5707964f - o) * 57.295776f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(n * 57.295776f));

        float q = j * 0.05F * -1.5F;
        int red = (int) (color.x() * 255);
        int green = (int) (color.y() * 255);
        int blue = (int) (color.z() * 255);
        float x = MathHelper.cos(q + 2.3561945F) * 0.282F;
        float y = MathHelper.sin(q + 2.3561945F) * 0.282F;
        float z = MathHelper.cos(q + 0.7853982F) * 0.282F;
        float aa = MathHelper.sin(q + 0.7853982F) * 0.282F;
        float ab = MathHelper.cos(q + 3.926991F) * 0.282F;
        float ac = MathHelper.sin(q + 3.926991F) * 0.282F;
        float ad = MathHelper.cos(q + 5.4977875F) * 0.282F;
        float ae = MathHelper.sin(q + 5.4977875F) * 0.282F;
        float af = MathHelper.cos(q + 3.1415927F) * 0.2F;
        float ag = MathHelper.sin(q + 3.1415927F) * 0.2F;
        float ah = MathHelper.cos(q + 0.0F) * 0.2F;
        float ai = MathHelper.sin(q + 0.0F) * 0.2F;
        float aj = MathHelper.cos(q + 1.5707964F) * 0.2F;
        float ak = MathHelper.sin(q + 1.5707964F) * 0.2F;
        float al = MathHelper.cos(q + 4.712389F) * 0.2F;
        float am = MathHelper.sin(q + 4.712389F) * 0.2F;
        float aq = -1.0F - k; // Negated K to reverse direction of laser movement
        float ar = m * 2.5f + aq;
        IVertexBuilder vertexConsumer = buffer.getBuffer(renderType);
        MatrixStack.Entry pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        vertex(vertexConsumer, matrix4f, matrix3f, af, m, ag, red, green, blue, 0.4999f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0f, ag, red, green, blue, 0.4999f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0f, ai, red, green, blue, 0.0f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, ah, m, ai, red, green, blue, 0.0f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, m, ak, red, green, blue, 0.4999f, ar);
        vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0f, ak, red, green, blue, 0.4999f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0f, am, red, green, blue, 0.0f, aq);
        vertex(vertexConsumer, matrix4f, matrix3f, al, m, am, red, green, blue, 0.0f, ar);

        float as = 0.0F;
        if (actor.tickCount % 2 == 0)
            as = 0.5F;

        vertex(vertexConsumer, matrix4f, matrix3f, x, m, y, red, green, blue, 0.5f, as + 0.5f);
        vertex(
                vertexConsumer,
                matrix4f,
                matrix3f,
                z,
                m,
                aa,
                red,
                green,
                blue,
                1.0f,
                as + 0.5f
            );
        vertex(vertexConsumer, matrix4f, matrix3f, ad, m, ae, red, green, blue, 1.0f, as);
        vertex(vertexConsumer, matrix4f, matrix3f, ab, m, ac, red, green, blue, 0.5f, as);
        poseStack.popPose();
    }

    public static void vertex(
            IVertexBuilder vertexConsumer,
            Matrix4f matrix4f,
            Matrix3f matrix3f,
            float f,
            float g,
            float h,
            int i,
            int j,
            int k,
            float l,
            float m
    ) {
        vertexConsumer.vertex(matrix4f, f, g, h)
                .color(i, j, k, 255)
                .uv(l, m)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728800)
                .normal(matrix3f, 0.0F, 0.0F, -1.0F) // Changed from normal(0, 1, 0) because that brightened it for some reason that I cannot fathom with my pathetic opengl knowledge
                .endVertex();
    }

    public static Vector3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp(delta, entity.xOld, entity.getX());
        double e = MathHelper.lerp(delta, entity.yOld, entity.getY()) + yOffset;
        double f = MathHelper.lerp(delta, entity.zOld, entity.getZ());
        return new Vector3d(d, e, f);
    }
}
