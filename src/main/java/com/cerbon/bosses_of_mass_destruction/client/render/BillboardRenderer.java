package com.cerbon.bosses_of_mass_destruction.client.render;


import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

public class BillboardRenderer<T extends Entity> implements IRenderer<T> {
    private final EntityRenderDispatcher dispatcher;
    private final RenderType renderType;
    private final ScaleFunction<T> scale;

    public BillboardRenderer(EntityRenderDispatcher dispatcher, RenderType renderLayer, ScaleFunction<T> scale) {
        this.dispatcher = dispatcher;
        this.renderType = renderLayer;
        this.scale = scale;
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float scaleValue = scale.apply(entity);
        poseStack.pushPose();
        poseStack.scale(scaleValue, scaleValue, scaleValue);
        VanillaCopies.renderBillboard(poseStack, buffer, light, dispatcher, renderType, Quaternion.ONE);
        poseStack.popPose();
    }

    public interface ScaleFunction<T> {
        float apply(T entity);
    }
}

