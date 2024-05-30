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
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;


public class BurstAction implements IActionWithCooldown {
    private final LivingEntity entity;
    private final EventScheduler eventScheduler;
    private final List<Vector3d> circlePoints;

    public static final int burstDelay = 30;

    public BurstAction(LivingEntity entity){
        this.entity = entity;
        this.eventScheduler = BMDCapabilities.getLevelEventScheduler(entity.level);
        this.circlePoints = MathUtils.buildBlockCircle(7.0);
    }

    @Override
    public int perform() {
        placeRifts();
        return 80;
    }

    private void placeRifts(){
        World level = entity.level;
        RiftBurst riftBurst = new RiftBurst(
                entity,
                (ServerWorld) level,
                BMDParticles.OBSIDILITH_BURST_INDICATOR.get(),
                BMDParticles.OBSIDILITH_BURST.get(),
                burstDelay,
                eventScheduler,
                this::damageEntity
        );

        BMDUtils.playSound(
                (ServerWorld) level,
                entity.position(),
                BMDSounds.OBSIDILITH_PREPARE_ATTACK.get(),
                SoundCategory.HOSTILE,
                3.0f,
                0.7f,
                64,
                null
        );

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> BMDUtils.playSound((ServerWorld) level, entity.position(), BMDSounds.OBSIDILITH_BURST.get(), SoundCategory.HOSTILE, 1.2f, 64, null),
                        burstDelay,
                        1,
                        () -> !entity.isAlive()
                )
        );

        for (Vector3d point : circlePoints)
            riftBurst.tryPlaceRift(entity.position().add(point));
    }

    private void damageEntity(LivingEntity livingEntity){
        float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (livingEntity instanceof ServerPlayerEntity)
            BMDPacketHandler.sendToPlayer(new SendDeltaMovementS2CPacket(new Vector3d(livingEntity.getDeltaMovement().x, 1.3, livingEntity.getDeltaMovement().z)), (ServerPlayerEntity) livingEntity);

        livingEntity.hurt(
                new UnshieldableDamageSource(entity),
                damage
        );
    }
}
