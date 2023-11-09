package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpikeAction implements IActionWithCooldown {
    private final Mob entity;
    private final EventScheduler eventScheduler;
    private final List<Vec3> circlePoints;

    public SpikeAction(Mob entity){
        this.entity = entity;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level());
        this.circlePoints = MathUtils.buildBlockCircle(2.0);
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayer)) return 80;
        placeSpikes((ServerPlayer) target);
        return 100;
    }

    private void placeSpikes(ServerPlayer target){
        int riftTime = 20;
        RiftBurst riftBurst = new RiftBurst(
                entity,
                target.serverLevel(),
                BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                BMDParticles.OBSIDILITH_SPIKE.get(),
                riftTime,
                eventScheduler,
                this::damageEntity
        );

        BMDUtils.playSound(target.serverLevel(), entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundSource.HOSTILE, 3.0f, 1.2f, 64, null);

        for(int i = 0; i < 3; i++){
            int timeBetweenRifts = 30;
            int initialDelay = 30;

            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vec3 placement = ObsidilithUtils.approximatePlayerNextPosition(BMDCapabilities.getPlayerPositions(target), target.position());
                                BMDUtils.playSound(target.serverLevel(), placement, BMDSounds.SPIKE_INDICATOR.get(), SoundSource.HOSTILE, 1.0f, 32, null);

                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> {
                                                    BMDUtils.playSound(
                                                            target.serverLevel(),
                                                            placement,
                                                            BMDSounds.SPIKE.get(),
                                                            SoundSource.HOSTILE,
                                                            1.2f,
                                                            32,
                                                            null
                                                    );
                                                },
                                                riftTime,
                                                1,
                                                () -> !entity.isAlive()
                                        )
                                );

                                for (Vec3 point : circlePoints)
                                    riftBurst.tryPlaceRift(placement.add(point));
                            },
                            initialDelay + i * timeBetweenRifts,
                            1,
                            () -> !entity.isAlive()
                    )
            );
        }
    }

    private void damageEntity(LivingEntity entity){
        float damage = (float) this.entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        entity.hurt(BMDUtils.shieldPiercing(entity.level(), this.entity), damage);
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
    }
}
