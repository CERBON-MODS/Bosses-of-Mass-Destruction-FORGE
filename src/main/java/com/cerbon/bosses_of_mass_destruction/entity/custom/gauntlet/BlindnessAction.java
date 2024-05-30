package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.BlindnessS2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BlindnessAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> cancelAction;
    private final ServerWorld serverLevel;

    public BlindnessAction(GauntletEntity entity, EventScheduler eventScheduler, Supplier<Boolean> cancelAction, ServerWorld serverLevel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.cancelAction = cancelAction;
        this.serverLevel = serverLevel;
    }

    @Override
    public int perform() {
        BMDUtils.playSound(
                serverLevel,
                entity.position(),
                BMDSounds.GAUNTLET_CAST.get(),
                SoundCategory.HOSTILE,
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
                            List<PlayerEntity> players = entity.level.getNearbyPlayers(
                                    EntityPredicate.DEFAULT.range(64),
                                    entity,
                                    new AxisAlignedBB(entity.position(), entity.position()).inflate(64.0, 32.0, 64.0)
                            );

                            if (!players.isEmpty()){
                                BMDPacketHandler.sendToAllPlayersTrackingChunk(new BlindnessS2CPacket(entity.getId(), players.stream().flatMapToInt(player -> IntStream.of(player.getId())).toArray()), serverLevel, entity.position());
                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> players.forEach(player -> player.addEffect(
                                                        new EffectInstance(Effects.BLINDNESS, 140))),
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
