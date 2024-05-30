package com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.random.ModRandom;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MobUtils;
import com.cerbon.bosses_of_mass_destruction.entity.ai.ValidatedTargetSelector;
import com.cerbon.bosses_of_mass_destruction.entity.ai.VelocitySteering;
import com.cerbon.bosses_of_mass_destruction.entity.ai.goals.VelocityGoal;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.CanMoveThrough;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.InDesiredRange;
import com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction.ValidDirectionAnd;
import com.cerbon.bosses_of_mass_destruction.entity.util.EntityAdapter;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class GauntletMovement {
    private final GauntletEntity entity;
    private final double reactionDistance = 4.0;
    private final EntityAdapter iEntity;
    private final double tooFarFromTargetDistance = 25.0;
    private final double tooCloseToTargetDistance = 5.0;

    public GauntletMovement(GauntletEntity entity) {
        this.entity = entity;
        this.iEntity =  new EntityAdapter(entity);
    }

    public VelocityGoal buildAttackMovement(){
        Supplier<Vector3d> targetPos = entity::safeGetTargetPos;
        ValidDirectionAnd canMoveTowardsPositionValidator = getValidDirectionAnd(targetPos);

        ValidatedTargetSelector targetSelector = new ValidatedTargetSelector(
                iEntity,
                canMoveTowardsPositionValidator,
                new ModRandom()
        );

        return new VelocityGoal(
                this::moveAndLookAtTarget,
                new VelocitySteering(iEntity, entity.getAttributeValue(Attributes.FLYING_SPEED), 120.0),
                targetSelector
        );
    }

    @Nonnull
    private ValidDirectionAnd getValidDirectionAnd(Supplier<Vector3d> targetPos) {
        Function<Vector3d, Boolean> tooCloseToTarget = vec3 -> getWithinDistancePredicate(tooCloseToTargetDistance, targetPos).apply(vec3);
        Function<Vector3d, Boolean> tooFarFromTarget = vec3 -> !getWithinDistancePredicate(tooFarFromTargetDistance, targetPos).apply(vec3);
        Function<Vector3d, Boolean> movingToTarget = vec3 -> MathUtils.movingTowards(entity.safeGetTargetPos(), entity.position(), vec3);

        return new ValidDirectionAnd(
                Arrays.asList(
                        new CanMoveThrough(entity, reactionDistance),
                        new InDesiredRange(tooCloseToTarget, tooFarFromTarget, movingToTarget)
                )
        );
    }

    private void moveAndLookAtTarget(Vector3d velocity){
        BMDUtils.addDeltaMovement(entity, velocity);

        LivingEntity target = entity.getTarget();
        if (target != null){
            entity.getLookControl().setLookAt(MobUtils.eyePos(target));
            entity.lookAt(target, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());
        }
    }

    private Function<Vector3d, Boolean> getWithinDistancePredicate(double distance, Supplier<Vector3d> targetPos){
        return vec3 -> {
            Vector3d target = entity.position().add(vec3.scale(reactionDistance));
            return MathUtils.withinDistance(target, targetPos.get(), distance);
        };
    }
}
