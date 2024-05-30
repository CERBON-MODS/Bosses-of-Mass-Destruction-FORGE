package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.client.render.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class LaserParticleRenderer implements IRenderer<GauntletEntity> {
    @Override
    public void render(GauntletEntity entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int light) {
        if (entity.laserHandler.shouldRenderLaser()){
            Pair<Vector3d, Vector3d> beamPos = entity.laserHandler.getLaserRenderPos();
            Vector3d lerpedPos = MathUtils.lerpVec(partialTicks, beamPos.getSecond(), beamPos.getFirst());
            Vector3d beamVel = MathUtils.unNormedDirection(beamPos.getSecond(), beamPos.getFirst()).normalize().scale(0.1);
            Vector3d laserDir = MathUtils.unNormedDirection(MobUtils.eyePos(entity), lerpedPos);
            entity.laserHandler.laserChargeParticles.build(MobUtils.eyePos(entity).add(laserDir.scale(entity.getRandom().nextDouble())), beamVel);
        }
    }
}
