package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.general.particle.ClientParticleBuilder;
import com.cerbon.cerbons_api.api.static_utilities.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class GauntletBlindnessIndicatorParticles implements IEntityEventHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    private final ClientParticleBuilder particleBuilder = new ClientParticleBuilder(BMDParticles.EYE_OPEN.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(f -> MathUtils.lerpVec(f, Vec3Colors.WHITE, Vec3Colors.LASER_RED));

    private final ClientParticleBuilder gauntletParticleBuilder = new ClientParticleBuilder(BMDParticles.SOUL_FLAME.get())
            .color(Vec3Colors.DARK_RED)
            .brightness(BMDParticles.FULL_BRIGHT)
            .scale(0.25f)
            .age(20);

    public GauntletBlindnessIndicatorParticles(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    public void handlePlayerEffects(List<Player> players){
        for (Player player : players)
            spawnRotatingParticles(player, particleBuilder);
    }

    private void spawnRotatingParticles(Entity player, ClientParticleBuilder particleBuilder){
        for (int i = 0; i <= 20; i++){
            int i1 = i;
            int startingRotation = new Random().nextInt(360);
            particleBuilder
                    .continuousPosition(simpleParticle -> calculatePositions(player, i1, simpleParticle.getAge(), startingRotation))
                    .build(calculatePositions(player, i1, 0, startingRotation), Vec3.ZERO);
        }
    }

    private Vec3 calculatePositions(Entity player, int i, int age, int startingRotation){
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
                                    Vec3 particlePos = MobUtils.eyePos(entity).add(RandomUtils.randVec().normalize().scale(2.0));
                                    Vec3 particleVel = RandomUtils.randVec().scale(0.05).add(VecUtils.yAxis.scale(0.05));
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
