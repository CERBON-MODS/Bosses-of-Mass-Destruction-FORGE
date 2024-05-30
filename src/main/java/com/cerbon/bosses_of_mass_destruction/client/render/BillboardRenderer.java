package com.cerbon.bosses_of_mass_destruction.client.render;


import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;

public class BillboardRenderer<T extends Entity> implements IRenderer<T> {
    private final EntityRendererManager dispatcher;
    private final RenderType renderType;
    private final ScaleFunction<T> scale;

    public BillboardRenderer(EntityRendererManager dispatcher, RenderType renderLayer, ScaleFunction<T> scale) {
        this.dispatcher = dispatcher;
        this.renderType = renderLayer;
        this.scale = scale;
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
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

