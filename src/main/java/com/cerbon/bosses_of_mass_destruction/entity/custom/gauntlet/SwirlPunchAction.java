package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.config.mob.GauntletConfig;
import com.cerbon.bosses_of_mass_destruction.entity.ai.action.IActionWithCooldown;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SwirlPunchAction implements IActionWithCooldown {
    private final GauntletEntity entity;
    private final EventScheduler eventScheduler;
    private final GauntletConfig mobConfig;
    private final Supplier<Boolean> cancelAction;
    private final ServerLevel serverLevel;

    private double previousSpeed = 0.0;

    public SwirlPunchAction(GauntletEntity entity, EventScheduler eventScheduler, GauntletConfig mobConfig, Supplier<Boolean> cancelAction, ServerLevel serverLevel) {
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

        Vec3 targetDirection = MathUtils.unNormedDirection(MobUtils.eyePos(entity), target.getBoundingBox().getCenter());
        Vec3 targetPos = MobUtils.eyePos(entity).add(targetDirection.scale(1.2));
        int accelerateStartTime = 30;
        int unclenchTime = 60;
        int closeFistAnimationTime = 7;

        entity.push(0.0, 0.7, 0.0);
        BMDUtils.playSound(
                serverLevel,
                entity.position(),
                BMDSounds.GAUNTLET_SPIN_PUNCH.get(),
                SoundSource.HOSTILE,
                2.0f,
                1.0f,
                64,
                null
        );
        entity.getEntityData().set(GauntletEntity.isEnergized, true);
        eventScheduler.addEvent(new TimedEvent(entity.hitboxHelper::setClosedFistHitbox, closeFistAnimationTime, 1, cancelAction));

        AtomicReference<Double> velocityStack = new AtomicReference<>(0.6);
        eventScheduler.addEvent(
                new TimedEvent(
                        () -> {
                            PunchAction.accelerateTowardsTarget(entity, targetPos, velocityStack.get());
                            velocityStack.set(0.40);
                        },
                        accelerateStartTime,
                        15,
                        () -> entity.position().distanceToSqr(targetPos) < 9 || cancelAction.get()
                )
        );
        eventScheduler.addEvent(new TimedEvent(this::whilePunchActive, accelerateStartTime, unclenchTime - accelerateStartTime, cancelAction));
        eventScheduler.addEvent(new TimedEvent(
                () -> {
                    entity.hitboxHelper.setOpenHandHitbox();
                    entity.getEntityData().set(GauntletEntity.isEnergized, false);
                },
                unclenchTime
        ));

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
            if (entity.getEntityData().get(GauntletEntity.isEnergized)){
                entity.level.explode(
                        entity,
                        pos.x,
                        pos.y,
                        pos.z,
                        (float) mobConfig.energizedPunchExplosionSize,
                        true,
                        Explosion.BlockInteraction.DESTROY //TODO: Check
                );
                entity.getEntityData().set(GauntletEntity.isEnergized, false);
            }else {
                entity.level.explode(
                        entity,
                        pos.x,
                        pos.y,
                        pos.z,
                        (float) (previousSpeed * mobConfig.normalPunchExplosionMultiplier),
                        Explosion.BlockInteraction.DESTROY
                );
            }
        }
    }

    private void testEntityImpact(){
        List<LivingEntity> collidedEntities = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox(), livingEntity -> livingEntity != entity);

        for (LivingEntity target : collidedEntities){
            entity.doHurtTarget(target);
            BMDUtils.addDeltaMovement(target, entity.getDeltaMovement().scale(0.5));
        }
    }
}
