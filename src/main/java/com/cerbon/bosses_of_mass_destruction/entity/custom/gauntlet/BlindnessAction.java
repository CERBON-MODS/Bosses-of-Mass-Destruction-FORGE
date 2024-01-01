package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.BlindnessS2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BlindnessAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> cancelAction;
    private final ServerLevel serverLevel;

    public BlindnessAction(GauntletEntity entity, EventScheduler eventScheduler, Supplier<Boolean> cancelAction, ServerLevel serverLevel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.cancelAction = cancelAction;
        this.serverLevel = serverLevel;
    }

    @Override
    public int perform() {
        SoundUtils.playSound(
                serverLevel,
                entity.position(),
                BMDSounds.GAUNTLET_CAST.get(),
                SoundSource.HOSTILE,
                3.0f,
                1.0f,
                64,
                null
        );

        eventScheduler.addEvent(new TimedEvent(entity.hitboxHelper::setClosedFistHitbox, 10, 1, cancelAction));
        eventScheduler.addEvent(new TimedEvent(entity.hitboxHelper::setOpenHandHitbox, 43));

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            List<Player> players = entity.level().getNearbyPlayers(
                                    TargetingConditions.forCombat().range(64),
                                    entity,
                                    new AABB(entity.position(), entity.position()).inflate(64.0, 32.0, 64.0)
                            );

                            if (!players.isEmpty()){
                                BMDPacketHandler.sendToAllPlayersTrackingChunk(new BlindnessS2CPacket(entity.getId(), players.stream().flatMapToInt(player -> IntStream.of(player.getId())).toArray()), serverLevel, entity.position());
                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> players.forEach(player -> player.addEffect(
                                                        new MobEffectInstance(MobEffects.BLINDNESS, 140))),
                                                50,
                                                1,
                                                cancelAction
                                        )
                                );
                            }
                        },
                        30,
                        1,
                        cancelAction
                )
        );
        return 80;
    }
}
