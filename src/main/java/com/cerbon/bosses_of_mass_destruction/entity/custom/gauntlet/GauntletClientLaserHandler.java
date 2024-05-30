package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IDataAccessorHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityEventHandler;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class GauntletClientLaserHandler implements IEntityTick<World>, IDataAccessorHandler, IEntityEventHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    private LivingEntity cachedBeamTarget;
    private final HistoricalData<Pair<Vector3d, Vector3d>> laserRenderPositions = new HistoricalData<>(Pair.of(Vector3d.ZERO, Vector3d.ZERO), LaserAction.laserLagTicks);

    public final ClientParticleBuilder laserChargeParticles = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(BMDColors.LASER_RED)
            .colorVariation(0.2);

    public GauntletClientLaserHandler(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }

    @Override
    public void tick(World level) {
        LivingEntity beamTarget = getBeamTarget();
        if (beamTarget != null){
            Vector3d centerBoxOffset = beamTarget.getBoundingBox().getCenter().subtract(beamTarget.position());
            laserRenderPositions.set(
                    Pair.of(
                            beamTarget.position().add(centerBoxOffset),
                            MobUtils.lastRenderPos(beamTarget).add(centerBoxOffset)
                    )
            );
        }else
            laserRenderPositions.clear();
    }

    public boolean shouldRenderLaser(){
        return laserRenderPositions.getSize() > 1;
    }

    public Pair<Vector3d, Vector3d> getLaserRenderPos(){
        Pair<Vector3d, Vector3d> laserPos = laserRenderPositions.getAll().get(0);
        Vector3d newPos = LaserAction.extendLaser(entity, laserPos.getFirst());
        Vector3d prevPos = LaserAction.extendLaser(entity, laserPos.getSecond());

        BlockRayTraceResult newResult = entity.level.clip(
                new RayTraceContext(
                        MobUtils.eyePos(entity),
                        newPos,
                        RayTraceContext.BlockMode.COLLIDER,
                        RayTraceContext.FluidMode.NONE,
                        entity
                )
        );

        BlockRayTraceResult prevResult = entity.level.clip(
                new RayTraceContext(
                        MobUtils.eyePos(entity),
                        prevPos,
                        RayTraceContext.BlockMode.COLLIDER,
                        RayTraceContext.FluidMode.NONE,
                        entity
                )
        );

        Vector3d colNewPos = newResult.getType() == RayTraceResult.Type.BLOCK ? newResult.getLocation() : newPos;
        Vector3d colPrevPos = prevResult.getType() == RayTraceResult.Type.BLOCK ? prevResult.getLocation() : prevPos;
        return Pair.of(colNewPos, colPrevPos);
    }

    private LivingEntity getBeamTarget(){
        if (!hasBeamTarget())
            return null;
        else if (entity.level.isClientSide())
            if (this.cachedBeamTarget != null)
                return this.cachedBeamTarget;
            else {
                Entity entity1 = entity.level.getEntity(entity.getEntityData().get(GauntletEntity.laserTarget));
                if (entity1 instanceof LivingEntity){
                    this.cachedBeamTarget = (LivingEntity) entity1;
                    return this.cachedBeamTarget;
                }else
                    return null;
            }
        else
            return entity.getTarget();
    }

    private boolean hasBeamTarget(){
        return entity.getEntityData().get(GauntletEntity.laserTarget) != 0;
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> data) {
        if (GauntletEntity.laserTarget == data)
            this.cachedBeamTarget = null;
    }

    public void initDataTracker(){
        entity.getEntityData().define(GauntletEntity.laserTarget, 0);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == GauntletAttacks.laserAttack){
            eventScheduler.addEvent(
                    new TimedEvent(
                            () -> {
                                Vector3d lookVec = entity.getLookAngle();
                                for (int i = 0; i <= 1; i++){
                                    Vector3d circularOffset = VecUtils.rotateVector(lookVec.cross(VecUtils.yAxis), lookVec, RandomUtils.range(0, 359));
                                    Vector3d velocity = circularOffset.normalize().reverse().scale(0.07).add(entity.getDeltaMovement().scale(1.2));
                                    Vector3d position = MobUtils.eyePos(entity).add(circularOffset).add(lookVec.scale(0.5));
                                    laserChargeParticles.build(position, velocity);
                                }
                            },
                            0,
                            85,
                            () -> false
                    )
            );
        }
    }
}
