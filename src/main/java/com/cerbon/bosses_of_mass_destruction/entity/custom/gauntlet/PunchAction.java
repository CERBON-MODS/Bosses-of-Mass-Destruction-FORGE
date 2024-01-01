package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.cerbon.cerbons_api.api.general.event.TimedEvent;
import com.cerbon.cerbons_api.api.static_utilities.MathUtils;
import com.cerbon.cerbons_api.api.static_utilities.MobUtils;
import com.cerbon.cerbons_api.api.static_utilities.SoundUtils;
import com.cerbon.cerbons_api.api.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class PunchAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final GauntletConfig mobConfig;
    private final Supplier<Boolean> cancelAction;
    private final ServerLevel serverLevel;

    private double previousSpeed = 0.0;

    public PunchAction(GauntletEntity entity, EventScheduler eventScheduler, GauntletConfig mobConfig, Supplier<Boolean> cancelAction, ServerLevel serverLevel) {
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

        Vec3 targetPos = MobUtils.eyePos(entity).add(MathUtils.unNormedDirection(MobUtils.eyePos(entity), target.position()).scale(1.2));
        int accelerateStartTime = 16;
        int unclenchTime = 56;

        BlockPos breakBoundCenter = BlockPos.containing(entity.position().add(entity.getLookAngle()));
        AABB breakBounds = new AABB(VecUtils.asVec3(breakBoundCenter.subtract(new BlockPos(1, 1, 1))), VecUtils.asVec3(breakBoundCenter.offset(1, 2, 1)));
        VanillaCopiesServer.destroyBlocks(entity, breakBounds);
        entity.push(0.0, 0.7, 0.0);

        eventScheduler.addEvent(
                new TimedEvent(
                        () -> SoundUtils.playSound(
                                serverLevel,
                                entity.position(),
                                BMDSounds.GAUNTLET_CLINK.get(),
                                SoundSource.HOSTILE,
                                2.0f,
                                SoundUtils.randomPitch(entity.getRandom()) * 0.8f,
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
                        () -> entity.level().broadcastEntityEvent(entity, GauntletAttacks.stopPunchAnimation),
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
            Vec3 pos = entity.position();
            entity.level().explode(
                    entity,
                    pos.x,
                    pos.y,
                    pos.z,
                    (float) (previousSpeed * mobConfig.normalPunchExplosionMultiplier),
                    Level.ExplosionInteraction.MOB
            );
        }
    }

    private void testEntityImpact(){
        List<LivingEntity> collidedEntities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox(), livingEntity -> livingEntity != entity);

        for (LivingEntity target : collidedEntities){
            entity.doHurtTarget(target);
            target.addDeltaMovement(entity.getDeltaMovement().scale(0.5));
        }
    }

    public static void accelerateTowardsTarget(Entity entity, Vec3 target, double velocity){
        Vec3 dir = MathUtils.unNormedDirection(MobUtils.eyePos(entity), target).normalize();
        Vec3 velocityCorrection = VecUtils.planeProject(entity.getDeltaMovement(), dir);
        entity.addDeltaMovement(dir.subtract(velocityCorrection).scale(velocity));
    }
}
