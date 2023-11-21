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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GauntletClientLaserHandler implements IEntityTick<Level>, IDataAccessorHandler, IEntityEventHandler {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;

    private LivingEntity cachedBeamTarget;
    private final HistoricalData<Pair<Vec3, Vec3>> laserRenderPositions = new HistoricalData<>(Pair.of(Vec3.ZERO, Vec3.ZERO), LaserAction.laserLagTicks);

    public final ClientParticleBuilder laserChargeParticles = new ClientParticleBuilder(BMDParticles.SPARKLES.get())
            .brightness(BMDParticles.FULL_BRIGHT)
            .color(BMDColors.LASER_RED)
            .colorVariation(0.2);

    public GauntletClientLaserHandler(GauntletEntity entity, EventScheduler eventScheduler) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
    }


    @Override
    public void tick(Level level) {
        LivingEntity beamTarget = getBeamTarget();
        if (beamTarget != null){
            Vec3 centerBoxOffset = beamTarget.getBoundingBox().getCenter().subtract(beamTarget.position());
            laserRenderPositions.set(
                    Pair.of(
                            beamTarget.position().add(centerBoxOffset),
                            MobUtils.lastRenderPos(beamTarget).add(centerBoxOffset)
                    )
            );
        }else
            laserRenderPositions.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderLaser(){
        return laserRenderPositions.getSize() > 1;
    }

    @OnlyIn(Dist.CLIENT)
    public Pair<Vec3, Vec3> getLaserRenderPos(){
        Pair<Vec3, Vec3> laserPos = laserRenderPositions.getAll().get(0);
        Vec3 newPos = LaserAction.extendLaser(entity, laserPos.getFirst());
        Vec3 prevPos = LaserAction.extendLaser(entity, laserPos.getSecond());

        BlockHitResult newResult = entity.level().clip(
                new ClipContext(
                        MobUtils.eyePos(entity),
                        newPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        entity
                )
        );

        BlockHitResult prevResult = entity.level().clip(
                new ClipContext(
                        MobUtils.eyePos(entity),
                        prevPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        entity
                )
        );

        Vec3 colNewPos = newResult.getType() == HitResult.Type.BLOCK ? newResult.getLocation() : newPos;
        Vec3 colPrevPos = prevResult.getType() == HitResult.Type.BLOCK ? prevResult.getLocation() : prevPos;
        return Pair.of(colNewPos, colPrevPos);
    }

    private LivingEntity getBeamTarget(){
        if (!hasBeamTarget())
            return null;
        else if (entity.level().isClientSide())
            if (this.cachedBeamTarget != null)
                return this.cachedBeamTarget;
            else {
                Entity entity1 = entity.level().getEntity(entity.getEntityData().get(GauntletEntity.laserTarget));
                if (entity1 instanceof LivingEntity livingEntity){
                    this.cachedBeamTarget = livingEntity;
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
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
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
                                Vec3 lookVec = entity.getLookAngle();
                                for (int i = 0; i <= 1; i++){
                                    Vec3 circularOffset = VecUtils.rotateVector(lookVec.cross(VecUtils.yAxis), lookVec, RandomUtils.range(0, 359));
                                    Vec3 velocity = circularOffset.normalize().reverse().scale(0.07).add(entity.getDeltaMovement().scale(1.2));
                                    Vec3 position = MobUtils.eyePos(entity).add(circularOffset).add(lookVec.scale(0.5));
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
