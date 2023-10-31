package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CanMoveThrough implements IValidDirection {
    private final Entity entity;
    private final double reactionDistance;

    public CanMoveThrough(Entity entity, double reactionDistance) {
        this.entity = entity;
        this.reactionDistance = reactionDistance;
    }

    @Override
    public boolean isValidDirection(Vec3 normedDirection) {
        Vec3 reactionDirection = normedDirection.multiply(reactionDistance, reactionDistance, reactionDistance).add(entity.getDeltaMovement());
        Vec3 target = entity.position().add(reactionDirection);
        boolean noBlockCollisions = MathUtils.willAABBFit(entity.getBoundingBox(), reactionDirection, box -> !entity.level().noCollision(entity, box));
        ClipContext context = new ClipContext(
                entity.position().add(normedDirection.multiply(1.0, 1.0, 1.0)),
                target,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                entity
        );
        HitResult blockCollision = entity.level().clip(context);
        boolean noFluidCollisions = blockCollision.getType() == HitResult.Type.MISS;

        return noFluidCollisions && noBlockCollisions;
    }
}
