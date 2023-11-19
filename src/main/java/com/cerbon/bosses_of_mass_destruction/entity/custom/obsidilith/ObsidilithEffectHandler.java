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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
            case ObsidilithUtils.burstAttackStatus -> burstEffect();
            case ObsidilithUtils.waveAttackStatus -> waveEffect();
            case ObsidilithUtils.spikeAttackStatus -> spikeEffect();
            case ObsidilithUtils.anvilAttackStatus -> anvilEffect();
            case ObsidilithUtils.deathStatus -> deathEffect();
        }
    }

    private void burstEffect(){
        Vec3 entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 50; i++){
            Vec3 pos = entityPos.add(RandomUtils.randVec().normalize().multiply(3.0, 3.0, 3.0));
            Vec3 vel = MathUtils.unNormedDirection(pos, entityPos).cross(VecUtils.yAxis).multiply(0.1, 0.1, 0.1);
            burstParticleFactory.build(pos, vel);
        }
    }

    private void waveEffect(){
        Vec3 entityPos = entity.position();
        for (int i = 0; i <= 50; i++){
            Vec3 randomYOffset = VecUtils.yAxis.multiply(entity.getRandom().nextDouble(), entity.getRandom().nextDouble(), entity.getRandom().nextDouble());
            Vec3 randomYVel = VecUtils.yAxis.multiply(entity.getRandom().nextDouble(), entity.getRandom().nextDouble(), entity.getRandom().nextDouble());

            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(3.0, 3.0, 3.0)).add(randomYOffset);
            waveParticleFactory.continuousVelocity(
                    simpleParticle -> MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).reverse().add(randomYVel).multiply(0.1, 0.1, 0.1)
            ).build(pos, Vec3.ZERO);
        }
    }

    private void spikeEffect(){
        Vec3 entityPos = entity.position();
        for (int i = 0; i <= 50; i++){
            Vec3 randomYOffset = VecUtils.yAxis.multiply(entity.getRandom().nextDouble(), entity.getRandom().nextDouble(), entity.getRandom().nextDouble());
            Vec3 randomYVel = VecUtils.yAxis.multiply(entity.getRandom().nextDouble(), entity.getRandom().nextDouble(), entity.getRandom().nextDouble());

            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(3.0, 3.0, 3.0)).add(randomYOffset);
            spikeParticleFactory.continuousVelocity(
                    simpleParticle -> MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).add(randomYVel).multiply(0.1, 0.1, 0.1)
            ).build(pos, Vec3.ZERO);
        }
    }

    private void anvilEffect(){
        Vec3 entityPos = MobUtils.eyePos(entity);
        for (int i = 0; i <= 50; i++){
            Vec3 pos = entityPos.add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis).normalize().multiply(3.0, 3.0, 3.0));
            anvilParticleFactory.continuousVelocity(
                    simpleParticle -> MathUtils.unNormedDirection(simpleParticle.getPos(), entityPos).cross(VecUtils.yAxis).add(VecUtils.yAxis.multiply(0.4, 0.4, 0.4)).multiply(0.1, 0.1, 0.1)
            ).build(pos, Vec3.ZERO);
        }

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 particlePos = entity.position().add(RandomUtils.randVec().multiply(3.0, 3.0, 3.0));
                            Vec3 vel = entity.getDeltaMovement().multiply(0.7, 0.7, 0.7);
                            teleportFactory.build(particlePos, vel);
                        },
                        0,
                        80,
                        () -> !entity.isAlive()
                )
        );
    }

    private void deathEffect(){
        Vec3 entityPos = VecUtils.asVec3(entity.blockPosition()).add(0.5, 0.5, 0.5);
        ObsidilithEffectHandler.spawnPillarParticles(entityPos, eventScheduler);
    }

    public static void spawnPillarParticles(Vec3 entityPos, EventScheduler eventScheduler){
        for (int i = 0; i <= ObsidilithUtils.deathPillarHeight; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> MathUtils.circleCallback(3.0, 30, VecUtils.yAxis, vec3 ->
                                    deathParticleFactory.build(entityPos.add(vec3).add(VecUtils.yAxis.multiply(i1, i1, i1)), Vec3.ZERO)),
                            i * ObsidilithUtils.ticksBetweenPillarLayer
                    )
            );
        }
    }



}
