package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class InDesiredRange implements IValidDirection {
    private final Function<Vec3, Boolean> tooClose;
    private final Function<Vec3, Boolean> tooFar;
    private final Function<Vec3, Boolean> movingCloser;

    public InDesiredRange(Function<Vec3, Boolean> tooClose, Function<Vec3, Boolean> tooFar, Function<Vec3, Boolean> movingCloser) {
        this.tooClose = tooClose;
        this.tooFar = tooFar;
        this.movingCloser = movingCloser;
    }

    @Override
    public boolean isValidDirection(Vec3 normedDirection) {
        boolean isTooClose = tooClose.apply(normedDirection);
        boolean isMovingCloser = movingCloser.apply(normedDirection);
        boolean isTooFar = tooFar.apply(normedDirection);

        return (isTooClose && !isMovingCloser) ||
                (isTooFar && isMovingCloser) ||
                (!isTooFar && !isTooClose);
    }
}
