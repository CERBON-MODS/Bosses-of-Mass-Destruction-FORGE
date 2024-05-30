package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class PetalBladeParticleRenderer<T extends Entity> implements IRenderer<T> {
    private final ClientParticleBuilder petalParticleFactory = new ClientParticleBuilder(BMDParticles.PETAL.get())
            .color(f -> f < 0.7
                    ? MathUtils.lerpVec(f, BMDColors.PINK, new Vector3d(1.0, 0.85, 0.95))
                    : MathUtils.lerpVec(f, new Vector3d(1.0, 0.85, 0.95), BMDColors.ULTRA_DARK_PURPLE))
            .brightness(BMDParticles.FULL_BRIGHT)
            .colorVariation(0.15)
            .scale(f -> (float) RandomUtils.range(0.1, 0.2) * (1 - f * 0.25f));

    @Override
    public void render(
            T entity,
            float yaw,
            float partialTicks,
            MatrixStack poseStack,
            IRenderTypeBuffer buffer,
            int light
    ) {
        Vector3d prevPos = new Vector3d(entity.xo, entity.yo, entity.zo);
        Vector3d pos = MathUtils.lerpVec(partialTicks, prevPos, entity.position());
        Vector3d dir = entity.position().subtract(prevPos).normalize();

        int randomRot = RandomUtils.range(0, 360);
        float angularMomentum = RandomUtils.randSign() * 4f;
        petalParticleFactory
                .continuousRotation(particle -> randomRot + particle.getAge() * angularMomentum)
                .build(
                        pos.add(RandomUtils.randVec().scale(0.25)),
                        VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis)
                                .subtract(VecUtils.yAxis).normalize()
                                .scale(0.1)
                                .add(dir.scale(0.05))
                );
    }
}
