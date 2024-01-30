package com.cerbon.bosses_of_mass_destruction.animation;

import java.util.function.Supplier;

public class PauseAnimationTimer implements IAnimationTimer {
    private final Supplier<Double> sysTimeProvider;
    private final Supplier<Boolean> isPaused;
    private double pauseTime = 0.0;
    private double pauseStart = 0.0;

    public PauseAnimationTimer(Supplier<Double> sysTimeProvider, Supplier<Boolean> isPaused) {
        this.sysTimeProvider = sysTimeProvider;
        this.isPaused = isPaused;
    }

    @Override
    public double getCurrentTick() {
        double sysTime = sysTimeProvider.get();

        if (isPaused.get()) {
            if (pauseStart == 0.0) {
                pauseStart = sysTime;
            }
            return (pauseStart - pauseTime);

        } else if (pauseStart != 0.0) {
            double timeElapsed = sysTime - pauseStart;
            pauseTime += timeElapsed;
            pauseStart = 0.0;
        }

        return (sysTime - pauseTime);
    }
}
