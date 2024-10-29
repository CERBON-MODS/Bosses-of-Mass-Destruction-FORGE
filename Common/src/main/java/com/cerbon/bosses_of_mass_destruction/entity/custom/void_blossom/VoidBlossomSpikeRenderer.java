package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.function.Function;

public class VoidBlossomSpikeRenderer implements IRenderer<VoidBlossomEntity> {
    private final ResourceLocation spikeTexture = ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "textures/entity/void_blossom_spike.png");
    private final RenderType type = RenderType.entityCutoutNoCull(spikeTexture);

    @Override
    public void render(VoidBlossomEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        for (var kv :  entity.clientSpikeHandler.getSpikes().entrySet()){
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

    private void renderBeam(LivingEntity actor, VoidBlossomClientSpikeHandler.Spike spike, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, RenderType type){
        float numTextures = 8.0f;
        float lifeRatio = 2.0f;
        float textureProgress = Math.max(0f, ((spike.age() + tickDelta) * lifeRatio / spike.maxAge()) - lifeRatio + 1);
        if(textureProgress >= 1) return;

        float spikeHeight = spike.height();
        float textureRatio = 22.0f / 64.0f;
        float spikeWidth = textureRatio * spikeHeight * 0.5f;
        double upProgress = (Math.sin((Math.min((spike.age() + tickDelta) / (spike.maxAge() * 0.4), 1.0)) * Math.PI * 0.5) - 1) * spikeHeight;
        Function<Float, Float> texTransformer = textureMultiplier(1 / numTextures, (float) (Math.floor(textureProgress * numTextures) / numTextures));
        poseStack.pushPose();
        Vec3 offset = VanillaCopies.fromLerpedPosition(actor, 0.0, tickDelta).subtract(spike.pos());
        poseStack.translate(-offset.x, upProgress - offset.y, -offset.z);
        Vec3 bottomPos = spike.offset();
        float n = (float) Math.acos(bottomPos.y);
        float o = (float) Math.atan2(bottomPos.z, bottomPos.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - o) * 57.295776f));
        poseStack.mulPose(Axis.XP.rotationDegrees(n * 57.295776f));
        float q = 0.0F;
        int red = (int) (Vec3Colors.WHITE.x * 255);
        int green = (int) (Vec3Colors.WHITE.y * 255);
        int blue = (int) (Vec3Colors.WHITE.z * 255);
        float af = Mth.cos(q + 3.1415927f) * spikeWidth;
        float ag = Mth.sin(q + 3.1415927f) * spikeWidth;
        float ah = Mth.cos(q + 0.0f) * spikeWidth;
        float ai = Mth.sin(q + 0.0f) * spikeWidth;
        float aj = Mth.cos(q + 1.5707964f) * spikeWidth;
        float ak = Mth.sin(q + 1.5707964f) * spikeWidth;
        float al = Mth.cos(q + 4.712389f) * spikeWidth;
        float am = Mth.sin(q + 4.712389f) * spikeWidth;
        VertexConsumer vertexConsumer= bufferSource.getBuffer(type);
        PoseStack.Pose entry = poseStack.last();
        Matrix4f matrix4f = entry.pose();
        float c0 = texTransformer.apply(0.4999f);
        float c2 = texTransformer.apply(0.0f);
        float c1 = texTransformer.apply(1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, af, spikeHeight, ag, red, green, blue, c0, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, af, 0.0f, ag, red, green, blue, c0, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, ah, 0.0f, ai, red, green, blue, c2, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, ah, spikeHeight, ai, red, green, blue, c2, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, aj, spikeHeight, ak, red, green, blue, c1, 0.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, aj, 0.0f, ak, red, green, blue, c1, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, al, 0.0f, am, red, green, blue, c0, 1.0f);
        VanillaCopies.vertex(vertexConsumer, matrix4f, entry, al, spikeHeight, am, red, green, blue, c0, 0.0f);
        poseStack.popPose();
    }

    public Function<Float, Float> textureMultiplier(Float multiplier, float adjustment) {
        return texCoord -> texCoord * multiplier + adjustment;
    }
}
