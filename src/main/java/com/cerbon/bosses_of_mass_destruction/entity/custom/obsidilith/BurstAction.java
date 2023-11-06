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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;


public class BurstAction implements IActionWithCooldown {
    private final LivingEntity entity;

    public static int burstDelay = 30;

    public BurstAction(LivingEntity entity){
        this.entity = entity;
    }

    @Override
    public int perform() {
        placeRifts();
        return 80;
    }

    private void placeRifts(){
        EventScheduler eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level());
        List<Vec3> circlePoints = MathUtils.buildBlockCircle(7.0);
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

        BMDUtils.playSound(
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
                        () -> BMDUtils.playSound((ServerLevel) level, entity.position(), BMDSounds.OBSIDILITH_HURT.get(), SoundSource.HOSTILE, 1.2f, 64, null),
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
            BMDPacketHandler.sendToPlayer(new SendDeltaMovementS2CPacket(new Vec3(livingEntity.getDeltaMovement().x, 1.3, livingEntity.getDeltaMovement().z)), serverPlayer);

        livingEntity.hurt(
                BMDUtils.shieldPiercing(entity.level(), entity),
                damage
        );
    }
}