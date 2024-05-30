package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.client.render.IOverlayOverride;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class NoRedOnDeathOverlay implements IRenderer<VoidBlossomEntity>, IOverlayOverride {
    private VoidBlossomEntity entity = null;

    @Override
    public int getOverlay() {
        if (entity == null)
            return OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(false));

        return OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(entity.hurtTime > 0 && !entity.isDeadOrDying()));
    }

    @Override
    public void render(VoidBlossomEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        this.entity = entity;
    }
}
