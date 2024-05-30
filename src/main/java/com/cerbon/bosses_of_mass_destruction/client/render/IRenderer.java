package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;

public interface IRenderer<T extends Entity> {
    void render(
            T entity,
            float yaw,
            float partialTicks,
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int light
    );
}

