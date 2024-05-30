package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.damagesource.UnshieldableDamageSource;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendDeltaMovementS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class WaveAction implements IActionWithCooldown {
    private final MobEntity entity;
    private final List<Vector3d> circlePoints;
    private final World level;
    private final EventScheduler eventScheduler;
    private final double riftRadius = 4.0;

    public static final int waveDelay = 20;
    public static final int attackStartDelay = 20;

    public WaveAction(MobEntity entity){
        this.entity = entity;
        this.circlePoints = MathUtils.buildBlockCircle(riftRadius);
        this.level = entity.level;
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
                (ServerWorld) level,
                BMDParticles.OBSIDILITH_WAVE_INDICATOR.get(),
                BMDParticles.OBSIDILITH_WAVE.get(),
                waveDelay,
                eventScheduler,
                this::damageEntity
        );

        BMDUtils.playSound((ServerWorld) level, entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundCategory.HOSTILE, 3.0f, 0.8f, 64, null);
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            Vector3d direction = MathUtils.unNormedDirection(entity.position(), target.position()).normalize().scale(riftRadius);
                            int numRifts = 5;
                            Vector3d startRiftPos = entity.position().add(direction);
                            Vector3d endRiftPos = startRiftPos.add(direction.scale((double) numRifts * 1.5));
                            MathUtils.lineCallback(startRiftPos, endRiftPos, numRifts, (linePos, i) -> eventScheduler.addEvent(
                                    new TimedEvent(
                                            () -> {
                                                BMDUtils.playSound((ServerWorld) level, linePos, BMDSounds.WAVE_INDICATOR.get(), SoundCategory.HOSTILE, 0.7f, 32, null);
                                                eventScheduler.addEvent(
                                                        new TimedEvent(
                                                                () -> BMDUtils.playSound((ServerWorld) level, linePos, BMDSounds.OBSIDILITH_WAVE.get(), SoundCategory.HOSTILE, 1.2f, 32, null),
                                                                waveDelay,
                                                                1,
                                                                () -> !entity.isAlive()
                                                        )
                                                );

                                                for (Vector3d point : circlePoints)
                                                    riftBurst.tryPlaceRift(linePos.add(point));
                                            },
                                            i * 8,
                                            1,
                                            () -> !entity.isAlive()
                                    )
                            ));
                        },
                        attackStartDelay,
                        1,
                        () -> !entity.isAlive()
                )
        );
    }

    private void damageEntity(LivingEntity livingEntity){
        float damage = (float) this.entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (livingEntity instanceof ServerPlayerEntity)
            BMDPacketHandler.sendToPlayer(new SendDeltaMovementS2CPacket(new Vector3d(livingEntity.getDeltaMovement().x, 0.8, livingEntity.getDeltaMovement().z)), (ServerPlayerEntity) livingEntity);
        livingEntity.setSecondsOnFire(5);
        livingEntity.hurt(new UnshieldableDamageSource(this.entity), damage);
    }
}
