package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class LaserParticleRenderer implements IRenderer<GauntletEntity> {
    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        if (entity.laserHandler.shouldRenderLaser()){
            Pair<Vec3, Vec3> beamPos = entity.laserHandler.getLaserRenderPos();
            Vec3 lerpedPos = MathUtils.lerpVec(partialTicks, beamPos.getSecond(), beamPos.getFirst());
            Vec3 beamVel = MathUtils.unNormedDirection(beamPos.getSecond(), beamPos.getFirst()).normalize().scale(0.1);
            Vec3 laserDir = MathUtils.unNormedDirection(MobUtils.eyePos(entity), lerpedPos);
            entity.laserHandler.laserChargeParticles.build(MobUtils.eyePos(entity).add(laserDir.scale(entity.getRandom().nextDouble())), beamVel);
        }
    }
}
