package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class GauntletLaserRenderer implements IRenderer<GauntletEntity> {
    private final ResourceLocation laserTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet_beam.png");
    private final RenderType type = RenderType.entityCutoutNoCull(laserTexture);

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        if (entity.laserHandler.shouldRenderLaser()){
            Pair<Vector3d, Vector3d> beamPos = entity.laserHandler.getLaserRenderPos();
            VanillaCopies.renderBeam(
                    entity,
                    beamPos.getFirst(),
                    beamPos.getSecond(),
                    partialTicks,
                    BMDColors.LASER_RED,
                    poseStack,
                    buffer,
                    type
            );
        }
    }
}
