package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public class SporeBallSizeRenderer implements IRenderer<SporeBallProjectile> {

    public SporeBallSizeRenderer(){}

    @Override
    public void render(
            SporeBallProjectile entity,
            float yaw,
            float partialTicks,
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int light
    ) {
        float deathProgress = (!entity.impacted || entity.removed) ? 0f : ((entity.impactedTicks + partialTicks) / SporeBallProjectile.explosionDelay) * 0.5f;
        float scaledDeathProgress = (float) (Math.pow(deathProgress, 2f) + 1);
        poseStack.scale(scaledDeathProgress, scaledDeathProgress, scaledDeathProgress);
    }
}
