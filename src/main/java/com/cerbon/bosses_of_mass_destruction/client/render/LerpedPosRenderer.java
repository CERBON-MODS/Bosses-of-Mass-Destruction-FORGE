package com.cerbon.bosses_of_mass_destruction.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;

public class LerpedPosRenderer<T extends Entity> implements IRenderer<T> {
    private final Consumer<Vector3d> callback;

    public LerpedPosRenderer(Consumer<Vector3d> callback) {
        this.callback = callback;
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
        double x = MathHelper.lerp(partialTicks, entity.xo, entity.getX());
        double y = MathHelper.lerp(partialTicks, entity.yo, entity.getY());
        double z = MathHelper.lerp(partialTicks, entity.zo, entity.getZ());

        callback.accept(new Vector3d(x, y, z));
    }
}