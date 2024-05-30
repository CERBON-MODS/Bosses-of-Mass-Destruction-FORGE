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
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LaserAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final Supplier<Boolean> cancelAction;
    private final ServerWorld serverLevel;

    public static final int laserLagTicks = 8;

    public LaserAction(GauntletEntity entity, EventScheduler eventScheduler, Supplier<Boolean> cancelAction, ServerWorld serverLevel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.cancelAction = cancelAction;
        this.serverLevel = serverLevel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (target == null) return 40;

        HistoricalData<Vector3d> laserRenderPositions = new HistoricalData<>(Vector3d.ZERO, laserLagTicks);

        BMDUtils.playSound(serverLevel, entity.position(), BMDSounds.GAUNTLET_LASER_CHARGE.get(), SoundCategory.HOSTILE, 3.0f, 1.0f, 64, null);

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

    private void applyLaser(HistoricalData<Vector3d> laserRenderPositions){
        Vector3d targetLaserPos = laserRenderPositions.getAll().get(0);
        Vector3d extendedLaserPos = extendLaser(entity, targetLaserPos);
        BlockRayTraceResult result = entity.level.clip(
                new RayTraceContext(
                        MobUtils.eyePos(entity),
                        extendedLaserPos,
                        RayTraceContext.BlockMode.COLLIDER,
                        RayTraceContext.FluidMode.NONE,
                        entity
                )
        );

        if (result.getType() == RayTraceResult.Type.BLOCK){
            if (entity.tickCount % 2 == 0)
                VanillaCopiesServer.destroyBlocks(entity, new AxisAlignedBB(result.getLocation(), result.getLocation()).inflate(0.1));

            applyLaserToEntities(result.getLocation());
        }else
            applyLaserToEntities(extendedLaserPos);
    }

    private void applyLaserToEntities(Vector3d laserTargetPos){
        List<LivingEntity> entitiesHit = BMDUtils.findEntitiesInLine(entity.level, MobUtils.eyePos(entity), laserTargetPos, entity)
                .stream().filter(LivingEntity.class::isInstance).map(entity1 -> (LivingEntity) entity1).collect(Collectors.toList());

        for (LivingEntity hitEntity : entitiesHit){
            double originalAttack = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(originalAttack * 0.75);
            entity.doHurtTarget(hitEntity);
            Objects.requireNonNull(entity.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(originalAttack);
        }
    }

    public static Vector3d extendLaser(Entity entity, Vector3d laserTargetPos){
        return MathUtils.unNormedDirection(MobUtils.eyePos(entity), laserTargetPos).normalize().scale(30.0).add(MobUtils.eyePos(entity));
    }
}
