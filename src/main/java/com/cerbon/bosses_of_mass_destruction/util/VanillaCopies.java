package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class VanillaCopies {
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

    public static void produceVertex(
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

    public static void renderBeam(LivingEntity actor, Vec3 target, Vec3 prevTarget, float partialTicks, Vec3 color, PoseStack poseStack, MultiBufferSource buffer, RenderType renderType){
        float j = actor.level().getGameTime() + partialTicks;
        float k = j % 1.0F;
        float l = actor.getEyeHeight();
        poseStack.pushPose();
        poseStack.translate(0.0, l, 0.0);
        Vec3 vec3 = MathUtils.lerpVec(partialTicks, prevTarget, target);
        Vec3 vec32 = fromLerpedPosition(actor, l, partialTicks);
        Vec3 vec33 = vec3.subtract(vec32);
        float m = (float) vec33.length();
        vec33 = vec33.normalize();
        float n = (float) Math.acos(vec33.y);
        float o = (float) Math.atan2(vec33.z, vec33.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - o) * 57.295776f));
        poseStack.mulPose(Axis.XP.rotationDegrees(n * 57.295776f));

        float q = j * 0.05F * -1.5F;
        int red = (int) (color.x() * 255);
        int green = (int) (color.y() * 255);
        int blue = (int) (color.z() * 255);
        float x = Mth.cos(q + 2.3561945F) * 0.282F;
        float y = Mth.sin(q + 2.3561945F) * 0.282F;
        float z = Mth.cos(q + 0.7853982F) * 0.282F;
        float aa = Mth.sin(q + 0.7853982F) * 0.282F;
        float ab = Mth.cos(q + 3.926991F) * 0.282F;
        float ac = Mth.sin(q + 3.926991F) * 0.282F;
        float ad = Mth.cos(q + 5.4977875F) * 0.282F;
        float ae = Mth.sin(q + 5.4977875F) * 0.282F;
        float af = Mth.cos(q + 3.1415927F) * 0.2F;
        float ag = Mth.sin(q + 3.1415927F) * 0.2F;
        float ah = Mth.cos(q + 0.0F) * 0.2F;
        float ai = Mth.sin(q + 0.0F) * 0.2F;
        float aj = Mth.cos(q + 1.5707964F) * 0.2F;
        float ak = Mth.sin(q + 1.5707964F) * 0.2F;
        float al = Mth.cos(q + 4.712389F) * 0.2F;
        float am = Mth.sin(q + 4.712389F) * 0.2F;
        float aq = -1.0F - k; // Negated K to reverse direction of laser movement
        float ar = m * 2.5f + aq;
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();
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
            VertexConsumer vertexConsumer,
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

    public static Vec3 fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = Mth.lerp(delta, entity.xOld, entity.getX());
        double e = Mth.lerp(delta, entity.yOld, entity.getY()) + yOffset;
        double f = Mth.lerp(delta, entity.zOld, entity.getZ());
        return new Vec3(d, e, f);
    }
}
