package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopies;
import com.cerbon.cerbons_api.api.static_utilities.Vec3Colors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class GauntletLaserRenderer implements IRenderer<GauntletEntity> {
    private final ResourceLocation laserTexture = new ResourceLocation(BMDConstants.MOD_ID, "textures/entity/gauntlet_beam.png");
    private final RenderType type = RenderType.entityCutoutNoCull(laserTexture);

    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (entity.laserHandler.shouldRenderLaser()){
            Pair<Vec3, Vec3> beamPos = entity.laserHandler.getLaserRenderPos();
            VanillaCopies.renderBeam(
                    entity,
                    beamPos.getFirst(),
                    beamPos.getSecond(),
                    partialTicks,
                    Vec3Colors.LASER_RED,
                    poseStack,
                    buffer,
                    type
            );
        }
    }
}
