package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithUtils;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.HitboxId;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.hitbox.NetworkedHitboxManager;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SpikeS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class SpikeAction implements IActionWithCooldown {
    private final VoidBlossomEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> shouldCancel;

    private final List<Vec3> circlePoints = MathUtils.buildBlockCircle(2.0);

    public static final int indicatorDelay = 20;

    public SpikeAction(VoidBlossomEntity entity, EventScheduler eventScheduler, Supplier<Boolean> shouldCancel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return 80;
        placeSpikes((ServerPlayer) target);
        return 150;
    }

    private void placeSpikes(ServerPlayer target){
        Spikes riftBurst = new Spikes(
                entity,
                target.getLevel(),
                BMDParticles.VOID_BLOSSOM_SPIKE_INDICATOR.get(),
                indicatorDelay,
                eventScheduler,
                shouldCancel
        );

        BMDUtils.playSound(
                target.getLevel(),
                entity.position(),
                BMDSounds.VOID_BLOSSOM_BURROW.get(),
                SoundSource.HOSTILE,
                1.5f,
                32,
                null
        );

        eventScheduler.addEvent(
                new EventSeries(
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Spike.getId()),
                                20,
                                1,
                                shouldCancel
                        ),
                        new TimedEvent(
                                () -> entity.getEntityData().set(NetworkedHitboxManager.hitbox, HitboxId.Idle.getId()),
                                100
                        )
                )
        );

        for (int i = 0; i < 3; i++){
            int timeBetweenRifts = 30;
            int initialDelay = 30;
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vec3 placement = ObsidilithUtils.approximatePlayerNextPosition(BMDCapabilities.getPlayerPositions(target), target.position());
                                BMDUtils.playSound(
                                        target.getLevel(),
                                        placement,
                                        BMDSounds.VOID_SPIKE_INDICATOR.get(),
                                        SoundSource.HOSTILE,
                                        1.0f,
                                        32,
                                        null
                                );

                                List<BlockPos> successfulSpikes = circlePoints.stream()
                                        .flatMap(point -> riftBurst.tryPlaceRift(placement.add(point)).stream())
                                        .toList();

                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> {
                                                    BMDUtils.playSound(
                                                            target.getLevel(),
                                                            placement,
                                                            BMDSounds.VOID_BLOSSOM_SPIKE.get(),
                                                            SoundSource.HOSTILE,
                                                            1.2f,
                                                            32,
                                                            null
                                                    );
                                                    BMDPacketHandler.sendToAllPlayersTrackingChunk(new SpikeS2CPacket(entity.getId(), successfulSpikes), (ServerLevel) entity.level, entity.position());
                                                },
                                                indicatorDelay,
                                                1,
                                                shouldCancel
                                        )
                                );

                            },
                            initialDelay + i * timeBetweenRifts,
                            1,
                            shouldCancel
                    )
            );
        }
    }
}
