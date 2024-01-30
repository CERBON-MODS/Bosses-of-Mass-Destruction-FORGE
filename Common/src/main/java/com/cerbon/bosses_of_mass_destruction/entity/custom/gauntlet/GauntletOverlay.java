package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IOverlayOverride;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GauntletOverlay implements IRenderer<GauntletEntity>, IOverlayOverride {
    private GauntletEntity entity;
    private float partialTicks;

    @Override
    public int getOverlay() {
        if (entity == null)
            return OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(false));

        float partialTicks = (this.partialTicks != 0f) ? this.partialTicks : 0f;
        float deathProgress = entity.deathTime == 0 ? 0f : (entity.deathTime + partialTicks) / ServerGauntletDeathHandler.deathAnimationTime;
        float flash = (float) Math.sin((double) deathProgress * 50) * 0.1f + deathProgress * 0.9f;
        return OverlayTexture.pack(OverlayTexture.u(flash), OverlayTexture.v(entity.hurtTime > 0));
    }

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }
}
