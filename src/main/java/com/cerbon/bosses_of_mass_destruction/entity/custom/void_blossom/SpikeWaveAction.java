package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.HitboxId;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SpikeS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class SpikeWaveAction implements IActionWithCooldown {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    private final List<Vec3> firstCirclePoints = MathUtils.buildBlockCircle(7.0);
    private final double secondRadius = 14.0;
    private final List<Vec3> secondCirclePoints;
    private final double thirdRadius = 21.0;
    private final List<Vec3> thirdCirclePoints;

    public static final int indicatorDelay = 30;

    public SpikeWaveAction(VoidBlossomEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;

        this.secondCirclePoints = MathUtils.buildBlockCircle(secondRadius);
        this.secondCirclePoints.removeAll(firstCirclePoints);

        this.thirdCirclePoints = MathUtils.buildBlockCircle(thirdRadius);
        thirdCirclePoints.removeAll(secondCirclePoints);
        thirdCirclePoints.removeAll(firstCirclePoints);

    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return 80;
        placeRifts((ServerPlayer) target);
        return 120;
    }

    private void placeRifts(ServerPlayer target){
        int firstBurstDelay = 20;
        int secondBurstDelay = 45;
        int thirdBurstDelay = 70;
        ServerLevel level = target.getLevel();

        eventScheduler.addEvent(
                new EventSeries(
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.SpikeWave1.getId()),
                                20,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.SpikeWave2.getId()),
                                26,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.SpikeWave3.getId()),
                                26,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Idle.getId()),
                                26,
                                1,
                                () -> false
                        )
                )
        );

        Spikes spikeGenerator = new Spikes(
                entity,
                level,
                BMDParticles.VOID_BLOSSOM_SPIKE_WAVE_INDICATOR.get(),
                indicatorDelay,
                eventScheduler,
                shouldCancel
        );

        createSpikeWave(
                () -> {
                    createBurst(spikeGenerator, firstCirclePoints);
                    BMDUtils.playSound(level, entity.position(), BMDSounds.SPIKE_WAVE_INDICATOR.get(), SoundSource.HOSTILE, 2.0f, 0.7f, 64, null);
                },
                () -> BMDUtils.playSound(level, entity.position(), BMDSounds.VOID_BLOSSOM_SPIKE.get(), SoundSource.HOSTILE, 1.2f, 64, null),
                firstBurstDelay
        );

        createSpikeWave(
                () -> {
                    createBurst(spikeGenerator, secondCirclePoints);
                    playSoundsInRadius(level, secondRadius, BMDSounds.SPIKE_WAVE_INDICATOR.get(), 2.0f, 0.7f);
                },
                () -> playSoundsInRadius(level, secondRadius, BMDSounds.VOID_BLOSSOM_SPIKE.get(),1.2f, BMDUtils.randomPitch(entity.getRandom())),
                secondBurstDelay
        );

        createSpikeWave(
                () -> {
                    createBurst(spikeGenerator, thirdCirclePoints);
                    playSoundsInRadius(level, thirdRadius, BMDSounds.SPIKE_WAVE_INDICATOR.get(), 2.0f, 0.7f);
                },
                () -> playSoundsInRadius(level, thirdRadius, BMDSounds.VOID_BLOSSOM_SPIKE.get(),1.2f, BMDUtils.randomPitch(entity.getRandom())),
                thirdBurstDelay
        );
    }

    private void createSpikeWave(Runnable indicationStageHandler, Runnable spikeStageHandler, int burstDelay){
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            indicationStageHandler.run();
                            eventScheduler.addEvent(new TimedEvent(spikeStageHandler, indicatorDelay, 1, shouldCancel));
                        },
                        burstDelay,
                        1,
                        shouldCancel
                )
        );
    }

    private void playSoundsInRadius(ServerLevel level, double radius, SoundEvent soundEvent, float volume, float pitch){
        for (Direction dir : Direction.Plane.HORIZONTAL){
            Vec3 pos = entity.position().add(new Vec3(dir.step()).scale(radius));
            BMDUtils.playSound(level, pos, soundEvent, SoundSource.HOSTILE, volume, pitch, 64, null);
        }
    }

    private void createBurst(Spikes spikesGenerator, List<Vec3> positions){
        List<BlockPos> placedPositions = positions.stream().flatMap(vec3 -> spikesGenerator.tryPlaceRift(entity.position().add(vec3)).stream()).toList();

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> BMDPacketHandler.sendToAllPlayersTrackingChunk(new SpikeS2CPacket(entity.getId(), placedPositions), (ServerLevel) entity.level, entity.position()),
                        indicatorDelay,
                        1,
                        shouldCancel
                )
        );
    }
}
