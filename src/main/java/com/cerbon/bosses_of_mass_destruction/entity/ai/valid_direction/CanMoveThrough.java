package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class CanMoveThrough implements IValidDirection {
    private final Entity entity;
    private final double reactionDistance;

    public CanMoveThrough(Entity entity, double reactionDistance) {
        this.entity = entity;
        this.reactionDistance = reactionDistance;
    }

    @Override
    public boolean isValidDirection(Vector3d normedDirection) {
        Vector3d reactionDirection = normedDirection.scale(reactionDistance).add(entity.getDeltaMovement());
        Vector3d target = entity.position().add(reactionDirection);
        boolean noBlockCollisions = MathUtils.willAABBFit(entity.getBoundingBox(), reactionDirection, box -> !entity.level.noCollision(entity, box));
        RayTraceContext context = new RayTraceContext(
                entity.position().add(normedDirection.scale(1.0)),
                target,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.ANY,
                entity
        );
        RayTraceResult blockCollision = entity.level.clip(context);
        boolean noFluidCollisions = blockCollision.getType() == RayTraceResult.Type.MISS;

        return noFluidCollisions && noBlockCollisions;
    }
}
