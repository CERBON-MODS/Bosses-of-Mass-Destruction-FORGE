package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.IBoneLight;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class VoidBlossomBoneLight implements IBoneLight, IRenderer<VoidBlossomEntity> {
    private VoidBlossomEntity entity;
    private Float partialTicks;

    @Override
    public int getLightForBone(GeoBone bone, int packedLight) {
        if (bone.getName().contains("Spike") || bone.getName().contains("Thorn") || bone.getName().contains("FlowerCenter"))
            return IBoneLight.fullbright;
        else
            return packedLight;
    }

    @Override
    public Vector4f getColorForBone(GeoBone bone, Vector4f rgbaColor) {
        if (entity == null)
            return rgbaColor;

        float partialTicks1 = this.partialTicks != null ? this.partialTicks : 0f;
        Vector4f newColor = new Vector4f(rgbaColor.x(), rgbaColor.y(), rgbaColor.z(), rgbaColor.w());

        if (entity.isDeadOrDying()){
            float interceptedTime = MathUtils.ratioLerp(entity.deathTime, 0.5f, LightBlockRemover.deathMaxAge, partialTicks1);
            newColor.mul(new Vector3f(1f - interceptedTime * 0.5f, 1f - interceptedTime * 0.5f, 1f - interceptedTime * 0.5f));
        }

        return newColor;
    }

    @Override
    public void render(VoidBlossomEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }
}
