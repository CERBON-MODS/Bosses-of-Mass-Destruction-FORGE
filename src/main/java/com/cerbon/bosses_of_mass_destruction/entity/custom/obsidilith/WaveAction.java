package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendDeltaMovementS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WaveAction implements IActionWithCooldown {
    private final Mob entity;
    private final double riftRadius = 4.0;
    private final List<Vec3> circlePoints;
    private final Level level;
    private final EventScheduler eventScheduler;

    public static final int waveDelay = 40;
    public static final int attackStartDelay = 40;

    public WaveAction(Mob entity){
        this.entity = entity;
        this.circlePoints = MathUtils.buildBlockCircle(riftRadius);
        this.level = entity.level();
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(level);
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        placeRifts(target);
        return 80;
    }

    private void placeRifts(LivingEntity target){
        RiftBurst riftBurst = new RiftBurst(
                entity,
                (ServerLevel) level,
                BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                BMDParticles.OBSIDILITH_WAVE.get(),
                waveDelay,
                eventScheduler,
                this::damageEntity
        );

        BMDUtils.playSound((ServerLevel) level, entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundSource.HOSTILE, 3.0f, 0.8f, 64, null);
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vec3 direction = MathUtils.unNormedDirection(entity.position(), target.position()).normalize().multiply(riftRadius, riftRadius, riftRadius);
                            int numRifts = 5;
                            Vec3 startRiftPos = entity.position().add(direction);
                            Vec3 endRiftPos = startRiftPos.add(direction.multiply((double) numRifts * 1.5, (double) numRifts * 1.5, (double) numRifts * 1.5));
                            MathUtils.lineCallback(startRiftPos, endRiftPos, numRifts, (linePos, i) -> {
                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> {
                                                    BMDUtils.playSound((ServerLevel) level, linePos, BMDSounds.WAVE_INDICATOR.get(), SoundSource.HOSTILE, 0.7f, 32, null);
                                                    eventScheduler.addEvent(
                                                            new TimedEvent(
                                                                    () -> BMDUtils.playSound((ServerLevel) level, linePos, BMDSounds.OBSIDILITH_WAVE.get(), SoundSource.HOSTILE, 1.2f, 32, null),
                                                                    waveDelay,
                                                                    1,
                                                                    () -> !entity.isAlive()
                                                            )
                                                    );

                                                    for (Vec3 point : circlePoints)
                                                        riftBurst.tryPlaceRift(linePos.add(point));
                                                },
                                                i * 8 * 2,
                                                1,
                                                () -> !entity.isAlive()
                                        )
                                );
                            });
                        },
                        attackStartDelay,
                        1,
                        () -> !entity.isAlive()
                )
        );
    }

    private void damageEntity(LivingEntity livingEntity){
        float damage = (float) this.entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (livingEntity instanceof ServerPlayer serverPlayer)
            BMDPacketHandler.sendToPlayer(new SendDeltaMovementS2CPacket(new Vec3(livingEntity.getDeltaMovement().x, 0.8, livingEntity.getDeltaMovement().z)), serverPlayer);
        livingEntity.setSecondsOnFire(5);
        livingEntity.hurt(BMDUtils.shieldPiercing(livingEntity.level(), this.entity), damage);
    }
}
