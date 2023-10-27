package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.List;

public class CompositeRenderer<T extends Entity> implements IRenderer<T> {
    private final List<IRenderer<T>> rendererList;

    public CompositeRenderer(IRenderer<T>... renderers) {
        this.rendererList = Arrays.asList(renderers);
    }


    @Override
    public void render(
            T entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light
    ) {
        for (IRenderer<T> renderer : rendererList) {
            renderer.render(entity, yaw, partialTicks, poseStack, buffer, light);
        }
    }
}

