package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.damagesource.UnshieldableDamageSource;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class SpikeAction implements IActionWithCooldown {
    private final MobEntity entity;
    private final EventScheduler eventScheduler;
    private final List<Vector3d> circlePoints;

    public SpikeAction(MobEntity entity){
        this.entity = entity;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level);
        this.circlePoints = MathUtils.buildBlockCircle(2.0);
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (!(target instanceof ServerPlayerEntity)) return 80;
        placeSpikes((ServerPlayerEntity) target);
        return 100;
    }

    private void placeSpikes(ServerPlayerEntity target){
        int riftTime = 20;
        RiftBurst riftBurst = new RiftBurst(
                entity,
                target.getLevel(),
                BMDParticles.OBSIDILITH_SPIKE_INDICATOR.get(),
                BMDParticles.OBSIDILITH_SPIKE.get(),
                riftTime,
                eventScheduler,
                this::damageEntity
        );

        BMDUtils.playSound(target.getLevel(), entity.position(), BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(), SoundCategory.HOSTILE, 3.0f, 1.2f, 64, null);

        for(int i = 0; i < 3; i++){
            int timeBetweenRifts = 30;
            int initialDelay = 30;

            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vector3d placement = ObsidilithUtils.approximatePlayerNextPosition(BMDCapabilities.getPlayerPositions(target), target.position());
                                BMDUtils.playSound(target.getLevel(), placement, BMDSounds.SPIKE_INDICATOR.get(), SoundCategory.HOSTILE, 1.0f, 32, null);

                                eventScheduler.addEvent(
                                        new TimedEvent(
                                                () -> BMDUtils.playSound(
                                                        target.getLevel(),
                                                        placement,
                                                        BMDSounds.SPIKE.get(),
                                                        SoundCategory.HOSTILE,
                                                        1.2f,
                                                        32,
                                                        null
                                                ),
                                                riftTime,
                                                1,
                                                () -> !entity.isAlive()
                                        )
                                );

                                for (Vector3d point : circlePoints)
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
        entity.hurt(new UnshieldableDamageSource(this.entity), damage);
        entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 120, 2));
    }
}
