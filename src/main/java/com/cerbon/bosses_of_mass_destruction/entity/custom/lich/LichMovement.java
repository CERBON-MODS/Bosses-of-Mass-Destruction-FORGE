package com.cerbon.bosses_of_mass_destruction.entity.custom.lich;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.ModRandom;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.ValidatedTargetSelector;
import com.cerbon.bosses_of_mass_destruction.entity.ai.VelocitySteering;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.VelocityGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.CanMoveThrough;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.InDesiredRange;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.ValidDirectionAnd;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntity;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LichMovement {
    private final LichEntity entity;
    private final double reactionDistance = 4.0;
    private final double idleWanderDistance = 50.0;
    private final IEntity iEntity;
    private final double tooFarFromTargetDistance = 30.0;
    private final double tooCloseToTargetDistance = 15.0;

    public LichMovement(LichEntity entity) {
        this.entity = entity;
        this.iEntity = new EntityAdapter(entity);
    }

    public VelocityGoal buildAttackMovement() {
        Function<Vec3, Boolean> tooCloseToTarget = v -> getWithinDistancePredicate(tooCloseToTargetDistance, entity::safeGetTargetPos).test(v);
        ValidDirectionAnd canMoveTowardsPositionValidator = getValidDirectionAnd(tooCloseToTarget);
        ValidatedTargetSelector targetSelector = new ValidatedTargetSelector(
                iEntity,
                canMoveTowardsPositionValidator,
                new ModRandom()
        );
        return new VelocityGoal(
                this::moveWhileAttacking,
                createSteering(),
                targetSelector
        );
    }

    @NotNull
    private ValidDirectionAnd getValidDirectionAnd(Function<Vec3, Boolean> tooCloseToTarget) {
        Function<Vec3, Boolean> tooFarFromTarget = v -> !getWithinDistancePredicate(tooFarFromTargetDistance, entity::safeGetTargetPos).test(v);
        Function<Vec3, Boolean> movingToTarget = v -> MathUtils.movingTowards(entity.safeGetTargetPos(), entity.position(), v);

        return new ValidDirectionAnd(
                Arrays.asList(
                        new CanMoveThrough(entity, reactionDistance),
                        new InDesiredRange(tooCloseToTarget, tooFarFromTarget, movingToTarget)
                )
        );
    }

    private void moveWhileAttacking(Vec3 velocity) {
        entity.addDeltaMovement(velocity);

        LivingEntity target = entity.getTarget();
        if (target != null) {
            entity.getLookControl().setLookAt(target.position());
            VanillaCopiesServer.lookAtTarget(entity, target.position(), (float)entity.getHeadRotSpeed(), (float)entity.getMaxHeadXRot());
        }
    }

    public VelocityGoal buildWanderGoal() {
        Function<Vec3, Boolean> tooFarFromTarget = v -> getWithinDistancePredicate(idleWanderDistance, () -> entity.idlePosition).test(v);
        Function<Vec3, Boolean> movingTowardsIdleCenter = v -> MathUtils.movingTowards(entity.idlePosition, entity.position(), v);

        ValidDirectionAnd canMoveTowardsPositionValidator = new ValidDirectionAnd(
                Arrays.asList(
                        new CanMoveThrough(entity, reactionDistance),
                        new InDesiredRange(v -> false, tooFarFromTarget, movingTowardsIdleCenter)
                )
        );

        ValidatedTargetSelector targetSelector = new ValidatedTargetSelector(
                iEntity,
                canMoveTowardsPositionValidator,
                new ModRandom()
        );

        return new VelocityGoal(
                this::moveTowards,
                createSteering(),
                targetSelector
        );
    }

    private VelocitySteering createSteering() {
        return new VelocitySteering(iEntity, entity.getAttributeValue(Attributes.FLYING_SPEED), 120.0);
    }

    private Predicate<Vec3> getWithinDistancePredicate(double distance, Supplier<Vec3> targetPos) {
        return v -> {
            Vec3 target = entity.position().add(v.multiply(reactionDistance, reactionDistance, reactionDistance));
            return MathUtils.withinDistance(target, targetPos.get(), distance);
        };
    }

    private void moveTowards(Vec3 velocity) {
        entity.addDeltaMovement(velocity);

        Vec3 lookTarget = entity.position().add(new Vec3(0.0, entity.getEyeHeight(), 0.0)).add(velocity);
        entity.getLookControl().setLookAt(lookTarget);
        VanillaCopiesServer.lookAtTarget(entity, lookTarget, (float)entity.getHeadRotSpeed(), (float)entity.getMaxHeadXRot());
    }
}

