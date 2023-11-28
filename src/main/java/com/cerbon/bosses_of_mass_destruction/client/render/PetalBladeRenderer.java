package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public class PetalBladeRenderer implements IRenderer<PetalBladeProjectile>{
    private final EntityRenderDispatcher dispatcher;
    private final RenderType renderType;

    public PetalBladeRenderer(EntityRenderDispatcher dispatcher, RenderType renderType){
        this.dispatcher = dispatcher;
        this.renderType = renderType;
    }

    @Override
    public void render(
            PetalBladeProjectile entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light
    ) {
        float scale = 0.5f;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        VanillaCopies.renderBillboard(poseStack, buffer, light, dispatcher, renderType, Vector3f.ZP.rotationDegrees(-entity.getEntityData().get(PetalBladeProjectile.renderRotation)));
        poseStack.popPose();
    }
}
