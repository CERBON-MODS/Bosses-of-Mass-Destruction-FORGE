package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Map;
import java.util.function.Function;

public class VoidBlossomSpikeRenderer implements IRenderer<VoidBlossomEntity> {
    private final ResourceLocation spikeTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/void_blossom_spike.png");
    private final RenderType type = RenderType.entityCutoutNoCull(spikeTexture);

    @Override
    public void render(VoidBlossomEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        for (Map.Entry<BlockPos, VoidBlossomClientSpikeHandler.Spike> kv :  entity.clientSpikeHandler.getSpikes().entrySet()){
            renderBeam(
                    entity,
                    kv.getValue(),
                    partialTicks,
                    poseStack,
                    buffer,
                    type
            );
        }
    }

    private void renderBeam(LivingEntity actor, VoidBlossomClientSpikeHandler.Spike spike, float tickDelta, MatrixStack poseStack, IRenderTypeBuffer bufferSource, RenderType type){
        float numTextures = 8.0f;
        float lifeRatio = 2.0f;
        float textureProgress = Math.max(0f, ((spike.age + tickDelta) * lifeRatio / spike.maxAge) - lifeRatio + 1);
        if(textureProgress >= 1) return;

        float spikeHeight = spike.height;
        float textureRatio = 22.0f / 64.0f;
        float spikeWidth = textureRatio * spikeHeight * 0.5f;
        double upProgress = (Math.sin((Math.min((spike.age + tickDelta) / (spike.maxAge * 0.4), 1.0)) * Math.PI * 0.5) - 1) * spikeHeight;
        Function<Float, Float> texTransformer = textureMultiplier(1 / numTextures, (float) (Math.floor(textureProgress * numTextures) / numTextures));
        poseStack.pushPose();
        Vector3d offset = VanillaCopies.fromLerpedPosition(actor, 0.0, tickDelta).subtract(spike.pos);
        poseStack.translate(-offset.x, upProgress - offset.y, -offset.z);
        Vector3d bottomPos = spike.offset;
        float n = (float) Math.acos(bottomPos.y);
        float o = (float) Math.atan2(bottomPos.z, bottomPos.x);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((1.5707964f - o) * 57.295776f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(n * 57.295776f));
        float q = 0.0F;
        int red = (int) (BMDColors.WHITE.x * 255);
        int green = (int) (BMDColors.WHITE.y * 255);
        int blue = (int) (BMDColors.WHITE.z * 255);
        float af = MathHelper.cos(q + 3.1415927f) * spikeWidth;
        float ag = MathHelper.sin(q + 3.1415927f) * spikeWidth;
        float ah = MathHelper.cos(q + 0.0f) * spikeWidth;
        float ai = MathHelper.sin(q + 0.0f) * spikeWidth;
        float aj = MathHelper.cos(q + 1.5707964f) * spikeWidth;
        float ak = MathHelper.sin(q + 1.5707964f) * spikeWidth;
        float al = MathHelper.cos(q + 4.712389f) * spikeWidth;
        float am = MathHelper.sin(q + 4.712389f) * spikeWidth;
        IVertexBuilder vertexConsumer= bufferSource.getBuffer(type);
        MatrixStack.Entry entry = poseStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        float c0 = texTransformer.apply(0.4999f);
        float c2 = texTransformer.apply(0.0f);
        float c1 = texTransformer.apply(1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, af, spikeHeight, ag, red, green, blue, c0, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0f, ag, red, green, blue, c0, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0f, ai, red, green, blue, c2, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, ah, spikeHeight, ai, red, green, blue, c2, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, aj, spikeHeight, ak, red, green, blue, c1, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0f, ak, red, green, blue, c1, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0f, am, red, green, blue, c0, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, matrix3f, al, spikeHeight, am, red, green, blue, c0, 0.0f);
        poseStack.popPose();
    }

    public Function<Float, Float> textureMultiplier(Float multiplier, float adjustment) {
        return texCoord -> texCoord * multiplier + adjustment;
    }
}
