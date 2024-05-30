package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Random;

public class GauntletBlindnessIndicatorParticles implements IEntityEventHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    private final ClientParticleBuilder particleBuilder = new ClientParticleBuilder(BMDParticles.EYE_OPEN.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(f -> MathUtils.lerpVec(f, BMDColors.WHITE, BMDColors.LASER_RED));

    private final ClientParticleBuilder gauntletParticleBuilder = new ClientParticleBuilder(BMDParticles.SOUL_FLAME.get())
            .color(BMDColors.DARK_RED)
            .brightness(BMDParticles.FULL_BRIGHT)
            .scale(0.25f)
            .age(20);

    public GauntletBlindnessIndicatorParticles(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public void handlePlayerEffects(List<PlayerEntity> players){
        for (PlayerEntity player : players)
            spawnRotatingParticles(player, particleBuilder);
    }

    private void spawnRotatingParticles(Entity player, ClientParticleBuilder particleBuilder){
        for (int i = 0; i <= 20; i++){
            int i1 = i;
            int startingRotation = new Random().nextInt(360);
            particleBuilder
                    .continuousPosition(simpleParticle -> calculatePositions(player, i1, simpleParticle.getAge(), startingRotation))
                    .build(calculatePositions(player, i1, 0, startingRotation), Vector3d.ZERO);
        }
    }

    private Vector3d calculatePositions(Entity player, int i, int age, int startingRotation){
        return player.position().add(VecUtils.yAxis.scale(i * 0.1)).add(
                VecUtils.xAxis.yRot(
                        (float) Math.toRadians(age * 2 + startingRotation)
                )
        );
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == GauntletAttacks.blindnessAttack){
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                for (int i = 0; i <= 3; i++){
                                    Vector3d particlePos = MobUtils.eyePos(entity).add(RandomUtils.randVec().normalize().scale(2.0));
                                    Vector3d particleVel = RandomUtils.randVec().scale(0.05).add(VecUtils.yAxis.scale(0.05));
                                    gauntletParticleBuilder.build(particlePos, particleVel);
                                }
                            },
                            0,
                            10,
                            () -> false
                    )
            );
        }

    }
}
