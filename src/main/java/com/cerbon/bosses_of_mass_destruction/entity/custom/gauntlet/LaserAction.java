package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventSeries;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class LaserAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> cancelAction;
    private final ServerLevel serverLevel;

    public static final int laserLagTicks = 8;

    public LaserAction(GauntletEntity entity, EventScheduler eventScheduler, Supplier<Boolean> cancelAction, ServerLevel serverLevel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.cancelAction = cancelAction;
        this.serverLevel = serverLevel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (target == null) return 40;

        HistoricalData<Vec3> laserRenderPositions = new HistoricalData<>(Vec3.ZERO, laserLagTicks);

        BMDUtils.playSound(serverLevel, entity.position(), BMDSounds.GAUNTLET_LASER_CHARGE.get(), SoundSource.HOSTILE, 3.0f, 1.0f, 64, null);

        TimedEvent sendStartToClient = new TimedEvent(
                () -> entity.getEntityData().set(GauntletEntity.laserTarget, target.getId()),
                25,
                1,
                cancelAction
        );

        TimedEvent applyLaser = new TimedEvent(
                () -> {
                    laserRenderPositions.set(target.getBoundingBox().getCenter());
                    if (laserRenderPositions.getSize() == laserLagTicks)
                        applyLaser(laserRenderPositions);
                },
                0,
                60,
                cancelAction
        );

        TimedEvent stop = new TimedEvent(
                () -> {
                    laserRenderPositions.clear();
                    entity.getEntityData().set(GauntletEntity.laserTarget, 0);
                    entity.level.broadcastEntityEvent(entity, GauntletAttacks.laserAttackStop);
                },
                0
        );

        eventScheduler.addEvent(new EventSeries(sendStartToClient, applyLaser, stop));
        return 120;
    }

    private void applyLaser(HistoricalData<Vec3> laserRenderPositions){
        Vec3 targetLaserPos = laserRenderPositions.getAll().get(0);
        Vec3 extendedLaserPos = extendLaser(entity, targetLaserPos);
        BlockHitResult result = entity.level.clip(
                new ClipContext(
                        MobUtils.eyePos(entity),
                        extendedLaserPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        entity
                )
        );

        if (result.getType() == HitResult.Type.BLOCK){
            if (entity.tickCount % 2 == 0)
                VanillaCopiesServer.destroyBlocks(entity, new AABB(result.getLocation(), result.getLocation()).inflate(0.1));

            applyLaserToEntities(result.getLocation());
        }else
            applyLaserToEntities(extendedLaserPos);
    }

    private void applyLaserToEntities(Vec3 laserTargetPos){
        List<LivingEntity> entitiesHit = BMDUtils.findEntitiesInLine(entity.level, MobUtils.eyePos(entity), laserTargetPos, entity)
                .stream().filter(LivingEntity.class::isInstance).map(entity1 -> (LivingEntity) entity1).toList();

        for (LivingEntity hitEntity : entitiesHit){
            double originalAttack = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(originalAttack * 0.75);
            entity.doHurtTarget(hitEntity);
            Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(originalAttack);
        }
    }

    public static Vec3 extendLaser(Entity entity, Vec3 laserTargetPos){
        return MathUtils.unNormedDirection(MobUtils.eyePos(entity), laserTargetPos).normalize().scale(30.0).add(MobUtils.eyePos(entity));
    }
}
