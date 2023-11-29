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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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
        Supplier<Vec3> targetPos = entity::safeGetTargetPos;
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

    @NotNull
    private ValidDirectionAnd getValidDirectionAnd(Supplier<Vec3> targetPos) {
        Function<Vec3, Boolean> tooCloseToTarget = vec3 -> getWithinDistancePredicate(tooCloseToTargetDistance, targetPos).apply(vec3);
        Function<Vec3, Boolean> tooFarFromTarget = vec3 -> !getWithinDistancePredicate(tooFarFromTargetDistance, targetPos).apply(vec3);
        Function<Vec3, Boolean> movingToTarget = vec3 -> MathUtils.movingTowards(entity.safeGetTargetPos(), entity.position(), vec3);

        return new ValidDirectionAnd(
                Arrays.asList(
                        new CanMoveThrough(entity, reactionDistance),
                        new InDesiredRange(tooCloseToTarget, tooFarFromTarget, movingToTarget)
                )
        );
    }

    private void moveAndLookAtTarget(Vec3 velocity){
        BMDUtils.addDeltaMovement(entity, velocity);

        LivingEntity target = entity.getTarget();
        if (target != null){
            entity.getLookControl().setLookAt(MobUtils.eyePos(target));
            entity.lookAt(target, entity.getHeadRotSpeed(), entity.getMaxHeadXRot());
        }
    }

    private Function<Vec3, Boolean> getWithinDistancePredicate(double distance, Supplier<Vec3> targetPos){
        return vec3 -> {
            Vec3 target = entity.position().add(vec3.scale(reactionDistance));
            return MathUtils.withinDistance(target, targetPos.get(), distance);
        };
    }
}
