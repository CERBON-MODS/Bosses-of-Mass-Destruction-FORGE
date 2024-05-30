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
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VanillaCopiesServer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LichMovement {
    private final LichEntity entity;
    private final IEntity iEntity;

    private final double reactionDistance = 4.0;
    private final double idleWanderDistance = 50.0;
    private final double tooFarFromTargetDistance = 30.0;
    private final double tooCloseToTargetDistance = 15.0;

    public LichMovement(LichEntity entity) {
        this.entity = entity;
        this.iEntity = new EntityAdapter(entity);
    }

    public VelocityGoal buildAttackMovement() {
        Function<Vector3d, Boolean> tooCloseToTarget = v -> getWithinDistancePredicate(tooCloseToTargetDistance, entity::safeGetTargetPos).test(v);
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

    @Nonnull
    private ValidDirectionAnd getValidDirectionAnd(Function<Vector3d, Boolean> tooCloseToTarget) {
        Function<Vector3d, Boolean> tooFarFromTarget = v -> !getWithinDistancePredicate(tooFarFromTargetDistance, entity::safeGetTargetPos).test(v);
        Function<Vector3d, Boolean> movingToTarget = v -> MathUtils.movingTowards(entity.safeGetTargetPos(), entity.position(), v);

        return new ValidDirectionAnd(
                Arrays.asList(
                        new CanMoveThrough(entity, reactionDistance),
                        new InDesiredRange(tooCloseToTarget, tooFarFromTarget, movingToTarget)
                )
        );
    }

    private void moveWhileAttacking(Vector3d velocity) {
        BMDUtils.addDeltaMovement(entity, velocity);

        LivingEntity target = entity.getTarget();
        if (target != null) {
            entity.getLookControl().setLookAt(target.position());
            VanillaCopiesServer.lookAtTarget(entity, target.position(), (float)entity.getHeadRotSpeed(), (float)entity.getMaxHeadXRot());
        }
    }

    public VelocityGoal buildWanderGoal() {
        Function<Vector3d, Boolean> tooFarFromTarget = v -> getWithinDistancePredicate(idleWanderDistance, () -> entity.idlePosition).test(v);
        Function<Vector3d, Boolean> movingTowardsIdleCenter = v -> MathUtils.movingTowards(entity.idlePosition, entity.position(), v);

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

    private Predicate<Vector3d> getWithinDistancePredicate(double distance, Supplier<Vector3d> targetPos) {
        return v -> {
            Vector3d target = entity.position().add(v.scale(reactionDistance));
            return MathUtils.withinDistance(target, targetPos.get(), distance);
        };
    }

    private void moveTowards(Vector3d velocity) {
        BMDUtils.addDeltaMovement(entity, velocity);

        Vector3d lookTarget = entity.position().add(new Vector3d(0.0, entity.getEyeHeight(), 0.0)).add(velocity);
        entity.getLookControl().setLookAt(lookTarget);
        VanillaCopiesServer.lookAtTarget(entity, lookTarget, (float)entity.getHeadRotSpeed(), (float)entity.getMaxHeadXRot());
    }
}

