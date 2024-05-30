package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class PunchAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final GauntletConfig mobConfig;
    private final Supplier<Boolean> cancelAction;
    private final ServerWorld serverLevel;

    private double previousSpeed = 0.0;

    public PunchAction(GauntletEntity entity, EventScheduler eventScheduler, GauntletConfig mobConfig, Supplier<Boolean> cancelAction, ServerWorld serverLevel) {
        this.entity = entity;
        this.eventScheduler = eventScheduler;
        this.mobConfig = mobConfig;
        this.cancelAction = cancelAction;
        this.serverLevel = serverLevel;
    }

    @Override
    public int perform() {
        LivingEntity target = entity.getTarget();
        if (target == null) return 40;

        Vector3d targetPos = MobUtils.eyePos(entity).add(MathUtils.unNormedDirection(MobUtils.eyePos(entity), target.position()).scale(1.2));
        int accelerateStartTime = 16;
        int unclenchTime = 56;

        BlockPos breakBoundCenter = new BlockPos(entity.position().add(entity.getLookAngle()));
        AxisAlignedBB breakBounds = new AxisAlignedBB(breakBoundCenter.subtract(new BlockPos(1, 1, 1)), breakBoundCenter.offset(1, 2, 1));
        VanillaCopiesServer.destroyBlocks(entity, breakBounds);
        entity.push(0.0, 0.7, 0.0);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> BMDUtils.playSound(
                                serverLevel,
                                entity.position(),
                                BMDSounds.GAUNTLET_CLINK.get(),
                                SoundCategory.HOSTILE,
                                2.0f,
                                BMDUtils.randomPitch(entity.getRandom()) * 0.8f,
                                32,
                                null
                        ),
                        12,
                        1,
                        cancelAction
                )
        );

        AtomicReference<Double> velocityStack = new AtomicReference<>(0.6);
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            accelerateTowardsTarget(entity, targetPos, velocityStack.get());
                            velocityStack.set(0.32);
                        },
                        accelerateStartTime,
                        15,
                        () -> entity.position().distanceToSqr(targetPos) < 9 || cancelAction.get()
                )
        );
        eventScheduler.addEvent(new TimedEvent(this::whilePunchActive, accelerateStartTime, unclenchTime - accelerateStartTime, cancelAction));

        int closeFistAnimationTime = 7;
        eventScheduler.addEvent(new TimedEvent(entity.hitboxHelper::setClosedFistHitbox, closeFistAnimationTime, 1, cancelAction));

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> entity.level.broadcastEntityEvent(entity, GauntletAttacks.stopPunchAnimation),
                        unclenchTime,
                        1,
                        cancelAction
                )
        );
        eventScheduler.addEvent(new TimedEvent(entity.hitboxHelper::setOpenHandHitbox, unclenchTime + 8));

        return 80;
    }

    private void whilePunchActive(){
        testBlockPhysicalImpact();
        testEntityImpact();
        previousSpeed = entity.getDeltaMovement().length();
    }

    private void testBlockPhysicalImpact(){
        if ((entity.horizontalCollision || entity.verticalCollision) && previousSpeed > 0.55f){
            Vector3d pos = entity.position();
            entity.level.explode(
                    entity,
                    pos.x,
                    pos.y,
                    pos.z,
                    (float) (previousSpeed * mobConfig.normalPunchExplosionMultiplier),
                    VanillaCopiesServer.getEntityDestructionType(entity.level)
            );
        }
    }

    private void testEntityImpact(){
        List<LivingEntity> collidedEntities = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox(), livingEntity -> livingEntity != entity);

        for (LivingEntity target : collidedEntities){
            entity.doHurtTarget(target);
            BMDUtils.addDeltaMovement(target, entity.getDeltaMovement().scale(0.5));
        }
    }

    public static void accelerateTowardsTarget(Entity entity, Vector3d target, double velocity){
        Vector3d dir = MathUtils.unNormedDirection(MobUtils.eyePos(entity), target).normalize();
        Vector3d velocityCorrection = VecUtils.planeProject(entity.getDeltaMovement(), dir);
        BMDUtils.addDeltaMovement(entity, dir.subtract(velocityCorrection).scale(velocity));
    }
}
