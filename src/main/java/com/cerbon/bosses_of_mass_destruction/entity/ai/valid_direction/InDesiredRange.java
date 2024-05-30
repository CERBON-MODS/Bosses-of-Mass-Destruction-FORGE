package com.cerbon.bosses_of_mass_destruction.entity.ai.valid_direction;

import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;

public class InDesiredRange implements IValidDirection {
    private final Function<Vector3d, Boolean> tooClose;
    private final Function<Vector3d, Boolean> tooFar;
    private final Function<Vector3d, Boolean> movingCloser;

    public InDesiredRange(Function<Vector3d, Boolean> tooClose, Function<Vector3d, Boolean> tooFar, Function<Vector3d, Boolean> movingCloser) {
        this.tooClose = tooClose;
        this.tooFar = tooFar;
        this.movingCloser = movingCloser;
    }

    @Override
    public boolean isValidDirection(Vector3d normedDirection) {
        boolean isTooClose = tooClose.apply(normedDirection);
        boolean isMovingCloser = movingCloser.apply(normedDirection);
        boolean isTooFar = tooFar.apply(normedDirection);

        return (isTooClose && !isMovingCloser) ||
                (isTooFar && isMovingCloser) ||
                (!isTooFar && !isTooClose);
    }
}
