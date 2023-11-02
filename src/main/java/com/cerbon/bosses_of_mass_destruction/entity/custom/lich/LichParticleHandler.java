package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.particle.ParticleFactories;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.cerbon.bosses_of_mass_destruction.util.CollectionUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class LichParticleHandler implements IEntityEventHandler, IEntityTick<ClientLevel> {
    private final LichEntity entity;
    private final EventScheduler eventScheduler;
    private final ClientParticleBuilder summonMissileParticleBuilder;
    private final ClientParticleBuilder teleportParticleBuilder;
    private final ClientParticleBuilder summonCometParticleBuilder;
    private final ClientParticleBuilder flameRingFactory;
    private final ClientParticleBuilder minionSummonParticleBuilder;
    private final ClientParticleBuilder thresholdParticleBuilder;
    private final ClientParticleBuilder summonRingFactory;
    private final ClientParticleBuilder summonRingCompleteFactory;
    private final ClientParticleBuilder deathParticleFactory;
    private final ClientParticleBuilder idleParticles;

    public LichParticleHandler(LichEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;

        this.summonMissileParticleBuilder = ParticleFactories.soulFlame().age(2).colorVariation(0.5);
        this.teleportParticleBuilder = new ClientParticleBuilder(BMDParticles.DISAPPEARING_SWIRL.get())
                .color(BMDColors.TELEPORT_PURPLE)
                .age(10, 15)
                .brightness(BMDParticles.FULL_BRIGHT);
        this.summonCometParticleBuilder = ParticleFactories.cometTrail().colorVariation(0.5);
        this.flameRingFactory = ParticleFactories.soulFlame()
                .color(t -> MathUtils.lerpVec(t, BMDColors.WHITE, BMDColors.WHITE.multiply(0.5, 0.5, 0.5)))
                .age(0, 7);
        this.minionSummonParticleBuilder = ParticleFactories.soulFlame()
                .color(BMDColors.WHITE);
        this.thresholdParticleBuilder = ParticleFactories.soulFlame()
                .age(20)
                .scale(0.5f);
        this.summonRingFactory = ParticleFactories.soulFlame()
                .color(LichUtils.blueColorFade)
                .colorVariation(0.5)
                .age(10);
        this.summonRingCompleteFactory = ParticleFactories.soulFlame()
                .color(BMDColors.WHITE)
                .age(20, 30);
        this.deathParticleFactory = ParticleFactories.soulFlame()
                .color(LichUtils.blueColorFade)
                .age(40, 80)
                .colorVariation(0.5)
                .scale(t -> 0.5f - (t * 0.3f));
        this.idleParticles = ParticleFactories.soulFlame()
                .color(LichUtils.blueColorFade)
                .age(30, 40)
                .colorVariation(0.5)
                .scale(t -> 0.25f - (t * 0.1f));
    }

    @Override
    public void tick(ClientLevel level) {
        if (entity.getRandom().nextDouble() > 0.9)
            idleParticles.build(entity.position().subtract(VecUtils.yAxis).add(RandomUtils.randVec().multiply(0.2, 0.2, 0.2)), entity.getDeltaMovement());
    }

    @Override
    public void handleEntityEvent(byte status) {
        switch (status) {
            case LichActions.cometAttack:
                cometEffect();
                break;
            case LichActions.volleyAttack:
                volleyEffect();
                break;
            case LichActions.minionAttack:
                minionEffect();
                break;
            case LichActions.minionRageAttack:
                minionRageEffect();
                break;
            case LichActions.teleportAction:
                teleportEffect();
                break;
            case LichActions.endTeleport:
                endTeleportEffect();
                break;
            case LichActions.cometRageAttack:
                cometRageEffect();
                break;
            case LichActions.volleyRageAttack:
                volleyRageEffect();
                break;
            case LichActions.hpBelowThresholdStatus:
                hpThresholdEffect();
                break;
            case 3:
                deathEffect();
                break;
        }
    }

    private void cometEffect(){
        eventScheduler.addEvent(
                new TimedEvent(() ->
                        summonCometParticleBuilder.build(MobUtils.eyePos(entity).add(CometAction.getCometLaunchOffset()), Vec3.ZERO),
                        CometAction.cometParticleSummonDelay,
                        CometAction.cometThrowDelay - CometAction.cometParticleSummonDelay,
                        this::shouldCancelParticles
                )
        );
    }

    private void volleyEffect(){
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            for (Vec3 offset : VolleyAction.getMissileLaunchOffsets(entity)) {
                                summonMissileParticleBuilder.build(MobUtils.eyePos(entity).add(offset), Vec3.ZERO);
                            }
                        },
                        VolleyAction.missileParticleSummonDelay,
                        VolleyAction.missileThrowDelay - VolleyAction.missileParticleSummonDelay,
                        this::shouldCancelParticles
                )
        );
    }

    private void minionEffect() {
        eventScheduler.addEvent(new TimedEvent(
                () -> minionSummonParticleBuilder.build(MobUtils.eyePos(entity)
                        .add(VecUtils.yAxis.multiply(1.0, 1.0, 1.0))
                        .add(VecUtils.planeProject(RandomUtils.randVec(), VecUtils.yAxis)
                                        .normalize()
                                        .multiply(entity.getRandom().nextGaussian(), entity.getRandom().nextGaussian(), entity.getRandom().nextGaussian())),
                        VecUtils.yAxis.multiply(RandomUtils.randomDouble(0.2), RandomUtils.randomDouble(0.2), RandomUtils.randomDouble(0.2))),
                MinionAction.minionSummonDelay,
                MinionAction.minionSummonDelay - MinionAction.minionSummonParticleDelay,
                this::shouldCancelParticles));
    }

    private void minionRageEffect() {
        eventScheduler.addEvent(new TimedEvent(() -> {
            animatedParticleMagicCircle(3.0, 30, 12, 0f);
            animatedParticleMagicCircle(6.0, 60, 24, 120f);
            animatedParticleMagicCircle(9.0, 90, 36, 240f);
        },
                10,
                1,
                this::shouldCancelParticles));
    }

    private void teleportEffect(){
        eventScheduler.addEvent(
                new TimedEvent(this::spawnTeleportParticles,
                        TeleportAction.beginTeleportParticleDelay,
                        TeleportAction.teleportParticleDuration,
                        this::shouldCancelParticles)
        );
    }

    private void endTeleportEffect(){
        eventScheduler.addEvent(
                new TimedEvent(this::spawnTeleportParticles, 1, TeleportAction.teleportParticleDuration, this::shouldCancelParticles)
        );
    }

    private void cometRageEffect(){
        int numComets = CometRageAction.getRageCometOffsets(entity).size();

        for (int i = 0; i < numComets; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(() -> {
                        Vec3 cometOffset = CometRageAction.getRageCometOffsets(entity).get(i1);
                        summonCometParticleBuilder.build(cometOffset.add(MobUtils.eyePos(entity)), Vec3.ZERO);
                    }, i * CometRageAction.delayBetweenRageComets, CometRageAction.initialRageCometDelay, this::shouldCancelParticles)
            );
        }
        eventScheduler.addEvent(
                new TimedEvent(() -> MathUtils.circleCallback(3.0, 72, entity.getLookAngle(), vec3 -> flameRingFactory.build(vec3.add(MobUtils.eyePos(entity)), Vec3.ZERO)),
                        0, CometRageAction.rageCometsMoveDuration, this::shouldCancelParticles)
        );
    }

    private void volleyRageEffect(){
        int numVolleys = VolleyRageAction.getRageMissileVolleys(entity).size();
        for (int i = 0; i < numVolleys; i++){
            int i1 = i;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                for (Vec3 offset : VolleyRageAction.getRageMissileVolleys(entity).get(i1))
                                    summonMissileParticleBuilder.build(MobUtils.eyePos(entity).add(offset), Vec3.ZERO);
                            },
                            VolleyRageAction.ragedMissileParticleDelay + (i1 * VolleyRageAction.ragedMissileVolleyBetweenVolleyDelay),
                            VolleyRageAction.ragedMissileVolleyBetweenVolleyDelay,
                            this::shouldCancelParticles
                    )
            );
        }
    }

    private void hpThresholdEffect(){
        for (int i = 0; i < 20; i++)
            thresholdParticleBuilder.build(MobUtils.eyePos(entity), RandomUtils.randVec());
    }

    private void deathEffect(){
        eventScheduler.addEvent(
                new TimedEvent(()-> {
                    for (int i = 0; i <=4; i++)
                        deathParticleFactory.build(MobUtils.eyePos(entity), RandomUtils.randVec());
                }, 0, 10, () -> false)
        );
    }

    private void spawnTeleportParticles(){
        teleportParticleBuilder.build(
                MobUtils.eyePos(entity).add(RandomUtils.randVec().multiply(3.0, 3.0, 3.0)),
                Vec3.ZERO);
    }

    // TODO: Test if it's working correctly
    private void animatedParticleMagicCircle(double radius, int points, int time, float rotationDegrees){
        Vec3 spellPos = entity.position();
        Collection<Vec3> circlePoints = MathUtils.circlePoints(radius, points, entity.getLookAngle());
        float timeScale = time / (float) points;

        Collection<Vec3> newCirclePoints = CollectionUtils.mapIndexed(circlePoints, (index, off) -> {
            eventScheduler.addEvent(new TimedEvent(() -> {
                off.yRot(rotationDegrees);
                summonRingFactory.build(off.add(spellPos), Vec3.ZERO);
            }, (int) (index * timeScale)));
            return off;
        });

        eventScheduler.addEvent(new TimedEvent(() -> newCirclePoints.stream().map(vec3 -> {
            summonRingCompleteFactory.build(vec3.add(spellPos), Vec3.ZERO);
            return vec3;
        }), (int) (points * timeScale)));
    }

    private boolean shouldCancelParticles(){
        return !entity.isAlive();
    }
}
