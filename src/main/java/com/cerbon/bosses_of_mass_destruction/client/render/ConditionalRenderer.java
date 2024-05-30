package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public class ConditionalRenderer<T extends Entity> implements IRenderer<T> {
    private final Predicate<T> predicate;
    private final IRenderer<T> renderer;

    public ConditionalRenderer(Predicate<T> predicate, IRenderer<T> renderer) {
        this.predicate = predicate;
        this.renderer = renderer;
    }

    @Override
    public void render(
            T entity,
            float yaw,
            float partialTicks,
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int light
    ) {
        if (predicate.test(entity))
            renderer.render(entity, yaw, partialTicks, poseStack, buffer, light);
    }
}

