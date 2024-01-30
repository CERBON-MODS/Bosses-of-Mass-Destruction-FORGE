package com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith;

import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendDeltaMovementS2CPacket;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.network.Dispatcher;
import com.cerbon.cerbons_api.api.static_utilities.CapabilityUtils;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BurstAction implements IActionWithCooldown {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;
    private final List<Vec3> circlePoints;

    public static final int burstDelay = 30;

    public BurstAction(LivingEntity entity){
        this.entity = entity;
        this.eventScheduler = CapabilityUtils.getLevelEventScheduler(entity.level());
        this.circlePoints = MathUtils.buildBlockCircle(7.0);
    }

    @Override
    public int perform() {
        placeRifts();
        return 80;
    }

    private void placeRifts(){
        Level level = entity.level();
        RiftBurst riftBurst = new RiftBurst(
                entity,
                (ServerLevel) level,
                BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                BMDParticles.OBSIDILITH_BURST.get(),
                burstDelay,
                eventScheduler,
                this::damageEntity
        );

        SoundUtils.playSound(
                (ServerLevel) level,
                entity.position(),
                BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(),
                SoundSource.HOSTILE,
                3.0f,
                0.7f,
                64,
                null
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> SoundUtils.playSound((ServerLevel) level, entity.position(), BMDSounds.OBSIDILITH_BURST.get(), SoundSource.HOSTILE, 1.2f, 64, null),
                        burstDelay,
                        1,
                        () -> !entity.isAlive()
                )
        );

        for (Vec3 point : circlePoints)
            riftBurst.tryPlaceRift(entity.position().add(point));
    }

    private void damageEntity(LivingEntity livingEntity){
        float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (livingEntity instanceof ServerPlayer serverPlayer)
            Dispatcher.sendToClient(new SendDeltaMovementS2CPacket(new Vec3(livingEntity.getDeltaMovement().x, 1.3, livingEntity.getDeltaMovement().z)), serverPlayer);

        livingEntity.hurt(
                BMDUtils.shieldPiercing(entity.level(), entity),
                damage
        );
    }
}
