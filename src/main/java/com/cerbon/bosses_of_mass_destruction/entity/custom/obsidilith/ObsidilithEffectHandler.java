package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class ObsidilithEffectHandler {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;

    private final ClientParticleBuilder burstParticleFactory = new ClientParticleBuilder(BMDParticles.ENCHANT.get())
            .color(BMDColors.ORANGE)
            .colorVariation(0.2);

    private final ClientParticleBuilder waveParticleFactory = new ClientParticleBuilder(BMDParticles.ENCHANT.get())
            .color(BMDColors.RED)
            .colorVariation(0.2);

    private final ClientParticleBuilder spikeParticleFactory = new ClientParticleBuilder(BMDParticles.ENCHANT.get())
            .color(BMDColors.COMET_BLUE)
            .colorVariation(0.2);

    private final ClientParticleBuilder anvilParticleFactory = new ClientParticleBuilder(BMDParticles.ENCHANT.get())
            .color(BMDColors.ENDER_PURPLE)
            .colorVariation(0.2);

    private final ClientParticleBuilder teleportFactory = new ClientParticleBuilder(BMDParticles.DOWNSPARKLE.get())
            .color(BMDColors.ENDER_PURPLE)
            .brightness(BMDParticles.FULL_BRIGHT)
            .age(() -> RandomUtils.range(25, 30))
            .colorVariation(0.2);

    private static final ClientParticleBuilder deathParticleFactory = new ClientParticleBuilder(BMDParticles.DOWNSPARKLE.get())
            .color(age -> MathUtils.lerpVec(age, BMDColors.ENDER_PURPLE, BMDColors.WHITE))
            .colorVariation(0.2)
            .brightness(BMDParticles.FULL_BRIGHT)
            .age(RandomUtils.range(35, 40))
            .scale(f -> (float) (Math.sin((double) f * Math.PI * 0.5) + 1f) * 0.1f);

    public ObsidilithEffectHandler(LivingEntity entity, EventScheduler eventScheduler){
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public void handleStatus(byte status){
        switch (status){
            case ObsidilithUtils.burstAttackStatus: burstEffect();
            case ObsidilithUtils.waveAttackStatus: waveEffect();
            case ObsidilithUtils.spikeAttackStatus: spikeEffect();
            case ObsidilithUtils.anvilAttackStatus: anvilEffect();
            case ObsidilithUtils.deathStatus: deathEffect();
        }
    }

    private void burstEffect(){
        Vector3d entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 50; i++){
            Vector3d pos = entityPos.add(RandomUtils.randVec().normalize().scale(3.0));
            Vector3d vel = MathUtils.unNormedDirection(pos, entityPos).cross(VecUtils.yAxis).scale(0.1);
            burstParticleFactory.build(pos, vel);
        }
    }

    private void waveEffect(){
        Vector3d entityPos = entity.position();
        for (int i = 0; i <= 50; i++){
            Vector3d randomYOffset = VecUtils.yAxis.scale(entity.getRandom().nextDouble());
            Vector3d randomYVel = VecUtils.yAxis.scale(entity.getRandom().nextDouble());

            Vector3d pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(3.0)).add(randomYOffset);
            waveParticleFactory.continuousVelocity(simpleParticle ->
                    MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).reverse().add(randomYVel).scale(0.1)
            ).build(pos, Vector3d.ZERO);
        }
    }

    private void spikeEffect(){
        Vector3d entityPos = entity.position();
        for (int i = 0; i <= 50; i++){
            Vector3d randomYOffset = VecUtils.yAxis.scale(entity.getRandom().nextDouble());
            Vector3d randomYVel = VecUtils.yAxis.scale(entity.getRandom().nextDouble());

            Vector3d pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().scale(3.0)).add(randomYOffset);
            spikeParticleFactory.continuousVelocity(simpleParticle ->
                    MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).add(randomYVel).scale(0.1)
            ).build(pos, Vector3d.ZERO);
        }
    }

    private void anvilEffect(){
        Vector3d entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 50; i++){
            Vector3d pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(3.0, 3.0, 3.0));
            anvilParticleFactory.continuousVelocity(simpleParticle ->
                    MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).add(VecUtils.yAxis.multiply(0.4, 0.4, 0.4)).multiply(0.1, 0.1, 0.1)
            ).build(pos, Vector3d.ZERO);
        }

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vector3d particlePos = entity.position().add(RandomUtils.randVec().scale(3.0));
                            Vector3d vel = entity.getDeltaMovement().scale(0.7);
                            teleportFactory.build(particlePos, vel);
                        },
                        0,
                        80,
                        () -> !entity.isAlive()
                )
        );
    }

    private void deathEffect(){
        Vector3d entityPos = VecUtils.asVec3(entity.blockPosition()).add(0.5, 0.5, 0.5);
        ObsidilithEffectHandler.spawnPillarParticles(entityPos, eventScheduler);
    }

    public static void spawnPillarParticles(Vector3d entityPos, EventScheduler eventScheduler){
        for (int i = 0; i <= ObsidilithUtils.deathPillarHeight; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> MathUtils.circleCallback(3.0, 30, VecUtils.yAxis, vec3 ->
                                    deathParticleFactory.build(entityPos.add(vec3).add(VecUtils.yAxis.scale(i1)), Vector3d.ZERO)),
                            i * ObsidilithUtils.ticksBetweenPillarLayer
                    )
            );
        }
    }
}
